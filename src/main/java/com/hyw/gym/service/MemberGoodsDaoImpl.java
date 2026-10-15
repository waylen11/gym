package com.hyw.gym.service;

import com.hyw.gym.common.Result;
import com.hyw.gym.common.UserHolder;
import com.hyw.gym.dao.GoodsDao;
import com.hyw.gym.dto.GoodsDTO;
import com.hyw.gym.entity.Goods;
import com.hyw.gym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员端-商品列表：展示前台在售商品，库存状态由后端计算（售罄标注）
 */
@Service
public class MemberGoodsDaoImpl {

    @Autowired
    private GoodsDao goodsDao;

    public Result<List<GoodsDTO>> goods() {
        User current = UserHolder.get();
        if (current == null) {
            return Result.error(401, "未登录");
        }
        List<Goods> list = goodsDao.findAll();
        List<GoodsDTO> vos = new ArrayList<>();
        for (Goods g : list) {
            GoodsDTO dto = new GoodsDTO();
            dto.setGoodsId(g.getId());
            dto.setGoodsName(g.getGoodsName());
            dto.setUnit(g.getUnit());
            dto.setPrice(g.getPrice());
            dto.setSellPrice(g.getSellPrice());
            dto.setStock(g.getStock());
            // 库存状态由后端计算，前端零逻辑：>0 有货，否则售罄
            dto.setStockText(g.getStock() != null && g.getStock() > 0 ? "有货" : "售罄");
            dto.setDescription(g.getDescription());
            dto.setPointsPrice(g.getPointsPrice() == null ? 0 : g.getPointsPrice());
            vos.add(dto);
        }
        return Result.success(vos);
    }
}
