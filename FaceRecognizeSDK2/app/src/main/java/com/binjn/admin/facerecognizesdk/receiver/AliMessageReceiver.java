package com.binjn.admin.facerecognizesdk.receiver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.binjn.admin.facerecognizesdk.MainActivity;
import com.binjn.admin.facerecognizesdk.model.PushPersonBean;
import com.binjn.admin.facerecognizesdk.utils.Constants;
import com.binjn.admin.facerecognizesdk.utils.LogUtils;
import com.binjn.admin.facerecognizesdk.utils.SQLiteUitls;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by wangdajian on 2018/3/1.
 */

public class AliMessageReceiver extends MessageReceiver {
    public static final String tag = "AliMessageReceiver";
    private Gson gson;
    private SQLiteUitls dbHelper;
    private SQLiteDatabase db;


    @Override
    protected void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        super.onNotification(context, title, summary, extraMap);
        //处理推送通知
        LogUtils.e("MyMessageReceiver", "Receive notification, title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);

    }

    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {
        super.onMessage(context, cPushMessage);
        if(dbHelper == null){
            dbHelper = new SQLiteUitls(context, "FaceFeatures.db", null, 1);
        }
        LogUtils.e("MyMessageReceiver", "onMessage, messageId: " + cPushMessage.getMessageId() + ", title: "
                + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
        //
        //PushPersonBean
        try {
            String content = cPushMessage.getContent();
            gson = new Gson();
            PushPersonBean pushPerson = gson.fromJson(content, PushPersonBean.class);
            PushPersonDataBase(pushPerson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PushPersonDataBase(PushPersonBean pushPerson) {
        PushPersonBean.ContentBean content = pushPerson.getContent();
        String action = content.getAction();
        PushPersonBean.ContentBean.DataBean data = content.getData();
        db = dbHelper.getWritableDatabase();
        String personId = data.getId();
        String idCard = data.getIdNo();
        String md5 = data.getMD5();
        String name = data.getName();
        ContentValues values1 = new ContentValues();
        values1.put("personId",personId);
        values1.put("idNo",idCard);
        values1.put("hashcode",md5);
        ContentValues values2= new ContentValues();
        values2.put("idCard",idCard);
        values2.put("idNo",personId);
        values2.put("name",name);
        if(Constants.PUSH_TYPE_UPDATE.equals(action)){
            //更新DevicePerson和UnRegisterPerson
            updatePersonInfoToDatabase(db,personId,values1,values2);
        }else if(Constants.PUSH_TYPE_ADD.equals(action)){
            //添加DevicePerson和UnRegisterPerson
            addPersonInfoToDatabase(db,personId,values1,values2);
        }else if(Constants.PUSH_TYPE_DELETE.equals(action)){
            deletePersonInfoToDatabase(db,personId);
        }
        db.close();
    }

    private void deletePersonInfoToDatabase(SQLiteDatabase db, String personId) {
        db.delete("DevicePerson","personId = ?",new String[]{personId});
        db.delete("UnRegisterPerson","idNo = ?",new String[]{personId});
    }

    private void addPersonInfoToDatabase(SQLiteDatabase db, String personId,ContentValues values1, ContentValues values2) {
        modefyPersonInfo(db, personId, values1, values2);
    }

    private void updatePersonInfoToDatabase(SQLiteDatabase db, String personId, ContentValues values1, ContentValues values2) {
        modefyPersonInfo(db, personId, values1, values2);
    }

    private void modefyPersonInfo(SQLiteDatabase db, String personId, ContentValues values1, ContentValues values2) {
        Cursor devicePersonCursor = db.query("DevicePerson", null, "personId = ?", new String[]{personId}, null, null, null);
        if(devicePersonCursor.getCount() > 0){
            db.update("DevicePerson",values1,"personId = ?",new String[]{personId});
        }else {
            db.insert("DevicePerson",null,values1);
        }
        Cursor unRegisterPersonCursor = db.query("UnRegisterPerson", null, "idNo = ?", new String[]{personId}, null, null, null);
        if(unRegisterPersonCursor.getCount() > 0){
            db.update("UnRegisterPerson",values2,"idNo = ?",new String[]{personId});
        }else {
            db.insert("UnRegisterPerson",null,values2);
        }
        devicePersonCursor.close();
        unRegisterPersonCursor.close();
        values1.clear();
        values2.clear();
    }




    @Override
    protected void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        super.onNotificationOpened(context, title, summary, extraMap);
        //LogUtils.e("MyMessageReceiver", "onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        super.onNotificationClickedWithNoAction(context, title, summary, extraMap);
        //LogUtils.e("MyMessageReceiver", "onNotificationClickedWithNoAction, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary,
                                               Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        super.onNotificationReceivedInApp(context, title, summary, extraMap, openType, openActivity, openUrl);
        /*LogUtils.e("MyMessageReceiver", "onNotificationReceivedInApp, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:"
                + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);*/
    }

    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        super.onNotificationRemoved(context, messageId);
        LogUtils.e("MyMessageReceiver", "onNotificationRemoved");

    }
}
