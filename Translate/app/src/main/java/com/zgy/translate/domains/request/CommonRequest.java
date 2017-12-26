package com.zgy.translate.domains.request;

import com.zgy.translate.base.BaseDomain;

/**
 * Created by zhouguangyue on 2017/12/26.
 */

public class CommonRequest extends BaseDomain{
    private String Name;
    private String Phone;
    private String Birthday;
    private String Sex;
    private String Icon;
    private String AppKey;
    private String Signature;
    private String AppId;
    private String PhoneCode;
    private String Password;
    private String PasswrodRepeat;
    private String Device;
    private String file;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public String getAppKey() {
        return AppKey;
    }

    public void setAppKey(String appKey) {
        AppKey = appKey;
    }

    public String getSignature() {
        return Signature;
    }

    public void setSignature(String signature) {
        Signature = signature;
    }

    public String getAppId() {
        return AppId;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }

    public String getPhoneCode() {
        return PhoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        PhoneCode = phoneCode;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPasswrodRepeat() {
        return PasswrodRepeat;
    }

    public void setPasswrodRepeat(String passwrodRepeat) {
        PasswrodRepeat = passwrodRepeat;
    }

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
