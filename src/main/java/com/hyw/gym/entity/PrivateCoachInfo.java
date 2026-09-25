package com.hyw.gym.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @Description: 私教信息实体类
 */
@Entity
@Table(name = "private_coach_record")
@Getter
@Setter
public class PrivateCoachInfo implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private long id;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Subject subject;

    @OneToOne
    @JoinColumn(name = "coach_id")
    private User coach;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;
    @Column(name = "count")
    private int count;
    private double countprice;
    private double realprice;
    @Column(name = "course_time")
    private java.sql.Date courseDate;
    private int status;
    private String remark;
    private String admin;
}
