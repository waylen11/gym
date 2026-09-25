package com.hyw.gym.service;

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
     * @Description: 遗失物品管理service实现层-分页查询
     */
    public Map<String,Object> query(Map<String,Object> map1){
        //分页
        String jpal="from Loos where 1=1";
        if(map1.get("itemName")!=null && !map1.get("itemName").equals("")){
            jpal=jpal+" and itemName like '%"+map1.get("itemName")+"%'";
        }
        Query qu=entityManager.createQuery(jpal);
        //起始页书
        qu.setFirstResult((int)map1.get("qi"));
        //结束页数
        qu.setMaxResults((int)map1.get("shi"));

        //查询多少条数据
        String jpa="select count(l) from Loos l where 1=1";

        if(map1.get("itemName")!=null && !map1.get("itemName").equals("")){
            jpa=jpa+" and itemName like '%"+map1.get("itemName")+"%'";
        }
        Long count=(Long) entityManager.createQuery(jpa).getSingleResult();
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
