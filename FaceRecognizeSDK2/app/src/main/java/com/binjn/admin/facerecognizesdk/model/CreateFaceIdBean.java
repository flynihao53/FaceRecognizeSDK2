package com.binjn.admin.facerecognizesdk.model;

import java.util.List;

/**
 * Created by wangdajian on 2018/2/1.
 */

public class CreateFaceIdBean {

    /**
     * total : 2
     * code : 0
     * id : [132440366887469056,132440366887469057]
     */

    private int total;
    private String code;
    private List<String> id;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }
}
