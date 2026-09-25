package com.hyw.gym.enums;

import lombok.Getter;

@Getter
public enum UserType {
    ADMIN(0,"管理员"),
    COACH(1,"教练"),
    MEMBER(2,"会员");


    private final Integer type;

    private final String name;

    UserType(int type, String name) {
        this.type = type;
        this.name = name;
    }
}
