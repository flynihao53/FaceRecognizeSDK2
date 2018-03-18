package com.binjn.admin.facerecognizesdk.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.binjn.admin.facerecognizesdk.face.FaceRecognizer;
import com.binjn.admin.facerecognizesdk.interfaced.FaceFeaturesCallback;
import com.binjn.admin.facerecognizesdk.interfaced.OkHttpCallBack2;
import com.binjn.admin.facerecognizesdk.model.FaceDetectorBean;
import com.binjn.admin.facerecognizesdk.utils.FileUtils;
import com.binjn.admin.facerecognizesdk.utils.ImgUtils;
import com.binjn.admin.facerecognizesdk.utils.LogUtils;
import com.binjn.admin.facerecognizesdk.utils.OkHttpUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;

/**
 * Created by admin on 2018/2/2.
 */

public class XiChuHuaYuan {
    private String path_id = "test/xichu_id.txt";
    private String path_img_url = "test/xichu_img_url.txt";

    private  FaceRecognizer faceRecognizer;
    private ArrayList<String> id;
    private ArrayList<String> imgUrl;
    private Message mes;
    private int count = 0;
    private static final String DOWNLOAD_IMG = "DOWNLOAD_IMG";
    private static final String CREATE_FEATURE_VALUE = "CREATE_FEATURE_VALUE";
    private static final String UPLOAD_PHOTO_FEATURE_VALUE = "UPLOAD_PHOTO_FEATURE_VALUE";
    private ArrayList<String> newImgUrl;
    private ArrayList<String> newId;
    private ArrayList<String> fileCategory;
    private String path;
    private ArrayList<String> fileNamePath;
    private int countPath;
    private int countCategory;
    private int indexCategory;
    private ArrayList<String> featureValueFileName;
    private int countFeature;
    private ArrayList<String> newFeatureValueFileName;
    private ArrayList<String> fileName1;


    public XiChuHuaYuan(Context context){
        faceRecognizer = FaceRecognizer.getInstantce(context);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            switch (message){
                case "REGISTER":
                    faceRecognizer.registerFinish("132872285341089823");
                    break;
                case UPLOAD_PHOTO_FEATURE_VALUE:
                    int indexFeature = mes.arg1;
                    if(indexFeature != newFeatureValueFileName.size()){
                        uploadFeatureValueAndPhoto(indexFeature);
                        Log.i("tag","indexFeature=" + indexFeature);
                    }else {
                        countFeature = 0;
                        LogUtils.i("tag","特征值和图片上传完成");
                    }

                    break;
                case DOWNLOAD_IMG:
                    int index = mes.arg1;
                    if(index != newId.size()){
                        downLoadImg(index);
                    }else {
                        count = 0;
                        LogUtils.i("tag","图片下载完成");
                        break;
                    }
                    break;
                case CREATE_FEATURE_VALUE:
                    int indexPath = mes.arg1;
                    Log.i("tag","indexCategory=" + indexCategory);
                    if(indexPath != fileNamePath.size()){
                        Log.i("tag","indexPath=" + indexPath);
                        startFeatureValue(indexPath);
                    }else {
                        indexCategory = mes.arg2;
                        if(indexCategory != fileCategory.size()){
                            getCategoryFile(indexCategory);
                            Log.i("tag","getCategoryFile开始");
                            countPath = 0;
                        }else {
                            countCategory = 0;
                            Log.i("tag","完成");
                        }
                    }
                    break;
            }
        }
    };

    public void uploadData(){
        newFeatureValueFileName = new ArrayList<>();
        String path = "binjnPhoto/featureValue";
        featureValueFileName = FileUtils.getFileName(path);
        for (int i = 0;i < featureValueFileName.size();i ++){
            String s = featureValueFileName.get(i);
            Log.i("tag","featureValueFileName=" + s);
            if(featureValueFileName.get(i).contains(".txt")){
                newFeatureValueFileName.add(s.replace(".txt","").trim());
            }
        }
        //faceRecognizer.registerFinish(newFeatureValueFileName.get(0));
        uploadFeatureValueAndPhoto(0);
    }

    private void uploadFeatureValueAndPhoto(final int index) {
        new Thread(){
            @Override
            public void run() {
                faceRecognizer.registerFinish(newFeatureValueFileName.get(index));
                mes = Message.obtain();
                mes.obj = UPLOAD_PHOTO_FEATURE_VALUE;
                mes.arg1 = ++countFeature;
                handler.sendMessageDelayed(mes,5000);
            }
        }.start();
    }

    /**
     * 生成特征值文件
     */
    public void createFeatureValue(){
        new Thread(){
            @Override
            public void run() {
                path = "test/img";
                fileCategory = FileUtils.getFileName(path, true);
                getCategoryFile(0);
            }
        }.start();
    }

    private void getCategoryFile(int index) {
        //String pathName = path + "/" + "132872285341089977";
        String pathName = path + "/" + fileCategory.get(index);
        Log.i("tag","pathName=" + pathName);
        fileNamePath = FileUtils.getFileName(pathName, false);
        countCategory ++;
        startFeatureValue(0);
    }

    public void getName(){
        path = "binjn16Photo/132872285341089795";
        ArrayList<String> fileName = FileUtils.getFileName(path);
        getFaceIdBytesByFileName(fileName);
    }

    public void register(){
        path = "sdcard/test/img";
        File filePath = new File(path);
        if(!filePath.exists()){
            Log.i("tag","==文件不存在==");
            return;
        }
        Bitmap bm = BitmapFactory.decodeFile(path);
        faceRecognizer.register16FaceFeatures(bm, "132872285341089823", new FaceFeaturesCallback() {
            @Override
            public void facePhoto(String result) {
                Log.i("tag","result=" + result);
                mes = Message.obtain();
                mes.obj = "REGISTER";
                handler.sendMessage(mes);
            }
        });
    }

    private void startFeatureValue(int index) {
        Log.i("tag","index=" + index);
        String path = fileNamePath.get(index);
        Log.i("tag","path=" + path);
        Log.i("tag","indexCategory=" + indexCategory);
        Log.i("tag","fileCategory.get(indexCategory)=" + fileCategory.get(indexCategory));
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        faceRecognizer.register16FaceFeatures(bitmap, fileCategory.get(indexCategory), new FaceFeaturesCallback() {
            @Override
            public void facePhoto(String result) {
                mes = Message.obtain();
                countPath ++;
                mes.arg1 =  countPath;
                if(countPath == fileNamePath.size()){
                    Log.i("tag","countPath=" + countPath);
                    mes.arg2 = countCategory ;
                }
                mes.obj = CREATE_FEATURE_VALUE;
                handler.sendMessage(mes);
            }
        });
    }

    public void detectorFace(){
        new Thread(){
            @Override
            public void run() {
                String path = "test/img";
                //获取文件名
                ArrayList<String> fileName = FileUtils.getFileName(path, true);
                for (int i = 0;i < fileName.size();i ++){
                    Log.i("tag","i==" + i + "  fileName.get(i)" + fileName.get(i));
                    String pathName = path + "/" + fileName.get(i);
                    //获取文件名下的文件路径
                    ArrayList<String> fileName1 = FileUtils.getFileName(pathName, false);
                    for (int j = 0;j < fileName1.size();j ++){
                    Bitmap bitmap = BitmapFactory.decodeFile(fileName1.get(j));
                        if(bitmap == null){
                            Log.i("tag","fileName1.get(j)" + j + "  fileName1.get(j)" + fileName1.get(j));
                            FileUtils.deleteFileFromSDCard(fileName1.get(j));
                            continue;
                        }
                    FaceDetectorBean faceDetectorBean = faceRecognizer.detectorFaces(bitmap);
                    String msgcode = faceDetectorBean.getMsgcode();
                    if("8001".equals(msgcode)){
                        Log.i("tag","j==" + j + "  没有检测到人脸。删除");
                        FileUtils.deleteFileFromSDCard(fileName1.get(j));
                    }else {
                        Log.i("tag","j==" + j + "检测到人脸，继续");
                    }
                    }
                }
            }
        }.start();
    }



    /**
     * 批量修改文件名
     */
    public void modifyFileName(){
        String path = "test/img";
        String path1 = "sdcard/test/img/";
        ArrayList<String> fileName = FileUtils.getFileName(path, true);
        Log.i("tag","fileName.size()=" + fileName.size());
        for (int i = 0;i < fileName.size();i ++){
            Log.i("tag","fileName=" + fileName.get(i));
            File file = new File(path1 + fileName.get(i));
            if(file.isDirectory()){
                String s = path1 + fileName.get(i).substring(0,18);
                file.renameTo(new File(s));
                Log.i("tag","newfileName=" +   s);
            }
            //
            //boolean b = new File(fileName.get(i)).renameTo(new File(fileName.get(i) + ".jpg"));
        }

    }

    public void getFileID(){
        newImgUrl = new ArrayList<>();
        newId = new ArrayList<>();
        id = FileUtils.readFileSD(path_id, 0);
        imgUrl = FileUtils.readFileSD(path_img_url, 0);
        for (int i = 0; i < imgUrl.size(); i ++){
            Log.i("tag","i=" + i);
            Log.i("tag","id=" + id.get(i));
            Log.i("tag","imgUrl=" + imgUrl.get(i));
            if(imgUrl.get(i).contains("http://phs-storage.binjn.com")){
                newImgUrl.add(imgUrl.get(i));
                newId.add(id.get(i));
            }
        }
        Log.i("tag","newImgUrl=" + newImgUrl.size());
        Log.i("tag","newId=" + newId.size());
       //downLoadImg(0);
    }

    private byte[] getFaceIdBytesByFileName(ArrayList<String> fileName) {
        if(fileName == null){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0;i < fileName.size(); i ++){
            if(i != fileName.size() - 1){
                builder.append(fileName.get(i)).append(",");
            }else {
                builder.append(fileName.get(i));
            }
        }
        ;
        LogUtils.i("tag","===" + builder.toString().replaceAll(".jpg",""));
        byte[] bytes = builder.toString().getBytes();
        return bytes;
    }

    private void downLoadImg(final int index) {

        Log.i("tag","请求地址：" + imgUrl.get(index));
        OkHttpUtils.getInstance().downLoadFile2(imgUrl.get(index), "", new OkHttpCallBack2() {
            @Override
            public void onResponse(Response response) {
                try {
                    byte[] bytes = response.body().bytes();
                    LogUtils.e("tag", "bytes：" + bytes.length);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if(bitmap != null){
                        Log.i("tag","id.get(index)=" + newId.get(index));
                        ImgUtils.saveImage(bitmap,newId.get(index));
                        LogUtils.e("tag", "count：第" + count + "张图片保存成功");
                    }
                    mes = Message.obtain();
                    count ++;
                    mes.obj = DOWNLOAD_IMG;
                    mes.arg1 = count;
                    handler.sendMessage(mes);
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.e("tag", "响应错误1：" + e.toString());
                }

            }

            @Override
            public void onFailure(String erorrMsg) {
                LogUtils.e("tag", "响应错误2：" + erorrMsg);
            }
        });
    }
}
