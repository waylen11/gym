package com.hyw.gym.config;

/**
 * 健身房位置配置（签到地理围栏）
 * ⚠️ GYM_LAT / GYM_LNG 换成你健身房的实际坐标（高德/腾讯地图搜健身房可看经纬度）
 */
public class GymLocation {

    // 健身房纬度
    public static final double GYM_LAT = 23.1291;
    // 健身房经度
    public static final double GYM_LNG = 113.2644;
    // 允许签到的最远距离（米）——GPS 有漂移，500 米较宽容
    public static final double MAX_DISTANCE_METERS = 500;
}
