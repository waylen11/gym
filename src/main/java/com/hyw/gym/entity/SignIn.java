package com.hyw.gym.entity;
// @author 黄元威

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * 会员签到记录实体（表 sign_in）
 */
@Entity
@Table(name = "sign_in")
@Getter
@Setter
public class SignIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sign_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;

    @Column(name = "sign_date")
    private Date signDate;          // 签到日期（java.sql.Date，按天算）

    @Column(name = "continuous_days")
    private Integer continuousDays; // 本次签到时连续天数

    @Column(name = "create_time")
    private Timestamp createTime;   // 签到时刻
}
