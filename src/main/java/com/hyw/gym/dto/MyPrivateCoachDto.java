package com.hyw.gym.dto;
// @author 黄元威

import lombok.Data;
@Data
public class MyPrivateCoachDto {

        private Long recordId;
        private SubjectItem subject;    // 课程
        private CoachItem coach;        // 教练
        private Integer count;          // 剩余次数
        private Double realprice;       // 实付金额
        private String courseDate;      // 日期（yyyy-MM-dd）
        private Integer status;
        private String remark;          // 打卡记录

        @Data
        public static class SubjectItem {
            private Long id;
            private String courseName;
            private Double sellingPrice;
        }

        @Data
        public static class CoachItem {
            private Long id;
            private String realName;
        }
    }
