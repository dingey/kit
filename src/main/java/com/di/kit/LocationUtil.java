package com.di.kit;

import java.text.DecimalFormat;

/**
 * 根据经纬度计算距离
 */
public class LocationUtil {
    // 地球平均半径
    private static final double EARTH_RADIUS = 6378137;

    // 把经纬度转为度（°）
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位：千米
     *
     * @param lng1 经度
     * @param lat1 纬度
     * @param lng2 经度
     * @param lat2 纬度
     * @return
     * @author ershuai
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(
                Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = s / 1000;
        DecimalFormat df = new DecimalFormat("#.00");
        s = Double.parseDouble(df.format(s));
        return s;
    }

    public static String getDistance(String lng1, String lat1, String lng2, String lat2) {
        return String.valueOf(
                getDistance(Double.valueOf(lng1), Double.valueOf(lat1), Double.valueOf(lng2), Double.valueOf(lat2)));
    }

    public static void main(String[] args) {
        double distance1 = getDistance(120.26763, 30.17024, 120.26457, 30.18534);
        System.out.println("Distance is: " + distance1 + " km");
    }
}
