package com.hyw.gym.dto;
// @author 黄元威

import lombok.Data;

/**
 * 我的预约列表 DTO
 */
@Data
public class AppointmentListDTO {
    private Long appointmentId;
    private Long recordId;              // 私教购买记录 ID
    private SubjectItem subject;        // 课程
    private CoachItem coach;            // 教练
    private String appointmentDate;     // 预约日期 yyyy-MM-dd
    private Integer status;             // 0已预约 1已打卡 2已取消
    private String statusText;          // 状态中文文案
    private String checkinTime;         // 打卡时间 yyyy-MM-dd HH:mm:ss
    private String remark;
    private String createTime;          // 创建时间 yyyy-MM-dd HH:mm:ss

    @Data
    public static class SubjectItem {
        private Long id;
        private String courseName;
    }

    @Data
    public static class CoachItem {
        private Long id;
        private String realName;
    }
}
