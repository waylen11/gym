package com.hyw.gym.dto;
// @author 黄元威

import lombok.Data;

import java.util.Date;

@Data
public class MemberInfoDto {
    private Long id;
    private String loginName;
    private String realName;
    private String phone;
    private Integer sex;
    private String gender;
    private Integer age;
    private String memberBirthday;
    private Integer status;
    private Integer remainingDays;
    private Date memberXufei;
    private Date memberDate;

}
