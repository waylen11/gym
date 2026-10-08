package com.hyw.gym.dto;

import lombok.Data;

@Data
public class CoachListDto {
    private Long id;
    private String realName;
    private Integer sex;
    private Integer age;
    private Integer coachTeach;
    private java.sql.Date coachDate;
}
