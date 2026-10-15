package com.hyw.gym.dto;
// @author 黄元威

import lombok.Data;

/**
 * 支付订单 DTO（创建订单 / 订单列表共用）
 */
@Data
public class OrderDTO {
    private Long orderId;        // 订单 id
    private String orderNo;      // 订单号
    private Double amount;       // 订单金额
    private Integer status;      // 0 待支付 / 1 已支付 / 2 已取消
    private String statusText;   // 状态中文文案
    private String typeName;     // 卡类型名称
    private String createTime;   // 下单时间 yyyy-MM-dd HH:mm:ss
    private String payTime;      // 支付时间 yyyy-MM-dd HH:mm:ss
}
