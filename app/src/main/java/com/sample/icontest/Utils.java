package com.sample.icontest;

import android.app.AppOpsManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiaoLiang
 * @date : 2020/12/2 15:59
 */
public class Utils {


    public static void main(String[] args) {
        Integer aa = new Integer(1);
        test(aa);
        System.out.print("aa======" + aa);
    }


    public static void test(Integer flag) {
        flag = 1;
        System.out.print("flag======" + flag);
    }

    public static  int TO_DYA = 0;//今天
    public  static int YESTERDAY_DYA = 1;//昨天
    public  static int THIS_WEEK = 2;//本周
    public  static int LAST_WEEK = 3;//上周
    public  static int THIS_MONTH = 4;//本月
    public  static int LAST_MONTH = 5;//上月
    public  static int LAST_MONTH_DAILY_DATA = 0;//上月
    public  static int THIS_MONTH_DAILY_DATA= 1;//本月
    static String[] DATE_MONTHLY = {"上月", "本月"};
    static String[] DATE_SELECETED = {"今天", "昨天", "本周", "上周", "本月", "上月"};
    static String[] NETWORK_TYPE = {"全部", "移动流量", "WiFi流量"};
    static List<Long> dates = new ArrayList<>();

    public static boolean isAccessGranted(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static int getUidByPackage(Context context, String pkgName) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(pkgName, 0);
            return ai.uid;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取应用列表
     * @param context
     * @return
     */
    public static ArrayList<AppInfo> getApplicationList(Context context) {
        ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); //用来存储获取的应用信息数据
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                AppInfo tmpInfo = new AppInfo();
                tmpInfo.appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                tmpInfo.packageName = packageInfo.packageName;
                tmpInfo.uid = getUidByPackage(context, packageInfo.packageName);
                tmpInfo.versionName = packageInfo.versionName;
                tmpInfo.versionCode = packageInfo.versionCode;
                tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
                //Only display the non-system app info
                appList.add(tmpInfo);//如果非系统应用，则添加至appList
            }
        }
        return appList;
    }

    /**
     * 获取某时间段的  数据使用情况
     * @param context  上下文
     * @param appList  获取集合列表
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static ArrayList<AppInfo> getApplicationTrafficStatistics(Context context, ArrayList<AppInfo> appList,long startTime, long endTime) {
        for (AppInfo appInfo : appList) {
            appInfo.usage = DataUsageTool.getUsageBytesByUid(context, startTime, endTime, appInfo.uid);
        }
        return appList;
    }

    /**
     * 获取集合时间
     * @param index
     * @return
     */
    public static List<Long> getDate(int index) {
        dates.clear();
        long time = 0;
        long start = 0, end = 0;
        switch (index) {
            case 0://今天
                start = TimeUtils.getToDay();
                end = TimeUtils.getTomorrow();
                dates.add(start);
                dates.add(end);
                break;
            case 1://昨天
                start = TimeUtils.getTheDayBefore();
                end = TimeUtils.getToDay();
                dates.add(start);
                dates.add(end);
                break;
            case 2://本周
                String date = TimeUtils.getThisWeekTime();
                String[] dateTimes = date.split(",");
                if (dateTimes.length > 1) {
                    start = Long.valueOf(dateTimes[0]);
                    end = Long.valueOf(dateTimes[1]);
                }
                dates.add(start);
                dates.add(end);
                break;
            case 3://上周
                date = TimeUtils.getLastWeekTime();
                dateTimes = date.split(",");
                if (dateTimes.length > 1) {
                    start = Long.valueOf(dateTimes[0]);
                    end = Long.valueOf(dateTimes[1]);
                }
                dates.add(start);
                dates.add(end);
                break;
            case 4://本月
                start = TimeUtils.getSupportBeginDayofMonth();
                end = TimeUtils.getSupportBeginDayofNextMonth();
                dates.add(start);
                dates.add(end);
                break;
            case 5://上月
                start = TimeUtils.getThePreviousSupportBeginDayofMonth();
                end = TimeUtils.getSupportBeginDayofMonth();
                dates.add(start);
                dates.add(end);
                break;
        }
        return dates;
    }



    /**
     * 获取某月分每一天的流量数据
     * @param context
     * @param month  0 代表上一月   1 代表本月
     * @return
     */
    public static List<MonthlyDataBean>  getMonthlyAndDailyData(Context context,int month){
        List<MonthlyDataBean> list = new ArrayList<>();
        int total = TimeUtils.getPreviousMonthTotalDay(month);
        total++;
        Log.e("total","total=========="+total);
        List<Long> dateList = new ArrayList<>();
        for(int i=0;i<total;i++){
            String date = TimeUtils.timestampConversionDate(TimeUtils.getEveryDayOfTheLastMonth(month,i)+"");
            Log.e("total","本月日期=========="+date);
            dateList.add(TimeUtils.getEveryDayOfTheLastMonth(month,i));
        }
        int size = dateList.size()-1;
        for(int i=0;i<size;i++){
            MonthlyDataBean monthlyDataBean = new MonthlyDataBean();
            DataUsageTool.Usage usage = DataUsageTool.getUsageByUidFromSummaryTotal(context,dateList.get(i),dateList.get(i+1));
//            String wifiTotalSize = StringUtil.getBytesString(usage.wifiTotalData);
//            String wifiUploadSize = StringUtil.getBytesString(usage.wifiRxBytes);//当天上传总流量
//            String wifiDownSize = StringUtil.getBytesString(usage.wifiTxBytes);//当天下载总流量
//
//            String mobileTotalSize = StringUtil.getBytesString(usage.mobleTotalData);
//            String mobileUploadSize = StringUtil.getBytesString(usage.mobleRxBytes);//当天上传总流量
//            String mobileDownSize = StringUtil.getBytesString(usage.mobleTxBytes);//当天下载总流量
//            String dates = TimeUtils.timestampConversionDate(dateList.get(i)+"");

            monthlyDataBean.time = TimeUtils.timestampConversionDate(dateList.get(i)+"");//时间日期
            monthlyDataBean.mobileTotal = StringUtil.byteToMB(usage.mobleTotalData);//总 移动数据
            monthlyDataBean.mobileDown =  StringUtil.byteToMB(usage.mobleTxBytes);// 移动 下载 数据
            monthlyDataBean.mobileUpload =  StringUtil.byteToMB(usage.mobleRxBytes);//移动 上传 数据

            monthlyDataBean.wifiTotal = StringUtil.byteToMB(usage.wifiTotalData);//总 wifi数据
            monthlyDataBean.wifiDown = StringUtil.byteToMB(usage.wifiTxBytes);//    wifi 下载数据
            monthlyDataBean.wifiUpload =  StringUtil.byteToMB(usage.wifiRxBytes);// wifi 上传数据
            list.add(monthlyDataBean);
//            Log.e("total",
//                    "\n\n时间："+dates
//                            +"\n\nWiFi 总流量："+wifiTotalSize
//                            +"\nWiFi 上传流量:"+wifiUploadSize
//                            +"\nWiFi 下载流量:"+wifiDownSize
//
//                            +"\n\n移动总流量："+mobileTotalSize
//                            +"\n移动 上传流量:"+mobileUploadSize
//                            +"\n移动 下载流量:"+mobileDownSize
//            );
        }
        return list;
    }

}
