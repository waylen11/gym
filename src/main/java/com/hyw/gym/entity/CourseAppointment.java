package com.hyw.gym.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @Description: 课程预约实体类 - 单次课程的预约/打卡记录
 */
@Entity
@Table(name = "course_appointment")
@Getter
@Setter
public class CourseAppointment implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "record_id")
    private PrivateCoachInfo record;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private User coach;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;

    @Column(name = "appointment_date")
    private java.sql.Date appointmentDate;

    /**
     * 状态 0已预约 1已打卡 2已取消
     */
    private int status;

    @Column(name = "checkin_time")
    private Timestamp checkinTime;

    private String remark;

    @Column(name = "create_time", updatable = false)
    private Timestamp createTime;
}
