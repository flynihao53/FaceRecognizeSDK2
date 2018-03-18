package com.binjn.admin.facerecognizesdk.utils;

import android.util.Log;

/**
 * Created by wdj on 2018/1/5.
 */

public class LogUtils {
    public static boolean DEBUG = true; //开关控制(debug设置为true，release设置为false)
    public static void i(String tag,String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }
    public static void e(String tag,String msg) {
        if(DEBUG) {
            Log.e(tag,msg);
        }
    }

    public static void d(String tag,String msg) {
        if(DEBUG) {
            Log.e(tag,msg);
        }
    }

    public static void setDebug(boolean debug){
        DEBUG = debug;
        /*if(!DEBUG){
            DEBUG = true;
        }*/
    }
}
