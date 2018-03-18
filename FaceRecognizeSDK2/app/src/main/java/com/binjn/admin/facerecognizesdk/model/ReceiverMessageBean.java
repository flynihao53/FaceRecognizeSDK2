package com.binjn.admin.facerecognizesdk.model;

/**
 * Created by admin on 2018/3/1.
 */

public class ReceiverMessageBean {
    /**
     * name : 熊杰
     * visibleFaceRegistered : 0
     * id : 131403700563345409
     * idNo : 532301198609103937
     * nirFaceRegistered : 0
     * MD5 : a612c8774a65a799da4e67edc8d992bb
     */

    private String name;
    private int visibleFaceRegistered;
    private long id;
    private String idNo;
    private int nirFaceRegistered;
    private String MD5;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVisibleFaceRegistered() {
        return visibleFaceRegistered;
    }

    public void setVisibleFaceRegistered(int visibleFaceRegistered) {
        this.visibleFaceRegistered = visibleFaceRegistered;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public int getNirFaceRegistered() {
        return nirFaceRegistered;
    }

    public void setNirFaceRegistered(int nirFaceRegistered) {
        this.nirFaceRegistered = nirFaceRegistered;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }
}
