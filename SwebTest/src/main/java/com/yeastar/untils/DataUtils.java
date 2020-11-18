package com.yeastar.untils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huangjx
 * @create 2019-06-20 16:03
 **/
public class DataUtils {
    /**
     * 获取当前的时间
     * 格式：yyyyMMddHHSS
     * @return
     */
    public static String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = calendar.getTime();
        return sdf.format(date);
    }

    public static String getNextMonthTime(String DataForm){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DataForm);
        calendar.add(Calendar.MONTH,1);
        Date date = calendar.getTime();
        return sdf.format(date);
    }

    public static String getCurrentTime(String DataForm){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DataForm);
        Date date = calendar.getTime();
        return sdf.format(date);
    }

    /**
     * 获取当前时间的 偏移时间
     * @param DataForm  HH:mm
     * @param HourOffset
     * @param MinuteOffset
     * @return
     */
    public static String getCurrentTimeAndOffset(String DataForm,int HourOffset,int MinuteOffset){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DataForm);
        calendar.add(Calendar.HOUR,HourOffset);
        calendar.add(Calendar.MINUTE,MinuteOffset);
        Date date = calendar.getTime();
        System.out.println(sdf.format(date));
        return sdf.format(date);
    }

    public static String getYesterdayTime(String DataForm){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        SimpleDateFormat sdf = new SimpleDateFormat(DataForm);
        Date date = calendar.getTime();
        return sdf.format(date);
    }

    public static String getCurrentWeekDay(){
        String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Calendar cal = Calendar.getInstance();
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * sun mon tue wed thu fri sat
     * @return
     */
    public static String getYesterdayWeekDay(){
        String[] weekDays = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
        Calendar cal = Calendar.getInstance();
        int w = cal.get(Calendar.DAY_OF_WEEK) - 2;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }


}
