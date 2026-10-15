package com.hyw.gym.dto;
// @author 黄元威

import lombok.Data;

@Data
public class ExchangeRecordDTO {
    private Long exchangeId;    // 兑换记录ID
    private String goodsName;   // 商品名称
    private Integer pointsCost; // 消耗积分
    private String createTime;  // 兑换时间
}
