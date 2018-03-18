package com.binjn.admin.facerecognizesdk.net;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.binjn.admin.facerecognizesdk.face.FaceRecognizerAutherity;
import com.binjn.admin.facerecognizesdk.interfaced.OkHttpCallBack;
import com.binjn.admin.facerecognizesdk.interfaced.OkHttpCallBack2;
import com.binjn.admin.facerecognizesdk.model.CreateFaceIdBean;
import com.binjn.admin.facerecognizesdk.model.DeviceIdBean;
import com.binjn.admin.facerecognizesdk.model.DevicePersonBean;
import com.binjn.admin.facerecognizesdk.model.DeviceUnRegisterBean;
import com.binjn.admin.facerecognizesdk.model.LocalHashCodeBean;
import com.binjn.admin.facerecognizesdk.model.ServerHashCodeBean;
import com.binjn.admin.facerecognizesdk.utils.ByteUtils;
import com.binjn.admin.facerecognizesdk.utils.Constants;
import com.binjn.admin.facerecognizesdk.utils.FastUtils;
import com.binjn.admin.facerecognizesdk.utils.FileUtils;
import com.binjn.admin.facerecognizesdk.utils.HashKitUtils;
import com.binjn.admin.facerecognizesdk.utils.JsonUtils;
import com.binjn.admin.facerecognizesdk.utils.LogUtils;
import com.binjn.admin.facerecognizesdk.utils.MacAddressUtils;
import com.binjn.admin.facerecognizesdk.utils.OkHttpUtils;
import com.binjn.admin.facerecognizesdk.utils.SQLiteUitls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Response;

/**
 * Created by wangdajian on 2017/11/29.
 */

public class RequestData {
    private static final String tag = RequestData.class.getSimpleName();
    private static final String LOCK_DEVICE_PERSON_LIST = "LOCK_DEVICE_PERSON_LIST"; //设备人员管理列表
    private static final String REGISTER_DEVICE_ID = "REGISTER_DEVICE_ID";           //使用系统ID生成器生成ID
    private static final String UNREGISTER_PERSON_LIST = "UNREGISTER_PERSON_LIST";   //未注册人员列表
    private static final String NIR_FACE_FEATURE = "NIR_FACE_FEATURE";                //获取近红外人脸特征值
    private static final String FACE_FEATURE_IS_CHANGED = "FACE_FEATURE_IS_CHANGED"; //判断人脸特征值是否发生变化
    private final static String DEVICE_UNREGISTER = "DEVICE_UNREGISTER";              //设备未注册
    private final static int DEVICE_UNREGISTER_VALUE = 8012;                         //设备未注册
    private final static int DEVICE_REGISTER_VALUE = 8013;                           //设备已注册
    //初始化设备人员管理列表失败
    private static final String INIT_LOCK_DEVICE_PERSON_LIST_FAILED = "INIT_LOCK_DEVICE_PERSON_LIST_FAILED";
    //初始化未注册人员列表失败
    private static final String INIT_UNREGISTER_PERSON_LIST_FAILED = "INIT_UNREGISTER_PERSON_LIST_FAILED";
    //初始化获取近红外人员特征值失败
    private static final String INIT_NIR_FACE_FEATURE_FAILED = "INIT_NIR_FACE_FEATURE_FAILED";
    //初始化门禁机注册失败
    private static final String INIT_DEVICE_UNREGISTER_FAILED = "INIT_DEVICE_UNREGISTER_FAILED";
    //获取生成的faceId失败
    private static final String INIT_CREATE_FACE_ID_FAILED = "INIT_CREATE_FACE_ID_FAILED";

    private  Activity activity;
    private DeviceIdBean deviceId;
    private DevicePersonBean persons;

    private String url;
    private ArrayList<String> list;
    private int count = 0;  //用于获取特征值
    private int changeCount = 0; //用于获取改变的特征值

    private Message msg;
    private SQLiteUitls dbHelper = null;
    private String personId;
    private ArrayList<String> personList;
    private ArrayList<String> hashcode;
    private InitReusltListener listeners = null;
    private static RequestData mRequestData = null;
    private DeviceRegisterListener deviceRegisterListener = null;
    private ArrayList<String> listPersonId;
    private byte[] featureValues;
    private String featureValuesMD5;

    public RequestData(Activity activity){
        this.activity = activity;
        dbHelper = new SQLiteUitls(activity,Constants.DATABASE_NAME,null,Constants.DATABASE_VERSION);
    }

    public static RequestData getInstantce(Activity activity){
        if(mRequestData == null){
            synchronized (RequestData.class){
                mRequestData = new RequestData(activity);
            }
        }
        return mRequestData;
    }

    public void initData(){
        FaceRecognizerAutherity.getInstance(activity).requestAPIAuthentication();
    }
    /**
     * 门禁机注册接口(获得门禁机的id)
     */
    public void getDeviceId(){
        final String macAddress = MacAddressUtils.getMacStr();
        String token = FileUtils.readFileSD(Constants.FILE_TOKEN);
        final HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("mac",macAddress);
        String js = JsonUtils.getJsonObjectParam(hashMap);
        LogUtils.i(tag,"js=" + js);
        String PATH = FileUtils.readFileSD(Constants.FILE_HTTP_ADDRESS);
        if("".equals(PATH)){
            LogUtils.i(tag,"请求地址不存在" );
            return;
        }
        String url =PATH + Constants.URLDevRegister(macAddress);
        LogUtils.i(tag,"门禁机注册接口url=" + url);
        OkHttpUtils.getInstance().requestJson(url, "", token,new DeviceIdBean(), new OkHttpCallBack() {
            @Override
            public void onSuccessed(final Object obj) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deviceId = (DeviceIdBean) obj;
                        if(!TextUtils.isEmpty(deviceId.getId()) && deviceId.getCode().equals("0")){
                            String deviceID = deviceId.getId();
                            LogUtils.i(tag,"门禁机ID=" + deviceID);
                            FileUtils.writeFileSDcard(Constants.DEVICE_ID,deviceID);
                            Message msg = Message.obtain();
                            msg.obj = LOCK_DEVICE_PERSON_LIST;
                            handler.sendMessage(msg);
                        }
                    }
                });
            }

            @Override
            public void onFailed(final String str) {
                if(str.equals("APIKEY不存在")){
                    msg = Message.obtain();
                    msg.obj = DEVICE_UNREGISTER;  //说明该设备未在平台注册
                    handler.sendMessage(msg);
                }else {
                    msg = Message.obtain();
                    msg.obj = INIT_DEVICE_UNREGISTER_FAILED; //初始化门禁机注册失败
                    handler.sendMessage(msg);
                }
            }
        });
    }

    /**
     * 使用系统ID生成器生成ID
     */
    public void getCreateFaceId(){
        String token = FileUtils.readFileSD(Constants.FILE_TOKEN);
        String PATH = FileUtils.readFileSD(Constants.FILE_HTTP_ADDRESS);
        if("".equals(PATH)){
            LogUtils.i(tag,"请求地址不存在" );
            return;
        }
        String url = Constants.CREATE_FACE_ID(PATH);
        OkHttpUtils.getInstance().requestHeadGet(url, token,new CreateFaceIdBean(), new OkHttpCallBack() {
            @Override
            public void onSuccessed(Object obj) {
                CreateFaceIdBean createFaceIdBean= (CreateFaceIdBean) obj;
                List<String> idList = createFaceIdBean.getId();
                insertFaceIdToDB(idList);
                msg = Message.obtain();
                msg.obj = REGISTER_DEVICE_ID;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailed(String str) {
                msg = Message.obtain();
                msg.obj = INIT_CREATE_FACE_ID_FAILED;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 将faceId插入到DB的FaceId表
     * @param idList
     */
    private void insertFaceIdToDB(List<String> idList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "insert into FaceID(faceId,isUse)values(?,?)";
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransaction();
        for (int i = 0;i < idList.size();i++){
            stat.bindString(1, idList.get(i));
            stat.bindString(2, "否");
            stat.executeInsert();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * 获取门禁设备管理的人员列表
     */
    /*public void getDevicePersons(){
        String lockId = "";
        if(deviceId != null && !TextUtils.isEmpty(deviceId.getId())){
            lockId = deviceId.getId();
        }else {
            lockId = FileUtils.readFileSD(Constants.DEVICE_ID);
        }
        String token = FileUtils.readFileSD(Constants.FILE_TOKEN);
        String PATH = FileUtils.readFileSD(Constants.FILE_HTTP_ADDRESS);
        if("".equals(PATH)){
            return;
        }
        String url =PATH + Constants.URLPersons(lockId);
        String resultUrl = url + "?pageSize=" + 9999 + "&lockId=" + lockId;
        LogUtils.i(tag,"resultUrl=" + resultUrl);
        LogUtils.i(tag,"token=" + token);
        OkHttpUtils.getInstance().requestHeadGet(resultUrl, token,new DevicePersonBean(), new OkHttpCallBack() {
            @Override
            public void onSuccessed(final Object obj) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //获取人员ID(本地数据)
                ArrayList<String> personInfos = getPersonInfoFromDevicePerson(db);
                persons = (DevicePersonBean) obj;
                //服务器数据
                List<DevicePersonBean.PersonsBean> person = persons.getPersons();
                LogUtils.i(tag,"person=" + person.size());
                if(person != null && person.size() > 0){
                    for (int i = 0;i < person.size();i++){
                        ContentValues values = new ContentValues();
                        String personId = person.get(i).getId();
                        String idNo = person.get(i).getIdNo();
                        values.put("personId",personId);
                        values.put("idNo",idNo);
                        values.put("hashcode",person.get(i).getHashcode());
                        String serverPersonId = person.get(i).getId();
                        boolean isExist = searchPersonIdFromDevicePerson(serverPersonId,personInfos);
                        if(isExist){
                            db.update("DevicePerson",values,"personId = ?",new String[]{personId});
                            LogUtils.i(tag,"更新门禁设备管理的人员列表成功1/本地和服务器都有该人员ID,更新");
                        }else {
                            db.insert("DevicePerson",null,values);
                            LogUtils.i(tag,"添加门禁设备管理的人员列表成功2/本地没有该人员ID而服务器有,添加");
                        }
                    }
                    if(personInfos != null && personInfos.size() > 0){
                        for (int i = 0;i < personInfos.size();i ++){
                            String localPersonId = personInfos.get(i);
                            boolean isExist = searchPersonIdFromServer(localPersonId,person);
                            if(!isExist){
                                db.delete("DevicePerson","personId = ?",new String[]{localPersonId});
                                LogUtils.i(tag,"删除门禁设备管理的人员列表成功3/本地有该人员ID而服务器没有,删除");
                            }
                        }
                    }
                }else {
                    //test1
                    if(personInfos != null && personInfos.size() >= 0){
                        db.delete("DevicePerson",null,null);
                        LogUtils.i(tag,"删除门禁设备管理的人员列表成功4/本地有该人员ID而服务器没有,删除");
                    }
                }
                db.close();

                msg = Message.obtain();
                msg.obj = UNREGISTER_PERSON_LIST; //未注册人员列表
                handler.sendMessage(msg);
            }

            @Override
            public void onFailed(final String str) {
                LogUtils.i(tag,"返回错误信息：" +str);
                msg = Message.obtain();
                msg.obj = INIT_LOCK_DEVICE_PERSON_LIST_FAILED; //未注册人员列表
                handler.sendMessage(msg);
            }
        });
    }*/

    /**
     * 获取门禁设备管理的人员列表(修改)
     */
    public void getDevicePersons(){
        String lockId = "";
        if(deviceId != null && !TextUtils.isEmpty(deviceId.getId())){
            lockId = deviceId.getId();
        }else {
            lockId = FileUtils.readFileSD(Constants.DEVICE_ID);
        }
        String token = FileUtils.readFileSD(Constants.FILE_TOKEN);
        String PATH = FileUtils.readFileSD(Constants.FILE_HTTP_ADDRESS);
        if("".equals(PATH)){
            return;
        }
        String url =PATH + Constants.URLPersons(lockId);
        String resultUrl = url + "?pageSize=" + 9999 + "&lockId=" + lockId;
        LogUtils.i(tag,"resultUrl=" + resultUrl);
        LogUtils.i(tag,"token=" + token);
        OkHttpUtils.getInstance().requestHeadGet(resultUrl, token,new DevicePersonBean(), new OkHttpCallBack() {
            @Override
            public void onSuccessed(final Object obj) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //获取人员ID(本地数据)
                ArrayList<String> personInfos = getPersonInfoFromDevicePerson(db);
                persons = (DevicePersonBean) obj;
                //服务器数据
                List<DevicePersonBean.PersonsBean> person = persons.getPersons();
                //获取本地待注册列表身份证号
                ArrayList<String> list = getDeviceUnRegisterPersonsFromUnRPerson(db);
                LogUtils.i(tag,"person=" + person.size());
                if(person != null && person.size() > 0){
                    for (int i = 0;i < person.size();i++){
                        ContentValues values = new ContentValues();
                        String personId = person.get(i).getId();
                        String idNo = person.get(i).getIdNo();
                        String name = person.get(i).getName();
                        values.put("personId",personId);
                        values.put("idNo",idNo);
                        values.put("hashcode",person.get(i).getHashcode());
                        String serverPersonId = person.get(i).getId();
                        boolean isExist = searchPersonIdFromDevicePerson(serverPersonId,personInfos);
                        if(isExist){
                            db.update("DevicePerson",values,"personId = ?",new String[]{personId});
                            LogUtils.i(tag,"更新门禁设备管理的人员列表成功1/本地和服务器都有该人员ID,更新");
                        }else {
                            db.insert("DevicePerson",null,values);
                            LogUtils.i(tag,"添加门禁设备管理的人员列表成功2/本地没有该人员ID而服务器有,添加");
                        }
                        //=================待注册列表==start=================//
                        boolean isExistId = searchPersonIdFromUnRegisterPersons(personId,list);
                        boolean nirFaceRegistered = person.get(i).isNirFaceRegistered();
                        ContentValues unRegisterValues = new ContentValues();
                        unRegisterValues.put("idCard",idNo);
                        unRegisterValues.put("idNo",personId);
                        unRegisterValues.put("name",name);
                        if(isExistId){
                            //存在，未注册
                            if(!nirFaceRegistered){
                                db.update("UnRegisterPerson",unRegisterValues,"idNo = ?",new String[]{personId});
                                LogUtils.i(tag,"UnRegisterPerson=服务器和本地都有，且未注册，更新");
                            }else {
                                //存在，已注册
                                db.delete("UnRegisterPerson","idNo=?",new String[]{personId});
                                LogUtils.i(tag,"UnRegisterPerson=服务器和本地都有，已注册，删除");
                            }
                        }else {
                            //不存在，添加
                            if(!nirFaceRegistered){
                                db.insert("UnRegisterPerson",null,unRegisterValues);
                                LogUtils.i(tag,"UnRegisterPerson=服务器有，本地没有，未注册,添加");
                            }
                        }
                    }
                    if(list != null && list.size() > 0){
                        for (int i = 0;i < list.size();i ++){
                            String localUngisterPersonId = list.get(i);
                            boolean isExist = checkLocalUnregisterPersonIdIsServerExist(localUngisterPersonId,person);
                            if(!isExist){
                                //不存在，删除
                                db.delete("UnRegisterPerson","idNo=?",new String[]{localUngisterPersonId});
                                LogUtils.i(tag,"UnRegisterPerson=不存在，删除");
                            }
                        }
                    }
                  //=================待注册列表===end==================//
                    if(personInfos != null && personInfos.size() > 0){
                        for (int i = 0;i < personInfos.size();i ++){
                            String localPersonId = personInfos.get(i);
                            boolean isExist = searchPersonIdFromServer(localPersonId,person);
                            if(!isExist){
                                db.delete("DevicePerson","personId = ?",new String[]{localPersonId});
                                LogUtils.i(tag,"删除门禁设备管理的人员列表成功3/本地有该人员ID而服务器没有,删除");
                            }
                        }
                    }
                }else {
                    //test1
                    if(personInfos != null && personInfos.size() >= 0){
                        db.delete("DevicePerson",null,null);
                        LogUtils.i(tag,"删除门禁设备管理的人员列表成功4/本地有该人员ID而服务器没有,删除");
                    }
                    if(list != null && list.size() > 0){
                        //数据为空，删除
                        db.delete("UnRegisterPerson",null,null);
                        LogUtils.i(tag,"服务器待注册列表返回为空，删除本地所有待注册人员");
                    }
                }
                db.close();
                getNirFaceFeatures(0);
                /*msg = Message.obtain();
                msg.obj = UNREGISTER_PERSON_LIST; //未注册人员列表
                handler.sendMessage(msg);*/
            }

            @Override
            public void onFailed(final String str) {
                LogUtils.i(tag,"返回错误信息：" +str);
                msg = Message.obtain();
                msg.obj = INIT_LOCK_DEVICE_PERSON_LIST_FAILED; //未注册人员列表
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 本地待注册人员列表中的personId在服务器中是否存在
     * @param localUngisterPersonId
     * @param person
     * @return
     */
    private boolean checkLocalUnregisterPersonIdIsServerExist(String localUngisterPersonId, List<DevicePersonBean.PersonsBean> person) {
        boolean isExist = false;
        for (int i = 0;i < person.size();i ++){
            if(localUngisterPersonId.equals(person.get(i).getId())){
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    /**
     * 从服务器返回的数据中查找当前获取本地数据库传递进来的personID是否存在
     * @param localPersonId
     * @param person
     * @return
     */
    private boolean searchPersonIdFromServer(String localPersonId, List<DevicePersonBean.PersonsBean> person) {
        boolean existFlag = false;
        for(int i = 0;i < person.size();i ++){
            if(localPersonId.equals(person.get(i).getId())){
                existFlag = true;
                break;
            }
        }
        return existFlag;
    }

    /**
     * 从本地数据库中查找当前获取服务器中传递进来的personID是否存在
     * @param serverPersonId
     * @param personInfos
     * @return
     */
    private boolean searchPersonIdFromDevicePerson(String serverPersonId, ArrayList<String> personInfos) {
        if(personInfos == null){
            return false;
        }
        boolean existFlag = false;
        for(int i = 0;i < personInfos.size();i ++){
            if(serverPersonId.equals(personInfos.get(i))){
                existFlag = true;
                break;
            }
        }
        return existFlag;
    }

    /**
     * 从设备管理人员列表中获取人员ID
     * 主要用于再次初始化时和服务端的人员ID比较
     * @param db
     * @return
     */
    private ArrayList<String> getPersonInfoFromDevicePerson(SQLiteDatabase db) {
        listPersonId = new ArrayList<>();
        Cursor cursor = db.query("DevicePerson", null, null, null, null, null, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return null;
        }
        while (cursor.moveToNext()){
            String personId = cursor.getString(cursor.getColumnIndex("personId"));
            //String idNo = cursor.getString(cursor.getColumnIndex("idNo"));
            listPersonId.add(personId);
        }
        cursor.close();
        return listPersonId;
    }

    /**
     * 获取未注册近红外人脸的人员信息列表
     */
    public void getDeviceUnRegisterPersons(){
        String lockId = "";
        if(deviceId != null && !TextUtils.isEmpty(deviceId.getId())){
            lockId = deviceId.getId();
        }else {
            lockId = FileUtils.readFileSD(Constants.DEVICE_ID);
        }
        String token = FileUtils.readFileSD(Constants.FILE_TOKEN);
        String PATH = FileUtils.readFileSD(Constants.FILE_HTTP_ADDRESS);
        if("".equals(PATH)){
            return;
        }
        String url =PATH + Constants.URLDevUnRegister(lockId);
        LogUtils.i(tag,"url=" + url);
        OkHttpUtils.getInstance().requestHeadGet(url, token, new DeviceUnRegisterBean(), new OkHttpCallBack() {
            @Override
            public void onSuccessed(final Object obj) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DeviceUnRegisterBean deviceUnRegister = (DeviceUnRegisterBean) obj;
                        List<DeviceUnRegisterBean.DataBean> data = deviceUnRegister.getData();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        if(data != null && data.size() > 0){
                            //获取本地待注册人员ID
                            ArrayList<String> list = getDeviceUnRegisterPersonsFromUnRPerson(db);
                            //保存到数据库UnRegisterPerson
                            for(int i = 0;i < data.size();i ++){
                                DeviceUnRegisterBean.DataBean dataBean = data.get(i);
                                String name = dataBean.getName();
                                String id = dataBean.getId();
                                String idCard = dataBean.getIdCard();
                                ContentValues values = new ContentValues();
                                values.put("idCard",idCard);
                                values.put("idNo",id);
                                values.put("name",name);
                                //搜索服务器中的人员ID在本地数据库中是否存在
                                boolean isExist = searchPersonIdFromUnRegisterPersons(id,list);
                                if(isExist){
                                    //存在，更新
                                    db.update("UnRegisterPerson",values,"idNo = ?",new String[]{id});
                                    LogUtils.i(tag,"UnRegisterPerson=存在，更新");
                                }else {
                                    //不存在，添加
                                    db.insert("UnRegisterPerson",null,values);
                                    LogUtils.i(tag,"UnRegisterPerson=不存在，添加");
                                }
                            }
                            //搜索本地数据库中的人员ID在服务器数据中是否存在
                            if(list != null && list.size() > 0){
                                for (int i = 0;i < list.size();i ++){
                                    String localUngisterPersonId = list.get(i);
                                    boolean isExist = searchPersonIdFromServerUngisterPersons(localUngisterPersonId,data);
                                    if(!isExist){
                                        //不存在，删除
                                        db.delete("UnRegisterPerson","idNo=?",new String[]{localUngisterPersonId});
                                        LogUtils.i(tag,"UnRegisterPerson=不存在，删除");
                                    }
                                }
                            }
                        }else {
                            //数据为空，删除
                            db.delete("UnRegisterPerson",null,null);
                            LogUtils.i(tag,"服务器待注册列表返回为空，删除本地所有待注册人员");
                        }
                        db.close();
                        getNirFaceFeatures(0);
                    }
                });
            }

            @Override
            public void onFailed(final String str) {
                LogUtils.i(tag,"获取未注册人员返回错误信息：" + str);
                msg = Message.obtain();
                msg.obj = INIT_UNREGISTER_PERSON_LIST_FAILED; //初始化未注册人员列表失败
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 检测本地中的人员ID在服务器数据中是否存在
     * @param localUngisterPersonId 本地待注册人员ID
     * @param data 服务器待注册人员数据
     * @return
     */
    private boolean searchPersonIdFromServerUngisterPersons(String localUngisterPersonId,
                                                              List<DeviceUnRegisterBean.DataBean> data) {
        boolean isExist = false;
        for (int i = 0;i < data.size();i ++){
            if(localUngisterPersonId.equals(data.get(i).getId())){
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    /**
     *服务器返回的待注册人员ID和本地数据库中的待注册人员ID比较是否存在
     * @param serPersonId 服务器人员ID
     * @param list 本地数据库
     * @return
     */
    private boolean searchPersonIdFromUnRegisterPersons(String serPersonId, ArrayList<String> list) {
        boolean isExist = false;
        if(list == null){
            return false;
        }
        for (int i = 0;i < list.size(); i ++){
            if(serPersonId.equals(list.get(i))){
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    /**
     * 从未注册人员列表中获取待注册人员ID
     * @param db
     * @return
     */
    private ArrayList<String> getDeviceUnRegisterPersonsFromUnRPerson(SQLiteDatabase db) {
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = db.query("UnRegisterPerson", null, null, null, null, null, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return null;
        }
        while (cursor.moveToNext()){
            String personId = cursor.getString(cursor.getColumnIndex("idNo"));
            list.add(personId);
        }
        cursor.close();
        return list;
    }

    private boolean initResult = false;

    public Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String message = (String) msg.obj;
                switch (message) {
                    case REGISTER_DEVICE_ID:         //使用系统ID生成器生成ID--------第1步
                        LogUtils.i(tag,"开始获取门禁ID");
                        LogUtils.i(tag,"使用系统ID生成器生成Id的解析结束时间：" + System.currentTimeMillis());
                        getDeviceId();
                        break;
                    case LOCK_DEVICE_PERSON_LIST:   //第二步返回成功
                        if(deviceRegisterListener != null){
                            deviceRegisterListener.onDeviceIsRegister(DEVICE_REGISTER_VALUE);
                        }
                        getDevicePersons();             //获取门禁机设备管理人员列表---第3步
                        break;
                    case UNREGISTER_PERSON_LIST:
                        //getDeviceUnRegisterPersons();  //获取近红外未注册人员信息列表--第4步
                        break;
                    case NIR_FACE_FEATURE:           //获取近红外人员特征值----------第5步
                        int count1 = msg.arg1;
                        if (list != null && (count1 < list.size())) {
                            LogUtils.i(tag, "count1 =" + count1);    //count1第一次已经执行了
                            getNirFaceFeatures(count1);
                        } else {
                            count = 0;
                            initResult = true;
                            if(listeners != null){
                                listeners.onInitResultBack(initResult);
                            }
                            LogUtils.i(tag, "初始化完成");
                            return;
                        }
                        break;
                    case FACE_FEATURE_IS_CHANGED:   //判断特对应人员ID特征值是否发生变化
                        int count2 = msg.arg1;
                        if(count2 < personList.size()){
                            LogUtils.i(tag, "count2 =" + count2);
                            getNirFaceFeatures(count2,personList,hashcode);
                        }else {
                            changeCount = 0;
                            return;
                        }
                        break;
                    case DEVICE_UNREGISTER:     //初始化门禁机注册失败(设备未注册)
                        if(deviceRegisterListener != null){
                            deviceRegisterListener.onDeviceIsRegister(DEVICE_UNREGISTER_VALUE);
                        }
                        break;
                    case INIT_DEVICE_UNREGISTER_FAILED: //初始化门禁机注册失败(设备已注册)
                        if(listeners != null){
                            listeners.onInitResultBack(false);
                        }
                        break;
                    case INIT_LOCK_DEVICE_PERSON_LIST_FAILED: //初始化设备人员管理列表失败
                        if(listeners != null){
                            listeners.onInitResultBack(false);
                        }
                        break;
                    case INIT_UNREGISTER_PERSON_LIST_FAILED: //初始化未注册人员列表失败
                        if(listeners != null){
                            listeners.onInitResultBack(false);
                        }
                        break;
                    case INIT_NIR_FACE_FEATURE_FAILED:      //初始化获取近红外人员特征值失败
                        if(listeners != null){
                            listeners.onInitResultBack(false);
                        }
                        break;
                    case INIT_CREATE_FACE_ID_FAILED:      //初始化获取生成的faceId失败
                        if(listeners != null){
                            listeners.onInitResultBack(false);
                        }
                        break;
                }
            }
        };


    /**
     * 监听初始化结果
     */
    public interface InitReusltListener{
        void onInitResultBack(boolean initReuslt);
    }
    public void setInitReusltListener(InitReusltListener listener) {
        this.listeners = listener;
    }
    /**
     * 监听设备注册结果
     */
    public interface DeviceRegisterListener{
        void onDeviceIsRegister(int registerResult);
    }
    public void setDeviceRegisterListener(DeviceRegisterListener deviceRegisterListener){
        this.deviceRegisterListener = deviceRegisterListener;
    }

    /**
     * 获取人员近红外特征值(单个人员)
     */
    public void getNirFaceFeatures(final int index){
        if(list == null || list.size() == 0){
            //从数据库中获取人员Id(DevicePerson表)
            list =getPersonIdFromDB();
            if(list != null){
                LogUtils.i(tag,"从数据库中获取人员Id大小：" + list.size());
            }else {
                SQLiteDatabase db1 = dbHelper.getWritableDatabase();
                db1.delete("FaceFeature",null,null);
                db1.close();
                count ++;
                msg = Message.obtain();
                msg.obj = NIR_FACE_FEATURE;
                msg.arg1 = count;
                handler.sendMessage(msg);
                return;
            }
        }
        final String token = FileUtils.readFileSD(Constants.FILE_TOKEN);
        String PATH = FileUtils.readFileSD(Constants.FILE_HTTP_ADDRESS);
        if("".equals(PATH)){
            return;
        }
        personId = list.get(index);
        LogUtils.i(tag,"list.size()=" + list.size());
        LogUtils.i(tag,"单个人员ID=" + list.get(index));
        //获取hashcode---测试
        boolean personIdHashcode = getPersonIdHashcode(list.get(index));
        LogUtils.i(tag,"特征值是否需要重新下载isEqual=" + personIdHashcode + " ，true:不需要 false:需要");
        if(personIdHashcode){
            count ++;
            msg = Message.obtain();
            msg.obj = NIR_FACE_FEATURE;
            msg.arg1 = count;
            handler.sendMessage(msg);
            return;
        }
        url = PATH + Constants.URLNIRFACEFRETURE(list.get(index));
        LogUtils.i(tag,"获取人员近红外特征值url=" + url);
        OkHttpUtils.getInstance().downLoadFile(url, token, new OkHttpCallBack2() {
            @Override
            public void onResponse(Response response) {
                try {
                    boolean personIdIsExist = false;
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    //从数据库的FaceFeature表中获取人员ID
                    ArrayList<String> listPersonId = getPersonIdFromFaceFeature(db);
                    if(listPersonId != null && listPersonId.size() > 0){
                        for (int i = 0;i < listPersonId.size();i ++){
                            String personIdFromFaceFeature = listPersonId.get(i);
                            if(personIdFromFaceFeature.equals(list.get(index))){
                                //更新特征值
                                personIdIsExist = true;
                                Log.i(tag,"personIdIsExist=" + personIdIsExist);
                                break;
                            }
                        }
                    }
                    byte[] bytes = response.body().bytes();
                    byte[] featureValueNum = new byte[4];
                    byte[] featureValueLength = new byte[4];

                    System.arraycopy(bytes,Constants.getFeatureLibLength(0),featureValueNum,0,4);
                    System.arraycopy(bytes,Constants.getFeatureLibLength(1),featureValueLength,0,4);
                    //特征个数
                    int faceNum = ByteUtils.bytesToInt(featureValueNum);
                    //单个特征值的长度
                    int faceFaetureLengths = ByteUtils.bytesToInt(featureValueLength);
                    byte[] destFeatureValue = new byte[faceFaetureLengths];
                    //faceId
                    int faceIdLength = bytes.length - (Constants.getFeatureLibLength(2) + faceNum * faceFaetureLengths);
                    LogUtils.i(tag,"bytes = " + bytes.length);
                    LogUtils.i(tag,"faceIdLength = " + faceIdLength);
                    int temp = Constants.getFeatureLibLength(2) + faceNum * faceFaetureLengths;
                    LogUtils.i(tag,"faceid之前的长度 = " + temp);
                    byte[] faceId = new byte[faceIdLength];
                    System.arraycopy(bytes,temp,faceId,0,faceIdLength);
                    String[] faceIdNums = new String(faceId).split(",");
                    if(faceIdNums.length != faceNum){
                        LogUtils.i(tag,"特征值个数和faceId个数不相等");
                    }
                    boolean isDelete = false;
                    LogUtils.i(tag,"faceIdNums = " + faceIdNums.length);
                    LogUtils.i(tag,"faceNum = " + faceNum);
                    LogUtils.i(tag,"faceFaetureLengths = " + faceFaetureLengths);
                    for(int i = 0;i < faceNum;i ++){
                            ContentValues values = new ContentValues();
                            System.arraycopy(bytes,Constants.getFeatureLibLength(2) + faceFaetureLengths * i,destFeatureValue,0,faceFaetureLengths);
                            values.put("personId",personId);
                            values.put("faceId",faceIdNums[i]);
                            values.put("featureValue",destFeatureValue);
                        try {
                                //db.beginTransaction();
                                if(personIdIsExist){
                                    //db.update("FaceFeature",values,"personId = ?",new String[]{personId});
                                    if(!isDelete){
                                        Log.i(tag,"存在,第一次存在，删除isDelete=" + isDelete);
                                        db.delete("FaceFeature","personId = ?",new String[]{personId});
                                        isDelete = true;
                                    }
                                    Log.i(tag,"存在，更新特征值");
                                    db.insert("FaceFeature",null,values);
                                   // db.setTransactionSuccessful();
                                }else {
                                    db.insert("FaceFeature",null,values);
                                    Log.i(tag,"不存在，插入特征值");
                                }
                           // db.endTransaction();
                        }catch (Exception e){
                                Log.i(tag,"特征值操作错误");
                            }
                    }
                    //test3
                    if(faceNum == 0){
                        if(listPersonId != null && listPersonId.size() > 0){
                            db.delete("FaceFeature","personId = ?",new String[]{personId});
                        }
                    }
                    db.close();
                    LogUtils.i(tag,"===========获取人员近红外特征值完成===============");
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.i(tag,"获取特征值解析数据错误：" + e.toString());
                }
                //============循环请求数据============//
                count ++;
                msg = Message.obtain();
                msg.obj = NIR_FACE_FEATURE;
                msg.arg1 = count;
                handler.sendMessage(msg);

            }

            @Override
            public void onFailure(String erorrMsg) {
                LogUtils.i(tag,"获取特征值返回错误信息：" +erorrMsg);
                msg = Message.obtain();
                msg.obj = INIT_NIR_FACE_FEATURE_FAILED;
                handler.sendMessage(msg);
            }
        });
        }

    private boolean getPersonIdHashcode(String personId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String DevicePerson_hashcode = "";
        String FaceHashCode_hashcode = "";
        boolean isEqual = false;
        Cursor dpCursor = db.query("DevicePerson", null, "personId=?", new String[]{personId}, null, null, null);
        if(dpCursor.getCount() <= 0){
            dpCursor.close();
            return false;
        }
        while (dpCursor.moveToNext()){
            DevicePerson_hashcode = dpCursor.getString(dpCursor.getColumnIndex("hashcode"));
        }
        dpCursor.close();
        ContentValues values = new ContentValues();
        values.put("personId",personId);
        values.put("hashcode",DevicePerson_hashcode);
        Cursor fhcCursor = db.query("FaceHashCode", null, "personId=?", new String[]{personId}, null, null, null);
        if(fhcCursor.getCount() > 0){
            while (fhcCursor.moveToNext()){
                FaceHashCode_hashcode = dpCursor.getString(fhcCursor.getColumnIndex("hashcode"));
            }
            if(DevicePerson_hashcode.equals(FaceHashCode_hashcode)){
                isEqual = true;
            }
            db.update("FaceHashCode",values,"personId=?", new String[]{personId});
        }else {
            db.insert("FaceHashCode",null,values);
        }

        fhcCursor.close();
        db.close();

        return isEqual;
    }

    /**
     * 从FaceFeature表中获取人员ID
     * @param db
     */
    private ArrayList<String> getPersonIdFromFaceFeature(SQLiteDatabase db) {
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = db.query("FaceFeature", null, null, null, null, null, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return null;
        }
        while (cursor.moveToNext()){
            String personId = cursor.getString(cursor.getColumnIndex("personId"));
            list.add(personId);
        }
        cursor.close();
        return list;
    }

    /**
     * 从数据库中获取人员Id
     * @return
     */
    private ArrayList<String> getPersonIdFromDB() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("DevicePerson", null, null, null, null, null, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return null;
        }
        String personId = "";
        while (cursor.moveToNext()){
             personId = cursor.getString(cursor.getColumnIndex("personId"));
            list.add(personId);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 获取人员近红外特征值
     * (该接口用于每次初始化时判断特征值的hashcode是否发生变化)
     */
    public void getNirFaceFeatures(final int index, ArrayList<String> personList, final ArrayList<String> hashCode){
        String token = FileUtils.readFileSD(Constants.FILE_TOKEN);
        String PATH = FileUtils.readFileSD(Constants.FILE_HTTP_ADDRESS);
        if("".equals(PATH)){
            return;
        }
        personId = personList.get(index);
        url = PATH + Constants.URLNIRFACEFRETURE(personList.get(index));
        LogUtils.i(tag,"获取人员近红外特征值url=" + url);
        OkHttpUtils.getInstance().downLoadFile(url, token, new OkHttpCallBack2() {
            @Override
            public void onResponse(Response response) {
                try {
                    String str = response.body().string();
                    LogUtils.e(tag, "响应结果：" + str);
                    //========================================//
                    byte[] bytes = response.body().bytes();
                    byte[] featureValueNum = new byte[4];
                    byte[] featureValueLength = new byte[4];
                    System.arraycopy(bytes,463,featureValueNum,0,4);
                    System.arraycopy(bytes,468,featureValueLength,0,4);
                    //特征值数量
                    int faceNum = ByteUtils.bytesToInt(featureValueNum);
                    //单个特征值的长度
                    int faceFaetureLengths = ByteUtils.bytesToInt(featureValueLength);
                    //将所有特征值拼接在一起
                    byte[] allFeatures = new byte[bytes.length - 472];
                    for (int i = 472;i < bytes.length;i ++){
                        allFeatures[i] = bytes[i];
                    }
                    String featureHashCode = HashKitUtils.md5(HashKitUtils.toHex(allFeatures));
                    //重新获取特征值的hashcode和之前的一致，保存更新
                    if(featureHashCode.equals(hashCode.get(index))){
                        byte[] destFeatureValue = new byte[faceFaetureLengths];
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        //先将原来的ID对应的行删除---1
                        //db.delete("FaceFeature","personId = ?",new String[]{personId});
                        for(int i = 0;i < faceNum;i ++){
                            ContentValues values = new ContentValues();
                            System.arraycopy(bytes,472 + faceFaetureLengths * i,destFeatureValue,0,faceFaetureLengths);
                            values.put("personId",personId);
                            values.put("featureValue",destFeatureValue);
                            //然后再重新插入---2
                            //db.insert("featureValue",null,values);
                            db.update("FaceFeature",values,"personId = ?",new String[]{personId});
                            LogUtils.i(tag,"更新FaceFeature表中的特征值成功");
                        }
                        db.close();
                    }
                    changeCount ++;
                     msg = Message.obtain();
                    msg.arg1 = changeCount;
                    msg.obj = FACE_FEATURE_IS_CHANGED;
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.i("tag","==========================");
                }
            }

            @Override
            public void onFailure(String erorrMsg) {
                LogUtils.i("tag","erorrMsg=" + erorrMsg);
            }
        });
    }

    /**
     * 每次搜索时检测特征值是否发生变化
     * 获取下载保存到数据库的hashcode(初始化时保存)和本地的hashcode(注册时保存)
     * @return
     */
    public void checkFeatureValueIsChanged(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("DevicePerson", null, null, null, null, null, null);
        if(cursor.getCount() <= 0){
            return ;
        }
        ArrayList<ServerHashCodeBean> serverList = getServerHashCodeBeen(cursor);
        Cursor cursorHashcode = db.query("FaceHashCode", null, null, null, null, null, null);
        if(cursorHashcode.getCount() <= 0){
            return ;
        }
        ArrayList<LocalHashCodeBean> localList = getLocalHashCodeBeen(cursorHashcode);
        cursor.close();
        cursorHashcode.close();
        db.close();
        personList = new ArrayList<>();
        hashcode = new ArrayList<>();
        for (int i = 0;i < serverList.size(); i ++){
            ServerHashCodeBean serverHashCodeBean = serverList.get(i);
            String serPersonId = serverHashCodeBean.getPersonId();
            String serHashCode = serverHashCodeBean.getHashCode();
            for (int j = 0;j < localList.size();j ++){
                LocalHashCodeBean localHashCodeBean = localList.get(j);
                String locPersonId = localHashCodeBean.getPersonId();
                String locHashCode = localHashCodeBean.getHashCode();
                if(serPersonId.equals(locPersonId)){
                    //特征值没有发生变化
                    if(!serHashCode.equals(locHashCode)){
                        //特征值发生变化，应该根据Id重新获取
                        personList.add(locPersonId);
                        hashcode.add(serHashCode);
                    }
                }
            }
        }
        //说明特征值没有发生变化，不需要重新获取近红外人员特征值
        LogUtils.i(tag,"personList="+ personList.size());
        if(personList == null || personList.size() == 0){
            return ;
        }else {
            //需要重新获取近红外人员特征值
            getNirFaceFeatures(0,personList,hashcode);
        }
        return ;
    }

    @NonNull
    private ArrayList<LocalHashCodeBean> getLocalHashCodeBeen(Cursor cursor) {
        ArrayList<LocalHashCodeBean> localList = new ArrayList<>();
        while (cursor.moveToNext()){
            LocalHashCodeBean localHashCodeBean = new LocalHashCodeBean();
            String personId = cursor.getString(cursor.getColumnIndex("personId"));
            String hashcode = cursor.getString(cursor.getColumnIndex("hashcode"));
            localHashCodeBean.setPersonId(personId);
            localHashCodeBean.setHashCode(hashcode);
            localList.add(localHashCodeBean);
        }
        return localList;
    }

    @NonNull
    private ArrayList<ServerHashCodeBean> getServerHashCodeBeen(Cursor cursor) {
        ArrayList<ServerHashCodeBean> serverList = new ArrayList<>();
        while (cursor.moveToNext()){
            ServerHashCodeBean hashCodeBean = new ServerHashCodeBean();
            String personId = cursor.getString(cursor.getColumnIndex("personId"));
            String idNo = cursor.getString(cursor.getColumnIndex("idNo"));
            String hashcode = cursor.getString(cursor.getColumnIndex("hashcode"));
            hashCodeBean.setPersonId(personId);
            hashCodeBean.setIdNo(idNo);
            hashCodeBean.setHashCode(hashcode);
            serverList.add(hashCodeBean);
        }
        return serverList;
    }
}
