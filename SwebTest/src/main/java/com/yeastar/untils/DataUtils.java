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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHSS");
        Date Sdate = calendar.getTime();
        return sdf.format(Sdate);
    }

    public static String getNextMonthTime(String DataForm){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DataForm);
        calendar.add(Calendar.MONTH,1);
        Date Sdate = calendar.getTime();
        return sdf.format(Sdate);
    }

    public static String getCurrentTime(String DataForm){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DataForm);
        Date Sdate = calendar.getTime();
        return sdf.format(Sdate);
    }

    public static String getYestoadayTime(String DataForm){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        SimpleDateFormat sdf = new SimpleDateFormat(DataForm);
        Date Sdate = calendar.getTime();
        return sdf.format(Sdate);
    }


}
