package com.hyw.gym.dto;

import lombok.Data;

import java.util.List;

@Data
public class CoachDetailDto {
    private Long id;
    private String realName;
    private Integer sex;
    private Integer age;
    private Integer coachTeach;
    private java.sql.Date coachDate;
    private Integer coachStatic;
    private Integer memberCount;          // 该教练名下私教会员数（按会员去重）
    private List<CourseItem> courses;     // 该教练教过的课程（按课程去重）

    @Data
    public static class CourseItem {
        private Long courseId;
        private String courseName;
        private Double sellingPrice;
        private Integer duration;
        private String description;
    }
}
