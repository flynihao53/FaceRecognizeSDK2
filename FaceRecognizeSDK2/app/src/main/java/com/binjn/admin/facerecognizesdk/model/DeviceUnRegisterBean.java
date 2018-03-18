package com.binjn.admin.facerecognizesdk.model;

import java.util.List;

/**
 * Created by wangdajian on 2017/11/29.
 */

public class DeviceUnRegisterBean {

    /**
     * code : 0
     * msg :
     * total : 0
     * data : [{"id":"身份证号","name":"姓名"}]
     */

    private String code;
    private String msg;
    private int total;
    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 人员ID
         * name : 姓名
         */

        private String id;   //人员ID  personId
        private String name;
        private String idNo; //人员身份证号

        public String getIdCard() {
            return idNo;
        }
        public void setIdCard(String idCard) {
            this.idNo = idCard;
        }
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
