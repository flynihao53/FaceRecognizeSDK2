package com.binjn.admin.facerecognizesdk.utils;

import java.util.ArrayList;
/**
 * Created by wangdajian on 2017/11/30.
 */

public class ByteUtils {

    private static int ibit;

    public static byte[] float2byte(float f) {
        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }
        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }

    /**
     * 将一个4位字节数组转换为浮点数。
     * @param b 字节数组
     * @return 浮点数
     */
    public static float bytesToFloat(byte[] b)
    {
        return Float.intBitsToFloat(bytesToInt(b));
    }

    /**
     * 将一个4位字节数组转换为4整数。<br>
     * 注意，函数中不会对字节数组长度进行判断，请自行保证传入参数的正确性。
     *
     * @param b 字节数组
     * @return 整数
     */
    public static int bytesToInt(byte[] b)
    {
        int i = (b[0] << 24) & 0xFF000000;
        i |= (b[1] << 16) & 0xFF0000;
        i |= (b[2] << 8) & 0xFF00;
        i |= b[3] & 0xFF;
        return i;
    }

    // float转换为byte[4]数组
    public static ArrayList<Byte> getByteArray(float[] f) {
        ArrayList<Byte> list = new ArrayList<>();
        for (int i = 0;i < f.length;i ++){
            //将float里面的二进制串解释为int整数
            ibit = Float.floatToIntBits(f[i]);
            byte[] b = new byte[4];
            b[0] = (byte) ((ibit & 0xff000000) >> 24);
            b[1] = (byte) ((ibit & 0x00ff0000) >> 16);
            b[2] = (byte) ((ibit & 0x0000ff00) >> 8);
            b[3] = (byte)  (ibit & 0x000000ff);
            for (int j = 0;j < b.length;j ++){
                list.add(b[j]);
            }
        }
        return list;
    }

    /**
     * 注意 f 数组中一定不能有无用的数据
     * float[] -> byte[]
     * @param f
     * @return
     */
    public static byte[] getByteArrays(float[] f) {
        byte[] result = new byte[f.length*4];
        for (int i = 0;i < f.length;i ++){
            //将float里面的二进制串解释为int整数
            ibit = Float.floatToIntBits(f[i]);
            result[4 * i + 0] = (byte) ((ibit & 0xff000000) >> 24);
            result[4 * i + 1] = (byte) ((ibit & 0x00ff0000) >> 16);
            result[4 * i + 2]= (byte) ((ibit & 0x0000ff00) >> 8);
            result[4 * i + 3] = (byte)  (ibit & 0x000000ff);
        }
        return result;
    }

    public static byte[] getByteArray(float f) {
        int i = Float.floatToIntBits(f);
        byte[] b = new byte[4];
        b[0] = (byte) ((i & 0xff000000) >> 24);
        b[1] = (byte) ((i & 0x00ff0000) >> 16);
        b[2] = (byte) ((i & 0x0000ff00) >> 8);
        b[3] = (byte)  (i & 0x000000ff);
        return b;
    }

    /**
     * 合并byte数组
     * @param data1
     * @param data2
     * @return data1 与 data2拼接的结果
     */
    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }

    /**
     * 截取数组
     * @param src
     * @param srcPos
     * @param dest
     * @param destPos
     * @param length
     * @return
     */
    public static byte[] subBytes(byte[] src,int srcPos,byte[] dest,int destPos,int length){
        System.arraycopy(src, srcPos, dest, destPos, length);
        return dest;
    }

    public static int bytesToInt2(byte[] b){
        String s = new String(b);
        return Integer.parseInt(s);
    }

    public static String byteArrayToStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        String str = new String(byteArray);   //byteArray对应为 str.getBytes()
        return str;
    }




}
