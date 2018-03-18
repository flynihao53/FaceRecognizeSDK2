package com.binjn.admin.facerecognizesdk.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangdajian on 2018/2/26.
 */

public class LogInfoUtils {
    private static final String LOG_DIRECTORY = "sdcard/com_binjn_log";
    private static final String LOG_FILE = "/log.txt";
    public static void logRecordWriteSdcard(String data){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String currentDate = dateFormat.format(date);
        currentDate += data + "\r\n";
        File fileDir = new File(LOG_DIRECTORY);
        if(!fileDir.exists()){
            fileDir.mkdir();
        }
        File file = new File(LOG_DIRECTORY + LOG_FILE);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(currentDate, true);
            byte[] bytes = currentDate.getBytes();
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
