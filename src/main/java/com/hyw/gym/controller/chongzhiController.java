package com.hyw.gym.controller;


import com.hyw.gym.dao.UserDao;
import com.hyw.gym.dao.chongzhiDao;
import com.hyw.gym.entity.Chongzhi;
import com.hyw.gym.service.MemberDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 消费记录管理Controller控制层(续卡+商品购买+私教记录)
 */
@Controller
@RequestMapping("/cz")
public class chongzhiController {

    @Autowired
    private chongzhiDao chongzhiDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MemberDaoImpl memberDaoImpl;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @Description: 消费记录管理-进入消费记录界面
     */
    @RequestMapping("/jin")
    public String jin(){
        return "WEB-INF/jsp/HYMemberjilu";
    }

    /**
     * @Description: 续费续卡记录-根据所选日期分页查询数据记录
     */
    @RequestMapping("/query")
    @ResponseBody
    public Map<String,Object> query(int pageSize,int pageNumber, String xdate, String ddate) throws ParseException{
        Map<String,Object> map = new HashMap();
        if(xdate.equals("")){
            xdate = "2022-01-01 00:00:00";
        } else {
            xdate = xdate + " 00:00:00";
        }
        if(ddate.equals("")){
            ddate = "2100-01-01 23:59:59";
        } else {
            ddate = ddate + " 23:59:59";
        }
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	final Date startTime = sdf.parse(xdate);
        final Date endTime = sdf.parse(ddate);
        Pageable pageable=new PageRequest(pageNumber-1,pageSize);
        Specification<Chongzhi> specification=new Specification<Chongzhi>() {
            @Override
            public Predicate toPredicate(Root<Chongzhi> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder)  {
                List<Predicate> list=new ArrayList<Predicate>();
                list.add(criteriaBuilder.between(root.<Date> get("rechargeTime"),startTime,endTime));

                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        Page<Chongzhi> list1 = chongzhiDao.findAll(specification,pageable);
        List<Chongzhi> li = list1.getContent();
        map.put("total",list1.getTotalElements());
        map.put("rows",li);
        return map;
   }

    /**
     * @Description: 信息统计-进入收入统计界面
     */
    @RequestMapping("/jin1")
    public String jin1(){
        return "WEB-INF/jsp/ShouRuTongJi";
    }

    /**
     * @Description: 信息统计-收入统计(动态获取当前年份,统计当年12个月收入)
     */
    @RequestMapping("/tongji")
    @ResponseBody
    public long[] Tongji(){
        // 动态获取当前年份,统计当年12个月的收入（会员卡购买/续费 + 私教课购买）
        int year = Calendar.getInstance().get(Calendar.YEAR);
        long[] inter = new long[12];
        for (int i = 0; i < 12; i++){
            String month = String.format("%d-%02d", year, i + 1);
            // 收入1：会员卡购买/续费金额（recharge_record）
            String jpa="select sum(a.money) from Chongzhi as a where rechargeTime like('%"+month+"%')";
            Query query=entityManager.createQuery(jpa);
            Object obj = query.getSingleResult();
            long cardMoney = (obj == null) ? 0 : (Long) obj;
            // 收入2：私教课购买实付金额（private_coach_record.realprice）
            String jpa2="select sum(b.realprice) from PrivateCoachInfo as b where courseDate like('%"+month+"%')";
            Query query2=entityManager.createQuery(jpa2);
            Object obj2 = query2.getSingleResult();
            long privateMoney = (obj2 == null) ? 0 : ((Number) obj2).longValue();
            inter[i] = cardMoney + privateMoney;
        }
        return inter;
   }
}
