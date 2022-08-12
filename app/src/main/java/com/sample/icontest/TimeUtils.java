package com.sample.icontest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class TimeUtils {

    /**
     * 上一个月份的第一天
     * @return
     */
    public static long getThePreviousSupportBeginDayofMonth() {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        int year = cal.get(Calendar.YEAR);
        int monthOfYear = cal.get(Calendar.MONTH);

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);

        cal.set(Calendar.DAY_OF_MONTH, 1);//设置天数为1
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, -1);//在本月第一天情况下，倒退一天回到上个月最后一天
        Date lastDate = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 1);//在上个月最后一天基础上，重置天数为第一天，至此回到上个月第一天
        Date firstDate = cal.getTime();
        System.out.println("前一个月时间为:" +timestampConversionDate(firstDate.getTime()+""));
        return firstDate.getTime();
    }


    /**
     * 当前月份的第一天
     *
     * @return
     */
    public static long getSupportBeginDayofMonth() {
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
        System.out.println("当前月时间为:" +timestampConversionDate(firstDate.getTime()+""));
        return firstDate.getTime();
    }


    /**
     * 下一月份的第一天
     * @return
     */
    public static long getSupportBeginDayofNextMonth() {
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
        System.out.println("下一月时间为:" +timestampConversionDate(firstDate.getTime()+""));
        return firstDate.getTime();
    }


    /*
     * 获取前一天时间
     */
    public static long getTheDayBefore() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1); //向前走一天
        Date date = calendar.getTime();
        String startTime = sdf.format(date) + " 00:00:00";
        System.out.println("前一天时间为:" +startTime);
        return date.getTime();
    }

    /*
     * 获取今天时间
     */
    public static long getToDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0); //今天
        Date date = calendar.getTime();
        String startTime = sdf.format(date) + " 00:00:00";
        System.out.println("今天时间为:" +startTime);
        return timeToStamp(startTime);
    }

    /*
     * 获取明天时间
     */
    public static long getTomorrow() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1); //明天
        Date date = calendar.getTime();
        String startTime = sdf.format(date) + " 00:00:00";
        System.out.println("明天时间为:"+startTime);
        return date.getTime();
    }

    /*
     * 获取上一周日期  周一   周日
     */
    public static String getLastWeekTime() {
        String dateTime = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.DATE, -7);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date sTime = calendar.getTime();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Date eTime = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = sdf.format(sTime) + " 00:00:00";
        String endTime = sdf.format(eTime) + " 23:59:59";
        System.out.println(startTime);
        System.out.println(endTime);

        //return s+","+e;
        return timeToStamp(startTime) + "," + timeToStamp(endTime);
    }


    /*
     * 获取本周日期  周一   周日
     */
    public static String getThisWeekTime() {
        String dateTime = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date sTime = calendar.getTime();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Date eTime = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = sdf.format(sTime) + " 00:00:00";
        String endTime = sdf.format(eTime) + " 23:59:59";
        System.out.println(startTime);
        System.out.println(endTime);

        //return s+","+e;
        return timeToStamp(startTime) + "," + timeToStamp(endTime);
    }

    /*
    *日期转换为时间戳
    */
    public static long timeToStamp(String timers) {
        Date d = new Date();
        long timeStemp = 0;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            d = sf.parse(timers);// 日期转换为时间戳
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeStemp = d.getTime();
        return timeStemp;
    }
    /*
     *时间戳转换为日期
     */
    public static String timestampConversionDate(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc));
        return times;
    }


    /**
     * 获取上一个月总天数
     * @return
     */
    public static int getPreviousMonthTotalDay(int monthIndex){
        int total = 0 ;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int monthOfYear = cal.get(Calendar.MONTH);
        monthOfYear += monthIndex;
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        total = cal.getActualMaximum(Calendar.DAY_OF_MONTH);    //获取本月最大天数
        return total;
    }



    /**
     * 上一个月份的每一天
     * currentDay  本月天数下标
     * monthIndex  添加月数  1则是当前月份
     * @return
     */
    public static long getEveryDayOfTheLastMonth(int monthIndex,int currentDay) {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        int year = cal.get(Calendar.YEAR);
        int monthOfYear = cal.get(Calendar.MONTH);
        monthOfYear += monthIndex;
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
       int last = cal.getActualMaximum(Calendar.DAY_OF_MONTH);    //获取本月最大天数
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, currentDay);
        Date firstDate = cal.getTime();
//        System.out.println("上个月的某一天:" +timestampConversionDate(firstDate.getTime()+""));
        return firstDate.getTime();
    }



    public static final int LAST_MONTH_FIRST_DAY = 0;//上个月
    public static final int THIS_MONTH_FIRST_DAY = 1;//本月
    public static final int NEXT_MONTH_FIRST_DAY = 2;//下个月
    /*
       type 类型  0  / 1 / 2  分别表示 上个月、本月、下个月 的第一天时间戳
       return  时间戳
     */
    public static long getMonthDate(int type){
        long dateTime = 0;
        switch(type){
            case LAST_MONTH_FIRST_DAY:
                dateTime =  TimeUtils.getThePreviousSupportBeginDayofMonth();
                break;
            case THIS_MONTH_FIRST_DAY:
                dateTime = TimeUtils.getSupportBeginDayofMonth();
                break;
            case NEXT_MONTH_FIRST_DAY:
                dateTime =  TimeUtils.getSupportBeginDayofNextMonth();
                break;
        }
        return dateTime;
    }



}




