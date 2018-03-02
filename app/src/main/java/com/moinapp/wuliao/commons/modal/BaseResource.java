package com.moinapp.wuliao.commons.modal;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/6/29.
 */
public class BaseResource implements Serializable {

    private String fileType;
    private String uri;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
