package com.binjn.admin.facerecognizesdk.interfaced;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by wangdajiang on 2017/12/6.
 */

public interface OkhttpCallbBack3 {
    void Response(Call call, Response response);
    void onFailure(Call call,String errorMsg);
}
