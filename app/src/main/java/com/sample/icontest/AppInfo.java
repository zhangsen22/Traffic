package com.sample.icontest;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public String appName="";
    public String packageName="";
    public String versionName="";
    public int versionCode=0;
    public int uid=0;
    public Drawable appIcon=null;
    public DataUsageTool.Usage usage;



    @Override
    public String toString() {
        return "AppInfo{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", uid=" + uid +
                ", appIcon=" + appIcon +
                ", usage=" + usage +
                '}';
    }
}