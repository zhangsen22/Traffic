package com.sample.icontest;

import android.graphics.drawable.Drawable;

public class MonthlyDataBean {
    public String time;
    public String mobileTotal;
    public String wifiTotal;
    public String wifiUpload;
    public String wifiDown;
    public String mobileUpload;
    public String mobileDown;

    @Override
    public String toString() {
        return "MonthlyDataBean{" +
                "time='" + time + '\'' +
                ", mobileTotal='" + mobileTotal + '\'' +
                ", wifiTotal='" + wifiTotal + '\'' +
                ", wifiUpload='" + wifiUpload + '\'' +
                ", wifiDown='" + wifiDown + '\'' +
                ", mobileUpload='" + mobileUpload + '\'' +
                ", mobileDown='" + mobileDown + '\'' +
                '}';
    }
}