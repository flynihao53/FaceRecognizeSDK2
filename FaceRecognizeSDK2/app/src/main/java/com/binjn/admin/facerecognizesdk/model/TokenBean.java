package com.binjn.admin.facerecognizesdk.model;

/**
 * Created by admin on 2017/11/27.
 */

public class TokenBean {

    /**
     * code : 0
     * token : “TOKEN”
     * expires_in : 43200
     */

    private int code;
    private String token;
    private int expires_in;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }
}
