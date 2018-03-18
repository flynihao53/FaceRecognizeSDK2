package com.binjn.admin.facerecognizesdk.model;

/**
 * Created by wangdajian on 2017/12/12.
 */

public class FaceDetectorBean {
    private String msgcode;
    private String msg;
    private int[] face;  //人脸坐标

    public String getMsgcode() {
        return msgcode;
    }

    public void setMsgcode(String msgcode) {
        this.msgcode = msgcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int[] getFace() {
        return face;
    }

    public void setFace(int[] face) {
        this.face = face;
    }
}
