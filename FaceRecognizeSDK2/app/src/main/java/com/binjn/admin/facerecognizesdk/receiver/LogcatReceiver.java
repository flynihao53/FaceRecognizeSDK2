package com.binjn.admin.facerecognizesdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.aliyun.sls.android.sdk.ClientConfiguration;
import com.aliyun.sls.android.sdk.LOGClient;
import com.aliyun.sls.android.sdk.LogException;
import com.aliyun.sls.android.sdk.SLSLog;
import com.aliyun.sls.android.sdk.core.auth.PlainTextAKSKCredentialProvider;
import com.aliyun.sls.android.sdk.core.callback.CompletedCallback;
import com.aliyun.sls.android.sdk.model.Log;
import com.aliyun.sls.android.sdk.model.LogGroup;
import com.aliyun.sls.android.sdk.request.PostLogRequest;
import com.aliyun.sls.android.sdk.result.PostLogResult;
import com.aliyun.sls.android.sdk.utils.IPService;
import com.binjn.admin.facerecognizesdk.utils.Constants;
import com.binjn.admin.facerecognizesdk.utils.DateTimeUtils;
import com.binjn.admin.facerecognizesdk.utils.FileUtils;
import com.binjn.admin.facerecognizesdk.utils.LogUtils;
import com.binjn.admin.facerecognizesdk.utils.MacAddressUtils;
import com.binjn.admin.facerecognizesdk.utils.NetWorkUtils;
import com.binjn.admin.facerecognizesdk.utils.TimeCountUtils;

import java.util.ArrayList;

/**
 * Created by wangdajian on 2018/2/27.
 */

public class LogcatReceiver extends BroadcastReceiver{
    public static final String tag = LogcatReceiver.class.getSimpleName();
    public final static int HANDLER_MESSAGE_UPLOAD_FAILED = 00011;
    public final static int HANDLER_MESSAGE_UPLOAD_SUCCESS = 00012;
    private ArrayList<String> logcatFileName;
    private String logcatFilePath = "";

    private String sdcard  = "sdcard/";
    private int logcatNum = 0;
    private Context context;
    private ArrayList<String> logcatFile;
    private String lockId;

    @Override
    public void onReceive(Context context, Intent intent) {
        //第一步
        this.context = context;
        IPService.getInstance().asyncGetIp(IPService.DEFAULT_URL, handler);
        LogUtils.i(tag, "接收到广播,内容为:" + intent.getExtras().getString("LOG_RECORD_UPLOAD"));
        boolean networkConnected = NetWorkUtils.isNetworkConnected(context);
        LogUtils.i(tag,"网络连接状态：" +networkConnected);
        if(!networkConnected){
            LogUtils.i(tag,"网络为断开状态，日志上传状态：否");
            TimeCountUtils.setLogcatUploadTime(context);
            return;
        }

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case IPService.HANDLER_MESSAGE_GETIP_CODE:
                    source_ip = (String) msg.obj;
                    LogUtils.i(tag,"source_ip=" + source_ip);
                    //阿里回调----第二步
                    scanLocalLogcatFile(context);
                    return;
                case HANDLER_MESSAGE_UPLOAD_FAILED:
                    //重新开始计时(1H)
                    LogUtils.i(tag,"日志文件发送失败，重新开始计时(1H)");
                    reStartTime(context);
                    return;
                case HANDLER_MESSAGE_UPLOAD_SUCCESS:
                    int logcatIndex = msg.arg1;
                    LogUtils.i(tag,"logcatIndex：" + logcatIndex);
                    if (logcatIndex != logcatFileName.size()){
                        readLogcatFile(logcatIndex);
                    }else {
                        logcatNum = 0;
                        //重新开始计时
                        reStartTime(context);
                        LogUtils.i(tag,"日志文件发送完成，重新开始计时(1H)");
                    }
                    return;
            }
        }
    };

    private void scanLocalLogcatFile(Context context) {
        logcatFileName = FileUtils.getFileName(context.getPackageName());
        if(logcatFileName == null || logcatFileName.size() == 0){
            //重新开始计时
            reStartTime(context);
            return;
        }
        logcatFile = new ArrayList<>();
        String currentDate = DateTimeUtils.getFormatDate();
        for (int i = 0; i < logcatFileName.size(); i++){
            LogUtils.i("tag","logcatFileName====" + logcatFileName.get(i));
            logcatFile.add(logcatFileName.get(i).substring(0, 10).trim());
            if(currentDate.equals( logcatFile.get(i))){
                logcatFileName.remove(i);
            }
        }
        if(logcatFileName == null || logcatFileName.size() == 0){
            //重新开始计时
            LogUtils.i(tag,"当前日志文件仅有一个");
            reStartTime(context);
            return;
        }
        logcatNum = 0;
        logcatFilePath = context.getPackageName();
        readLogcatFile(logcatNum);
    }

    private void reStartTime(Context context) {
        TimeCountUtils.setLogcatUploadTime(context);
    }

    public String source_ip = "";
    //public String AK = "LTAIerqkr4scPzeZ";
    //public String SK = "SdNtD3ls1lePRSwxH23MMk0Srn3kXk";

    public String endpoint = "cn-shenzhen.log.aliyuncs.com";
    public String AK = "LTAIMZnGxIUmgN4c";
    public String SK = "0EnBEYNqYGFxtEHjaIlR9qdAZr0Hnd";
    public String project = "phslog";
    public String logStore = "logs";

    private void readLogcatFile(int index) {
        Log log = new Log();
        final String logcatFileUrl = logcatFilePath + "/" + logcatFileName.get(index);
        LogUtils.i(tag,"上传的日志文件索引index：" + logcatNum + "，路径：" +logcatFileUrl);
        String logcatFileContent = FileUtils.readFileSD(logcatFileUrl,"\n");
        LogUtils.i(tag,"logcatFileContent----" + logcatFileContent.length());
        log.PutContent("current time", "当前时间:" + System.currentTimeMillis() / 1000);
        log.PutContent("content", logcatFileContent);
        PlainTextAKSKCredentialProvider credentialProvider =
                new PlainTextAKSKCredentialProvider(AK,SK);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(30 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(30 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(0); // 失败后最大重试次数，默认2次
        SLSLog.enableLog(); // log打印在控制台
        LOGClient logClient = new LOGClient(endpoint, credentialProvider, conf);
        /* 创建logGroup */
        LogUtils.i("tag","source_ip----" + source_ip);
        //String macStr = MacAddressUtils.getMacStr();
        lockId = FileUtils.readFileSD(Constants.DEVICE_ID);
        LogGroup logGroup = new LogGroup(lockId, TextUtils.isEmpty(source_ip) ? " no ip " : source_ip);
        logGroup.PutLog(log);
        try {
            PostLogRequest request = new PostLogRequest(project, logStore, logGroup);
            logClient.asyncPostLog(request, new CompletedCallback<PostLogRequest, PostLogResult>() {
                @Override
                public void onSuccess(PostLogRequest request, PostLogResult result) {
                    FileUtils.deleteFileFromSDCard(sdcard + logcatFileUrl);
                    logcatNum ++;
                    Message message = Message.obtain(handler);
                    message.what = HANDLER_MESSAGE_UPLOAD_SUCCESS;
                    message.arg1 = logcatNum;
                    message.sendToTarget();
                }

                @Override
                public void onFailure(PostLogRequest request, LogException exception) {
                    Message message = Message.obtain(handler);
                    message.what = HANDLER_MESSAGE_UPLOAD_FAILED;
                    message.obj = exception.getMessage();
                    message.sendToTarget();
                    LogUtils.i(tag,"日志上传错误信息：" + exception.getMessage());
                }
            });
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

}
