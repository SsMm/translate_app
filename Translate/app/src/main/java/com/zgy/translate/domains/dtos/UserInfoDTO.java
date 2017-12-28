package com.zgy.translate.domains.dtos;

/**
 * Created by zhouguangyue on 2017/12/13.
 */

public class UserInfoDTO {
    private String name;
    private String birthday;
    private String sex;
    private String icon;
    private String signature;
    private String appKey;
    private boolean mic = true; //扬声器还是听筒
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setMic(boolean mic) {
        this.mic = mic;
    }

    public boolean isMic() {
        return mic;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
