package com.hyw.gym.common;

import com.hyw.gym.entity.User;

public class UserHolder {
    private static final ThreadLocal<User> TL = new ThreadLocal<>();

    public static void save(User user) { TL.set(user); }
    public static User get() { return TL.get(); }
    public static void remove() { TL.remove(); }
}