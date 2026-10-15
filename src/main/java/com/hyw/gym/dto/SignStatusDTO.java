package com.hyw.gym.dto;
// @author 黄元威

import lombok.Data;

@Data
public class SignStatusDTO {
    private Boolean signed;          // 今日是否已签到
    private Integer continuousDays;  // 当前连续签到天数
    private Integer monthCount;      // 本月累计签到次数
    private Integer points;          // 当前积分余额
    private Integer nextRewardRemain;// 再签几天拿周奖励（满7天额外+50）
    private String message;          // 提示文案
}
