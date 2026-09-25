package com.hyw.gym.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @Description: 丢失物品信息实体类
 */
@Entity
@Table(name = "lost_property")
@Getter
@Setter
public class Loos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lost_id")
    private long id;
    private String itemName;
    private String loosImages;
    private String loosAddress;
    @Column(name = "found_time")
    private java.sql.Date foundDate;
    private Integer status;
    private String scavenger;
    private String scavengerPhone;
    private String receiveName;
    private String receivePhone;
    private java.sql.Date receiveDate;
    private String admin;
    private String description;
}
