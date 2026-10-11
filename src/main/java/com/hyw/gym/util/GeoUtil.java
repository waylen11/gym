package com.hyw.gym.util;

/**
 * 地理距离工具：Haversine 公式计算两点球面距离
 */
public class GeoUtil {

    // 地球平均半径（公里）——地球非正球，取平均半径，误差几十米内
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * 计算两点球面距离
     *
     * @param lat1 点1纬度
     * @param lng1 点1经度
     * @param lat2 点2纬度
     * @param lng2 点2经度
     * @return 距离（米）
     */
    public static double distanceMeters(double lat1, double lng1,
                                        double lat2, double lng2) {
        // 经纬度（度）→ 弧度：Java 的 sin/cos 只认弧度
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);   // 纬度差 Δφ
        double deltaLng = Math.toRadians(lng2 - lng1);   // 经度差 Δλ

        // Haversine 核心：中间量 a
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                 + Math.cos(radLat1) * Math.cos(radLat2)
                 * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);

        // 圆心角 c（弧度）
        double c = 2 * Math.asin(Math.sqrt(a));

        // 弧长 = 半径 × 圆心角；公里 × 1000 → 米
        return EARTH_RADIUS_KM * c * 1000;
    }
}
