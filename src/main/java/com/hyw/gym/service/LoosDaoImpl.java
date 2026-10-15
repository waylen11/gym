package com.hyw.gym.service;
// @author 黄元威

import com.hyw.gym.dao.LoosDao;
import com.hyw.gym.entity.Loos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 遗失物品管理service实现层
 */
@Service
public class LoosDaoImpl {
    @Autowired
    private LoosDao loosDao;
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @Description: 遗失物品管理service实现层-分页查询（名称模糊 + 状态筛选，参数化防注入）
     */
    public Map<String,Object> query(Map<String,Object> map1){
        String itemName = (String) map1.get("itemName");
        Object statusObj = map1.get("status");
        Integer status = (statusObj == null || statusObj.toString().isEmpty())
                ? null : Integer.parseInt(statusObj.toString());

        // 分页查询：参数绑定，避免字符串拼接注入
        String jpal = "from Loos where 1=1";
        if (itemName != null && !itemName.equals("")) {
            jpal = jpal + " and itemName like :itemName";
        }
        if (status != null) {
            jpal = jpal + " and status = :status";
        }
        Query qu = entityManager.createQuery(jpal);
        if (itemName != null && !itemName.equals("")) {
            qu.setParameter("itemName", "%" + itemName + "%");
        }
        if (status != null) {
            qu.setParameter("status", status);
        }
        qu.setFirstResult((int)map1.get("qi"));
        qu.setMaxResults((int)map1.get("shi"));

        // 总数统计：参数同样绑定
        String jpa = "select count(l) from Loos l where 1=1";
        if (itemName != null && !itemName.equals("")) {
            jpa = jpa + " and itemName like :itemName";
        }
        if (status != null) {
            jpa = jpa + " and status = :status";
        }
        Query countQuery = entityManager.createQuery(jpa);
        if (itemName != null && !itemName.equals("")) {
            countQuery.setParameter("itemName", "%" + itemName + "%");
        }
        if (status != null) {
            countQuery.setParameter("status", status);
        }
        Long count = (Long) countQuery.getSingleResult();
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("total",count);
        map.put("rows",qu.getResultList());
        return map;
    }

    /**
     * @Description: 遗失物品管理service实现层-修改信息
     */
    public int update(Loos loos){
        Loos loos1=loosDao.findById(loos.getId()).get();
       loos1.setStatus(loos.getStatus());
       loos1.setReceiveName(loos.getReceiveName());
       loos1.setReceivePhone(loos.getReceivePhone());
       loos1.setReceiveDate(loos.getReceiveDate());
       loosDao.save(loos1);
        return 1;
    }
}
