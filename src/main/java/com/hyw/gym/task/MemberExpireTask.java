package com.hyw.gym.task;

import com.hyw.gym.dao.UserDao;
import com.hyw.gym.entity.User;
import com.hyw.gym.enums.UserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 会员剩余天数每日校准任务
 *
 * 背景：数据库不会随时间流逝自动变化，remaining_days 只是一个静态数字，
 * 必须由代码在某个时机主动更新，否则"剩余天数"永远停在续卡时的值。
 *
 * 思路：以 member_xufei(到期日) 为唯一权威，每天按"到期日 - 今天"重算 remaining_days。
 * 之所以按到期日重算而不是简单"每天减一"：即使某天任务漏跑或字段被改乱，
 * 下次校准会自动纠正，不会积累误差。
 *
 * 注意：如需临时验证，把 cron 改成每分钟执行（"0 * * * * ?"），验证完改回。
 */
@Slf4j
@Component
public class MemberExpireTask {

    @Autowired
    private UserDao userDao;

    // 每天凌晨 00:10 执行一次（cron 格式：秒 分 时 日 月 周）
    @Scheduled(cron = "0 10 0 * * ?")
    public void updateRemainingDays() {
        List<User> members = userDao.findByUserType(UserType.MEMBER.getType());
        // 注意：必须用"当天零点"，不能 new java.sql.Date(System.currentTimeMillis())
        // 后者带当前时分秒，与到期日(零点)相减后取整会少算 1 天
        java.sql.Date today = java.sql.Date.valueOf(java.time.LocalDate.now());
        int updated = 0;
        for (User member : members) {
            java.sql.Date expire = member.getMemberXufei();
            if (expire == null) {
                continue; // 从没办过卡，跳过
            }
            // 剩余天数 = 到期日 - 今天（毫秒差换算成天，可能为负表示已过期）
            long diffDays = (expire.getTime() - today.getTime()) / (24 * 3600 * 1000L);
            int remain = (int) Math.max(0, diffDays); // 不小于 0

            // 有变化才写库，避免无谓更新
            Integer oldRemain = member.getRemainingDays();
            if (oldRemain == null || oldRemain != remain) {
                member.setRemainingDays(remain);
                if (remain <= 0) {
                    member.setStatus(0); // 0 = 请续卡（与会员端 renew 的状态口径一致）
                }
                userDao.save(member);
                updated++;
            }
        }
        log.info("会员剩余天数校准完成：会员数={}，更新={}", members.size(), updated);
    }
}
