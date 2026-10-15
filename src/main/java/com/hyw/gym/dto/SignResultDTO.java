package com.hyw.gym.dto;
// @author 黄元威

import lombok.Data;

@Data
public class SignResultDTO {
    private Boolean signed;         // 是否签到成功
    private Integer continuousDays; // 连续签到天数
    private Integer earnPoints;     // 本次获得积分
    private Integer totalPoints;    // 当前积分余额
    private String message;         // 提示文案（前端展示）
}
