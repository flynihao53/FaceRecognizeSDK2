package com.binjn.admin.facerecognizesdk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangdajian on 2017/11/22.
 */

public class DateTimeUtils {
    public static long fromDateStringToLong(String inVal) { //此方法计算时间毫秒
        Date date = new Date();   //定义时间类型
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String format = inputFormat.format(date);
            long parse = Date.parse(format);
            date = inputFormat.parse(inVal); //将字符型转换成日期型
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date.getTime();   //返回毫秒数
    }
    public static String calRunTime(long startTime,long endTime){
        String str = "";
        long ss = (endTime - startTime) / 1000; //共计秒数
        int MM = (int)ss/60;   //共计分钟数
        int hh=(int)ss/3600;   //共计小时数
        int dd=(int)hh/24;     //共计天数
        str = "共计" + dd + "天" + hh + "小时" +MM + "分" + ss + "秒";
        return str;
    }
    public static long calRunSecondsTime(long startTime,long endTime){
        long ss = (endTime - startTime) / 1000; //共计秒数
        return ss;
    }

    public static String getCurrentTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        //设置时间戳
        //2017-11-27 10:26:30   -> 20171127102630
        String date = df.format(new Date(System.currentTimeMillis()));
        return date;
    }

    public static String getCurrentSystemTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String format = df.format(new Date());
        return format;
    }

    public static String getFormatDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        return sdf.format(System.currentTimeMillis());
    }
}
