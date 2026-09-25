package com.hyw.gym.service;

import com.hyw.gym.enums.UserType;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 教练管理service实现层
 */
@Service
public class CoachDaoImpl {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @Description: 教练管理service实现层-分页查询
     */
    public Map<String,Object> query(Map<String,Object> map1){
        //分页
        String jpal="from User where userType = " + UserType.COACH.getType();
        if(map1.get("coachname")!=null && !map1.get("coachname").equals("")){
            jpal=jpal+" and realName like '%"+map1.get("coachname")+"%'";
        }
        Query qu=entityManager.createQuery(jpal);
        //起始页书
        qu.setFirstResult((int)map1.get("qi"));
        //结束页数
        qu.setMaxResults((int)map1.get("shi"));

        //查询多少条数据
        String jpa="select count(c) from User c where userType = " + UserType.COACH.getType();

        if(map1.get("coachname")!=null && !map1.get("coachname").equals("")){
            jpa=jpa+" and realName like '%"+map1.get("coachname")+"%'";
        }

        Long count=(Long) entityManager.createQuery(jpa).getSingleResult();
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("total",count);
        map.put("rows",qu.getResultList());
        return map;
    }

    /**
     * @Description: 教练管理service实现层-查询总数据
     */
    public Long count(String  coachName){
        String jpa="select count(c) from User c where realName ='"+coachName+"'";
        Query query=entityManager.createQuery(jpa);
        System.out.println(query);
        return (Long)query.getSingleResult();
    }
}
