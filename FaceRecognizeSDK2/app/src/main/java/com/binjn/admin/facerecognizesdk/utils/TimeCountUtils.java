package com.binjn.admin.facerecognizesdk.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

/**
 * Created by wangdajian on 2017/11/28.
 */
public class TimeCountUtils {
    private final static String MIDNIGHT_ALARM_FILTER = "MIDNIGHT_ALARM_FILTER";
    private final static String LOG_RECORD_UPLOAD = "LOG_RECORD_UPLOAD";
    /**
     * 设置午夜定时器, 午夜12点发送广播, MIDNIGHT_ALARM_FILTER.
     * 实际测试可能会有一分钟左右的偏差.
     *
     * @param context 上下文
     */

    public static void setMidnightAlarm(Context context) {
        Context appContext = context.getApplicationContext();
        Intent intent = new Intent(MIDNIGHT_ALARM_FILTER);
        intent.putExtra("MIDNIGHT_ALARM_FILTER",MIDNIGHT_ALARM_FILTER);
        PendingIntent pi = PendingIntent.getBroadcast(appContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        //午夜12点的标准计时, 来源于SO, 实际测试可能会有一分钟左右的偏差.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        // 显示剩余时间
        long now = Calendar.getInstance().getTimeInMillis();
        LogUtils.i("TimeCountUtils","剩余时间(秒): " + ((calendar.getTimeInMillis() - now) / 1000));
        // 设置之前先取消前一个PendingIntent
        am.cancel(pi);
        // 设置每一天的计时器
        //am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        //每一分钟
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //参数2是开始时间、参数3是允许系统延迟的时间
            am.setWindow(AlarmManager.RTC, 0, AlarmManager.INTERVAL_DAY, pi);
            //am.setWindow(AlarmManager.RTC, 0, 1*1000, pi);
        } else {
            am.setRepeating(AlarmManager.RTC, 0, AlarmManager.INTERVAL_DAY, pi);
            //参数1：定时 参数2：开始时间 参数3：延迟时间 参数4：意图
            //am.setRepeating(AlarmManager.RTC, 0, 1 * 1000, pi);
        }
    }

    public static void setLogcatUploadTime(Context context) {
        Context appContext = context.getApplicationContext();
        Intent intent = new Intent(LOG_RECORD_UPLOAD);
        intent.putExtra("LOG_RECORD_UPLOAD",LOG_RECORD_UPLOAD);
        PendingIntent pi = PendingIntent.getBroadcast(appContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        // 设置之前先取消前一个PendingIntent
        am.cancel(pi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //参数2是开始时间、参数3是允许系统延迟的时间
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60*60 * 1000, pi);
            //am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60 * 1000, pi);
            LogUtils.i("TimeCountUtils","执行--------first");
        } else {
            //am.setRepeating(AlarmManager.RTC_WAKEUP, 0, AlarmManager.INTERVAL_HOUR, pi);
            //参数1：定时 参数2：开始时间 参数3：延迟时间 参数4：意图
            am.setRepeating(AlarmManager.RTC_WAKEUP, 0, 60*60 * 1000, pi);
            //am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3000, pi);
            LogUtils.i("TimeCountUtils","执行--------second");
        }
    }

    //-------------------方法二--------------------//
    public static void setAlarmTime(Context context, long timeInMillis,String action, int time) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(action);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);
        int interval = time;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //参数2是开始时间、参数3是允许系统延迟的时间
            am.setWindow(AlarmManager.RTC, timeInMillis, interval, sender);
        } else {
            am.setRepeating(AlarmManager.RTC, timeInMillis, interval, sender);
        }
    }

    public static void canalAlarm(Context context, String action) {
        Intent intent = new Intent(action);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }
}
