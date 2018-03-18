package com.binjn.admin.facerecognizesdk.face;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.binjn.admin.facerecognizesdk.interfaced.FaceInitDataListener;
import com.binjn.admin.facerecognizesdk.net.RequestData;
import com.binjn.admin.facerecognizesdk.utils.LogUtils;
import com.binjn.admin.facerecognizesdk.utils.MacAddressUtils;
import com.binjn.admin.facerecognizesdk.utils.TimeCountUtils;

/**
 * Created by wangdajian on 2018/2/7.
 */

public class InintData {
    private static InintData mInintData = null;
    private  Context context;
    private  Activity activity;
    private CloudPushService mPushService;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            switch (message){
                case "ADD_ALIAS":
                    addAlias();
                    break;
            }
        }
    };

    private InintData(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public static InintData getInstantce(Activity activity, Context context){
        if(mInintData == null){
            synchronized (InintData.class){
                mInintData = new InintData(activity,context);
            }
        }
        return mInintData;
    }

    public void initDatas(final FaceInitDataListener faceInitDataListener){
        FaceRecognizerAutherity.getInstance(activity).setDeviceRegisterCallback(new FaceRecognizerAutherity.DeviceRegisterCallback() {
            @Override
            public void deviceRegisterCallback(int registerResult) {
                faceInitDataListener.onDeviceRegister(registerResult);
            }
        });
        RequestData.getInstantce(activity).setDeviceRegisterListener(new RequestData.DeviceRegisterListener() {
            @Override
            public void onDeviceIsRegister(int registerResult) {
                faceInitDataListener.onDeviceRegister(registerResult);
            }
        });
        RequestData.getInstantce(activity).setInitReusltListener(new RequestData.InitReusltListener() {
            @Override
            public void onInitResultBack(boolean initReuslt) {
                faceInitDataListener.onInitResultBack(initReuslt);
            }
        });
        RequestData.getInstantce(activity).initData();
    }

    /*public void startLogcat(){
        LogCatHelper.getInstance(context,null).start();
    }
    public void stopLogcat(){
        LogCatHelper.getInstance(context,null).stop();
    }*/

    public void setAlias(){
        //设置定时任务
        TimeCountUtils.setLogcatUploadTime(context);
        mPushService = PushServiceFactory.getCloudPushService();
        mPushService.listAliases(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                if(!TextUtils.isEmpty(s)){
                    LogUtils.i("tag","alias:" + s + "别名存在");
                    return;
                }else {
                    Message message = Message.obtain();
                    message.obj = "ADD_ALIAS";
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFailed(String s, String s1) {
                Message message = Message.obtain();
                message.obj = "ADD_ALIAS";
                handler.sendMessage(message);
            }
        });

    }

    private void addAlias(){
        String macAddress = MacAddressUtils.getMacStr();
        mPushService.addAlias(macAddress, new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtils.i("tag","alias:" + s + "添加成功");
            }

            @Override
            public void onFailed(String s, String s1) {

            }
        });
    }
}
