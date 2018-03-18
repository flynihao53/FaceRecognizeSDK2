package com.binjn.admin.facerecognizesdk.face;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.binjn.admin.facerecognizesdk.interfaced.FaceFeaturesCallback;
import com.binjn.admin.facerecognizesdk.interfaced.OkHttpCallBack;
import com.binjn.admin.facerecognizesdk.interfaced.faceSimilarityCallback;
import com.binjn.admin.facerecognizesdk.model.FaceBandPhotoBean;
import com.binjn.admin.facerecognizesdk.model.FaceDetectorBean;
import com.binjn.admin.facerecognizesdk.model.FaceFeatureLib;
import com.binjn.admin.facerecognizesdk.model.FeatureValueBean;
import com.binjn.admin.facerecognizesdk.model.NirFaceFeatureBean;
import com.binjn.admin.facerecognizesdk.model.NirFaceFeaturesBean;
import com.binjn.admin.facerecognizesdk.model.TestBean;
import com.binjn.admin.facerecognizesdk.model.UploadFileBean;
import com.binjn.admin.facerecognizesdk.presenter.XThreadPoolExecutor;
import com.binjn.admin.facerecognizesdk.utils.Base64Utils;
import com.binjn.admin.facerecognizesdk.utils.ByteUtils;
import com.binjn.admin.facerecognizesdk.utils.Constants;
import com.binjn.admin.facerecognizesdk.utils.DateTimeUtils;
import com.binjn.admin.facerecognizesdk.utils.FileUtils;
import com.binjn.admin.facerecognizesdk.utils.HashKitUtils;
import com.binjn.admin.facerecognizesdk.utils.JsonUtils;
import com.binjn.admin.facerecognizesdk.utils.LogUtils;
import com.binjn.admin.facerecognizesdk.utils.NetWorkUtils;
import com.binjn.admin.facerecognizesdk.utils.OkHttpUtils;
import com.binjn.admin.facerecognizesdk.utils.SQLiteUitls;
import com.seetatech.toolchainv3.ToolChain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * Created by wangdajian on 2017/11/28.
 */

public class FaceRecognizer {
    private final static String tag = FaceRecognizer.class.getSimpleName();
    //未检测到身份证照片上的人脸
    private final static String UNDETCCTOR_IDCARD_FACE_NUM = "8004"; //
    //未检测到现场采集(近红外)照片上的人脸
    private final static String UNDETCCTOR_PHOTO_FACE_NUM = "8001";  //
    //识别授权失败
    private final static String AUTHORIZATION_FAILED = "8005";
    //图片上传成功、注册成功
    private final static String UPLOAD_PHOTO_SUCCESS = "8008";
    //图片上传失败
    private final static String UPLOAD_PHOTO_FAIL = "8009";
    //人员绑定人脸特征值上传成功
    private final static String FACE_BINDING_FEATURE_SUCCESS = "8010";
    //人员绑定人脸特征值上传失败
    private final static String FACE_BINDING_FEATURE_FAIL = "8011";
    //识别成功、检测成功
    private final static String FACE_SEARCH_RECOGNIZR_SUCCESS = "0";  //
    //识别失败、匹配失败
    private final static String FACE_SEARCH_RECOGNIZR_FAIL = "8002";  //
    //核验失败
    private final static String FACE_VALIDATE_FAIL = "8003";           //
    //人员存在(该人员未登记)
    private final static String FACE_VALIDATE_UNREGISTER = "8006";     //
    //人证核验成功，刷身份证需要注册
    private final static String FACE_VALIDATE_NEED_REGISTER = "8007";  //
    //检测成功
    private final static String FACE_MATCH_SUCCESS = "Match successfully";
    //检测失败
    private final static String FACE_MATCH_FAIL = "Match fail";
    private final static String FACE_MATCH_FAIL_BTM = "bitmap is null";
    //注册成功
    private final static String FACE_REGISTER_SUCCESS = "REGISTER_SUCCESS";
    //注册失败
    private final static String FACE_REGISTER_FAIL = "REGISTER_FAIL";
    //匹配分数
    private final static String FACE_MATCH_SCORE = "0.000000";
    private final static String FACE_RECOGNIZE_PASS = "10125";
    private final static String FACE_RECOGNIZE_UNPASS = "10126";
    //近红外照片
    private final static String FACE_NIR_PHOTO = "10121";

    //人脸搜索特征值小于3个,搜索数据上传失败
    private static final String SEARCH_FACE_FAILED = "-1";
    private static final String VALUE_SIM = "0.80";     //(搜索接口)
    private static final String VALUE_SIM_IDCARD = "0.2";     //验证接口(身份证和现场照片)
    private  Context context;
    private  Activity activity;
    private Bitmap src_bitmap;
    private ByteBuffer buffer;
    private int[] face_pts;
    private FileOutputStream out;
    //存储接收到的图片路径
    private ArrayList<String> pathList = new ArrayList<>();
    //存储获取的人脸特征值
    private List<String> faceFeattures = new ArrayList<>();

    //存储16个特征值
    ArrayList<byte[]> featureListBytes = new ArrayList<>();

    private final static String PATH16  = Environment.getExternalStorageDirectory()+ "/" +Constants.FILE_16_PHOTO_DIRECTORY +"/";
    private final static String PATH2  = Environment.getExternalStorageDirectory()+ "/" +Constants.FILE_2_PHOTO_DIRECTORY +"/";
    private final static int REGISTER_PHOTO_FINISH = 100;  //图片注册完成

    private ArrayList<String> listIDCards;
    private Bitmap dec_bitmap;
    private ByteBuffer dec_buffer;
    private float sim;
    private SQLiteUitls dbHelper;
    private ArrayList<String> simList;
    private String maxSim;
    private static FaceRecognizer mFaceRecognizer = null;
    private ToolChain toolChain = new ToolChain();
    private boolean isDetectorFace;


    public FaceRecognizer(Context context){
        this.context = context;
        dbHelper = new SQLiteUitls(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        toolChain.Initialize(Constants.modelDir, Constants.faceDetectorModel, Constants.pointDetectorModel, Constants.faceRecognizerModel);
    }
    
    public static FaceRecognizer getInstantce(Context context){
        if(mFaceRecognizer == null){
            synchronized (FaceRecognizer.class){
                mFaceRecognizer = new FaceRecognizer(context);
            }
        }
        return mFaceRecognizer;
    }

    private void setInitParameter() {
        toolChain.SetMinFaceSize(40);
        double[] scoreThresh = {0.62f, 0.47f, 0.985f};
        toolChain.SetScoreThresh(scoreThresh);
        toolChain.SetImagePyramidScaleFactor(1.6f);
    }

    /**
     * 1、检测人脸(获取特征值)---注册流程
     * @param bitmap 外层传入采集的图片，传递10张
     * @param featuresCallback
     */
    public void register16FaceFeatures(final Bitmap bitmap, final String personId, final FaceFeaturesCallback featuresCallback){
        new Thread(){
            @Override
            public void run() {
                if(bitmap == null){
                    LogUtils.i(tag,"传入图片对象为空");
                    return;
                }
                synchronized (FaceRecognizer.class){
                    setInitParameter();
                    src_bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    int bytes = src_bitmap.getByteCount();
                    buffer = ByteBuffer.allocate(bytes);
                    src_bitmap.copyPixelsToBuffer(buffer);     //把图片像素复制到指定的缓冲区
                    byte[] argb = buffer.array();
                    int[] ints = toolChain.FaceDetect(argb, src_bitmap.getWidth(), src_bitmap.getHeight());
                    if(ints == null){
                        LogUtils.i(tag,"注册：传入图片没有检测到人脸");
                        HashMap<String, String> hashMap = getResultMsg(UNDETCCTOR_PHOTO_FACE_NUM, FACE_MATCH_FAIL, FACE_MATCH_SCORE);
                        hashMap.put("personId",personId);
                        String js = JsonUtils.getJsonObjectParam(hashMap);
                        featuresCallback.facePhoto(js);
                        return;
                    }
                    //获取faceId
                    String faceId = getFaceIdFromFaceIDTable();
                    if(TextUtils.isEmpty(faceId)){
                        faceId = "register" + DateTimeUtils.getCurrentTime();
                    }
                    LogUtils.i(tag,"faceId=" + faceId);
                    //保存图片在自己的personId目录下
                    String path = FileUtils.saveBitmapToSDCard(bitmap, personId, Constants.FILE_16_PHOTO_DIRECTORY, faceId);
                    pathList.add(path);
                    buffer.clear();
                    int feat_size = toolChain.GetFeatureSize();
                    float sdkFeatsA[] = new float[feat_size];
                    Bitmap bm = BitmapFactory.decodeFile(path);
                    int byteCount = bm.getByteCount();
                    buffer = ByteBuffer.allocate(byteCount);
                    bm.copyPixelsToBuffer(buffer);
                    byte[] arrayArgb = buffer.array();
                    boolean b = toolChain.ExtractFeature(arrayArgb, bm.getWidth(), bm.getHeight(), sdkFeatsA);
                    if(!b){
                        LogUtils.i(tag,"注册：特征值提取没有检测到人脸");
                        HashMap<String, String> hashMap = getResultMsg(UNDETCCTOR_PHOTO_FACE_NUM, FACE_MATCH_FAIL, FACE_MATCH_SCORE);
                        hashMap.put("personId",personId);
                        String js = JsonUtils.getJsonObjectParam(hashMap);
                        featuresCallback.facePhoto(js);
                        return;
                    }
                    //添加特征值
                    byte[] byteArrays = ByteUtils.getByteArrays(sdkFeatsA);
                    //将字节数组的特征值添加到集合
                    featureListBytes.add(byteArrays);
                    //人员id
                    String person_Id = personId;
                    //将特征值写到文件(不带特征库头文件用于上传)
                    writeFeatureToSD(byteArrays,personId);
                    Log.i("tag","将特征值写到文件(不带特征库头文件用于上传)");
                    //将特征值写到数据库(用于搜索)
                    writeFeatureToDB(personId,byteArrays,faceId);
                    Log.i("tag","将特征值写到数据库(用于搜索)");
                    HashMap<String, String> hashMap3 = getResultMsg(UPLOAD_PHOTO_SUCCESS, FACE_REGISTER_SUCCESS, FACE_MATCH_SCORE);
                    hashMap3.put("personId",personId);
                    String js = JsonUtils.getJsonObjectParam(hashMap3);
                    featuresCallback.facePhoto(js); //(注册成功)
                }
            }
        }.start();
    }

    /**
     * 从数据库中获取一条faceId
     */
    private String getFaceIdFromFaceIDTable() {
        String faceId = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("FaceID", null, null, null, null, null, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return null;
        }
        while (cursor.moveToNext()){
            String isUse = cursor.getString(cursor.getColumnIndex("isUse"));
            if("否".equals(isUse)){
                faceId = cursor.getString(cursor.getColumnIndex("faceId"));
                break;
            }
        }
        ContentValues values = new ContentValues();
        values.put("isUse","是");
        db.update("FaceID",values,"faceId=?",new String[]{faceId});
        cursor.close();
        db.close();
        return faceId;
    }

    /**
     * 注册完成时调用
     * 1、上传特征值文件 2、上传注册时的图片 3、删除注册时的特征值文件和照片
     * @param IDCardNo 本次注册的身份证号
     */
    public void registerFinish(String IDCardNo){
        try {
        //1、根据身份证号获取对应人员ID
        String personId = getPersonIdByIDCard(IDCardNo);
            saveFeatureValueHashCodeToDB(personId);
        /*if(featureListBytes.size() == 0){
            byte[] bytes = FileUtils.toByteArray(personId, Constants.FILE_PHOTO_DIRECTORY, Constants.DIR_FEATURE_VALUE);
            saveFeatureValueHashCodeToDB(bytes,personId);
        }else {
            saveFeatureValueHashCodeToDB(featureListBytes,personId);
        }*/
        LogUtils.i(tag,"personId=" + personId);
        byte[] faceFeatureLib = null;
        //获取特征值
        byte[] featureValues = FileUtils.toByteArray(personId,Constants.FILE_PHOTO_DIRECTORY,Constants.DIR_FEATURE_VALUE);
         int index = 0;
        if(featureValues != null){
            index = featureValues.length / 8192;
            LogUtils.i(tag,"特征值个数index：" + index);
        }
        //获得特征库头文件
        if(featureListBytes != null && featureListBytes.size() > 0){
             faceFeatureLib = FaceFeatureLib.getFaceFeatureLib(featureListBytes.get(0).length, index);
        }else {
             faceFeatureLib = FaceFeatureLib.getFaceFeatureLib(8192, index);
        }
        //用于遍历注册16张照片的地址
        String path = Constants.FILE_16_PHOTO_DIRECTORY + "/"+ personId;
        ArrayList<String> fileName = FileUtils.getFileName(path);
        LogUtils.i(tag,"fileName：" + fileName.size());
        //faceId
        byte[] faceIdBytes = getFaceIdBytesByFileName(fileName);
        //bytes = 特征库头结构 + 特征值
        byte[] bytes = ByteUtils.addBytes(faceFeatureLib, featureValues);
        LogUtils.i(tag,"bytes=" + bytes.length);
        //newBytes = bytes(特征库头结构 + 特征值) + faceId(16个)
        byte[] newBytes = ByteUtils.addBytes(bytes,faceIdBytes);
        LogUtils.i(tag,"newBytes=" + newBytes.length);
        //4、将特征值写到文件
        FileUtils.getFileFromBytes(newBytes,Constants.FILE_FEATURE_VALUE);
        String PATH = FileUtils.readFileSD(Constants.FILE_HTTP_ADDRESS);
        String type = FACE_NIR_PHOTO; //近红外照片类型
        //特征值地址
        String url = PATH + Constants.URLPERSONBANDFACEFEATURE(personId,type);
        LogUtils.i(tag,"特征值上传地址：" + url);
        String token = FileUtils.readFileSD(Constants.FILE_TOKEN);
        LogUtils.i(tag,"特征值上传token：" + token);
        featureListBytes.clear();
        boolean networkConnected = NetWorkUtils.isNetworkConnected(context);
        if(!networkConnected)  {
            LogUtils.i(tag,"网络为断开状态，不上传文件");
            pathList.clear();
            return;
        }
        //上传特征值文件
        uploadBindingFeatureValue(personId,token,url);
        //图片地址
        String imgUrl = PATH + Constants.URLREGISTER16PHOTO(personId);
        //6、上传图片
        if(pathList != null && pathList.size() > 0){
            uploadRegister16Photo(personId,imgUrl,token,pathList);
        }else {
            //从本地获得路径上传图片
            pathList = FileUtils.getFileName(path, false);
            if(pathList != null && pathList.size() > 0){
                uploadRegister16Photo(personId,imgUrl,token,pathList);
            }
        }
        pathList.clear();
    }catch (Exception e){
        LogUtils.i(tag,"registerFinish_注册完成接口错误信息：" + e.toString());
    }
    }

    /**
     * 将获取到的文件名称(faceID)转换为字节数组
     * @param fileName
     * @return
     */
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
        byte[] bytes = builder.toString().replaceAll(".jpg","").getBytes();
        return bytes;
    }


    /**
     * 上传注册的16张照片(文件)
     * @param personId
     * @param url
     * @param token
     * @param pathList
     */
    private void uploadRegister16Photo(final String personId, String url,  String token, final ArrayList<String> pathList) {
        LogUtils.i(tag,"上传注册的16张照片(文件)");
        OkHttpUtils.getInstance().upLoadPhotoFile(url, token, pathList, new UploadFileBean(), new OkHttpCallBack() {
            @Override
            public void onSuccessed(Object obj) {
                //注册图片上传成功后更新本地数据库
                LogUtils.i(tag,"注册图片上传成功，对应personId=" + personId);
                pathList.clear();
                //删除注册时的图片
                String path = Constants.FILE_16_PHOTO_DIRECTORY + "/" + personId;
                FileUtils.deleteDirAndFile(path);
            }

            @Override
            public void onFailed(String str) {
                pathList.clear();
                LogUtils.i(tag,"注册图片上传失败，对应personId=" + personId);
            }
        });
    }

    /**
     * 根据人员ID删除数据库注册16张照片的state字段
     * @param personId
     */
    private void delRegisterPhotoFeature(String personId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("RegisterPhotoFeature","personId=?",new String[]{personId});
        db.close();
    }

    /**
     * 根据personId查询注册图片的个数
     * @param personId
     * @return
     */
    private int queryRecordImgNumFromDB(String personId) {
        int count = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("RegisterPhotoFeature",null,"personId=?",new String[]{personId},null,null,null);
        if(cursor.getCount() <= 0){
            return 0;
        }
        while (cursor.moveToNext()){
            String uploadState = cursor.getString(cursor.getColumnIndex("uploadState"));
            if("否".equals(uploadState)){
                count ++;
            }
        }
        db.close();
        return count;
    }

    /**
     * 将注册时获得的特征值保存到数据库
     * @param personId 人员ID
     * @param featureValue 人员ID对应特征值
     * @param faceId
     */
    private void writeFeatureToDB(String personId, byte[] featureValue, String faceId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("personId",personId);
        values.put("featureValue",featureValue);
        values.put("faceId",faceId);
        db.insert("FaceFeature",null,values);
        db.close();
    }

    /**
     * 注册上传特征值(文件)
     * @param token
     * @param url
     */
    private void uploadBindingFeatureValue(final String personId, final String token, final String url) {
        String path = FileUtils.getFilePathFromSDCard(Constants.FILE_FEATURE_VALUE);
        if(TextUtils.isEmpty(path)){
            LogUtils.i(tag,"文件并不存在===");
            return;
        }
        final File file = new File(path);
        OkHttpUtils.getInstance().upLoadFile(url, token, file, new FaceBandPhotoBean(), new OkHttpCallBack() {
            @Override
            public void onSuccessed(Object obj) {
                LogUtils.i(tag,"注册上传特征值(文件)成功");
                //删除特征值文件(带特征库的文件)
                FileUtils.clearInfoForFile(Constants.FILE_FEATURE_VALUE);
                //删除特征值文件(不带特征库)
                String pathHead = Environment.getExternalStorageDirectory()+ "/";
                String path = pathHead + Constants.FILE_PHOTO_DIRECTORY + "/" + Constants.DIR_FEATURE_VALUE
                        + "/" + personId + ".txt";
                //FileUtils.deleteFileFromSDCard(path);
            }

            @Override
            public void onFailed(String str) {
                LogUtils.i(tag,"上传特征值失败，返回信息：" + str);
                //删除特征值文件(带特征库的文件)
                FileUtils.clearInfoForFile(Constants.FILE_FEATURE_VALUE);
            }
        });
    }

    /**
     *将注册16个特征值拼接后的hashcode保存到数据库
     * @param
     * @param personId  人员Id
     */
    private void saveFeatureValueHashCodeToDB(String personId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor faceFeatureCursor = db.query("FaceFeature", null, "personId=?", new String[]{personId}, null, null, null);
        int count = faceFeatureCursor.getCount();
        if(count <= 0){
            return;
        }
        ArrayList<byte[]> list = new ArrayList<>();
        byte[] bytes = new byte[count*8192];
        while (faceFeatureCursor.moveToNext()){
            byte[] featureValues = faceFeatureCursor.getBlob(faceFeatureCursor.getColumnIndex("featureValue"));
            list.add(featureValues);

        }
        for (int i = 0;i < list.size();i ++){
            System.arraycopy(list.get(i),0,bytes,i*8192,8192);
        }
        String featureValueHashCode = HashKitUtils.md5(HashKitUtils.toHex(bytes));
        LogUtils.i(tag,"featureValueHashCode=" + featureValueHashCode);
        ContentValues values = new ContentValues();
        values.put("hashcode",featureValueHashCode);
        values.put("personId",personId);

        Cursor fhcCursor = db.query("FaceHashCode", null, "personId=?", new String[]{personId}, null, null, null);
        if(fhcCursor.getCount() > 0){
            LogUtils.i(tag,"personId存在，update==");
            db.update("FaceHashCode",values,"personId=?", new String[]{personId});
        }else {
            LogUtils.i(tag,"personId不存在，add==");
            db.insert("FaceHashCode",null,values);
        }
        fhcCursor.close();
        db.close();
        LogUtils.i(tag,"特征值插入FaceHashCode完成");
    }

    private void saveFeatureValueHashCodeToDB(byte[] bytes, String personId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String featureValueHashCode = HashKitUtils.md5(HashKitUtils.toHex(bytes));
        ContentValues values = new ContentValues();
        values.put("hashcode",featureValueHashCode);
        values.put("personId",personId);

        Cursor fhcCursor = db.query("FaceHashCode", null, "personId=?", new String[]{personId}, null, null, null);
        if(fhcCursor.getCount() > 0){
            LogUtils.i(tag,"personId存在，update");
            db.update("FaceHashCode",values,"personId=?", new String[]{personId});
        }else {
            LogUtils.i(tag,"personId不存在，add");
            db.insert("FaceHashCode",null,values);
        }

        fhcCursor.close();
        db.close();
        LogUtils.i(tag,"特征值插入FaceHashCode完成");
    }

    /**
     * 将特征值写到文件
     * @param byteArrays 一个特征值的字节数组
     * @param personId
     */
    private void writeFeatureToSD(byte[] byteArrays, String personId) {
        FileUtils.writeFeatureToSD(byteArrays,personId,Constants.FILE_PHOTO_DIRECTORY,Constants.DIR_FEATURE_VALUE);
    }

    /**
     * 判断身份证号是否是同一个人
     * 通过传入的身份证和从服务器上下载保存在本地获取的身份证号比较
     * @param IDCard 传入身份证号
     * @return ture:属于未注册 false:已注册
     */
    public boolean validatePersonsIDCard(String IDCard){
        listIDCards = new ArrayList<>();
        //listIDCards = FileUtils.readFileSD(Constants.FILE_UNPERSON_IDCARD, 0);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("UnRegisterPerson", null, null, null, null, null, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
            while (cursor.moveToNext()){
            String idCardNo = cursor.getString(cursor.getColumnIndex("idCard"));
            listIDCards.add(idCardNo);
        }
        cursor.close();
        db.close();
        if(listIDCards == null){
            return false;
        }
        for(String card : listIDCards){
            if(card.equals(IDCard)){
                return true;
            }
        }
        return false;
    }

    /**
     * 通过传入的身份证号和门禁设备管理人员的身份证号比较，并返回对应的personId
     * @param IDCard 传入身份证号
     * @return
     */
    public String getPersonIdByIDCard(String IDCard){
        //还需要获取未注册近红外人脸的人员信息列表的id和下面的数据库比较么
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("DevicePerson", null, null, null, null, null, null);
        if(cursor.getCount() <= 0){
            return null;
        }
        String personId = "";
        String idNo = "";
        while (cursor.moveToNext()){
            idNo = cursor.getString(cursor.getColumnIndex("idNo"));
            if(IDCard.equals(idNo)){
                personId = cursor.getString(cursor.getColumnIndex("personId"));
                break;
            }
        }
        cursor.close();
        return personId;
    }
    /**
     * 验证流程
     *1、人证核验
     * @param idcardBmp 身份证上的照片图像
     * @param decBmp 现场采集的近红外照片图像
     * @return
     */
    public void validatePersonsPhoto(final Bitmap idcardBmp, final Bitmap decBmp, final String idCardNo, final faceSimilarityCallback faceSimilarityCallback){
        new Thread(){
            @Override
            public void run() {
                synchronized (FaceRecognizer.class){
                    ArrayList<Bitmap> bitmapList = new ArrayList<>();
                    bitmapList.add(idcardBmp);   //第一张是身份证照片
                    bitmapList.add(decBmp);      //现场采集照片
                    //ArrayList<String> list = FileUtils.saveBitmapToSDCard(bitmapList, Constants.FILE_2_PHOTO_DIRECTORY);
                    setInitParameter();
                    String simIdcard = FileUtils.readFileSD(Constants.FILE_SIM_IDCARD);
                    if("".equals(simIdcard)){
                        FileUtils.writeFileSDcard(Constants.FILE_SIM_IDCARD,VALUE_SIM_IDCARD); //0.2
                    }
                    //身份证照片
                    src_bitmap = idcardBmp.copy(Bitmap.Config.ARGB_8888, true);
                    //现场采集照片
                    dec_bitmap = decBmp.copy(Bitmap.Config.ARGB_8888, true);
                    int bytes = src_bitmap.getByteCount();
                    int dec_bytes = dec_bitmap.getByteCount();
                    buffer = ByteBuffer.allocate(bytes);
                    dec_buffer = ByteBuffer.allocate(dec_bytes);
                    //把图片像素复制到指定的缓冲区
                    src_bitmap.copyPixelsToBuffer(buffer);
                    dec_bitmap.copyPixelsToBuffer(dec_buffer);
                    byte[] argb = buffer.array();
                    byte[] dec_argb = dec_buffer.array();
                    //获得特征维度
                    //int feat_size = toolChain.GetFeatureSize();
                    sim = toolChain.CalcSimilarityWithTwoImages(argb, src_bitmap.getWidth(), src_bitmap.getHeight(),
                            dec_argb, dec_bitmap.getWidth(), dec_bitmap.getHeight());
                    if(sim == -1.0){
                        HashMap<String, String> hashMap = getResultMsg(UNDETCCTOR_PHOTO_FACE_NUM, FACE_MATCH_FAIL, FACE_MATCH_SCORE);
                        String js = JsonUtils.getJsonObjectParam(hashMap);
                        faceSimilarityCallback.faceSimilarity(js);
                        return ;
                    }
                    LogUtils.i(tag,"sim=" + sim);
                    String fileSim = FileUtils.readFileSD(Constants.FILE_SIM_IDCARD);
                    float aFloatSim = Float.parseFloat(fileSim);
                    if(sim > aFloatSim){
                        boolean isRegister = personIsRegister(idCardNo);
                        if(isRegister){
                            //需要注册
                            HashMap<String, String> hashMap = getResultMsg(FACE_VALIDATE_NEED_REGISTER, FACE_MATCH_SUCCESS, String.valueOf(FaceRecognizer.this.sim));
                            String js = JsonUtils.getJsonObjectParam(hashMap);
                            faceSimilarityCallback.faceSimilarity(js);
                        }else {
                            //返回提示信息(该人员未登记)
                            HashMap<String, String> hashMap = getResultMsg(FACE_VALIDATE_UNREGISTER, FACE_MATCH_SUCCESS, String.valueOf(FaceRecognizer.this.sim));
                            String js = JsonUtils.getJsonObjectParam(hashMap);
                            faceSimilarityCallback.faceSimilarity(js);
                        }
                    }else {
                        //核验失败
                        HashMap<String, String> hashMap = getResultMsg(FACE_VALIDATE_FAIL, FACE_MATCH_FAIL, FACE_MATCH_SCORE);
                        String js = JsonUtils.getJsonObjectParam(hashMap);
                        faceSimilarityCallback.faceSimilarity(js);
                    }
                    bitmapList.clear();
                }
            }
        }.start();
    }

    /**
     * 返回信息(公共字段)
     * @param msgcode
     * @param msg
     * @param matchscore
     * @return
     */

    private HashMap<String,String> getResultMsg(String msgcode,String msg,String matchscore){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("msgcode",msgcode);
        hashMap.put("msg",msg);
        hashMap.put("matchscore",matchscore);
        return hashMap;
    }

    /**
     * 验证流程
     * 2、是否需要注册
     * @param idCardNo 身份证号
     * @return ture:需要注册  false:该人员不在待注册列表
     */
    public boolean personIsRegister(String idCardNo){
        boolean isRegister = validatePersonsIDCard(idCardNo);
        return isRegister;
    }

    /**
     * 人脸检测
     * @param bitmap 传入图片
     * @return
     */
    private FaceDetectorBean detectorBean = new FaceDetectorBean();
    public FaceDetectorBean detectorFaces(Bitmap bitmap){
        //FaceDetectorBean detectorBean = new FaceDetectorBean();
        setInitParameter();
        Bitmap src_bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int bytes = src_bitmap.getByteCount();
        buffer = ByteBuffer.allocate(bytes);
        //把图片像素复制到指定的缓冲区
        src_bitmap.copyPixelsToBuffer(buffer);
        byte[] argb = buffer.array();
        face_pts = toolChain.FaceDetect(argb, src_bitmap.getWidth(), src_bitmap.getHeight());
        if(face_pts != null){
            detectorBean.setMsgcode(FACE_SEARCH_RECOGNIZR_SUCCESS);
            detectorBean.setMsg("检测成功");
        }else {
            detectorBean.setMsgcode(UNDETCCTOR_PHOTO_FACE_NUM);
            detectorBean.setMsg("图像中没有检测到人脸");
        }
        detectorBean.setFace(face_pts);
        return detectorBean;
    }

    // 获取CPU数量
    int processors = Runtime.getRuntime().availableProcessors();
    //核心线程数量
    int corePoolSize =processors + 1;
   //最大线程数量
    int maximumPoolSize =  processors * 2 + 1;
    //空闲有效时间
    long keepAliveTime = 60;
    private ExecutorService threadPoolExecutor = new XThreadPoolExecutor(
            corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>());
    /**
     * 搜索流程(人脸搜索)
     * @param liveBmp 现场采集图片(Bitmap ---> byte[])
     * @return
     */
    public void searchPersonsPhoto(final Bitmap liveBmp, final faceSimilarityCallback faceSimilarityCallback){
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                startFaceRecognize(liveBmp,faceSimilarityCallback);
            }
        });
    }


    private void startFaceRecognize(Bitmap liveBmp, faceSimilarityCallback faceSimilarityCallback) {
        try {
            synchronized (FaceRecognizer.class){
                if(liveBmp == null){
                    HashMap<String, String> hashMap1 = getResultMsg(FACE_SEARCH_RECOGNIZR_FAIL, FACE_MATCH_FAIL_BTM, maxSim);
                    hashMap1.put("matchperson","");
                    String js1 = JsonUtils.getJsonObjectParam(hashMap1);
                    faceSimilarityCallback.faceSimilarity(js1);
                    return;
                }
                LogUtils.i(tag,"开始人脸识别");
                simList = new ArrayList<>();
                setInitParameter();
                //现场采集照片
                long start = System.currentTimeMillis();
                //修改
                String path = FileUtils.saveBitmapToSDCard(liveBmp, Constants.FILE_PHOTO_DIRECTORY_STRANGER,Constants.FILE_PHOTO_DIRECTORY,false);
                LogUtils.i(tag,"时间："+ (System.currentTimeMillis() - start) + "ms" );
                LogUtils.i(tag,"传入对比图片路径：" + path);
                src_bitmap =  BitmapFactory.decodeFile(path);
                int bytes = src_bitmap.getByteCount();
                buffer = ByteBuffer.allocate(bytes);
                src_bitmap.copyPixelsToBuffer(buffer);
                byte[] argb = buffer.array();
                //获得特征维度
                int feat_size = toolChain.GetFeatureSize();
                float sdkFeatsA[] = new float[feat_size];
                LogUtils.i(tag,"ExtractFeature---start---sdkFeatsA=" + sdkFeatsA.length);
                try {
                    isDetectorFace = toolChain.ExtractFeature(argb, src_bitmap.getWidth(), src_bitmap.getHeight(), sdkFeatsA);
                    LogUtils.i(tag,"ExtractFeature---end");
                }catch (Exception e){
                    HashMap<String, String> hashMap = getResultMsg(UNDETCCTOR_PHOTO_FACE_NUM, FACE_MATCH_FAIL, FACE_MATCH_SCORE);
                    hashMap.put("matchperson","");
                    String js = JsonUtils.getJsonObjectParam(hashMap);
                    faceSimilarityCallback.faceSimilarity(js);
                    LogUtils.i(tag,"搜索接口特征值提取错误：" + e.toString());
                    return ;
                }
                if(!isDetectorFace){
                    HashMap<String, String> hashMap = getResultMsg(UNDETCCTOR_PHOTO_FACE_NUM, FACE_MATCH_FAIL, FACE_MATCH_SCORE);
                    hashMap.put("matchperson","");
                    String js = JsonUtils.getJsonObjectParam(hashMap);
                    faceSimilarityCallback.faceSimilarity(js);
                    return ;
                }
                LogUtils.i(tag,"搜索---对比流程---获取本地的近红外特征值");
                //搜索---对比流程---获取本地的近红外特征值
                //记录相似度及其对应的人员Id
                HashMap<Float,NirFaceFeaturesBean> hm = new HashMap<>();
                //记录相似度
                ArrayList<Float> listRecordFeatureLocal = new ArrayList<>();
                //获取近红外人员特征值
                HashMap<float[], NirFaceFeaturesBean> nirFaceFeatures = getNirFaceFeatures();
                if(nirFaceFeatures == null){
                    HashMap<String, String> hashMap1 = getResultMsg(FACE_SEARCH_RECOGNIZR_FAIL, FACE_MATCH_FAIL, maxSim);
                    String js1 = JsonUtils.getJsonObjectParam(hashMap1);
                    faceSimilarityCallback.faceSimilarity(js1);
                    LogUtils.i(tag,"获取近红外人员特征值为null");
                    return;
                }
                Iterator<Map.Entry<float[], NirFaceFeaturesBean>> iterator = nirFaceFeatures.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<float[], NirFaceFeaturesBean> next = iterator.next();
                    float[] sdkFeatsB = next.getKey();
                    //计算两个特征之间的相似度
                    sim = toolChain.CalcSimilarity(sdkFeatsA, sdkFeatsB);
                    if(sim > 0.80){
                        simList.add(String.valueOf(sim));
                    }
                    listRecordFeatureLocal.add(sim);
                    hm.put(sim,next.getValue());
                }
                LogUtils.i(tag,"获得门禁Id");
                Collections.sort(listRecordFeatureLocal,Collections.reverseOrder());
                //获得门禁Id
                HashMap<String,String> hashMap = new HashMap<>();
                String lockId = FileUtils.readFileSD(Constants.DEVICE_ID);
                LogUtils.i(tag,"lockId=" + lockId);
                //识别结果
                String result = "";
                if(simList.size() > 0){
                    result = FACE_RECOGNIZE_PASS;   //通过
                }else {
                    result = FACE_RECOGNIZE_UNPASS;  //未通过
                }
                hashMap.put("result",result);
                LogUtils.i(tag,"result=" + result);
                //String personIdTemp = "";
                //int simPersonCount = 0;
                if(listRecordFeatureLocal.size() >= 3){
                    for (int i = 0;i < 3;i ++){
                        NirFaceFeaturesBean nirFace = hm.get(listRecordFeatureLocal.get(i));
                        hashMap.put("similarPersonId" + (i + 1),nirFace.getPersonId());
                        hashMap.put("similarFaceId" + (i + 1),nirFace.getFaceId());
                        hashMap.put("score" + (i + 1),String.valueOf(listRecordFeatureLocal.get(i)));
                        LogUtils.i(tag,"similarPersonId" + (i + 1) + "==========" + nirFace.getPersonId() + ", faceId=" + nirFace.getFaceId());
                        LogUtils.i(tag,"score" + (i + 1) + "==========" + String.valueOf(listRecordFeatureLocal.get(i)));
                    }
                }else {
                    HashMap<String, String> hashMap1 = getResultMsg(SEARCH_FACE_FAILED, FACE_MATCH_FAIL, FACE_MATCH_SCORE);
                    hashMap1.put("matchperson","");
                    String js = JsonUtils.getJsonObjectParam(hashMap1);
                    faceSimilarityCallback.faceSimilarity(js);
                    return;
                }
                String simIdcard = FileUtils.readFileSD(Constants.FILE_SIM);
                if("".equals(simIdcard)){
                    FileUtils.writeFileSDcard(Constants.FILE_SIM,VALUE_SIM); //0.80
                }
                //获取当前时间
                String currentSystemTime = DateTimeUtils.getCurrentSystemTime();
                //现场抓拍照片base64编码的二进制图片数据
                //String codeBase64 = Base64Utils.enCodeBase64(argb);
                hashMap.put("captureTime",currentSystemTime);
                hashMap.put("liveImg1","");
                String token = FileUtils.readFileSD(Constants.FILE_TOKEN);
                String PATH = FileUtils.readFileSD(Constants.FILE_HTTP_ADDRESS);
                String url = PATH + Constants.URLSAVEDEVICEFACERECORD(lockId);
                LogUtils.i(tag,"url=" + url);
                LogUtils.i(tag,"hashMap=" + hashMap.size());
                //将结果返回给调用者
                if(simList.size() > 0){
                    maxSim = Collections.max(simList);
                }else {
                    ArrayList<String> list = getSimStringFromFloat(listRecordFeatureLocal);
                    maxSim = Collections.max(list);
                    list.clear();
                }
                simList.clear();
                float maxValue = Float.valueOf(maxSim);
                LogUtils.i(tag,"maxValue=" + maxValue);
                String fileSim = FileUtils.readFileSD(Constants.FILE_SIM);
                float aFloatSim = Float.parseFloat(fileSim);
                NirFaceFeaturesBean nirFaceFeaturesBean = hm.get(maxValue);
                if(maxValue > aFloatSim){  //这个值待定
                    //识别成功
                    LogUtils.i(tag,"------识别成功，开门---------");
                    HashMap<String, String> hashMap1 = getResultMsg(FACE_SEARCH_RECOGNIZR_SUCCESS, FACE_MATCH_SUCCESS, maxSim);
                    hashMap1.put("matchperson", nirFaceFeaturesBean.getPersonId());
                    String js1 = JsonUtils.getJsonObjectParam(hashMap1);
                    faceSimilarityCallback.faceSimilarity(js1);
                }else {
                    //识别失败
                    LogUtils.i(tag,"=====识别失败，不开门=======");
                    //String personId = hm.get(maxValue);
                    HashMap<String, String> hashMap1 = getResultMsg(FACE_SEARCH_RECOGNIZR_FAIL, FACE_MATCH_FAIL, maxSim);
                    hashMap1.put("matchperson",nirFaceFeaturesBean.getPersonId());
                    String js1 = JsonUtils.getJsonObjectParam(hashMap1);
                    faceSimilarityCallback.faceSimilarity(js1);
                }
                //将对比结果上传服务器
               // uploadFaceSearchData(url,hashMap,token,path);
            }
        }catch (Exception e){
            LogUtils.i(tag,"搜索接口错误信息：" + e.toString());
            String personId = "";
            HashMap<String, String> hashMap1 = getResultMsg(FACE_SEARCH_RECOGNIZR_FAIL, FACE_MATCH_FAIL, maxSim);
            hashMap1.put("matchperson",personId);
            String js1 = JsonUtils.getJsonObjectParam(hashMap1);
            faceSimilarityCallback.faceSimilarity(js1);
        }
    }

    /**
     * 删除特征值文件
     */
    public void delFeatureValueFileFromSD(){
        FileUtils.clearInfoForFile(Constants.FILE_FEATURE_VALUE);
    }

    private ArrayList<String> getSimStringFromFloat(ArrayList<Float> listRecordFeatureLocal) {
        if(listRecordFeatureLocal == null){
            return null;
        }
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0;i < listRecordFeatureLocal.size();i ++){
            list.add(String.valueOf(listRecordFeatureLocal.get(i)));
        }
        return list;
    }

    private boolean getSim(float[] sdkFeatsA) {
        ArrayList<FeatureValueBean> faceFeatureList = getNirFaceFeature();
        if(faceFeatureList != null && faceFeatureList.size() == 0){
            return true;
        }
        //如果是hashMap呢
        for (int i = 0;i < faceFeatureList.size(); i ++){
            FeatureValueBean featureValueBean = faceFeatureList.get(i);
            ArrayList<float[]> featureValueBeanList = featureValueBean.getList();
            for (int j = 0;j < featureValueBeanList.size();j ++){
                float[] sdkFeatsB = featureValueBeanList.get(j);
                //计算两个特征之间的相似度
                sim = toolChain.CalcSimilarity(sdkFeatsA, sdkFeatsB);
            }
        }
        return false;
    }

    private void uploadFaceSearchData(String url, HashMap<String, String> hashMap, String token, final String path) {
        ArrayList<File> fileAccrodFilePath = FileUtils.getFileAccrodFilePath(path);
        ArrayList<String> filePaths = new ArrayList<>();
        for (int i = 0;i < fileAccrodFilePath.size(); i ++){
            filePaths.add(fileAccrodFilePath.get(i).getPath());
        }
        OkHttpUtils.getInstance().upLoadFilesForDataUpload(hashMap, url, token, filePaths, new TestBean(), new OkHttpCallBack() {
            @Override
            public void onSuccessed(Object obj) {
                LogUtils.i(tag,"搜索上传成功");
                FileUtils.deleteFileFromSDCard(path);
            }

            @Override
            public void onFailed(String str) {
                LogUtils.i(tag,"搜索上传失败");
            }
        });

    }


    /**
     * 获取近红外人脸特征值
     * @return ArrayList集合对象
     */
    private ArrayList<FeatureValueBean> getNirFaceFeature() {
        ArrayList<FeatureValueBean> featureLists = new ArrayList<>();
        ArrayList<float[]> byteList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("FaceFeature", null, null, null, null, null, null);
        if(cursor.getCount() <= 0){
            return null;
        }
        byte[] bytes = new byte[4];
        while (cursor.moveToNext()){
            FeatureValueBean featureValue = new FeatureValueBean();
            String personId = cursor.getString(cursor.getColumnIndex("personId"));
            byte[] featureValues = cursor.getBlob(cursor.getColumnIndex("featureValue"));
            float[] floats = new float[featureValues.length / 4];
            for (int i = 0;i < featureValues.length / 4;i ++){
                System.arraycopy(featureValues,4 * i,bytes,0,4);
                float aFloat = ByteUtils.bytesToFloat(bytes);
                floats[i] = aFloat;
            }
            byteList.add(floats);
            featureValue.setPersonId(personId);
            featureValue.setList(byteList);
            featureLists.add(featureValue);
        }
        cursor.close();
        return featureLists;
    }

    /**
     * 在Activity的OnDestory方法中调用
     */
    public void destory(){
        toolChain.Destroy();
    }

    /**
     * 获取近红外人脸特征值
     * @return hashMap集合
     */
    private HashMap<float[],NirFaceFeaturesBean> getNirFaceFeatures() {
        HashMap<float[],NirFaceFeaturesBean> hashMaps = new HashMap<>();

        //HashMap<float[],String> hashMap = new HashMap<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("FaceFeature", null, null, null, null, null, null);
        if(cursor.getCount() <= 0){
            return null;
        }
        LogUtils.i(tag,"cursor.getCount()=" + cursor.getCount());
        byte[] bytes = new byte[4];
        while (cursor.moveToNext()){
            NirFaceFeaturesBean nirFaceFeatures = new NirFaceFeaturesBean();
            String personId = cursor.getString(cursor.getColumnIndex("personId"));
            String faceId = cursor.getString(cursor.getColumnIndex("faceId"));
            byte[] featureValues = cursor.getBlob(cursor.getColumnIndex("featureValue"));
            float[] floats = new float[featureValues.length / 4];
            for (int i = 0;i < featureValues.length / 4;i ++){
                //一个浮点数为4个字节
                System.arraycopy(featureValues,4 * i,bytes,0,4);
                float aFloat = ByteUtils.bytesToFloat(bytes);
                floats[i] = aFloat;
            }
            nirFaceFeatures.setPersonId(personId);
            nirFaceFeatures.setFaceId(faceId);
            hashMaps.put(floats,nirFaceFeatures);
            //hashMap.put(floats,personId);
        }
        cursor.close();
        LogUtils.i(tag,"hashMap.size()=" + hashMaps.size());
        return hashMaps;
    }

    /**
     * 对获取近红外人脸特征值进行base64解码
     *@param faceFeatureList base64编码的特征值集合
     *@return 解码后的特征值集合数组
     */
    private ArrayList<float[]> decryptedFeatureValue(List<NirFaceFeatureBean.FeatureBean> faceFeatureList) {
        ArrayList<float[]> floatList = new ArrayList<>();
        for (int i = 0;i < faceFeatureList.size();i ++){
            NirFaceFeatureBean.FeatureBean featureBean = faceFeatureList.get(i);
            String value = featureBean.getValue();
            String[] values = value.split(",");
            float[] sdkFeatsB = new float[2048];
            for(int j = 0;j < values.length;j ++){
                byte[] bytes = Base64Utils.deCodeBase64(values[j]);
                sdkFeatsB[j] = ByteUtils.bytesToFloat(bytes);
            }
            floatList.add(sdkFeatsB);
        }
        return floatList;
    }

    /**
     * 通过flag的状态判断本次大门是否关闭---预留接口
     * (大门关闭之后才能开始下一次人脸搜索)
     * @param flag 大门关闭状态
     * @return
     */
    public boolean doorIsCloase(boolean flag){
        return flag;
    }

    /**
     * int[] -> float[]
     * @param pointDetect
     * @return
     */
    private float[] Int2Float(int[] pointDetect) {
        float[] temps = new float[pointDetect.length];
        for (int i = 0;i < pointDetect.length; i ++){
            temps[i] = pointDetect[i];
        }
        return temps;
    }


    public float compareTwoPhoto(byte[] imgs1,int w1,int h1,byte[] imgs2,int w2,int h2){
        float sim = toolChain.CalcSimilarityWithTwoImages(imgs1, w1, h1, imgs2, w2, h2);
        return sim;
    }

}
