package com.binjn.admin.facerecognizesdk.model;

import java.util.List;

/**
 * Created by admin on 2017/12/5.
 */

public class DevicePersonBean {


    /**
     * persons : [{"id":111129577958408192,"name":"王大坚","idNo":"532129199012040718","hashcode":"2208ae5deda51e8325930eda7f7c2055","visibleFaceRegistered":false,"nirFaceRegistered":true}]
     * code : 0
     */

    private String code;
    private List<PersonsBean> persons;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<PersonsBean> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonsBean> persons) {
        this.persons = persons;
    }

    public static class PersonsBean {
        /**
         * id : 111129577958408192
         * name : 王大坚
         * idNo : 532129199012040718
         * hashcode : 2208ae5deda51e8325930eda7f7c2055
         * visibleFaceRegistered : false
         * nirFaceRegistered : true
         */

        private String id;
        private String name;
        private String idNo;
        private String hashcode;
        private boolean visibleFaceRegistered;
        private boolean nirFaceRegistered;

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

        public String getIdNo() {
            return idNo;
        }

        public void setIdNo(String idNo) {
            this.idNo = idNo;
        }

        public String getHashcode() {
            return hashcode;
        }

        public void setHashcode(String hashcode) {
            this.hashcode = hashcode;
        }

        public boolean isVisibleFaceRegistered() {
            return visibleFaceRegistered;
        }

        public void setVisibleFaceRegistered(boolean visibleFaceRegistered) {
            this.visibleFaceRegistered = visibleFaceRegistered;
        }

        public boolean isNirFaceRegistered() {
            return nirFaceRegistered;
        }

        public void setNirFaceRegistered(boolean nirFaceRegistered) {
            this.nirFaceRegistered = nirFaceRegistered;
        }
    }
}
