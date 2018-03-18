package com.binjn.admin.facerecognizesdk.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by lly on 2016/11/16.
 */
public class FastUtils {

    static long secondTime = 0;
    public static boolean isFastSecondForce(){
        long time = System.currentTimeMillis();
        if ((time - secondTime)>800) {
            secondTime = time;
            return false;
        }else {
            Log.v("TAG","isFastSecondForce为true");
            secondTime = time;
            return true;
        }

    }



    /**
     * 得到上传的json串
     */
    public static String getJsonString(JSONObject requestobject, Context context) {
        JSONObject obj = new JSONObject();
        //CustomInfo info = MyApplication.getInstance().getInfo();
        try {
            //requestobject.put("mercode", info.getMercode());//商户编码
            obj.put("requestobject", requestobject);
            String s = requestobject.toString();
            //Log.v("TAG","签名串："+s+ "|"+info.getSignKey());
           // Log.v("wdj11","签名串："+s+ "|"+info.getSignKey());
           // String sign = md51(s + "|"+info.getSignKey());//.replaceAll("null", "")
            //obj.put("sign", sign);
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return obj.toString();
    }





    /**
     * 返回当前格式的时间
     */
    public static String getNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssS");
        return format.format(Calendar.getInstance().getTime());
    }

    /**
     * 返回当前格式的时间
     */
    public static String getNormalTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyymmddhhmmss");
        return format.format(Calendar.getInstance().getTime());
    }

    /**
     * 返回当前格式的时间
     */
    public static String getNormalTime2() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(Calendar.getInstance().getTime());
    }

    /**
     * MD5
     */
    public static final String md51(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 获得签名字符串
     * @param hashMap
     * @return
     */
    public static String getSignMd5Str(HashMap<String,String> hashMap){
        String macAddress = MacAddressUtils.getMacStr();
        if(macAddress == null){
            return null;
        }
        String signature = "";
        String secretkey = FastUtils.md51(Constants.KEY + macAddress);
        try {
             signature = SignUtils.getSignature(hashMap, secretkey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signature;
    }
    /**
     * 字节数组转化为十六进制字符串
     * @param byteArray
     * @return
     */
    public static String byteArrayToHex(byte[] byteArray){
        //首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        //new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符）
        char[] resultCharArray = new char[byteArray.length*2];
        //遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for(byte b : byteArray){
            resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b& 0xf];
        }
        //字符数组组合成字符串返回
        return new String(resultCharArray);
    }

    //public static String signMd5(HashMap)

    /**
     * 获取Assets路径下的文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = context.getClass().getClassLoader().getResourceAsStream("assets/" + fileName);
            BufferedReader bf = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "josn:" + stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 统一的toast提示
     */
    public static void Toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 统一的toast提示UI
     */
    public static void ToastInUi(final Activity context, final String message) {
        context.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 乘以100
     */
    public static String Multiply100(String value) {
        value = "0.58";
        Double money = Double.valueOf(value);
        Double money_i = money * 100;
        Log.d("TAG",String.format("%.0f", money_i));
        return String.format("%.0f", money_i);
    }

    /**
     * 除以100
     */
    public static String divided100(String value) {
        Double i = Double.valueOf(value);
        Double result = i / 100;
        return String.format("%.2f", result);
    }






    /**
     * 得到本日的时间区间
     */
    public static String[] getTodaySpan() {
        String today[] = new String[2];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str1 = format.format(Calendar.getInstance().getTime());
        today[0] = str1 + " 00:00:00";
        today[1] = str1 + " 24:00:00";
        Log.e("TAG", "day:1:" + today[0] + "--2:" + today[1]);
        return today;
    }

    /**
     * 得到本周的时间区间
     *
     * @param
     * @return
     */
    public static String[] getWeekSpan() {
        String week[] = new String[2];
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // MONDAY
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = format.format(calendar.getTime());
        String time2 = format.format(Calendar.getInstance().getTime());
        week[0] = time + " 00:00:00";
        week[1] = time2 + " 24:00:00";
        Log.e("TAG", "周:" + week[0] + "=====" + week[1]);
        return week;
    }

    /**
     * 得到本月的时间区间
     */
    public static String[] getMonthSpan() {
        String month[] = new String[2];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        String str1 = format.format(Calendar.getInstance().getTime());
        String str2 = format2.format(Calendar.getInstance().getTime());
        month[0] = str1 + "-01 00:00:00";
        month[1] = str2 + " 24:00:00";
        Log.e("TAG", "month:1:" + month[0] + "--2:" + month[1]);
        return month;
    }


    private static Dialog loadingDialog = null;




    /**
     * 忽略https
     */
    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();
            OkHttpClient okHttpClient = new OkHttpClient();

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
