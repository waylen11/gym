package com.hyw.gym.dto;

import lombok.Data;

@Data
public class SignStatusDTO {
    private Boolean signed;          // 今日是否已签到
    private Integer continuousDays;  // 当前连续签到天数
    private Integer monthCount;      // 本月累计签到次数
    private String message;          // 提示文案
}
