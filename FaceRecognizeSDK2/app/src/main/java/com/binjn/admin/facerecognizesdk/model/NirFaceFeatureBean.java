package com.binjn.admin.facerecognizesdk.model;

import java.io.Serializable;

/**
 * Created by wangdajian on 2017/12/1.
 */

public class NirFaceFeatureBean {

    /**
     * code : 0
     * feature : {"personId":"530101199002130018","hashcode":"6433b7feab2dabb8531d7ed0c6b03dcb","value":"ZGprc2xkanNsaGZqc2RrbGZqZHNsZmtkc2o="}
     */

    private String code;
    private FeatureBean feature;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public FeatureBean getFeature() {
        return feature;
    }

    public void setFeature(FeatureBean feature) {
        this.feature = feature;
    }

    public static class FeatureBean implements Serializable{
        /**
         * personId : 530101199002130018
         * hashcode : 6433b7feab2dabb8531d7ed0c6b03dcb
         * value : ZGprc2xkanNsaGZqc2RrbGZqZHNsZmtkc2o=
         */

        private String personId;
        private String hashcode;
        private String value;

        public String getPersonId() {
            return personId;
        }

        public void setPersonId(String personId) {
            this.personId = personId;
        }

        public String getHashcode() {
            return hashcode;
        }

        public void setHashcode(String hashcode) {
            this.hashcode = hashcode;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
