package com.hyw.gym.dto;

import lombok.Data;

/**
 * 失物招领 DTO（会员端）：只暴露公开信息，不携带认领人联系方式（隐私保护）
 */
@Data
public class LostPropertyDTO {
    private Long lostId;          // 失物编号
    private String itemName;      // 物品名称
    private String image;         // 物品图片
    private String address;       // 拾取地点
    private String foundDate;     // 拾取时间 yyyy-MM-dd
    private Integer status;       // 0 未认领 / 1 已认领
    private String statusText;    // 状态中文
    private String description;   // 物品描述
}
