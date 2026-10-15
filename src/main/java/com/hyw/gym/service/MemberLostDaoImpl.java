package com.hyw.gym.service;

import com.hyw.gym.common.Result;
import com.hyw.gym.common.UserHolder;
import com.hyw.gym.dao.LoosDao;
import com.hyw.gym.dto.LostPropertyDTO;
import com.hyw.gym.entity.Loos;
import com.hyw.gym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员端-失物招领：查询前台登记的遗失物品（未认领优先展示）
 */
@Service
public class MemberLostDaoImpl {

    @Autowired
    private LoosDao loosDao;

    /**
     * 失物招领列表
     *
     * @param status 可选：null=全部 / 0=未认领 / 1=已认领
     */
    public Result<List<LostPropertyDTO>> losts(Integer status) {
        User current = UserHolder.get();
        if (current == null) {
            return Result.error(401, "未登录");
        }
        List<Loos> list = loosDao.queryLosts(status);
        // 手动组装 DTO：不把实体整串带出去，且不暴露认领人姓名/电话（隐私保护）
        List<LostPropertyDTO> vos = new ArrayList<>();
        for (Loos l : list) {
            LostPropertyDTO dto = new LostPropertyDTO();
            dto.setLostId(l.getId());
            dto.setItemName(l.getItemName());
            dto.setImage(l.getLoosImages());
            dto.setAddress(l.getLoosAddress());
            dto.setFoundDate(l.getFoundDate() == null ? null : l.getFoundDate().toString());
            dto.setStatus(l.getStatus());
            dto.setStatusText(l.getStatus() != null && l.getStatus() == 1 ? "已认领" : "未认领");
            dto.setDescription(l.getDescription());
            vos.add(dto);
        }
        return Result.success(vos);
    }
}
