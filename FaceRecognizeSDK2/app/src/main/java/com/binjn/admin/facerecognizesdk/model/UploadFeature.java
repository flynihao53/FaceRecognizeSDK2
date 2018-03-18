package com.binjn.admin.facerecognizesdk.model;

import java.io.File;

/**
 * Created by wangdajian on 2018/1/12.
 */

public class UploadFeature {
    private String url;
    private String token;
    private File file;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
