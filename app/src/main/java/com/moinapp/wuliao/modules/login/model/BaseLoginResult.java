package com.moinapp.wuliao.modules.login.model;

/**
 * Created by liujiancheng on 15/5/11.
 */
public class BaseLoginResult {
    private int result;
    private int error;
    private String uid;
    private String passport;

    public void setResult(int result) { this.result = result; }
    public int getResult() { return this.result; }
    public void setError(int error) { this.error = error; }
    public int getError() { return this.error; }
    public void setUid(String uid) { this.uid = uid; }
    public String getUid() { return this.uid; }
    public void setPassport(String passport) { this.passport = passport; }
    public String getPassport() { return this.passport; }
}
