package com.hyw.gym.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @Description: 系统公告实体类
 */
@Entity
@Table(name = "system_notice")
@Getter
@Setter
public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private User publisher;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "publish_time")
    private String publishTime;

    private String title;


}
