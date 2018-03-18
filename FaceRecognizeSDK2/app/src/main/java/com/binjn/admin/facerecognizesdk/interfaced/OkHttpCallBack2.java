package com.binjn.admin.facerecognizesdk.interfaced;


import okhttp3.Response;

/**
 * Created by wangdajian on 2017/12/6.
 */

public interface OkHttpCallBack2 {
    void onResponse(Response response);
    void onFailure(String erorrMsg);
}
