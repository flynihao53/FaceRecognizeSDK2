package com.binjn.admin.facerecognizesdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.binjn.admin.facerecognizesdk.face.FaceRecognizer;
import com.binjn.admin.facerecognizesdk.model.FaceDetectorBean;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private String sdPath;
    private String photoPath;
    private String modelPath;
    private String modelName;
    private String pointdetector_modelPath;
    private String modelPathRecoginze;
    private String trueFeatsPath;
    private Context appContext;

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.binjn.admin.facerecognizesdk", appContext.getPackageName());
    }

    @Test
    public void init(){
        appContext = InstrumentationRegistry.getTargetContext();
        Log.i("tag","--------");
        sdPath = "/sdcard";
        photoPath = sdPath + "/vipl";
        modelPath = sdPath + "/seeta/";
        modelName = "VIPLFaceDetector5.1.2.NIR.640x480.dat";

        pointdetector_modelPath = sdPath + "/vipl/VIPLPointDetector4.0.3.dat";
        modelPathRecoginze = sdPath + "/vipl/data/test_face_recognizernir402/viplfacenet_90x90_HE3.dat";
        trueFeatsPath = sdPath + "/vipl/data/test_face_recognizernir402/win_feat.dat";

        String path = photoPath + "/ee.png";
        File filePath = new File(path);
        if(!filePath.exists()){
            Log.i("tag","==文件不存在==");
            return;
        }
        Bitmap bm = BitmapFactory.decodeFile(path);
        testDetector(bm);
    }


    public void testDetector(Bitmap bm) {
        FaceDetectorBean faceDetectorBean = FaceRecognizer.getInstantce(appContext).detectorFaces(bm);
        String msg = faceDetectorBean.getMsg();
        if("0".equals(msg)){
            Log.i("tag","检测到人脸");
        }else {
            Log.i("tag","没有检测到人脸");
        }
    }
}
