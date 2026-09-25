package com.hyw.gym.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @Description: 充值信息实体类
 */
@Getter
@Setter
@Table(name = "recharge_record")
@Entity
public class Chongzhi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recharge_id")
    private long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private User member;

    @OneToOne
    @JoinColumn(name = "type_id")
    private Membertype membertype;
    private long money;
    private long ssmoney;
    private long zlmoney;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "recharge_time")
    private java.sql.Timestamp rechargeTime;
    private long amount;
    private String remark;
    private long czStatic;
}
