package com.binjn.admin.facerecognizesdk;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.binjn.admin.facerecognizesdk.face.FaceRecognizer;
import com.binjn.admin.facerecognizesdk.face.InintData;
import com.binjn.admin.facerecognizesdk.interfaced.FaceFeaturesCallback;
import com.binjn.admin.facerecognizesdk.interfaced.FaceInitDataListener;
import com.binjn.admin.facerecognizesdk.interfaced.faceSimilarityCallback;
import com.binjn.admin.facerecognizesdk.net.RequestData;
import com.binjn.admin.facerecognizesdk.presenter.XiChuHuaYuan;
import com.binjn.admin.facerecognizesdk.utils.Constants;
import com.binjn.admin.facerecognizesdk.utils.FileUtils;
import com.binjn.admin.facerecognizesdk.utils.ImgUtils;
import com.binjn.admin.facerecognizesdk.utils.LogCatHelper;
import com.binjn.admin.facerecognizesdk.utils.LogUtils;
import com.binjn.admin.facerecognizesdk.utils.NetWorkUtils;
import com.binjn.admin.facerecognizesdk.utils.SQLiteUitls;
import com.binjn.admin.facerecognizesdk.utils.TimeCountUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView imageViewFace1;
    private ImageView imageViewFace2;
    private Button btnPhoto;
    private Button btnPhoto1;
    private Button btnVideo;
    private Button btnLocalMac;
    private Bitmap bitmap2;   //拍照获得的图像
    private Bitmap bitmap;   //拍照获得的图像
    private String modelPath;
    private String pointdetector_modelPath;
    private String SDPath;
    private int[] face_5_ptsA;
    private int[] face_5_ptsB;
    private String photoPath;
    private String pathA;
    private String pathB;
    private boolean flag = false;
    private String modelPathRecoginze;
    private String trueFeatsPath;
    private String modelName;
    private ByteBuffer buffer;
    private ByteBuffer bufferDec;
    private Bitmap bmp_src;
    private Bitmap bmp_dec;
    private String imgUrl1;
    private String imgUrl12;

    //SQLiteUitls dbHelper;
    private float[] key;
    private String value;
    private FaceRecognizer faceRecognizer;
    private Message msg;

    private long currentTimeMillis;
    private Object test1;
    private Object test2;
    private SQLiteUitls dbHelper;
    private Object token;
    private RequestData requestData;
    private ArrayList<String> list;
    private XiChuHuaYuan xiChuHuaYuan;
    private Button btn4;
    private InintData instantce;
    private String comparePhoto;
    private ArrayList<String> fileName1;
    private byte[] img1;
    private Bitmap bmp_src1;
    private int countA;
    private File filePath;
    private Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
       // LogCatHelper.getInstance(getApplicationContext(),null).start();
        instantce = InintData.getInstantce(MainActivity.this,getApplicationContext());
        dbHelper = new SQLiteUitls(MainActivity.this, "FaceFeatures.db", null, 1);
        faceRecognizer = FaceRecognizer.getInstantce(MainActivity.this);
        initView();
        initData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
       //LogCatHelper.getInstance(getApplicationContext(),null).stop();
    }

    private void initData() {
        SDPath = "/sdcard";
        photoPath = SDPath + "/test";
        //photoPath = SDPath + "/logs";
        modelPath = SDPath + "/seeta/";
        modelName = "VIPLFaceDetector5.1.2.NIR.640x480.dat";

        pointdetector_modelPath = SDPath + "/vipl/VIPLPointDetector4.0.3.dat";
        modelPathRecoginze = SDPath + "/vipl/data/test_face_recognizernir402/viplfacenet_90x90_HE3.dat";
        trueFeatsPath = SDPath + "/vipl/data/test_face_recognizernir402/win_feat.dat";
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = (String) msg.obj;
            switch (message){
                case "REGISTER_SUCCESS":
                    long endTime = System.currentTimeMillis();
                    Log.i("tag","每次注册的时间：" + (endTime - currentTimeMillis) + "ms");
                    int index = msg.arg1;
                    Log.i("tag","index=" + index);
                    if(index != list.size()){
                        testRegister(index);
                    }else {
                        Log.i("tag","完成注册");
                        count = 0;
                        faceRecognizer.registerFinish("532129199012040718");
                    }
                    break;
                case "SIM":
                   int count =  msg.arg1;
                    if(count != fileName1.size()){
                        getTest(count);
                    }else {
                        LogUtils.i("tag_guxiang","测试完成");
                    }
                    break;
                case "search":
                    testSearch();
                    /*int indexS = msg.arg1;
                    if(indexS != 50){
                        testSearch();
                    }else {
                        count = 0;
                        LogUtils.i("tag","search finish");
                    }*/
                    break;
            }
        }
    };
    private void initView() {

        imageViewFace1 = (ImageView) findViewById(R.id.iv_face1);
        imageViewFace2 = (ImageView) findViewById(R.id.iv_face2);
        btnPhoto = (Button) findViewById(R.id.btn);
        btnPhoto1 = (Button) findViewById(R.id.btn1);
        btnVideo = (Button) findViewById(R.id.btn2);
        btnLocalMac = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        xiChuHuaYuan = new XiChuHuaYuan(getApplicationContext());
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第三步：生成特征值
                //xiChuHuaYuan.createFeatureValue();
            }
        });
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第一步：批量修改文件名
                //xiChuHuaYuan.modifyFileName();
            }
        });
        //获取本地mac
        btnLocalMac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //readFile();
                //TimeCountUtils.setMidnightAlarm(getApplicationContext());
               //TimeCountUtils.setLogcatUploadTime(getApplicationContext());
                //String s = FileUtils.readFileSD(Constants.DEVICE_ID);
                //Log.i("tag","lockId====" + s);
                //testUploadData(); //初始化数据
                //getNirFaceFeatures();
                //getPersonIdHashcode("131416652028837888");
                //testScanFile();
                //testNetwork();
                 testSearch();
                //TimeCountUtils.setAlarmTime(getApplicationContext(),0,"MIDNIGHT_ALARM_FILTER",1 * 1000);
                //String path = photoPath + "/Register1.jpg";   //119467146085400576  111129577958408192
                //String path = "/test/img/20180305";
               // list = FileUtils.getFileName(path, false);
                //Log.i("tag","list =" + list.size());
                //testRegister(0);
                //saveFeatureValueHashCodeToDB(null,"111129577958408192");
              //  Bitmap bm = BitmapFactory.decodeFile(path);
               /* String personId = faceRecognizer.getPersonIdByIDCard("532129199012040718");
                Log.i("tag","personId====" + personId); //111129577958408192
                String paths ="/sdcard/" + "test";*/
                //faceRecognizer.registerFinish("532129199012040718");
                 //testXiChuHuaYuan();
               // instantce.checkAlias();
                /*new Thread(){
                    @Override
                    public void run() {
                        testGuxiangPhoto();
                    }
                }.start();*/
            }
        });
        //拍照---服务器获取
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!RequestReadPermission()){
                    return;
                }
                Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory() + "/vipl", "aa" + System.currentTimeMillis() / 1000 + ".jpg");
                Uri imageUri = Uri.fromFile(file);
                pathA = imageUri.getPath();Log.i("tag","拍照1==" + pathA);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,100);
                //bitmap2 = BitmapFactory.decodeFile(imgUrl1);
                //imageViewFace1.setImageBitmap(bitmap2);
            }
        });
        //拍照2---现场采集
        btnPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory() + "/vipl", "bb" + System.currentTimeMillis() / 1000 + ".jpg");
                Uri imageUri = Uri.fromFile(file );
                pathB = imageUri.getPath();
                Log.i("tag","拍照2==" + pathB);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,101);
                 // bitmap = BitmapFactory.decodeFile(imgUrl12);
                  //imageViewFace2.setImageBitmap(bitmap);
            }
        });
    }

    private void testNetwork() {
        boolean networkConnected = NetWorkUtils.isNetworkConnected(getApplicationContext());
        LogUtils.i("tag","networkConnected=" +networkConnected);
    }

    private void testXiChuHuaYuan() {
        //第二步：检测人脸
        xiChuHuaYuan.detectorFace();
    }

    private int count = 0;
    private void testSearch() {
        String path = "sdcard/test/aa.jpg";   //119467146085400576  111129577958408192
        filePath = new File(path);
        if(!filePath.exists()){
            Log.i("tag","==文件不存在==");
            return;
        }
        //getNirFaceFeatures();
        bm = BitmapFactory.decodeFile(path);
        faceRecognizer.searchPersonsPhoto(bm, new faceSimilarityCallback() {
            @Override
            public void faceSimilarity(String sim) {
                Log.i("tag","==sim==" + sim);
                //count ++;
                Message msg = Message.obtain();
                msg.arg1 = count;
                msg.obj = "search";
                handler.sendMessage(msg);
            }
        });

        /*FaceRecognizer.getInstantce(getApplicationContext()).searchPersonsPhoto(bm, new faceSimilarityCallback() {
            @Override
            public void faceSimilarity(String sim) {
                Log.i("tag","sim=" + sim);
            }
        });*/
    }

    public void testGuxiangPhoto(){
        String path = "test/img/20180305";
        comparePhoto = "sdcard/test/img/a1.jpg";
        fileName1 = FileUtils.getFileName(path, false);
        LogUtils.i("tag_guxiang","----------start---------------");
        Bitmap bmp1 = BitmapFactory.decodeFile(comparePhoto);
        // true is RGBA
        bmp_src1 = bmp1.copy(Bitmap.Config.ARGB_8888, true);
        int bytes1 = bmp_src1.getByteCount();
        ByteBuffer buffer1 = ByteBuffer.allocate(bytes1);
        bmp_src1.copyPixelsToBuffer(buffer1);
        img1 = buffer1.array();
        countA = 0;
        getTest(0);

    }

    private void getTest(int index) {
        Bitmap bmp1 = BitmapFactory.decodeFile(fileName1.get(index));
        LogUtils.i("tag_guxiang","fileName1.get(index)=" + fileName1.get(index));
        Bitmap bmp_src2 = bmp1.copy(Bitmap.Config.ARGB_8888, true); // true is RGBA
        int bytes2 = bmp_src2.getByteCount();
        ByteBuffer buffer2 = ByteBuffer.allocate(bytes2);
        bmp_src2.copyPixelsToBuffer(buffer2);
        byte[] img2 = buffer2.array();

        float sim = faceRecognizer.compareTwoPhoto(img1, bmp_src1.getWidth(), bmp_src1.getHeight(), img2, bmp_src2.getWidth(), bmp_src2.getHeight());
        countA ++;
        LogUtils.i("tag_guxiang","sim= " + sim);
        Message msg = Message.obtain();
        msg.obj = "SIM";
        msg.arg1 = countA;
        handler.sendMessage(msg);
    }

    private void getNirFaceFeatures() {
        byte[] bytes = new byte[8];
        for (int i = 0;i < bytes.length;i ++){
            bytes[i] = (byte) i;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("personId","1111222");
        values.put("faceId","11");
        values.put("featureValue",bytes);
        long faceFeature = db.insert("FaceFeature", null, values);
        LogUtils.i("tag","添加完成faceFeature=" + faceFeature);

    }
    private void testUploadData() {
        instantce.initDatas(new FaceInitDataListener() {
            @Override
            public void onDeviceRegister(int deviceIsRegister) {
                LogUtils.i("tag","deviceIsRegister=" + deviceIsRegister);
            }

            @Override
            public void onInitResultBack(boolean initReuslt) {
                LogUtils.i("tag","initReuslt=" + initReuslt);
            }
        });
    }
    private void readFile() {
        //FileUtils.getFileFromBytes(list.get(0),"test.1.txt");
        //byte[] bytes = FileUtils.toByteArray("test1.txt");
        //FileUtils.getFileFromBytes(bytes,"test2.txt");
        Log.i("tag","完成");
    }


    private void testRegister(int index) {
        Bitmap bm = BitmapFactory.decodeFile(list.get(index));
        currentTimeMillis = System.currentTimeMillis();
        faceRecognizer.register16FaceFeatures(bm, "111129577958408192", new FaceFeaturesCallback() {
            @Override
            public void facePhoto(String result) {
                Log.i("tag","facePhoto=" + result);
                try {
                    JSONObject json = new JSONObject(result);
                    String msgcode = json.getString("msgcode");
                if("8008".equals(msgcode)){
                    count ++;
                    msg = Message.obtain();
                    msg.obj = "REGISTER_SUCCESS";
                    msg.arg1 = count;
                    handler.sendMessage(msg);
                }else {
                    Log.i("tag","注册返回错误信息：" + msgcode);
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }













    private Bitmap drawRectangles(Bitmap imageBitmap, int num_face, int[] face_pts) {
        int left, top, right, bottom;
        Bitmap mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        for (int i = 0; i < num_face; i++) {
            left = face_pts[i * 4];
            top = face_pts[i * 4 + 1];
            right = left + face_pts[i * 4 + 2];
            bottom = top + face_pts[i * 4 + 3];
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);//不填充
            paint.setStrokeWidth(10);  //线的宽度
            canvas.drawRect(left, top, right, bottom, paint);

        }
        return mutableBitmap;
    }

    private Bitmap drawPoints(Bitmap imageBitmap, int num_faces, int[] face_5_pts) {
        int x,y;
        Bitmap mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        for (int i = 0; i < num_faces; i++) {
            // draw r5 points
            for (int j = 0; j < 5; ++j) {
                x = face_5_pts[i * 10 + j * 2];
                y = face_5_pts[i * 10 + j * 2 + 1];
                paint.setColor(Color.YELLOW);
                paint.setStyle(Paint.Style.FILL);//填充
                paint.setStrokeWidth(10);  //线的宽度
                canvas.drawCircle(x,y,10,paint);
            }

        }
        return mutableBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("tag","========photo=======");
        if(resultCode == RESULT_OK && requestCode == 100){
                Log.i("tag","========photo==success====data="+data);
                //bitmap2 = (Bitmap) data.getExtras().get("data");
                ImgUtils.saveBmp(bitmap2);
                //ImgUtils.saveImage(bitmap2);
                bitmap2 = BitmapFactory.decodeFile(pathA);
                imageViewFace1.setImageBitmap(bitmap2);
                //validateRegisterProccess(bitmap2);
        }else if(resultCode == RESULT_OK && requestCode == 101){
            //bitmap = (Bitmap) data.getExtras().get("data");
            bitmap = BitmapFactory.decodeFile(pathB);
            imageViewFace2.setImageBitmap(bitmap);

        }
    }


    private boolean RequestReadPermission() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            return false;
        }
        return true;
    }


}
