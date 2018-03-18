package com.binjn.admin.facerecognizesdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.binjn.admin.facerecognizesdk.interfaced.OkHttpCallBack;
import com.binjn.admin.facerecognizesdk.model.UploadFileBean;
import com.binjn.admin.facerecognizesdk.utils.Constants;
import com.binjn.admin.facerecognizesdk.utils.FileUtils;
import com.binjn.admin.facerecognizesdk.utils.LogUtils;
import com.binjn.admin.facerecognizesdk.utils.OkHttpUtils;
import com.binjn.admin.facerecognizesdk.utils.SQLiteUitls;
import com.binjn.admin.facerecognizesdk.utils.TimeCountUtils;
import com.binjn.admin.facerecognizesdk.utils.TimerUtils;

import java.util.ArrayList;

/**
 * Created by wangdajian on 2018/1/19.
 */

public class TimerReceiver extends BroadcastReceiver{
    private final static String tag = TimerReceiver.class.getSimpleName();
    private final static String UPLOAD_16_PHOTO = "UPLOAD_16_REGISTER_PHOTO";
    private ArrayList<String> fileName;
    private String path;
    private String token;
    private SQLiteUitls dbHelper;
    private int count;
    private Context context;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
           String message = (String) msg.obj;
            switch (message){
                case UPLOAD_16_PHOTO:
                   int index =  msg.arg1;
                    if(index != fileName.size()){
                        getFileNameFromFileDirectory(index);
                    }else {
                        count = 0;
                        TimeCountUtils.setMidnightAlarm(context);
                    }
                    break;
            }
        }
    };

    private String tokenTimeInit;
    private int tokenTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i("tagBroadcast", "接收到广播,开始扫描注册文件" + intent.getExtras().getString("MIDNIGHT_ALARM_FILTER"));
        if (dbHelper == null) {
            this.context = context;
            dbHelper = new SQLiteUitls(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        }
        tokenTimeInit = FileUtils.readFileSD(Constants.FILE_TOKEN_NAME);
        tokenTime = Integer.parseInt(tokenTimeInit);
        if (tokenTime <= 0) {
            getToken();
        }
        String midnightAlarmFilter = intent.getExtras().getString("MIDNIGHT_ALARM_FILTER");
            if ("MIDNIGHT_ALARM_FILTER".equals(midnightAlarmFilter)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //开始扫描文件
                fileName = FileUtils.getFileName(Constants.FILE_16_PHOTO_DIRECTORY, true);
                if (fileName != null) {
                    path = FileUtils.readFileSD(Constants.FILE_HTTP_ADDRESS);
                    token = FileUtils.readFileSD(Constants.FILE_TOKEN);
                    getFileNameFromFileDirectory(0);
                } else {
                    TimeCountUtils.setMidnightAlarm(context);
                }
            }
        }
    }

    /**
     * 根据文件目录获取该目录下的所有文件路径
     * @param index
     */
    private void getFileNameFromFileDirectory(int index) {
        String personId = fileName.get(index);
        String imgUrl = path + Constants.URLREGISTER16PHOTO(personId);
        String fileNamePath = Constants.FILE_16_PHOTO_DIRECTORY + "/" + fileName.get(index);
        ArrayList<String> fileNames = FileUtils.getFileName(fileNamePath, false);
        uploadRegister16Photo(personId,imgUrl,token,fileNames);
    }

    private void uploadRegister16Photo(final String personId, String imgUrl, String token, final ArrayList<String> fileNames) {
        OkHttpUtils.getInstance().upLoadPhotoFile(imgUrl, token, fileName, new UploadFileBean(), new OkHttpCallBack() {
            @Override
            public void onSuccessed(Object obj) {
                //注册图片上传成功后更新本地数据库
                LogUtils.i(tag,"注册图片上传成功，对应personId=" + personId);
                fileNames.clear();
                //删除数据库RegisterPhotoFeature表中state字段(记录注册时保存的图片路径)
                delRegisterPhotoFeature(personId);
                //删除注册时的图片
                String path = Constants.FILE_16_PHOTO_DIRECTORY + "/" + personId;
                count ++;
                FileUtils.deleteDirAndFile(path);
                Message msg = Message.obtain();
                msg.arg1 = count;
                msg.obj = UPLOAD_16_PHOTO;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailed(String str) {
                fileNames.clear();
                LogUtils.i(tag,"注册图片上传失败，对应personId=" + personId);
            }
        });
    }

    private void delRegisterPhotoFeature(String personId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("RegisterPhotoFeature","personId=?",new String[]{personId});
        db.close();
    }

    public void getToken() {
        TimerUtils.getInstance().refrashTokenToSD();
    }
}
