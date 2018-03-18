package com.binjn.admin.facerecognizesdk.model;

/**
 * Created by wangdajian on 2018/1/31.
 */

public class UploadFeatureValueBean {
    private String token;
    private String path;
    private String personId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }
}
