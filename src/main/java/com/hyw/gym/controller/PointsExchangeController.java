package com.hyw.gym.controller;
// @author 黄元威

import com.hyw.gym.dao.PointsExchangeRecordDao;
import com.hyw.gym.entity.PointsExchangeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 管理端-积分兑换记录查询
 */
@Controller
@RequestMapping("/pointsExchange")
public class PointsExchangeController {

    @Autowired
    private PointsExchangeRecordDao exchangeDao;

    /**
     * 进入兑换记录页面
     */
    @RequestMapping("/sp")
    public String sp() {
        return "WEB-INF/jsp/PointsExchange";
    }

    /**
     * 兑换记录列表（运营复盘：谁、用多少积分、换了什么商品、什么时候）
     */
    @RequestMapping("/list")
    @ResponseBody
    public List<Map<String, Object>> list() {
        List<PointsExchangeRecord> list = exchangeDao.findAll();
        List<Map<String, Object>> vos = new ArrayList<>();
        for (PointsExchangeRecord r : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("exchangeId", r.getId());
            m.put("memberName", r.getMember() == null ? null : r.getMember().getRealName());
            m.put("goodsName", r.getGoods() == null ? null : r.getGoods().getGoodsName());
            m.put("pointsCost", r.getPointsCost());
            m.put("createTime", r.getCreateTime() == null ? null : r.getCreateTime().toString());
            vos.add(m);
        }
        return vos;
    }
}
