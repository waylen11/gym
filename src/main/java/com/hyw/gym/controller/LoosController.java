package com.hyw.gym.controller;
// @author 黄元威

import com.hyw.gym.dao.LoosDao;
import com.hyw.gym.service.LoosDaoImpl;
import com.hyw.gym.entity.Loos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @Description: 遗失物品管理Controller控制层
 */
@Controller
@RequestMapping("/loos")
public class LoosController {
   @Autowired
    private LoosDaoImpl loosDaoImpl;
   @Autowired
   private LoosDao loosDao;

    /**
     * @Description: 遗失物品管理-进入遗失物品信息界面
     */
    @RequestMapping("/jin9")
    public String jin7(){

        return "WEB-INF/jsp/loos";
    }

    /**
     * @Description: 遗失物品管理-根据遗失物品名称/状态分页查询
     */
    @RequestMapping("/query")
    @ResponseBody
    public Map<String,Object> query(String itemName, Integer status, int pageSize, int pageNumber){
        Map<String,Object>  map1=new HashMap<String,Object>();
        map1.put("itemName",itemName);
        map1.put("status", status == null ? "" : status);
        map1.put("qi",(pageNumber-1)*pageSize);
        map1.put("shi",pageSize);
        return loosDaoImpl.query(map1);
    }

    /**
     * @Description: 遗失物品管理-添加遗失物品
     */
    @RequestMapping("/add")
    @ResponseBody
    public  void save(Loos loos){
        loosDao.save(loos);
    }

    /**
     * @Description: 遗失物品管理-根据遗失物品id查询信息
     */
    @RequestMapping("/query3")
    @ResponseBody
    public List<Loos> query3(long loosId){
        List<Loos> list  = new ArrayList<Loos>();
        list.add(loosDao.findById(loosId).get());
        return list;
    }

    /**
     * @Description: 遗失物品管理-取回遗失物品修改其信息
     */
    @RequestMapping("/quhui")
    @ResponseBody
    public  void upd(Loos loos){
        loosDaoImpl.update(loos);
    }

    /**
     * @Description: 遗失物品管理-根据遗失物品id查询单个物品信息
     */
    @RequestMapping("/cha")
    @ResponseBody
    public Optional<Loos> one(Long loosId){
        Optional<Loos> loos =loosDao.findById(loosId);
        return loos;
    }

}
