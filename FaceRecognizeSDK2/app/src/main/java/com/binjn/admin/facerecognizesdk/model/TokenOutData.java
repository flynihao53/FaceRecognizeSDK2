package com.binjn.admin.facerecognizesdk.model;

/**
 * Created by wangdajian on 2018/1/24.
 */

public class TokenOutData {

    /**
     * msg : token失效，请重新登录
     * code : 401
     */

    private String msg;
    private String code;

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
