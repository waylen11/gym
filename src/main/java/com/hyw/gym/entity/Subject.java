package com.hyw.gym.entity;
// @author 黄元威

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @Description: 课程信息实体类
 */
@Entity
@Table(name = "course")
@Getter
@Setter
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private long id;
    private String courseName;
    private double sellingPrice;
    private String description;
    private Integer duration;
}
