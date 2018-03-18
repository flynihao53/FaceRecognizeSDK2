package com.binjn.admin.facerecognizesdk.model;

import java.util.ArrayList;

/**
 * 存储搜索的特征值
 * Created by wangdajian on 2017/12/5.
 */

public class FeatureValueBean {
    private String personId;
    private ArrayList<float[]> list;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public ArrayList<float[]> getList() {
        return list;
    }

    public void setList(ArrayList<float[]> list) {
        this.list = list;
    }
}
