package com.hyw.gym.dto;

import lombok.Data;

@Data
public class SignResultDTO {
    private Boolean signed;         // 是否签到成功
    private Integer continuousDays; // 连续签到天数
    private String message;         // 提示文案（前端展示）
}
