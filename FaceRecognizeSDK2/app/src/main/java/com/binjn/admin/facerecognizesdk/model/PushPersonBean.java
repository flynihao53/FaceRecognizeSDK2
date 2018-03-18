package com.binjn.admin.facerecognizesdk.model;

/**
 * Created by wangdajian on 2018/3/17.
 */

public class PushPersonBean {

    /**
     * title : 10500
     * content : {"data":{"MD5":"11","id":"107206832426057728","idNo":"530103196212290018","name":"吕远杰","nirFaceRegistered":0,"visibleFaceRegistered":0},"action":"update","pushtype":10500}
     */

    private String title;
    private ContentBean content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * data : {"MD5":"11","id":"107206832426057728","idNo":"530103196212290018","name":"吕远杰","nirFaceRegistered":0,"visibleFaceRegistered":0}
         * action : update
         * pushtype : 10500
         */

        private DataBean data;
        private String action;
        private int pushtype;

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public int getPushtype() {
            return pushtype;
        }

        public void setPushtype(int pushtype) {
            this.pushtype = pushtype;
        }

        public static class DataBean {
            /**
             * MD5 : 11
             * id : 107206832426057728
             * idNo : 530103196212290018
             * name : 吕远杰
             * nirFaceRegistered : 0
             * visibleFaceRegistered : 0
             */

            private String MD5;
            private String id;
            private String idNo;
            private String name;
            private int nirFaceRegistered;
            private int visibleFaceRegistered;

            public String getMD5() {
                return MD5;
            }

            public void setMD5(String MD5) {
                this.MD5 = MD5;
            }

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

            public int getNirFaceRegistered() {
                return nirFaceRegistered;
            }

            public void setNirFaceRegistered(int nirFaceRegistered) {
                this.nirFaceRegistered = nirFaceRegistered;
            }

            public int getVisibleFaceRegistered() {
                return visibleFaceRegistered;
            }

            public void setVisibleFaceRegistered(int visibleFaceRegistered) {
                this.visibleFaceRegistered = visibleFaceRegistered;
            }
        }
    }
}
