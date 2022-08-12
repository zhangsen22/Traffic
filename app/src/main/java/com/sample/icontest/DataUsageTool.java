package com.sample.icontest;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.NETWORK_STATS_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

/**
 * @author LiaoLiang
 * @date : 2020/12/2 17:06
 * Android 6.0+   API>=23
 */
public class DataUsageTool {

    private static Usage usage;

    public static class Usage {
        long wifiTotalData;//wifi总流量数据
        long mobleTotalData;//移动总流量数据
        long totalRxBytes;//总数据 下载字节
        long totalTxBytes;//总数据 上传字节
        long mobleRxBytes;//移动 下载字节
        long mobleTxBytes;//移动 上传字节
        long wifiRxBytes;//wifi 下载字节
        long wifiTxBytes;//wifi 上传字节
        String uid;//包名

        @Override
        public String toString() {
            return "Usage{" +
                    "totalRxBytes=" + totalRxBytes +
                    ", totalTxBytes=" + totalTxBytes +
                    ", mobleRxBytes=" + mobleRxBytes +
                    ", mobleTxBytes=" + mobleTxBytes +
                    ", wifiRxBytes=" + wifiRxBytes +
                    ", wifiTxBytes=" + wifiTxBytes +
                    '}';
        }
    }


    /**
     * 该方法刷新较慢，统计范围需要拉长
     */
    public static Usage getUsageBytesByUid(Context context, long startTime, long endTime, int uid) {
        Usage usage = new Usage();
        usage.totalRxBytes = 0;
        usage.totalTxBytes = 0;
        usage.mobleRxBytes = 0;
        usage.mobleTxBytes = 0;
        usage.wifiTxBytes = 0;
        usage.wifiTxBytes = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkStatsManager nsm = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            assert nsm != null;
            NetworkStats wifi = nsm.queryDetailsForUid(ConnectivityManager.TYPE_WIFI, null, startTime, endTime, uid);
            NetworkStats moble = nsm.queryDetailsForUid(ConnectivityManager.TYPE_MOBILE, null, startTime, endTime, uid);
            do {
                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                wifi.getNextBucket(bucket);
                usage.wifiRxBytes += bucket.getRxBytes();
                usage.wifiTxBytes += bucket.getTxBytes();
            } while (wifi.hasNextBucket());
            do {
                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                moble.getNextBucket(bucket);
                usage.mobleRxBytes += bucket.getRxBytes();
                usage.mobleTxBytes += bucket.getTxBytes();
            } while (moble.hasNextBucket());
            usage.wifiTotalData = usage.wifiRxBytes + usage.wifiTxBytes;
            usage.mobleTotalData = usage.mobleRxBytes + usage.mobleTxBytes;
        }
        return usage;
    }


    /**
     * 获取所有应用流量
     */
    public static Usage getUsageByUidFromSummaryTotal(Context context, long startTime, long endTime) {
        usage = new Usage();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkStatsManager nsm = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            assert nsm != null;
            NetworkStats ns = null;

            try {
                //移动数据
                ns = nsm.querySummary(ConnectivityManager.TYPE_MOBILE, null, startTime, endTime);
                do {
                    NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                    ns.getNextBucket(bucket);
                    usage.mobleRxBytes += bucket.getRxBytes();
                    usage.mobleTxBytes += bucket.getTxBytes();
                } while (ns.hasNextBucket());
                usage.mobleTotalData = usage.mobleRxBytes + usage.mobleTxBytes;

                //WIFI数据  querySummary  结果过滤为仅包含属于呼叫用户的 uid  当前电话卡
                ns = nsm.querySummary(ConnectivityManager.TYPE_WIFI, null, startTime, endTime);
                do {
                    NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                    ns.getNextBucket(bucket);
                    usage.wifiRxBytes += bucket.getRxBytes();
                    usage.wifiTxBytes += bucket.getTxBytes();
                } while (ns.hasNextBucket());
                usage.wifiTotalData = usage.wifiRxBytes + usage.wifiTxBytes;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return usage;
    }


    /**
     * 获取当天的零点时间
     *
     * @return
     */
    public static long getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis());
    }


    public static Usage getUsageByUidFromSummary(Context context) {
        long startTime = getSupportBeginDayofMonth().getTime();
        long endTime = System.currentTimeMillis();
//        if(usage == null && networkType == ConnectivityManager.TYPE_WIFI){
        usage = new Usage();
//        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkStatsManager nsm = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
            assert nsm != null;
//            NetworkStats ns = null;
            try {
                NetworkStatsManager ns = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
                NetworkStats.Bucket bucket = nsm.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, "", startTime, endTime);
                usage.mobleTotalData = bucket.getTxBytes() + bucket.getRxBytes();
                usage.mobleTxBytes = bucket.getTxBytes();
                usage.mobleRxBytes = bucket.getRxBytes();
//                do {
////                    NetworkStats.Bucket bucket = new NetworkStats.Bucket();
////                    ns.getNextBucket(bucket);
////                    usage.packageName = bucket.getUid()+"";
//                    usage.wifiRxBytes += bucket.getRxBytes();
//                    usage.wifiTxBytes += bucket.getTxBytes();
//                } while (ns.hasNextBucket());
//
//                ns = nsm.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, "", startTime, endTime);
//                do {
//                    NetworkStats.Bucket bucket = new NetworkStats.Bucket();
//                    ns.getNextBucket(bucket);
//                    usage.mobleRxBytes += bucket.getRxBytes();
//                    usage.mobleTxBytes += bucket.getTxBytes();
//                } while (ns.hasNextBucket());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return usage;
    }


    /**
     * 根据提供的年月日获取该月份的第一天
     *
     * @return
     * @Description: (这里用一句话描述这个方法的作用)
     * @Author: gyz
     * @Since: 2017-1-9下午2:26:57
     */
    public static Date getSupportBeginDayofMonth() {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        int year = cal.get(Calendar.YEAR);
//月
        int monthOfYear = cal.get(Calendar.MONTH) + 1;

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = cal.getTime();
        return firstDate;
    }


    /**
     * 根据提供的年月日获取下一月份的第一天
     *
     * @return
     * @Description: (这里用一句话描述这个方法的作用)
     * @Author: gyz
     * @Since: 2017-1-9下午2:26:57
     */
    public static Date getSupportBeginDayofNextMonth() {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        int year = cal.get(Calendar.YEAR);
//月
        int monthOfYear = cal.get(Calendar.MONTH) + 2;

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = cal.getTime();
        return firstDate;
    }


    /**
     * 根据提供的年月获取该月份的最后一天
     *
     * @param year
     * @param monthOfYear
     * @return
     * @Description: (这里用一句话描述这个方法的作用)
     * @Author: gyz
     * @Since: 2017-1-9下午2:29:38
     */
    public static Date getSupportEndDayofMonth(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = cal.getTime();
        return lastDate;
    }

    public static String Hourmin(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc));
        return times;
    }


}
