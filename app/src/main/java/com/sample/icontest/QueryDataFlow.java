package com.sample.icontest;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

class QueryDataFlow {

    public static void queryAllDataFlow(Handler handle, Context context, long startTime, long endTime, ThreadPoolExecutor threadPool) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // 子线程中统计流量
                DataUsageTool.Usage tempUsage = DataUsageTool.getUsageByUidFromSummaryTotal(context, startTime, endTime);
                Log.e("tempUsage", "tempUsage======" + tempUsage.toString());
                Message message = new Message();
                message.what = FlowStatisticsActivity.ALL_APP_FLOW;
                message.obj = tempUsage;
                handle.sendMessage(message);
            }
        };
        threadPool.execute(runnable);
    }


    /**
     * 获取某月分每一天的流量数据
     *
     * @param context
     * @param month   0 代表上一月   1 代表本月
     * @return
     */
    public static void getMonthlyAndDailyData(Handler handle, Context context, int month, ThreadPoolExecutor threadPool) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<MonthlyDataBean> list = new ArrayList<>();
                int total = TimeUtils.getPreviousMonthTotalDay(month);
                total++;
                Log.e("total", "total==========" + total);
                List<Long> dateList = new ArrayList<>();
                for (int i = 0; i < total; i++) {
                    String date = TimeUtils.timestampConversionDate(TimeUtils.getEveryDayOfTheLastMonth(month, i) + "");
                    Log.e("total", "本月日期==========" + date);
                    dateList.add(TimeUtils.getEveryDayOfTheLastMonth(month, i));
                }
                int size = dateList.size() - 1;
                for (int i = 0; i < size; i++) {
                    MonthlyDataBean monthlyDataBean = new MonthlyDataBean();
                    DataUsageTool.Usage usage = DataUsageTool.getUsageByUidFromSummaryTotal(context, dateList.get(i), dateList.get(i + 1));

                    monthlyDataBean.time = TimeUtils.timestampConversionDate(dateList.get(i) + "");//时间日期
                    monthlyDataBean.mobileTotal = StringUtil.byteToMB(usage.mobleTotalData);//总 移动数据
                    monthlyDataBean.mobileDown = StringUtil.byteToMB(usage.mobleTxBytes);// 移动 下载 数据
                    monthlyDataBean.mobileUpload = StringUtil.byteToMB(usage.mobleRxBytes);//移动 上传 数据

                    monthlyDataBean.wifiTotal = StringUtil.byteToMB(usage.wifiTotalData);//总 wifi数据
                    monthlyDataBean.wifiDown = StringUtil.byteToMB(usage.wifiTxBytes);//    wifi 下载数据
                    monthlyDataBean.wifiUpload = StringUtil.byteToMB(usage.wifiRxBytes);// wifi 上传数据
                    list.add(monthlyDataBean);
                }
                Message message = new Message();
                message.what = FlowStatisticsActivity.MONTH_FLOW;
                message.obj = list;
                handle.sendMessage(message);
            }
        };
        threadPool.execute(runnable);
    }
}
