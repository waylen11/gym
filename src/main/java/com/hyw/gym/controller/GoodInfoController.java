package com.hyw.gym.controller;
// @author 黄元威

import com.hyw.gym.common.Result;
import com.hyw.gym.dao.GoodInfoDao;
import com.hyw.gym.dao.GoodsDao;
import com.hyw.gym.dao.UserDao;
import com.hyw.gym.entity.GoodInfo;
import com.hyw.gym.entity.Goods;
import com.hyw.gym.entity.User;
import com.hyw.gym.service.GoodInfoDaoImpl;
import com.hyw.gym.service.GoodsDaoImpl;
import com.hyw.gym.service.MemberDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Description: 商品售卖信息管理Controller控制层
 */
@Controller
@RequestMapping("/goodinfo")
public class GoodInfoController {
   @Autowired
    private GoodInfoDao goodInfoDao;
   @Autowired
   private GoodsDao goodsDao;
   @Autowired
   private GoodInfoDaoImpl goodInfoDaoImpl;
    @Autowired
    private UserDao userDao;
    @Autowired
    private GoodsDaoImpl goodsDaoImpl;
    @Autowired
    private MemberDaoImpl memberDaoImpl;

    /**
     * @Description: 商品售卖信息管理-进入商品售卖信息界面
     */
    @RequestMapping("/spinfo")
    public String spinfo(){

        return "WEB-INF/jsp/GoodInfo";
    }

    /**
     * @Description: 商品售卖信息管理-根据商品id,会员id查询商品售卖信息
     */
    @RequestMapping("/query")
    @ResponseBody
    public Map<String,Object> query(Integer goodsid,Integer memberid, int pageSize, int pageNumber){
        Map<String,Object>  map1=new HashMap<String,Object>();
        map1.put("goodsid",goodsid);
        map1.put("memberid",memberid);
        map1.put("qi",(pageNumber-1)*pageSize);
        map1.put("shi",pageSize);
        return goodInfoDaoImpl.query(map1);
    }

    /**
     * @Description: 商品售卖信息管理-根据商品id,会员id删除信息
     */
    @RequestMapping("/del")
    @ResponseBody
    public  Map<String,Object> del(long id,Integer goodsid,Integer memberid,int pageSize, int pageNumber){
         goodInfoDao.deleteById(id);
         Map<String,Object>  map1=new HashMap<String,Object>();
         map1.put("goodsid",goodsid);
         map1.put("memberid",memberid);
         map1.put("qi",(pageNumber-1)*pageSize);
         map1.put("shi",pageSize);
         return goodInfoDaoImpl.query(map1);
    }

    /**
     * @Description: 商品售卖信息管理-根据商品id,会员id批量删除信息
     */
    @RequestMapping("/dellist")
    @ResponseBody
    public Map<String,Object> delete(int array[],Integer goodsid,Integer memberid,int pageSize, int pageNumber){
        System.out.println(array[0]);
        for(int i = 0;i<array.length;i++){
            goodInfoDao.deleteById((long) array[i]);
        }
        return query(goodsid,memberid,pageSize,pageNumber);
    }

    /**
     * @Description: 商品售卖信息管理-添加商品售卖信息
     */
    @RequestMapping("/add")
    @ResponseBody
    @Transactional
    public Result<Void> save(GoodInfo goodInfo){
        // 参数校验：商品和会员必须存在
        if (goodInfo.getGoods() == null || goodInfo.getMember() == null
                || goodInfo.getGoods().getId() <= 0 || goodInfo.getMember().getId() <= 0) {
            return Result.error(400, "请选择商品和会员");
        }
        if (goodInfo.getQuantity() <= 0) {
            return Result.error(400, "数量必须大于0");
        }
        Optional<Goods> goodsOpt = goodsDao.findById(goodInfo.getGoods().getId());
        if (!goodsOpt.isPresent()) {
            return Result.error(400, "商品不存在");
        }
        Goods goods = goodsOpt.get();
        // 库存校验：防止超卖
        if (goods.getStock() == null || goods.getStock() < goodInfo.getQuantity()) {
            return Result.error(400, "商品库存不足");
        }
        // 金额由后端按售价计算，不信任前端传入金额（防篡改）
        goodInfo.setAmount(goods.getSellPrice() * goodInfo.getQuantity());
        goodInfo.setGoods(goods);
        // 扣库存 + 插入销售记录（同一事务，保证一致性）
        goods.setStock(goods.getStock() - goodInfo.getQuantity());
        goodsDao.save(goods);
        goodInfoDao.save(goodInfo);
        return Result.success(null);
    }

    /**
     * @Description: 商品售卖信息管理-根据商品id查询商品售卖信息
     */
    @RequestMapping("/cha")
    @ResponseBody
    public Optional<GoodInfo> one(long goodsId){
        return goodInfoDao.findById(goodsId);
    }

    /**
     * @Description: 商品售卖信息管理-根据商品id查询商品信息
     */
    @RequestMapping("/chagoods")
    @ResponseBody
    public Optional<Goods> oneg(long goodsId){
        return goodsDao.findById(goodsId);
    }

    /**
     * @Description: 商品售卖信息管理-根据会员id查询会员信息
     */
    @RequestMapping("/chamember")
    @ResponseBody
    public Optional<User> onem(long memberId){
        return userDao.findById(memberId);
    }

    /**
     * @Description: 商品售卖信息管理-修改商品售卖信息
     */
    @RequestMapping("/upd")
    @ResponseBody
    public  void upd(GoodInfo goodInfo){
        goodInfoDao.save(goodInfo);
    }

    /**
     * @Description: 商品售卖信息管理-修改会员信息
     */
    @RequestMapping("/updmember")
    @ResponseBody
    public void upd(User member){
        memberDaoImpl.upd(member);
    }

    /**
     * @Description: 商品售卖信息管理-修改商品信息
     */
    @RequestMapping("/updgoods")
    @ResponseBody
    public  void upd(Goods goods){
        goodsDaoImpl.update(goods);
    }

}
