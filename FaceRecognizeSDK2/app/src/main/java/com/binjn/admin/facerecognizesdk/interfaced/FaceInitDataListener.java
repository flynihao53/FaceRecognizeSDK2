package com.binjn.admin.facerecognizesdk.interfaced;

/**
 * Created by wangdajian on 2018/2/7.
 */

public interface FaceInitDataListener {
    void onDeviceRegister(int deviceIsRegister);
    void onInitResultBack(boolean initReuslt);
}
