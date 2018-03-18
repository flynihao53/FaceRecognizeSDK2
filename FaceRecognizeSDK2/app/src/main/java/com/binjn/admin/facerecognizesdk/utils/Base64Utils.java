package com.binjn.admin.facerecognizesdk.utils;


import android.util.Base64;

/**
 * Created by wangdajian on 2017/12/1.
 */

public class Base64Utils {

    public static String enCodeBase64(byte[] bytes){
        String s = Base64.encodeToString(bytes, Base64.DEFAULT);
        return s;
    }

    public static byte[] deCodeBase64(String bytes){
        byte[] decode = Base64.decode(bytes, Base64.DEFAULT);
        return decode;
    }
}
