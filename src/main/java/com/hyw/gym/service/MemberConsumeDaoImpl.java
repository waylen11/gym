package com.hyw.gym.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hyw.gym.common.Result;
import com.hyw.gym.common.UserHolder;
import com.hyw.gym.dao.GoodInfoDao;
import com.hyw.gym.dto.ConsumeRecordDTO;
import com.hyw.gym.entity.GoodInfo;
import com.hyw.gym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 会员端-我的消费记录（商品购买明细）
 * Redis 短缓存：cache:consume:{memberId}，TTL 5 分钟
 */
@Service
public class MemberConsumeDaoImpl {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GoodInfoDao goodInfoDao;

    private static final String CONSUME_PREFIX = "cache:consume:";
    private static final long CACHE_EXPIRE_MINUTES = 5;

    public Result<List<ConsumeRecordDTO>> showMyConsumptions() {
        // 1. 登录校验
        User current = UserHolder.get();
        if (current == null) {
            return Result.error(401, "未登录");
        }
        Long memberId = current.getId();
        // 2. 查缓存
        String key = CONSUME_PREFIX + memberId;
        String cached = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(cached)) {
            return Result.success(JSONUtil.toList(cached, ConsumeRecordDTO.class));
        }
        // 3. 查库：复用现成的按会员查销售记录方法
        List<GoodInfo> records = goodInfoDao.queryByIdNative(memberId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ConsumeRecordDTO> vos = new ArrayList<>();
        for (GoodInfo g : records) {
            ConsumeRecordDTO vo = new ConsumeRecordDTO();
            vo.setId(g.getId());
            vo.setCount(g.getQuantity());
            vo.setTotalPrice(g.getAmount());
            vo.setCreateTime(g.getSaleTime() == null ? null : sdf.format(g.getSaleTime()));
            if (g.getGoods() != null) {
                ConsumeRecordDTO.GoodsItem item = new ConsumeRecordDTO.GoodsItem();
                item.setId(g.getGoods().getId());
                item.setGoodsName(g.getGoods().getGoodsName());
                item.setPrice(g.getGoods().getPrice());
                vo.setGoods(item);
            }
            vos.add(vo);
        }
        // 4. 写缓存 5 分钟
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(vos), CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return Result.success(vos);
    }
}
