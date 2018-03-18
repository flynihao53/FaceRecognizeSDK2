package com.binjn.admin.facerecognizesdk.model;

/**
 * 存储搜索的人脸特征值的hashcode(服务器下载)
 * Created by wangdajian on 2017/12/13.
 */

public class ServerHashCodeBean {
    private String personId;
    private String idNo;
    private String hashCode;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }
}
