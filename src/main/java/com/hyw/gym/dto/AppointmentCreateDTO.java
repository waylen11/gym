package com.hyw.gym.dto;

import lombok.Data;

/**
 * 创建预约请求体
 */
@Data
public class AppointmentCreateDTO {
    private Long recordId;          // 私教购买记录 ID
    private String appointmentDate; // 预约日期 yyyy-MM-dd
    private String remark;          // 备注(可选)
}
