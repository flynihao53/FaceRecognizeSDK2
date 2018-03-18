package com.binjn.admin.facerecognizesdk.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.binjn.admin.facerecognizesdk.interfaced.OkHttpCallBack;
import com.binjn.admin.facerecognizesdk.interfaced.OkHttpCallBack2;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OkHttpUtils {
    private OkHttpClient mOkHttpClient = Okhttp3Utils.getUnsafeOkHttpClient();
    private static OkHttpUtils mInstance;

    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    public void requestGet(String url, final Object activity, final OkHttpCallBack okResponse) {
        Request request = new Request.Builder().url(url).get().build();
        Log.e("TAG", "request:" + request);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                okResponse.onFailed(e.toString());
                Log.e("TAG", "失败" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.e("TAG", "当前获取字符串" + str);
                try {
                    JSONObject object = new JSONObject(str);
                    String result = object.getString("code");
                    if("0".equals(result)){
                        Gson gson = new Gson();
                        Object obj = gson.fromJson(str, activity.getClass());
                        okResponse.onSuccessed(obj);
                    }else {
                        String msg = object.getString("msg");
                        okResponse.onFailed(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void requestHeadGet(String url,String token,final Object activity, final OkHttpCallBack okResponse) {
        Request request = new Request.Builder().addHeader("token",token).url(url).get().build();
        Log.e("TAG", "request:" + request);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                okResponse.onFailed(e.toString());
                Log.e("TAG", "失败" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                //Log.e("TAG", "当前获取字符串" + str);
                try {
                    JSONObject object = new JSONObject(str);
                    String result = object.getString("code");
                    if("0".equals(result)){
                        Gson gson = new Gson();
                        Object obj = gson.fromJson(str, activity.getClass());
                        okResponse.onSuccessed(obj);
                    }else {
                        String msg = object.getString("msg");
                        okResponse.onFailed(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    okResponse.onFailed(e.toString());
                }
            }
        });
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     * json上传
     *
     * @param activity
     */
    public void requestJson(String url, String js,String token,final Object activity, final OkHttpCallBack okResponse) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), js);
        Log.v("TAG", "发送的json:" + js);
        //创建一个请求对象
        final Request request = new Request.Builder()
                .url(url).addHeader("token",token)
                .post(requestBody)
                .build();

        Log.e("TAG", "request:" + request.toString());
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                okResponse.onFailed("网络异常!" + e.toString());
                Log.e("TAG", "失败：" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.e("Tag", "响应结果：" + str);
                try {
                    JSONObject object = new JSONObject(str);
                    String result = object.getString("code");
                    if("0".equals(result)){
                        Gson gson = new Gson();
                        Object obj = gson.fromJson(str, activity.getClass());
                        okResponse.onSuccessed(obj);
                    }else {
                        String msg = object.getString("msg");
                        okResponse.onFailed(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    okResponse.onFailed("服务异常");
                }
            }
        });
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     * 加载图片
     *
     * @param url
     */
    public void loadImg(String url, final ImageView iv, final Activity activity) {
        Request request = new Request.Builder().url(url).build();
        Log.e("TAG", "request:" + request);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "获取图片失败" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] bytes = response.body().bytes();
                Log.e("TAG", "当前获取字符串" + bytes);
                final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //网络图片请求成功，更新到主线程的ImageView
                        iv.setImageBitmap(bmp);
                    }
                });
            }
        });
    }

    //
    /**
     * 上传文件(不带参数)
     * @param actionUrl 接口地址
     * @param filePath  本地文件地址
     */
    public <T> void upLoadFile(String actionUrl, String filePath,String token, final Object objClass, final OkHttpCallBack callBack) {
        //补全请求地址
        String requestUrl = actionUrl;
        //创建File
        File file = new File(filePath);
        //创建RequestBody
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), file);
        //创建Request
        final Request request = new Request.Builder().url(requestUrl).post(body).addHeader("token",token).build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailed("服务异常" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.e("Tag", "响应结果：" + str);
                try {
                    JSONObject object = new JSONObject(str);
                    String result = object.getString("code");
                    if("0".equals(result)){
                        Gson gson = new Gson();
                        Object obj = gson.fromJson(str, objClass.getClass());
                        callBack.onSuccessed(obj);
                    }else {
                        String msg = object.getString("msg");
                        callBack.onFailed(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callBack.onFailed("服务异常");
                }
            }
        });
    }

    //
    /**
     *单个上传文件(带参数)
     * @param  url 接口地址
     * @param callBack 回调
     * @param
     */
    public void upLoadFile(String url, String token, File file, final Object objClass, final OkHttpCallBack callBack) {
        try {
            Log.i("tag","要上传的文件名：" + file.getName());
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("file",file.getName(),           //application/octet-stream;text/x-markdown
                    RequestBody.create(MediaType.parse("Multipart/form-data; charset=utf-8"),file));
            final Request request = new Request.Builder()
                    .url(url).addHeader("token",token)
                    .post(builder.build())
                    .build();
            OkHttpClient client = Okhttp3Utils.getUnsafeOkHttpClient();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    try {
                    String str = response.body().string();
                    Log.e("Tag", "响应结果==：" + str);
                        JSONObject object = new JSONObject(str);
                        String result = object.getString("code");
                        if("0".equals(result)){
                            Gson gson = new Gson();
                            Object obj = gson.fromJson(str, objClass.getClass());
                            callBack.onSuccessed(obj);
                        }else {
                            String msg = object.getString("msg");
                            callBack.onFailed(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callBack.onFailed("===请求返回时解析数据异常====");
                    }
                }
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    callBack.onFailed("服务异常：" + e.toString());
                }
            });
        } catch (Exception e) {
            callBack.onFailed("===请求返回时解析数据异常====");
        }
    }

    /**
     *批量上传文件(带参数)
     * @param  url 接口地址
     * @param callBack 回调
     * @param
     */
    public void upLoadPhotoFile(String url, String token, ArrayList<String> filePaths, final Object objClass, final OkHttpCallBack callBack) {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            for (int i = 0;i <filePaths.size();i ++) {
                File file = new File(filePaths.get(i));
                builder.addFormDataPart("file",file.getName(),
                        RequestBody.create(MediaType.parse("Multipart/form-data; charset=utf-8"),file));
            }

             Request request = new Request.Builder()
                    .url(url).addHeader("token",token)
                    .post(builder.build())
                    .build();
            Log.i("tag","请求地址：" + url);
            //OkHttpClient client = Okhttp3Utils.getUnsafeOkHttpClient();
            mOkHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    try {
                        String str = response.body().string();
                        Log.e("Tag", "响应结果==：" + str);
                        JSONObject object = new JSONObject(str);
                        String result = object.getString("code");
                        if("0".equals(result)){
                            Gson gson = new Gson();
                            Object obj = gson.fromJson(str, objClass.getClass());
                            callBack.onSuccessed(obj);
                        }else {
                            String msg = object.getString("msg");
                            callBack.onFailed(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callBack.onFailed("===请求返回时解析数据异常====");
                    }
                }
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    callBack.onFailed("服务异常：" + e.toString());
                }
            });
        } catch (Exception e) {
            callBack.onFailed("===请求返回时解析数据异常====");
        }
    }


    /**
     * 上传文件(带参数-主要用于数据上报的接口)
     * @param url
     * @param token
     * @param filePaths
     * @param objClass
     * @param callBack
     */
    public void upLoadFiles(HashMap<String,String> hashMap , String url, String token, ArrayList<String> filePaths, final Object objClass, final OkHttpCallBack callBack) {
        try {  //application/json
         //   RequestBody requestBody = RequestBody.create(MediaType.parse("Multipart/form-data; charset=utf-8"), js);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            //builder.addPart(requestBody);
            Iterator<Map.Entry<String, String>> iterator = hashMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, String> next = iterator.next();
                String key = next.getKey();
                String value = next.getValue();
                builder.addFormDataPart(key,value);
            }
            for (int i = 0;i <filePaths.size();i ++) {
                File file = new File(filePaths.get(i));
                if(i == 0){
                    builder.addFormDataPart("file",filePaths.get(0),   //单个文件
                            RequestBody.create(MediaType.parse("Multipart/form-data; charset=utf-8"),file));
                }else {
                    builder.addFormDataPart("liveImgfiles",file.getName(), //多个文件
                            RequestBody.create(MediaType.parse("Multipart/form-data; charset=utf-8"),file));
                }
            }
            Request request = new Request.Builder()
                    .url(url).addHeader("token",token)
                    .post(builder.build())
                    .build();
            Log.i("tag","请求地址：" + url);
            mOkHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    try {
                        String str = response.body().string();
                        Log.e("Tag", "响应结果==：" + str);
                        JSONObject object = new JSONObject(str);
                        String result = object.getString("code");
                        if("0".equals(result)){
                            Gson gson = new Gson();
                            Object obj = gson.fromJson(str, objClass.getClass());
                            callBack.onSuccessed(obj);
                        }else {
                            String msg = object.getString("msg");
                            callBack.onFailed(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callBack.onFailed("===请求返回时解析数据异常====");
                    }
                }
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    callBack.onFailed("服务异常：" + e.toString());
                }
            });
        } catch (Exception e) {
            callBack.onFailed("===请求返回时解析数据异常====");
        }
    }

    /**
     * 上传文件(带参数-主要用于数据上报的接口)
     * @param url
     * @param token
     * @param filePaths
     * @param objClass
     * @param callBack
     */
    public void upLoadFilesForDataUpload(HashMap<String,String> hashMap , String url, String token, ArrayList<String> filePaths, final Object objClass, final OkHttpCallBack callBack) {
        try {  //application/json
            //   RequestBody requestBody = RequestBody.create(MediaType.parse("Multipart/form-data; charset=utf-8"), js);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            //builder.addPart(requestBody);
            Iterator<Map.Entry<String, String>> iterator = hashMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, String> next = iterator.next();
                String key = next.getKey();
                String value = next.getValue();
                builder.addFormDataPart(key,value);
            }
            for (int i = 0;i <filePaths.size();i ++) {
                File file = new File(filePaths.get(i));
                builder.addFormDataPart("liveImgfiles",file.getName(), //多个文件
                        RequestBody.create(MediaType.parse("Multipart/form-data; charset=utf-8"),file));
            }
            Request request = new Request.Builder()
                    .url(url).addHeader("token",token)
                    .post(builder.build())
                    .build();
            Log.i("tag","请求地址：" + url);
            mOkHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    try {
                        String str = response.body().string();
                        Log.e("Tag", "响应结果==：" + str);
                        JSONObject object = new JSONObject(str);
                        String result = object.getString("code");
                        if("0".equals(result)){
                            Gson gson = new Gson();
                            Object obj = gson.fromJson(str, objClass.getClass());
                            callBack.onSuccessed(obj);
                        }else {
                            String msg = object.getString("msg");
                            callBack.onFailed(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callBack.onFailed("===请求返回时解析数据异常====");
                    }
                }
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    callBack.onFailed("服务异常：" + e.toString());
                }
            });
        } catch (Exception e) {
            callBack.onFailed("===请求返回时解析数据异常====");
        }
    }

    //
    /**
     * 下载文件
     * @param url url
     * @param token
     */
    public void downLoadFile(final String url, final String token, final OkHttpCallBack2 callBack) {
        Request request = new Request.Builder().addHeader("token",token).url(url).get().build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure("服务异常" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callBack.onResponse(response);
            }
        });
    }

    public void downLoadFile2(final String url, final String token, final OkHttpCallBack2 callBack) {
        Request request = new Request.Builder().url(url).get().build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure("服务异常" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callBack.onResponse(response);
            }
        });
    }

}
