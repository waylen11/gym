package com.hyw.gym.service;
// @author 黄元威

import com.hyw.gym.common.Result;
import com.hyw.gym.common.UserHolder;
import com.hyw.gym.dao.GoodsDao;
import com.hyw.gym.dao.PointsExchangeRecordDao;
import com.hyw.gym.dao.UserDao;
import com.hyw.gym.dto.ExchangeRecordDTO;
import com.hyw.gym.entity.Goods;
import com.hyw.gym.entity.PointsExchangeRecord;
import com.hyw.gym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员端-积分兑换商品
 * 流程：校验登录 → 校验商品参与积分兑换 → 校验积分/库存 → 事务扣积分+扣库存+写记录
 */
@Service
public class MemberExchangeDaoImpl {

    @Autowired
    private PointsExchangeRecordDao exchangeDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private GoodsDao goodsDao;

    // POST /api/member/exchange/{goodsId}：积分兑换商品
    @Transactional
    public Result<Map<String, Object>> exchange(Long goodsId) {
        // 1. 登录校验
        User current = UserHolder.get();
        if (current == null) {
            return Result.error(401, "未登录");
        }

        // 2. 商品存在 + 参与积分兑换
        Goods goods = goodsDao.findById(goodsId).orElse(null);
        if (goods == null) {
            return Result.error(400, "商品不存在");
        }
        int need = goods.getPointsPrice() == null ? 0 : goods.getPointsPrice();
        if (need <= 0) {
            return Result.error(400, "该商品不参与积分兑换");
        }
        // 3. 库存校验
        if (goods.getStock() == null || goods.getStock() <= 0) {
            return Result.error(400, "商品已售罄");
        }

        // 4. 积分校验（重新查库，UserHolder 是登录时快照）
        User fresh = userDao.findById(current.getId()).orElse(null);
        if (fresh == null) {
            return Result.error(400, "会员不存在");
        }
        int points = fresh.getPoints() == null ? 0 : fresh.getPoints();
        if (points < need) {
            return Result.error(400, "积分不足，需要" + need + "积分，当前只有" + points + "积分");
        }

        // 5. 同一事务：扣积分 + 扣库存 + 写兑换记录（防超卖/防积分错乱）
        fresh.setPoints(points - need);
        userDao.save(fresh);
        goods.setStock(goods.getStock() - 1);
        goodsDao.save(goods);

        PointsExchangeRecord rec = new PointsExchangeRecord();
        rec.setMember(fresh);
        rec.setGoods(goods);
        rec.setPointsCost(need);
        rec.setCreateTime(new Timestamp(System.currentTimeMillis()));
        exchangeDao.save(rec);

        Map<String, Object> data = new HashMap<>();
        data.put("goodsName", goods.getGoodsName());
        data.put("pointsCost", need);
        data.put("remainPoints", points - need);
        return Result.success(data);
    }

    // GET /api/member/exchanges：我的兑换记录
    public Result<List<ExchangeRecordDTO>> myExchanges() {
        User current = UserHolder.get();
        if (current == null) {
            return Result.error(401, "未登录");
        }
        List<PointsExchangeRecord> list =
                exchangeDao.findByMemberIdOrderByCreateTimeDesc(current.getId());
        List<ExchangeRecordDTO> vos = new ArrayList<>();
        for (PointsExchangeRecord r : list) {
            ExchangeRecordDTO v = new ExchangeRecordDTO();
            v.setExchangeId(r.getId());
            v.setGoodsName(r.getGoods() == null ? null : r.getGoods().getGoodsName());
            v.setPointsCost(r.getPointsCost());
            v.setCreateTime(r.getCreateTime() == null ? null : r.getCreateTime().toString());
            vos.add(v);
        }
        return Result.success(vos);
    }
}
