package com.binjn.admin.facerecognizesdk.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.binjn.admin.facerecognizesdk.model.NirFaceFeatureBean;
import com.binjn.admin.facerecognizesdk.model.PersonsBean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangdajian on 2017/11/27.
 */

public class FileUtils {

    /**
     *保存文件到SDcard
     * @param filename 文件名
     * @param content 要写入文件的内容
     */
    public static boolean writeFileSDcard(String filename,String content){
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator + filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(file);
            out.write(content.getBytes());
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<File> getFileAccrodFilePath(String path){
        File file = new File(path);
        ArrayList<File> files = new ArrayList<>();
        if(!file.exists()){
            return null;
        }
        files.add(file);
        return files;
    }

    //文件读取的方法
    public static String readFileSD(String filename){
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator +  filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
       // Log.i("tag","filepath=" + filepath);
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                Log.i("tag","文件并不存在");
                return "";
            }
            //Log.i("tag","file=" + file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String readline = "";
            StringBuffer sb = new StringBuffer();
            while ((readline = br.readLine()) != null) {
                sb.append(readline);
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //文件读取的方法
    public static String readFileSD(String filename,String d){
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator +  filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        Log.i("tag","filepath=" + filepath);
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                Log.i("tag","文件并不存在");
                return "";
            }
            //Log.i("tag","file=" + file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String readline = "";
            StringBuffer sb = new StringBuffer();
            while ((readline = br.readLine()) != null) {
                sb.append(readline).append(d);
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 根据文件名返回文件路径
     * @param filename
     * @return
     */
    public static String getFilePathFromSDCard(String filename){
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator +  filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        File file = new File(filepath);
        if (!file.exists()) {
            return "";
        }
        String path = file.getPath();
        return path;
    }

    /**
     * 清空文件内容
     * @param filename
     */
    public static void clearInfoForFile(String filename) {
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator +  filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        File file =new File(filepath);
        if(file.exists()) {
            file.delete();
            return;
        }
    }

    /**
     * 根据文件路径删除文件
     * @param filepath
     * @return
     */
    public static boolean deleteFileFromSDCard(String filepath){
        Log.i("tag","filepath= " + filepath);
        File file =new File(filepath);
        if(file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    public static void deleteDirAndFile(String dir) {
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator +  dir;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + dir;
        }
        File file =new File(filepath);
        FileUtils.deleteDir(file);
    }
    /**
     * 删除文件夹下的所有文件和目录
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        Log.i("tag","dir=" + dir.getPath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
        //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
    //文件读取的方法
    public static ArrayList<String> readFileSD(String filename,int i){
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator +  filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                return null;
            }
            Log.i("tag","file=" + file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String readline = "";
            ArrayList<String> list = new ArrayList<>();
            //StringBuffer sb = new StringBuffer();
            while ((readline = br.readLine()) != null) {
                list.add(readline);
                //sb.append(readline);
            }
            br.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将获取到的近红外的人脸特征值保存到文件
     * @param feature 人脸特征类
     * @param filename 保存文件
     * @return
     */
    public static boolean writeFaceFeatureDatasToSD(NirFaceFeatureBean.FeatureBean feature,String filename){
        String filepath = "";
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator + filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        ObjectOutputStream os = null;
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            os = new ObjectOutputStream(new FileOutputStream(file));
            os.writeObject(feature);
            os.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从文件中读取近红外特征值对象
     * @param filename
     */
    public static List<NirFaceFeatureBean.FeatureBean> readFaceFeatureDatasFromSD(String filename){
        ArrayList<NirFaceFeatureBean.FeatureBean> featureList = new ArrayList<>();
        String filepath = "";
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator + filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        ObjectInputStream ois = null;
        File file = new File(filepath);
        if (!file.exists()) {
            return  null;
        }
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            NirFaceFeatureBean.FeatureBean featureBean = (NirFaceFeatureBean.FeatureBean) ois.readObject();
            featureList.add(featureBean);
            return featureList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将接收的图片保存到本地
     * @param bitmapList
     */
    public static ArrayList<String> saveBitmapToSDCard(ArrayList<Bitmap> bitmapList, String fileDirectory) {
        FileOutputStream out = null;
        ArrayList<String> pathList = new ArrayList<>();
        if(!Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)){
            Log.i("tag","sd卡没有挂载");
            return null;
        }
        for (int i = 0;i < bitmapList.size();i ++){
            String  sdCardDir = Environment.getExternalStorageDirectory()+ "/" +fileDirectory +"/";
            File dirFile  = new File(sdCardDir);
            if (!dirFile .exists()) {
                dirFile .mkdir();
            }
            long currentTimeMillis = System.currentTimeMillis();
            File file = new File(sdCardDir, "binjn" + currentTimeMillis +".jpg");
            try {
                out = new FileOutputStream(file);
                bitmapList.get(i).compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String path = sdCardDir + "binjn" + currentTimeMillis +".jpg";
            pathList.add(path);
        }
        return pathList;
    }

    //获取指定文件夹下的所有路径
    public static ArrayList<String> getFile(String path){
        // get file list where the path has
        File file = new File(path);
        // get the folder list
        File[] array = file.listFiles();
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<array.length;i++){
            if(array[i].isFile()){
                // only take file name
                list.add(array[i].getPath());
                Log.i("tag","文件名:" + array[i].getPath());
            }else if(array[i].isDirectory()){
                getFile(array[i].getPath());
                Log.i("tag","目录名:" + array[i].getPath());
            }
        }
        return list;
    }

    //获取指定文件夹下的文件夹名称/文件路径
    public static ArrayList<String> getFileName(String filename,boolean flag){
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator + filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        Log.i("tag","filepath="+ filepath);
        File file = new File(filepath);
        if(!file.exists()){
            return null;
        }
        // get the folder list
        File[] array = file.listFiles();
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<array.length;i++){
            if(flag){
                if(array[i].isDirectory()){
                    list.add(array[i].getName());
                }
            }else {
                if(array[i].isFile()){
                    list.add(array[i].getPath());
                }
            }

        }
        return list;
    }

    //获取指定文件夹下的文件夹名称/文件夹路径
    public static ArrayList<String> getFileName(String filename){
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator + filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        Log.i("tag","filepath" + filepath);
        File file = new File(filepath);
        if(!file.exists()){
            return null;
        }
        // get the folder list
        File[] array = file.listFiles();
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<array.length;i++){
                if(array[i].isFile()) {
                    list.add(array[i].getName());
                }
            }
        return list;
    }



    //文件读取的方法
    public static boolean isExists(String filename){
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator + filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    /**
     * 将接收的图片保存到本地并返回路径
     * @param bitmap  传入图片
     * @param fileDirectory  图片要保存的目录
     * @return
     */
    public static String saveBitmapToSDCard(Bitmap bitmap,String personId,String fileDirectory,boolean flag) {
        if(!Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)){
            LogUtils.i("tag","sd卡没有挂载");
            return null;
        }
         FileOutputStream out = null;
        String  sdCardDir = Environment.getExternalStorageDirectory()+ "/" +fileDirectory +"/";
        File dirFile  = new File(sdCardDir);
        if (!dirFile .exists()) {
            dirFile .mkdirs();
        }
        Log.i("tag","sdCardDir=" +sdCardDir);
        String sdPath = dirFile.getPath() + "/" + personId + "/";
        File dirFilePath  = new File(sdPath);
        if (!dirFilePath .exists()) {
            dirFilePath .mkdirs();
        }
        Log.i("tag","sdPath=" +sdPath);
        String currentTimeMillis = DateTimeUtils.getCurrentTime();
        String str = "";
        if(flag){
            str = "register" + currentTimeMillis +".jpg";
        }else {
            str = "compare" + currentTimeMillis +".jpg";
        }

        File file = new File(dirFilePath, str);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = file.getPath();
        return path;
    }

    /**
     * 将接收的图片保存到本地并返回路径
     * @param bitmap  传入图片
     * @param fileDirectory  图片要保存的目录
     * @return
     */
    public static String saveBitmapToSDCard(Bitmap bitmap,String personId,String fileDirectory,String faceId) {
        if(!Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)){
            LogUtils.i("tag","sd卡没有挂载");
            return null;
        }
        FileOutputStream out = null;
        String  sdCardDir = Environment.getExternalStorageDirectory()+ "/" +fileDirectory +"/";
        File dirFile  = new File(sdCardDir);
        if (!dirFile .exists()) {
            dirFile .mkdirs();
        }
        String sdPath = dirFile.getPath() + "/" + personId + "/";
        File dirFilePath  = new File(sdPath);
        if (!dirFilePath .exists()) {
            dirFilePath .mkdirs();
        }
       String str = faceId +".jpg";

        File file = new File(dirFilePath, str);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = file.getPath();
        return path;
    }

    public static boolean writePersonIdFileSDcard(List<PersonsBean.DataBean> personsData,String filename) {
        String filepath = null;
        File file = null;
        FileWriter fw = null;
        BufferedWriter writer = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator +  filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        try {
            for (int i = 0;i < personsData.size();i ++) {
                file = new File(filepath);
                fw = new FileWriter(file);
                writer = new BufferedWriter(fw);
                writer.write(personsData.get(i).getId());
                writer.newLine();//换行
                }
            writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
            try {
                writer.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }


    /**
     * 将字节数写到文件
     * @param b
     * @param filename
     * @return
     */
    public static boolean getFileFromBytes(byte[] b, String filename) {
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator +  filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(filepath);
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fstream = new FileOutputStream(file,true);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    /*读取文件的字节数组---读取文件的特征值*/
    public static byte[] toByteArray(String filename){
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator +  filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        File file = new File(filepath);
        if (!file.exists()) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            int buf_size = 2048 * 4;
            //int buf_size = 4;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*读取文件的字节数组---读取文件的特征值*/
    public static byte[] toByteArray(String personId,String filePhotoDirectory, String fileTempFeatureValue){
        String fileName = filePhotoDirectory + "/" + fileTempFeatureValue + "/" + personId + ".txt";
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator +  fileName;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + fileName;
        }
        File file = new File(filepath);
        if(!file.exists()){
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            int buf_size = 2048 * 4;
            //int buf_size = 4;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] intToBytes2(int n){
        byte[] b = new byte[4];
        for(int i = 0;i < 4;i++){
            b[i] = (byte)(n >> (24 - i * 8));
        }
        return b;
    }

    /*读取文件的字节数组*/
    public static byte[] toByteArray1(String filename){
        String filepath = null;
        boolean hascard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hascard) {
            //card
            filepath = Environment.getExternalStorageDirectory().toString() + File.separator +  filename;
        }else {
            //缓存目录
            filepath = Environment.getDownloadCacheDirectory().toString() + File.separator + filename;
        }
        File file = new File(filepath);
        if (!file.exists()) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            int buf_size = 4 * 4;
            int times = 1;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                if((times%2)==1){
                    bos.write(buffer, 0, len);
                }
                times++;
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 将特征值写到文件(不带特征库)
     * @param byteArrays
     * @param personId
     * @param filePhotoDirectory
     * @param fileTempFeatureValue
     */
    public static void writeFeatureToSD(byte[] byteArrays, String personId, String filePhotoDirectory, String fileTempFeatureValue) {
        if(!Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)){
            LogUtils.i("tag","sd卡没有挂载");
            return ;
        }
        FileOutputStream out = null;
        String  sdCardDir = Environment.getExternalStorageDirectory()+ "/" +filePhotoDirectory +"/";
        File dirFile  = new File(sdCardDir);
        if (!dirFile .exists()) {
            dirFile .mkdirs();
        }
        String sdPath = dirFile.getPath() + "/" + fileTempFeatureValue + "/";
        File dirFilePath  = new File(sdPath);
        if (!dirFilePath .exists()) {
            dirFilePath .mkdirs();
        }
        //long currentTimeMillis = SystemClock.currentThreadTimeMillis();
        String str = personId + ".txt";
        File file = new File(dirFilePath, str);
        BufferedOutputStream stream = null;
        try {
            //file = new File(filepath);
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fstream = new FileOutputStream(file,true);
            stream = new BufferedOutputStream(fstream);
            stream.write(byteArrays);
            return ;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
