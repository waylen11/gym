package com.hyw.gym.controller;
// @author 黄元威

import com.hyw.gym.dao.MessageDao;
import com.hyw.gym.entity.Messages;
import com.hyw.gym.service.MessageServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Description: 管理员Controller控制层
 */
@Controller
@RequestMapping("/message")
public class MessageController {
    //每页数量
    private final int PAGENUM = 5;
    private final float FPAGENUM = 5.0f;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private MessageServiceImp messageServiceImp;

    @RequestMapping("/jin")
    public String jin(Model model){
        model.addAttribute("pageCount", (int)( Math.ceil(messageDao.count() / FPAGENUM) ));
        return "WEB-INF/jsp/message";
    }

    @RequestMapping("/cha")
    @ResponseBody
    public Optional<Messages> one(long id){
        return messageDao.findById(id);
    }

    @RequestMapping("/queryByUserid")
    @ResponseBody
    public List<Messages> queryByUserid(long userId){
        return messageDao.findMessagesByUserId(userId);
    }

    @RequestMapping("/query/{page}")
    @ResponseBody
    public List<Messages> query(@PathVariable int page) {
        return messageServiceImp.query((page-1)*PAGENUM,PAGENUM);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public void del(long id){
        messageDao.deleteById(id);
    }

    @RequestMapping("/insert")
    @ResponseBody
    public void insert(Messages message){
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        message.setPublishTime(sdf.format(now));
        messageDao.save(message);
    }
}
