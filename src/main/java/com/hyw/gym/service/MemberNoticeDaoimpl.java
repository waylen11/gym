package com.hyw.gym.service;

import com.hyw.gym.common.Result;
import com.hyw.gym.dao.MessageDao;
import com.hyw.gym.dto.MessageDTO;
import com.hyw.gym.entity.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberNoticeDaoimpl {
    @Autowired
    private MessageDao messageDao;

    //查看所有公告
    public Result<List<MessageDTO>> showNotices() {
         List<Messages> messagesList=messageDao.findAll();
         List<MessageDTO>messageDTOS=new ArrayList<>();
         for (Messages m:messagesList){
             MessageDTO messageDTO = new MessageDTO();
             messageDTO.setId(m.getId());
             messageDTO.setTitle(m.getTitle());
             messageDTO.setContent(m.getContent());
             messageDTO.setPublishTime(m.getPublishTime());

             MessageDTO.Publisher publisher = new MessageDTO.Publisher();
             publisher.setId(m.getPublisher().getId());
             publisher.setRealName(m.getPublisher().getRealName());

             messageDTO.setPublisher(publisher);
             messageDTOS.add(messageDTO);
         }
         return Result.success(messageDTOS);
    }
}
