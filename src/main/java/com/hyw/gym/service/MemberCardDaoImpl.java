package com.hyw.gym.service;
// @author 黄元威

import com.hyw.gym.common.Result;
import com.hyw.gym.common.UserHolder;
import com.hyw.gym.dao.MemberTypeDao;
import com.hyw.gym.dao.UserDao;
import com.hyw.gym.dao.ChongzhiDao;
import com.hyw.gym.dto.CardInfoVO;
import com.hyw.gym.entity.Chongzhi;
import com.hyw.gym.entity.Membertype;
import com.hyw.gym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 会员卡业务：购买/续费会员卡
 * 设计：前端只传卡类型 id，金额取卡类型价格，到期日自动计算
 * Redis：防重复提交锁（SETNX）+ 会员卡缓存失效
 */
@Service
public class MemberCardDaoImpl {

    @Autowired
    private UserDao userDao;
    @Autowired
    private MemberTypeDao memberttypeDao;
    @Autowired
    private ChongzhiDao chongzhiDao;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String LOCK_KEY_PREFIX = "lock:renew:";
    private static final String CACHE_CARD_INFO_PREFIX = "cache:cardInfo:";
    private static final String CACHE_RENEW_PREFIX = "cache:renew:";

    /**
     * 购买/续费会员卡
     *
     * @param typeId 卡类型 ID（member_type.type_id）
     * @return Result，成功时 data 为续费后的最新卡信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> renew(Long typeId) {
        // 1. 当前登录会员（拦截器已放入 UserHolder）
        User current = UserHolder.get();
        if (current == null) {
            return Result.error(401, "未登录");
        }
        if (typeId == null) {
            return Result.error(400, "卡类型ID不能为空");
        }

        Long memberId = current.getId();

        // 2. Redis 分布式锁：防重复提交/并发重复续费
        //    Spring Data Redis 2.0 没有 setIfAbsent(带超时) 重载：先 SETNX 再 EXPIRE（毫秒级竞态可接受），
        //    finally 中释放，10 秒 TTL 作为进程崩溃时的兜底
        String lockKey = LOCK_KEY_PREFIX + memberId;
        Boolean lockOk = redisTemplate.opsForValue().setIfAbsent(lockKey, "1");
        if (Boolean.FALSE.equals(lockOk)) {
            return Result.error(400, "操作过于频繁，请稍后重试");
        }
        redisTemplate.expire(lockKey, 10, TimeUnit.SECONDS);

        try {
            // 3. 查卡类型
            Membertype membertype = memberttypeDao.findById(typeId).orElse(null);
            if (membertype == null) {
                return Result.error(400, "会员卡类型不存在");
            }

            // 4. 查完整会员信息（UserHolder 里的可能是旧数据，重新查库保证准确）
            User member = userDao.findById(memberId).orElse(null);
            if (member == null) {
                return Result.error(400, "会员不存在");
            }

            // 5. 保存充值记录（czStatic=2 续卡标记，金额取卡类型价格）
            Chongzhi chongzhi = new Chongzhi();
            chongzhi.setMember(member);
            chongzhi.setMembertype(membertype);
            chongzhi.setMoney((long) membertype.getPrice());
            chongzhi.setCzStatic(2L);
            chongzhi.setRechargeTime(new java.sql.Timestamp(System.currentTimeMillis()));
            chongzhiDao.save(chongzhi);

            // 6. 累加剩余天数 + 更新卡类型 + 到期日
            int oldDays = member.getRemainingDays() != null ? member.getRemainingDays() : 0;
            int newDays = oldDays + (int) membertype.getValidDays();
            member.setRemainingDays(newDays);
            member.setMemberType(membertype);
            member.setMemberXufei(calcExpireDate(member, membertype));
            member.setStatus(newDays > 0 ? 1 : 0);
            userDao.save(member);

            // 7. 更新当前线程上下文：本请求内 /me 立即返回最新数据（下次请求拦截器也会重新查库）
            UserHolder.save(member);

            // 8. 失效会员卡相关缓存 key（接口文档规划的缓存位，预留）
            redisTemplate.delete(CACHE_CARD_INFO_PREFIX + memberId);
            redisTemplate.delete(CACHE_RENEW_PREFIX + memberId);

            // 9. 返回续费后的最新卡信息
            Map<String, Object> data = new HashMap<>();
            data.put("remainingDays", newDays);
            data.put("memberXufei", member.getMemberXufei().toString());
            data.put("memberType", membertype.getTypeName());
            return Result.success(data);
        } finally {
            // 释放锁（无论成功失败都必须释放）
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 计算续费后的到期日：基准取 max(今天, 现有到期日)，再加卡类型有效天数
     * 即：会员卡还有效 → 从原到期日顺延；已过期 → 从今天重新起算
     */
    private java.sql.Date calcExpireDate(User member, Membertype membertype) {
        Calendar cal = Calendar.getInstance();
        java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
        if (member.getMemberXufei() != null && member.getMemberXufei().after(now)) {
            cal.setTime(member.getMemberXufei());
        } else {
            cal.setTime(now);
        }
        cal.add(Calendar.DAY_OF_YEAR, (int) membertype.getValidDays());
        return new java.sql.Date(cal.getTimeInMillis());
    }

    public Result<CardInfoVO> cardInfo() {
        User current = UserHolder.get();
        if (current == null) {
            return Result.error(401, "未登录");
        }
        User member = userDao.findById(current.getId()).orElse(null);
        if (member == null) {
            return Result.error(400, "会员不存在");
        }

        List<CardInfoVO.RenewRecordItem> records = new ArrayList<>();
        for (Chongzhi c : chongzhiDao.queryByIdNative(current.getId())) {
            if (c.getCzStatic() == 2L) {
                CardInfoVO.RenewRecordItem rec = new CardInfoVO.RenewRecordItem();
                rec.setRechargeId(c.getId());
                rec.setMoney(c.getMoney());
                rec.setRechargeTime(c.getRechargeTime() == null ? null : c.getRechargeTime().toString());
                if (c.getMembertype() != null) {
                    CardInfoVO.MemberTypeItem t = new CardInfoVO.MemberTypeItem();
                    t.setId(c.getMembertype().getId());
                    t.setTypeName(c.getMembertype().getTypeName());
                    rec.setMembertype(t);
                }
                rec.setRemark(c.getRemark());
                records.add(rec);
            }
        }

        CardInfoVO vo = new CardInfoVO();
        vo.setRemainingDays(member.getRemainingDays());
        vo.setMemberXufei(member.getMemberXufei() == null ? null : member.getMemberXufei().toString());
        vo.setStatus(member.getStatus());
        vo.setRenewRecords(records);
        return Result.success(vo);
    }
}
