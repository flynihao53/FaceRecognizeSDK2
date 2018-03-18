package com.binjn.admin.facerecognizesdk.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wangdajian on 2017/12/3.
 */

public class SQLiteUitls extends SQLiteOpenHelper{
    //存放获取的近红外特征值
    private static final String CREATE_FACE_FEATURE = "create table FaceFeature("
                            + "id Integer primary key autoincrement,"
                            + "personId text,"
                            + "faceId text,"
                            + "featureValue BLOB"
                            +")";
    //存放人员ID和对应的身份证号
    private static final String CREATE_DEVICE_PERSONS = "create table DevicePerson("
            + "id Integer primary key autoincrement,"
            + "personId text,"
            + "idNo text,"
            + "hashcode text"
            +")";
    //存放人员ID和对应的特征值的hashcode
    private static final String CREATE_PERSONS_HASHCODE = "create table FaceHashCode("
            + "id Integer primary key autoincrement,"
            + "personId text,"
            + "hashcode text"
            +")";
    //存放未注册人员personID
    private static final String CREATE_UNREGISTER_PERSONS = "create table UnRegisterPerson("
            + "id Integer primary key autoincrement,"
            + "idCard text,"
            + "idNo text,"
            + "name text"
            +")";
    //存放注册时的图片路径
    private static final String CREATE_REGISTER_PHOTO_FEATURE = "create table RegisterPhotoFeature("
            + "id Integer primary key autoincrement,"
            + "personId text,"
            + "imgPath text,"
            + "uploadState text"
            +")";

    //存放特征值对应的faceID
    private static final String CREATE_FEATUREVALUE_FACEID = "create table FaceID("
            + "id Integer primary key autoincrement,"
            + "faceId text,"
            + "isUse text"
            +")";

    private Context mContext;
    public SQLiteUitls(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FACE_FEATURE);
        db.execSQL(CREATE_DEVICE_PERSONS);
        db.execSQL(CREATE_PERSONS_HASHCODE);
        db.execSQL(CREATE_UNREGISTER_PERSONS);
        db.execSQL(CREATE_FEATUREVALUE_FACEID);
        db.execSQL(CREATE_REGISTER_PHOTO_FEATURE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
