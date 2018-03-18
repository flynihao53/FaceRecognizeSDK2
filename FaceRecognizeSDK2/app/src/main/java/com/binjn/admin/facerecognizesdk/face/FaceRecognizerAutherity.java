package com.binjn.admin.facerecognizesdk.face;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.binjn.admin.facerecognizesdk.interfaced.OkHttpCallBack;
import com.binjn.admin.facerecognizesdk.model.APIAutherityBean;
import com.binjn.admin.facerecognizesdk.model.TokenBean;
import com.binjn.admin.facerecognizesdk.net.RequestData;
import com.binjn.admin.facerecognizesdk.utils.Constants;
import com.binjn.admin.facerecognizesdk.utils.DateTimeUtils;
import com.binjn.admin.facerecognizesdk.utils.FastUtils;
import com.binjn.admin.facerecognizesdk.utils.FileUtils;
import com.binjn.admin.facerecognizesdk.utils.LogUtils;
import com.binjn.admin.facerecognizesdk.utils.MacAddressUtils;
import com.binjn.admin.facerecognizesdk.utils.OkHttpUtils;
import com.binjn.admin.facerecognizesdk.utils.SignUtils;
import com.binjn.admin.facerecognizesdk.utils.TimerUtils;

import java.io.IOException;
import java.util.HashMap;


/**
 * Created by wangdajian on 2017/11/27.
 */

public class FaceRecognizerAutherity {
    private static final String DEVICE_UNREGISTER = "DEVICE_UNREGISTER";
    private final static int DEVICE_UNREGISTER_VALUE = 8012;                         //设备未注册
    private static String tag = FaceRecognizerAutherity.class.getSimpleName();
    private static final String FILE_NAME = "binjn_http.txt";
    private String path;
    private String apikey;
    private String timestamp;
    private HashMap<String, String> hashMap;
    private String signature;
    private String requestPath;
    private  static Activity activity;
    private String secretkey;
    private HashMap<String, String> hm;
    private static FaceRecognizerAutherity mFaceRecognizerAutherity = null;
    private DeviceRegisterCallback mDeviceRegisterCallback = null;

    //test2
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = (String) msg.obj;
            switch (message){
                case DEVICE_UNREGISTER:
                    if(mDeviceRegisterCallback != null){
                        mDeviceRegisterCallback.deviceRegisterCallback(DEVICE_UNREGISTER_VALUE);
                    }
                    break;
            }
        }
    };
    private Message msg;

    protected FaceRecognizerAutherity(Activity activity){
        this.activity = activity;
    }

    public static FaceRecognizerAutherity getInstance(Activity activity){
        if(mFaceRecognizerAutherity == null){
            synchronized (FaceRecognizerAutherity.class){
                mFaceRecognizerAutherity = new FaceRecognizerAutherity(activity);
            }
        }
        return mFaceRecognizerAutherity;
    }

    public void requestAPIAuthentication(){
        boolean isSuccess = FileUtils.isExists(FILE_NAME);
        if(isSuccess){
            requestTokenAndAuthority();
        }else {
            //文件请求路径不存在(需要在设备根目录添加一个请求根地址文件)
            FileUtils.writeFileSDcard(Constants.FILE_HTTP_ADDRESS,Constants.HTTP_PATH);
            requestTokenAndAuthority();
        }
    }

    /**
     * 第一步 开始获得授权并获取token
     */
    private void requestTokenAndAuthority() {
        path = FileUtils.readFileSD(FILE_NAME);
        if("".equals(path)){
            return;
        }
        //得到的mac地址为去掉:后的小写字符串
        String macAddress = MacAddressUtils.getMacStr();
        if(macAddress == null){
            return;
        }
        apikey = macAddress;                        //获得APIkey
        timestamp = DateTimeUtils.getCurrentTime(); //获取当前时间戳
        Log.i(tag,"apikey=" + apikey);
        Log.i(tag,"timestamp=" + timestamp);
        hashMap = new HashMap<>();
        hashMap.put("apikey",apikey);
        hashMap.put("timestamp",timestamp);
        //
        secretkey = FastUtils.md51(Constants.KEY + macAddress);
        try {
            signature = SignUtils.getSignature(hashMap, secretkey);
            hashMap.put("sign", signature);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestPath = path + Constants.API_AUTHERITY +"?apikey=" + apikey
                +"&timestamp=" + timestamp + "&sign=" + signature;
        Log.i(tag,"请求地址requestPath=" + requestPath);
        startAPIGetRequest(requestPath);
    }

    private void startAPIGetRequest(String requestPath) {
        OkHttpUtils.getInstance().requestGet(requestPath, new APIAutherityBean(), new OkHttpCallBack() {
            @Override
            public void onSuccessed(final Object obj) {
                activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        APIAutherityBean apiBean = (APIAutherityBean) obj;
                        String token = apiBean.getToken();
                        int expiresIn = apiBean.getExpires_in();
                        String tokenOutdate = String.valueOf(expiresIn);
                        FileUtils.writeFileSDcard(Constants.FILE_TOKEN,token);
                        FileUtils.writeFileSDcard(Constants.FILE_TOKEN_NAME,tokenOutdate);
                        TimerUtils.getInstance().TimeTask();
                        //time.TimeTask();
                        //获取门禁机注册接口----第2步
                        //RequestData.getInstantce(activity).getDeviceId();
                        RequestData.getInstantce(activity).getCreateFaceId();
                    }
                });
            }

            @Override
            public void onFailed(String str) {
                if(str.contains("APKIKEY不存在")){
                    msg = Message.obtain();
                    msg.obj = DEVICE_UNREGISTER;  //说明该设备未在平台注册
                    handler.sendMessage(msg);
                }
                LogUtils.i(tag,"授权接口返回错误信息：" +str);
            }
        });
    }


    public interface DeviceRegisterCallback{
        void deviceRegisterCallback(int registerResult);
    }

    public void setDeviceRegisterCallback(DeviceRegisterCallback callback){
        this.mDeviceRegisterCallback = callback;
    }


    private void queryTokenState(String token) {
        hm = new HashMap<>();
        hm.put("apikey",apikey);
        //String tokenUrl = path + Constants.API_AUTHERITY +"?token=" + token + "&apikey=" + apikey + "&sign=" + signature;
        String tokenUrl = "https://api.binjn.com:59001/phs/security/token/" +  token;
        Log.i(tag,"tokenUrl=" + tokenUrl);  //6694288aec41d58348cfeae01b94ddb9
        OkHttpUtils.getInstance().requestGet(tokenUrl, new TokenBean(), new OkHttpCallBack() {
            @Override
            public void onSuccessed(Object obj) {
                
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }
}
