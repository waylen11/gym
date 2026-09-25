package com.hyw.gym.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 会员卡类型信息实体类
 */
@Entity
@Table(name = "member_type")
@Getter
@Setter
public class Membertype {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "type_id")
private long id;
private String typeName;
private long typeciShu;
private long validDays;
private float price;
private String description;

@OneToMany(mappedBy = "memberType")
@JsonIgnore
private List<User> list = new ArrayList<User>();
}
