package com.hyw.gym.service;
// @author 黄元威

import com.hyw.gym.dao.GoodInfoDao;
import com.hyw.gym.dao.PrivateCoachInfoDao;
import com.hyw.gym.dao.UserDao;
import com.hyw.gym.dao.ChongzhiDao;
import com.hyw.gym.entity.Chongzhi;
import com.hyw.gym.entity.GoodInfo;
import com.hyw.gym.entity.PrivateCoachInfo;
import com.hyw.gym.entity.User;
import com.hyw.gym.dao.*;
import com.hyw.gym.entity.*;
import com.hyw.gym.enums.UserType;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 会员管理service实现层
 */
@Service
public class MemberDaoImpl {


    @Autowired
    private UserDao userDao;

    @Autowired
    private PrivateCoachInfoDao privateCoachInfoDao;

    @Autowired
    private GoodInfoDao goodInfoDao;

    @Autowired
    private ChongzhiDao chongZhiDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${custom.default.password}")
    private String defaultPassword;

    /**
     * @Description: 会员管理service实现层-分页查询
     */
    public Map<String,Object> query(Map<String,Object> map1){
        //根据会员到期的日期修改到期后状态
        List<User> memberslist = userDao.findByUserType(UserType.MEMBER.getType());
        //得到现在的时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        try {
            Date now = sdf.parse(date);
            for (User member:memberslist){
                //得到会员到期时间(先判空,避免 getMemberXufei() 为 null 时调用 toString 抛 NPE)
                java.sql.Date memberXufei = member.getMemberXufei();
                String date1 = (memberXufei != null) ? memberXufei.toString() : date;
                Date daoqi = sdf.parse(date1);
                if(daoqi.before(now)){
                    //设置会员状态为不正常
                    member.setStatus(2);
                }else {
                    //设置会员状态为正常
                    member.setStatus(1);
                }
                userDao.save(member);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //分页
        String jpal="from User where userType = " + UserType.MEMBER.getType();
        if(map1.get("hyname")!=null && !map1.get("hyname").equals("")){
            jpal=jpal+" and realName like '%"+map1.get("hyname")+"%'";
        }
        long ktype=(long)map1.get("ktype");
        if(ktype!=0){
            jpal=jpal+" and memberType.id="+ktype;
        }
        Query qu=entityManager.createQuery(jpal);
        //起始页书
        qu.setFirstResult((int)map1.get("qi"));
        //结束页数
        qu.setMaxResults((int)map1.get("shi"));

        //查询多少条数据
        String jpa="select count(m) from User m where userType = " + UserType.MEMBER.getType();

        if(map1.get("hyname")!=null && !map1.get("hyname").equals("")){
            jpa=jpa+" and realName like '%"+map1.get("hyname")+"%'";
        }
        if(ktype!=0){
            jpa=jpa+" and memberType.id="+ktype;
        }
        Long count=(Long) entityManager.createQuery(jpa).getSingleResult();
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("total",count);
        map.put("rows",qu.getResultList());
        return map;
    }

    /**
     * @Description: 会员管理service实现层-会员到期分页查询
     */
    //会员到期查询
    public Map<String,Object> query2(Map<String,Object> map1){
        //分页
        String jpal="from User where userType = " + UserType.MEMBER.getType();
        if(map1.get("hyname")!=null && !map1.get("hyname").equals("")){
            jpal=jpal+" and realName like '%"+map1.get("hyname")+"%'";
        }
        long ktype=(long)map1.get("ktype");
        if(ktype!=0){
            jpal=jpal+" and memberType.id="+ktype;
        }
        jpal+=" and status = 2";
        Query qu=entityManager.createQuery(jpal);
        //起始页书
        qu.setFirstResult((int)map1.get("qi"));
        //结束页数
        qu.setMaxResults((int)map1.get("shi"));

        //查询多少条数据
        String jpa="select count(m) from User m where userType = " + UserType.MEMBER.getType();

        if(map1.get("hyname")!=null && !map1.get("hyname").equals("")){
            jpa=jpa+" and realName like '%"+map1.get("hyname")+"%'";
        }
        if(ktype!=0){
            jpa=jpa+" and memberType.id="+ktype;
        }
        jpa+=" and status = 2";
        Long count=(Long) entityManager.createQuery(jpa).getSingleResult();
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("total",count);
        map.put("rows",qu.getResultList());
        return map;
    }

    /**
     * @Description: 会员管理service实现层-根据会员id删除
     */
    public int del(long id){
        //先根据会员id在私教信息表里查询是否有其信息
        List<PrivateCoachInfo> privateCoachInfoList = privateCoachInfoDao.queryByIdNative(id);
        if(privateCoachInfoList !=null && privateCoachInfoList.size() > 0){
            //如果有,先循环删除
            for(PrivateCoachInfo privateCoachInfo : privateCoachInfoList){
                if(id == privateCoachInfo.getMember().getId()){
                    privateCoachInfoDao.delete(privateCoachInfo);
                }
            }
        }
        //再根据会员id在商品信息表中查是否有其信息
        List<GoodInfo> goodInfoList = goodInfoDao.queryByIdNative(id);
        if(goodInfoList != null && goodInfoList.size()>0){
            for(GoodInfo goodInfo : goodInfoList){
                if(id == goodInfo.getMember().getId()){
                    goodInfoDao.delete(goodInfo);
                }
            }
        }
        //根据会员id在充值信息表中查询是否有其信息
        List<Chongzhi> chongzhiList = chongZhiDao.queryByIdNative(id);
        if(chongzhiList != null && chongzhiList.size()>0){
            for(Chongzhi chongzhi : chongzhiList){
                if(id == chongzhi.getMember().getId()){
                    chongZhiDao.delete(chongzhi);
                }
            }
        }
        //最后删除此会员
        userDao.deleteById(id);
        return 1;
    }


    /**
     * @Description: 会员管理service实现层-添加会员
     */
    public int insert(User member){
        member.setPassword(DigestUtils.md5Hex(defaultPassword));
        member.setUserType(UserType.MEMBER.getType());
        member.setStatus(1);
        member.setRemainingDays(0);
        userDao.save(member);
        return  1;
    }

    /**
     * @Description: 会员管理service实现层-修改会员信息
     */
    public void update(User member){
        Optional<User> user = userDao.findById(member.getId());
        if(user.isPresent() && UserType.MEMBER.getType().equals(user.get().getUserType())){
            User one = user.get();
            member.setLoginName(one.getLoginName());
            member.setPassword(one.getPassword());
            member.setUserType(one.getUserType());
            userDao.save(member);
        }
    }

    /**
     * @Description: 会员管理service实现层-根据id查询会员信息
     */
    public User cha(long id){
        return userDao.findById(id).get();
    }

    /**
     * @Description: 会员管理service实现层-修改会员信息(仅用于同步剩余天数等字段)
     */
    public void upd(User member){
        User one = userDao.findById(member.getId()).get();
        if(member.getRemainingDays() != null){
            one.setRemainingDays(member.getRemainingDays());
        }
        if(member.getMemberXufei() != null){
            one.setMemberXufei(member.getMemberXufei());
        }
        if(member.getMemberType() != null){
            one.setMemberType(member.getMemberType());
        }
        if(member.getStatus() != null){
            one.setStatus(member.getStatus());
        }
        userDao.save(one);
    }
}
