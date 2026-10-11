package com.hyw.gym.service;

import com.hyw.gym.common.Result;
import com.hyw.gym.common.UserHolder;
import com.hyw.gym.config.GymLocation;
import com.hyw.gym.dao.SignInDao;
import com.hyw.gym.dao.UserDao;
import com.hyw.gym.dto.SignRankDTO;
import com.hyw.gym.dto.SignResultDTO;
import com.hyw.gym.dto.SignStatusDTO;
import com.hyw.gym.entity.SignIn;
import com.hyw.gym.entity.User;
import com.hyw.gym.util.GeoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 会员端-每日签到
 * 流程：登录 → 坐标合法性 → 距离校验 → Redis 今日防重 → 连续天数 → 月度位图 → 落库
 */
@Service
public class MemberSignDaoImpl {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SignInDao signInDao;
    @Autowired
    private UserDao userDao;

    private static final String SIGN_TODAY_PREFIX = "sign:today:";          // 今日防重
    private static final String SIGN_BITMAP_PREFIX = "sign:bitmap:";        // 月度位图
    private static final String SIGN_CONTINUOUS_PREFIX = "sign:continuous:"; // 连续天数
    private static final String SIGN_RANK_PREFIX = "sign:rank:";            // 排行榜 ZSET
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyyMM");

    public Result<SignResultDTO> sign(double latitude, double longitude) {
        // 1. 登录校验
        User current = UserHolder.get();
        if (current == null) {
            return Result.error(401, "未登录");
        }

        // 2. 坐标合法性校验（防止前端传非法值，如 999）
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return Result.error(400, "坐标不合法");
        }

        Long memberId = current.getId();
        LocalDate today = LocalDate.now();
        Date todaySql = Date.valueOf(today);

        // 3. 距离校验（地理围栏）：服务端裁决，防前端改坐标作弊
        double distance = GeoUtil.distanceMeters(latitude, longitude,
                GymLocation.GYM_LAT, GymLocation.GYM_LNG);
        if (distance > GymLocation.MAX_DISTANCE_METERS) {
            return Result.error(400, "不在健身房附近，无法签到（距健身房约" + (int) distance + "米）");
        }

        // 4. Redis 今日防重：SETNX 原子设置（防并发双击）
        String todayKey = SIGN_TODAY_PREFIX + memberId + ":" + today;
        Boolean first = redisTemplate.opsForValue().setIfAbsent(todayKey, "1");
        if (!Boolean.TRUE.equals(first)) {
            return Result.error(400, "今日已签到");
        }
        // TTL 设为【到明日 0 点的剩余秒数】：避免晚上签到后，次日凌晨被"24h 固定 TTL"误拦
        long ttlSeconds = Duration.between(LocalDateTime.now(), today.plusDays(1).atStartOfDay()).getSeconds();
        redisTemplate.expire(todayKey, Math.max(1, ttlSeconds), TimeUnit.SECONDS);

        // 5. 计算连续天数：昨天签过 → 昨天连续+1，否则从 1 重新开始
        int continuousDays = 1;
        Date yesterdaySql = Date.valueOf(today.minusDays(1));
        SignIn yesterday = signInDao.findByMemberIdAndSignDate(memberId, yesterdaySql).orElse(null);
        if (yesterday != null) {
            continuousDays = yesterday.getContinuousDays() + 1;
        }

        // 6. 写月度位图：第几天 = 当月第几天-1（位从 0 开始）
        String bitmapKey = SIGN_BITMAP_PREFIX + memberId + ":" + today.format(MONTH_FMT);
        redisTemplate.opsForValue().setBit(bitmapKey, today.getDayOfMonth() - 1, true);

        // 7. 连续天数写 Redis（后续首页/奖励判断秒出）
        redisTemplate.opsForValue().set(SIGN_CONTINUOUS_PREFIX + memberId, String.valueOf(continuousDays));

        // 8. 落库：MySQL 持久化，唯一约束做最后防线（Redis 拦漏时兜底）
        SignIn record = new SignIn();
        record.setMember(current);
        record.setSignDate(todaySql);
        record.setContinuousDays(continuousDays);
        record.setCreateTime(new Timestamp(System.currentTimeMillis()));
        try {
            signInDao.save(record);
        } catch (Exception e) {
            // 唯一约束冲突：极端并发下 Redis 漏拦，数据库拒绝 → 幂等返回"今日已签到"
            redisTemplate.delete(todayKey);
            return Result.error(400, "今日已签到");
        }

        // 9. 排行榜累加：sign:rank:{yyyyMM} 本月次数 +1（ZINCRBY 原子累加）
        redisTemplate.opsForZSet().incrementScore(
                SIGN_RANK_PREFIX + today.format(MONTH_FMT),
                String.valueOf(memberId), 1);

        // 10. 组装返回
        SignResultDTO dto = new SignResultDTO();
        dto.setSigned(true);
        dto.setContinuousDays(continuousDays);
        dto.setMessage("签到成功，连续签到" + continuousDays + "天");
        return Result.success(dto);
    }

    public Result<SignStatusDTO> status() {
        // 1. 登录校验
        User current = UserHolder.get();
        if (current == null) return Result.error(401, "未登录");

        Long memberId = current.getId();
        LocalDate today = LocalDate.now();
        String bitmapKey = SIGN_BITMAP_PREFIX + memberId + ":" + today.format(MONTH_FMT);
        int dayOfMonth = today.getDayOfMonth();   // 比如 10 号 → 位下标 9

        // 2. 今日是否签到：位图 GETBIT（不存在 key 返回 false，天然安全）
        boolean signed = Boolean.TRUE.equals(
                redisTemplate.opsForValue().getBit(bitmapKey, dayOfMonth - 1));

        // 3. 连续天数：从今天位往回数连续的 1（今天、昨天、前天……遇 0 停）
        int continuousDays = 0;
        for (int i = dayOfMonth - 1; i >= 0; i--) {
            if (Boolean.TRUE.equals(redisTemplate.opsForValue().getBit(bitmapKey, i))) {
                continuousDays++;
            } else {
                break;
            }
        }

        // 4. 本月签到次数：BITCOUNT 数本月位图里所有 1
        Integer monthCount = 0;
        for (int i = 0; i < dayOfMonth; i++) {
            if (Boolean.TRUE.equals(redisTemplate.opsForValue().getBit(bitmapKey, i))) {
                monthCount++;
            }
        }

        // 5. 组装
        SignStatusDTO dto = new SignStatusDTO();
        dto.setSigned(signed);
        dto.setContinuousDays(continuousDays);
        dto.setMonthCount(monthCount == null ? 0 : monthCount.intValue());
        dto.setMessage(signed ? "今日已签到" : "今日未签到");
        return Result.success(dto);
    }

    // 签到排行榜：本月 Top10 + 我的排名（ZSET 原生排序）
    public Result<SignRankDTO> rank() {
        User current = UserHolder.get();
        if (current == null) return Result.error(401, "未登录");

        LocalDate today = LocalDate.now();
        String rankKey = SIGN_RANK_PREFIX + today.format(MONTH_FMT);

        // 1. Top 10：ZREVRANGE 按分数倒序取前 10（含分数）
        Set<ZSetOperations.TypedTuple<String>> top =
                redisTemplate.opsForZSet().reverseRangeWithScores(rankKey, 0, 9);

        SignRankDTO dto = new SignRankDTO();
        List<SignRankDTO.RankItem> list = new ArrayList<>();
        if (top != null && !top.isEmpty()) {
            // 2. 收集 id 与分数，批量查姓名（一次 SQL，避免循环查库）
            List<Long> ids = new ArrayList<>();
            Map<Long, Double> scoreMap = new HashMap<>();
            for (ZSetOperations.TypedTuple<String> t : top) {
                Long mid = Long.valueOf(t.getValue());
                ids.add(mid);
                scoreMap.put(mid, t.getScore());
            }
            List<User> users = userDao.findAllById(ids);
            Map<Long, String> nameMap = new HashMap<>();
            for (User u : users) {
                nameMap.put(u.getId(), u.getRealName());
            }
            // 3. 组装榜单（按分数从高到低，名次从 1 开始）
            int rank = 1;
            for (Long mid : ids) {
                SignRankDTO.RankItem item = new SignRankDTO.RankItem();
                item.setRank(rank++);
                item.setMemberId(mid);
                item.setRealName(nameMap.get(mid));
                item.setCount(scoreMap.get(mid).intValue());
                list.add(item);
            }
        }
        dto.setRankList(list);

        // 4. 我的排名：ZREVRANK 返回 0 基下标，+1 才是人类理解的第几名
        Long myPos = redisTemplate.opsForZSet().reverseRank(rankKey, String.valueOf(current.getId()));
        if (myPos != null) {
            Double myScore = redisTemplate.opsForZSet().score(rankKey, String.valueOf(current.getId()));
            SignRankDTO.RankItem mine = new SignRankDTO.RankItem();
            mine.setRank(myPos.intValue() + 1);
            mine.setMemberId(current.getId());
            mine.setRealName(current.getRealName());
            mine.setCount(myScore == null ? 0 : myScore.intValue());
            dto.setMyRank(mine);
        }
        return Result.success(dto);
    }

}
