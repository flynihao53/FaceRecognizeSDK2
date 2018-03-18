package com.binjn.admin.facerecognizesdk.utils;

/**
 * Created by wangdajian on 2017/11/27.
 */

public class Constants {
    public static final String KEY = "binjn";

    //该文件用于存储token有效时间
    public static final  String FILE_TOKEN_NAME = "binjn_token.txt";

    //用于存储token
    public static final  String FILE_TOKEN = "binjn_token_info.txt";

    //存储相似度(搜索接口)
    public static final  String FILE_SIM = "binjn_sim.txt";

    //验证接口(身份证和现场照片)
    public static final  String FILE_SIM_IDCARD = "binjn_sim_idcard.txt";

    //存放请求根地址
    public static final String FILE_HTTP_ADDRESS = "binjn_http.txt";

    //存放门禁机id
    public static final String DEVICE_ID = "binjn_dev_id.txt";

    //门禁设备管理的人员id(不用，人员id和身份证号存放数据库中)
    //public static final String FILE_PERSON_ID = "binjn_person_id.txt";

    //近红外未注册的人员身份证号
    public static final String FILE_UNPERSON_IDCARD = "binjn_unresgiter_idcard.txt";

    //保存注册时16张照片的特征值(带特征库头文件)
    public static final String FILE_FEATURE_VALUE = "binjn_feature_value.txt";
    //保存注册时16张照片的特征值(不带特征库头文件)
    public static final String FILE_TEMP_FEATURE_VALUE = "binjn_feature_value_temp.txt";

    //近红外人员特征值(不用，近红外人员特征值已放在数据库中)
    //public static final String FILE_FEATURE_VALUE = "binjn_nir_feature_value.txt";

    //保存传入的16张照片的临时文件夹
    public static final String FILE_16_PHOTO_DIRECTORY = "binjn16Photo";

    //保存传入要识别人的照片
    public static final String FILE_PHOTO_DIRECTORY_STRANGER = "comparePhoto";

    //保存传入的2张照片的临时文件夹
    public static final String FILE_2_PHOTO_DIRECTORY = "binjn2Photo";

    //保存传入的张照片的临时文件夹
    public static final String FILE_PHOTO_DIRECTORY = "binjnPhoto";

    //特征值文件夹
    public static final String DIR_FEATURE_VALUE = "featureValue";

    //请求地址一级地址
    public static final String HTTP_PATH = "https://icbc.binjn.com:59002/phs";

    //API接口授权认证
    public static final String API_AUTHERITY = "/security/token";

    //门禁机注册接口
    public static String URLDevRegister(String mac){
        String URL_DEV_REGISTER = "/v1/lock/register/" + mac;
        return URL_DEV_REGISTER;
    }
    //获取门禁设备管理的人员列表
    public static String URLPersons(String lockId){
        String URL_PERSONS = "/v1/lock/" +lockId + "/persons";
        return URL_PERSONS;
    }

    //获取未注册近红外人脸的人员信息列表
    public static String URLDevUnRegister(String lockId){
        String URL_DEV_UNREGISTER = "/v1/lock/" +lockId + "/unregistered/nir";
        return URL_DEV_UNREGISTER;
    }

    //获取人员近红外特征值
    public static String URLNIRFACEFRETURE(String personId){
        String URL_NIR_FACE_FRETURE = "/v1/person/" + personId +"/face/feature/nir/file";
        return URL_NIR_FACE_FRETURE;
    }

    //人员绑定人脸照片
    public static String URLPERSONBANGPHOTO(String personId){
        String URLPERSONBANGPHOTO = "/v1/person/" +personId +"/face/image";
        return URLPERSONBANGPHOTO;
    }

    //人员绑定人脸照片(批量上传图片)
    public static String URLREGISTER16PHOTO(String personId){
        ///person/111129577958408192/face/nir/image/file
        String URLREGISTER16PHOTO = "/v1/person/" + personId + "/face/nir/image/file";
        return  URLREGISTER16PHOTO;
    }
    //人员绑定人脸特征值
    public static String URLPERSONBANDFACEFEATURE(String personId,String type){
        ///v1/person/{personId}/face/feature
        String URL_PERSON_BAND_FACE_FEATURE = "/v1/person/" + personId + "/face/feature/"+type +"/file";
        return URL_PERSON_BAND_FACE_FEATURE;
    }

    //保存门禁机人脸识别记录(识别数据上传)
    public static String URLSAVEDEVICEFACERECORD(String lockId){
        String URL_SAVE_DEVICE_FACE_RECORD = "/v1/upload/lock/" + lockId+ "/face/file";
        return URL_SAVE_DEVICE_FACE_RECORD;
    }

    public static String CREATE_FACE_ID(String url){
        String  CREATE_FACE_ID = url + "/v1/common/id/new?count=10000";
        return CREATE_FACE_ID;
    }

    public static String CHECK_TOKEN_STATE(String token){
        String TOEKN = "/security/token/" + token;
        return TOEKN;
    }

    /**
     * 根据传值不同获取特征数量或者特征长度
     * @param index
     * @return
     */
    public static int getFeatureLibLength(int index){
        byte[] id = new byte[32];                   //特征库编号
        byte[] name = new byte[128];                //特征库名称
        byte[] version = new byte[16];              //特征库版本
        byte[] update_time = new byte[32];          //特征库更新时间
        byte[] reserved = new byte[256];            //保留字段
        byte[] count = new byte[4];                 //特征数量
        byte[] featureValueLength = new byte[4];    //单个特征长度
        if(index == 0){
            int length = id.length + name.length + version.length + update_time.length + reserved.length;
            return length;
        }else if(index == 1){
            int length = id.length + name.length + version.length + update_time.length + reserved.length + count.length;
            return length;
        }else if(index == 2){
            int length = id.length + name.length + version.length + update_time.length
                    + reserved.length + count.length + featureValueLength.length;
            return length;
        }
        return 0;
    }

    //pushType
    public final static String PUSH_TYPE_UPDATE = "update";
    public final static String PUSH_TYPE_ADD = "add";
    public final static String PUSH_TYPE_DELETE = "delete";

    //数据库版本号
    public final static int DATABASE_VERSION = 1;
    //数据库名称
    public final static String DATABASE_NAME = "FaceFeatures.db";

    //以下路径用于存放人脸识别模型文件
    //==================================================//
    public final static String  SDPath = "/sdcard/";
    public final static String modelDir = SDPath + "seeta/";
    public final static String faceDetectorModel = "VIPLFaceDetector5.1.2.NIR.640x480.sta";
    public final static String pointDetectorModel = "VIPLPointDetector5.0.pts5.dat";
    public final static String faceRecognizerModel = "VIPLFaceRecognizer4.3.HE3.dat";
    //==================================================//

}
