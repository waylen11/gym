package com.hyw.gym.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @Description: 用户信息实体类
 */
@Entity
@Table(name = "sys_user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String loginName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Integer userType;
    private String realName;
    private String phone;
    private Integer sex;
    private Integer age;
    private java.sql.Date coachDate;
    private Integer coachTeach;
    private Double coachWages;
private Integer coachStatic;
private String coachAddress;
@ManyToOne
@JoinColumn(name = "member_type_id")
private Membertype memberType;
private String memberBirthday;
private java.sql.Date memberDate;
private Integer status;
private Integer remainingDays;
private java.sql.Date memberXufei;
@Column(name = "create_time")
private java.sql.Timestamp createTime;
}
