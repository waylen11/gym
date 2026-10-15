package com.hyw.gym.dto;
// @author 黄元威

import com.hyw.gym.entity.User;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
public class MessageDTO {

    private long id;
    private Publisher publisher;
    private String content;
    private String publishTime;
    private String title;

    @Data
    public static class Publisher {
        private Long id;
        private String realName;
    }
}
