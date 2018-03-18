package com.binjn.admin.facerecognizesdk.model;

/**
 * Created by wangdajian on 2018/2/27.
 */

public class CheckedTokenBean {

    /**
     * msg : TOKEN不存在或已过期
     * code : 3008
     * token : token
     * expires_in : 43200
     */

    private String msg;
    private String code;
    private String token;
    private String expires_in;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
