package com.hyw.gym.service;
// @author 黄元威


import com.hyw.gym.entity.Messages;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * @Description: 系统公告service实现层
 */
@Service
public class MessageServiceImp {

    @PersistenceContext
    private EntityManager entityManager;

    public String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdf.format(now);
    }

    public List<Messages> query(int page, int size) {
        String jpal = "from Messages Order by publishTime desc";
        Query qu=entityManager.createQuery(jpal);
        //起始页书
        qu.setFirstResult(page);
        //结束页数
        qu.setMaxResults(size);

        return qu.getResultList();
    }
}
