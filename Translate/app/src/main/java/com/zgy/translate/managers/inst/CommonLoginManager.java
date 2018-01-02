package com.zgy.translate.managers.inst;

import android.content.Context;

import com.zgy.translate.activitys.VoiceTranslateActivity;
import com.zgy.translate.controllers.RequestController;
import com.zgy.translate.domains.dtos.UserInfoDTO;
import com.zgy.translate.domains.request.CommonRequest;
import com.zgy.translate.domains.response.CommonResponse;
import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.managers.GsonManager;
import com.zgy.translate.managers.UserMessageManager;
import com.zgy.translate.managers.inst.inter.CommonLoginManagerInterface;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.RedirectUtil;
import com.zgy.translate.utils.StringUtil;

/**
 * Created by zhouguangyue on 2018/1/2.
 */

public class CommonLoginManager implements RequestController.RequestCallInterface {

    private Context mContext;
    private CommonLoginManagerInterface managerInterface;
    private String phone;

    public CommonLoginManager(Context context, CommonLoginManagerInterface managerInterface){
        mContext = context;
        this.managerInterface = managerInterface;
    }


    public void comLogin(String num, String paw){
        if(StringUtil.isEmpty(num) || StringUtil.isEmpty(paw)){
            ConfigUtil.showToask(mContext, "登录信息不全");
            return;
        }
        phone = num;
        CommonRequest request = new CommonRequest();
        request.setPhone(num);
        request.setPassword(paw);
        request.setAppId("earbud_app");
        request.setDevice(ConfigUtil.phoneDevice() + ConfigUtil.phoneMsg(mContext));
        RequestController.getInstance().init(mContext)
                .addRequest(RequestController.LOGIN, request)
                .addCallInterface(this)
                .build();
    }


    @Override
    public void success(CommonResponse response) {
        if(response != null){
            if(UserMessageManager.isUserInfo(mContext)){
                UserMessageManager.deleteUserInfo(mContext);
            }
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setAppKey(response.getAppKey());
            userInfoDTO.setBirthday(response.getBirthday());
            userInfoDTO.setIcon(response.getIcon());
            userInfoDTO.setName(response.getName());
            userInfoDTO.setSignature(response.getSignature());
            userInfoDTO.setSex(response.getSex());
            userInfoDTO.setMic(true);
            userInfoDTO.setPhone(phone);
            GlobalParams.userInfoDTO = userInfoDTO;
            String user = GsonManager.getInstance().toJson(userInfoDTO);
            UserMessageManager.saveUserInfo(mContext, user);
            managerInterface.loginSuccess();
        }

    }

    @Override
    public void error(CommonResponse response) {
        managerInterface.loginError();
    }

    @Override
    public void fail(String error) {
        managerInterface.loginFail();
    }


}
