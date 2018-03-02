package com.moinapp.wuliao.modules.wowo.network;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/6/8.
 * 窝窝模块基本的网络交互reponse信息
 */
public class BaseResponse implements Serializable {
    private int result;
    private int error;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
