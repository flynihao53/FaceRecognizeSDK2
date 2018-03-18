package com.binjn.admin.facerecognizesdk.model;

import java.util.List;

/**
 * Created by wangdajian on 2017/11/29.
 */

public class PersonsBean {

    /**
     * code : 0
     * msg :
     * total : 0
     * data : [{"id":"1","name":"测试人1","sex":10123,"nation":10130,"education":10303,"type":10112,"birthday":"2017-09-13","birthPlace":"SYSTEM","mobile":"0","occupation":"Test","address":"Test","employer":"云南冰鉴科技","priority":1,"description":"测试人员","icbcOpenId":null,"createPersonId":null,"visibleFaceRegistered":false,"nirFaceRegistered":false}]
     */

    private int code;
    private String msg;
    private int total;
    private List<DataBean> data;
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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
         * id : 1
         * name : 测试人1
         * idNo: 53010198601010028
         * sex : 10123
         * nation : 10130
         * education : 10303
         * type : 10112
         * birthday : 2017-09-13
         * birthPlace : SYSTEM
         * mobile : 0
         * occupation : Test
         * address : Test
         * employer : 云南冰鉴科技
         * priority : 1
         * description : 测试人员
         * icbcOpenId : null
         * createPersonId : null
         * visibleFaceRegistered : false
         * nirFaceRegistered : false
         */

        private String id;
        private String idNo;
        private String name;
        private int sex;
        private int nation;
        private int education;
        private int type;
        private String birthday;
        private String birthPlace;
        private String mobile;
        private String occupation;
        private String address;
        private String employer;
        private int priority;
        private String description;
        private Object icbcOpenId;
        private Object createPersonId;
        private boolean visibleFaceRegistered;
        private boolean nirFaceRegistered;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }


        public String getIdNo() {
            return idNo;
        }

        public void setIdNo(String idNo) {
            this.idNo = idNo;
        }
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public int getNation() {
            return nation;
        }

        public void setNation(int nation) {
            this.nation = nation;
        }

        public int getEducation() {
            return education;
        }

        public void setEducation(int education) {
            this.education = education;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getBirthPlace() {
            return birthPlace;
        }

        public void setBirthPlace(String birthPlace) {
            this.birthPlace = birthPlace;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getOccupation() {
            return occupation;
        }

        public void setOccupation(String occupation) {
            this.occupation = occupation;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEmployer() {
            return employer;
        }

        public void setEmployer(String employer) {
            this.employer = employer;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Object getIcbcOpenId() {
            return icbcOpenId;
        }

        public void setIcbcOpenId(Object icbcOpenId) {
            this.icbcOpenId = icbcOpenId;
        }

        public Object getCreatePersonId() {
            return createPersonId;
        }

        public void setCreatePersonId(Object createPersonId) {
            this.createPersonId = createPersonId;
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

        @Override
        public String toString() {
            return "DataBean{" +
                    "id='" + id + '\'' +
                    ", idNo='" + idNo + '\'' +
                    ", name='" + name + '\'' +
                    ", sex=" + sex +
                    ", nation=" + nation +
                    ", education=" + education +
                    ", type=" + type +
                    ", birthday='" + birthday + '\'' +
                    ", birthPlace='" + birthPlace + '\'' +
                    ", mobile='" + mobile + '\'' +
                    ", occupation='" + occupation + '\'' +
                    ", address='" + address + '\'' +
                    ", employer='" + employer + '\'' +
                    ", priority=" + priority +
                    ", description='" + description + '\'' +
                    ", icbcOpenId=" + icbcOpenId +
                    ", createPersonId=" + createPersonId +
                    ", visibleFaceRegistered=" + visibleFaceRegistered +
                    ", nirFaceRegistered=" + nirFaceRegistered +
                    '}';
        }

    }
}
