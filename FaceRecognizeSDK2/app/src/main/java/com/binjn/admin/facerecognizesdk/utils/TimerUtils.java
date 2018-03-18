package com.binjn.admin.facerecognizesdk.utils;

import android.os.Handler;
import android.util.Log;

import com.binjn.admin.facerecognizesdk.interfaced.OkHttpCallBack;
import com.binjn.admin.facerecognizesdk.model.APIAutherityBean;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by wangdajian on 2017/11/28.
 */

public class TimerUtils {
    private static TimerUtils timerUtils = null;
    Handler handler = new Handler();
    private String tag = "TimerUtils";
    private  int CountTime = -1;
    private int count = 0;
    private String tokenTimeInit;
    private String tokenTimeSub;

    private static final String FILE_NAME = "binjn_http.txt";
    private String path;
    private String apikey;
    private String timestamp;
    private HashMap<String, String> hashMap;
    private String signature;
    private String requestPath;
    private String secretkey;
    //private int tempCount = 0;

    public static TimerUtils getInstance(){
        if(timerUtils == null){
            synchronized (TimerUtils.class){
                timerUtils = new TimerUtils();
            }
        }
        return timerUtils;
    }
    public void TimeTask(){
        handler.postDelayed(runnable,1000);
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if(CountTime <= -1){
                    tokenTimeInit = FileUtils.readFileSD(Constants.FILE_TOKEN_NAME);
                    CountTime = Integer.parseInt(tokenTimeInit);
                }
                count ++;
                CountTime --;
                if(count == 60){
                    if(CountTime <= 0){
                        CountTime = -1;
                    }
                    tokenTimeSub = String.valueOf(CountTime);
                    FileUtils.writeFileSDcard(Constants.FILE_TOKEN_NAME, tokenTimeSub);
                    count = 0;
                }

                if(CountTime <= 0){
                    refrashTokenToSD();
                }else if(CountTime > 0 && CountTime < 1000){
                    refrashTokenToSD();
                }else {
                    handler.postDelayed(runnable,1000);
                }
            }catch (Exception e){
                LogUtils.i("tag","定时出错" + e.toString());
                handler.postDelayed(runnable,1000);
            }

        }
    };

    public void refrashTokenToSD(){
        //这个路径不应该是写进去的，应该是直接在sd卡生成
        boolean isSuccess = FileUtils.isExists(FILE_NAME);
        if(isSuccess){
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
            startRequestToken(requestPath);
        }
    }

    private void startRequestToken(String requestPath) {
        OkHttpUtils.getInstance().requestGet(requestPath, new APIAutherityBean(), new OkHttpCallBack() {
            @Override
            public void onSuccessed(Object obj) {
                APIAutherityBean apiBean = (APIAutherityBean) obj;
                String token = apiBean.getToken();
                int expiresIn = apiBean.getExpires_in();
                String tokenOutdate = String.valueOf(expiresIn);
                FileUtils.writeFileSDcard(Constants.FILE_TOKEN,token);
                FileUtils.writeFileSDcard(Constants.FILE_TOKEN_NAME,tokenOutdate);
                tokenTimeInit = FileUtils.readFileSD(Constants.FILE_TOKEN_NAME);
                CountTime = Integer.parseInt(tokenTimeInit);
                handler.postDelayed(runnable,1000);
            }

            @Override
            public void onFailed(String str) {
                tokenTimeInit = FileUtils.readFileSD(Constants.FILE_TOKEN_NAME);
                CountTime = Integer.parseInt(tokenTimeInit);
                handler.postDelayed(runnable,1000);
            }
        });
    }
}
