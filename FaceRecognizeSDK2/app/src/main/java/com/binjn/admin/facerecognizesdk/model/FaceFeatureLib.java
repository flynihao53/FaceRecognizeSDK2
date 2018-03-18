package com.binjn.admin.facerecognizesdk.model;

import com.binjn.admin.facerecognizesdk.utils.ByteUtils;
import com.binjn.admin.facerecognizesdk.utils.DateTimeUtils;
import com.binjn.admin.facerecognizesdk.utils.FileUtils;

/**
 * Created by wangdajian on 2018/1/15.
 */

/**
 * 特征库头文件
 */
public class FaceFeatureLib {
    /**
     * 获得特征库头文件
     * @param featureLength 单个特征值长度
     * @param featureNum 特征数量
     * @return
     */
    public static byte[] getFaceFeatureLib(int featureLength,int featureNum){
        byte[] id = new byte[32];                   //特征库编号
        byte[] name = new byte[128];                //特征库名称
        byte[] version = new byte[16];              //特征库版本
        byte[] update_time = new byte[32];          //特征库更新时间
        byte[] reserved = new byte[256];            //保留字段
        int count = featureNum;    //4字节         //特征数量
        int len = featureLength;  //4字节          //单个特征长度
        String currentTime = DateTimeUtils.getCurrentTime();
        byte[] bytes = currentTime.getBytes();
        for(int i = 0;i < 32; i ++){
            if(i < bytes.length){
                update_time[i] = bytes[i];
            }else if(i >=  bytes.length){
                update_time[i] = 0;
            }
        }
        byte[] byteCount = FileUtils.intToBytes2(count);
        byte[] byteLen = FileUtils.intToBytes2(len);
        byte[] bytes0 = ByteUtils.addBytes(id, name);
        byte[] bytes1 = ByteUtils.addBytes(bytes0, version);
        byte[] bytes2 = ByteUtils.addBytes(bytes1, update_time);
        byte[] bytes3 = ByteUtils.addBytes(bytes2, reserved);
        byte[] bytes4 = ByteUtils.addBytes(bytes3, byteCount);
        byte[] bytes5 = ByteUtils.addBytes(bytes4, byteLen);
       return bytes5;
    }
}
