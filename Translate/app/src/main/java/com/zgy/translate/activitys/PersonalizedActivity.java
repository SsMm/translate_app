package com.zgy.translate.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.controllers.RequestController;
import com.zgy.translate.domains.dtos.UserInfoDTO;
import com.zgy.translate.domains.request.CommonRequest;
import com.zgy.translate.domains.response.CommonResponse;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.managers.GsonManager;
import com.zgy.translate.managers.UserMessageManager;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.StringUtil;
import com.zgy.translate.widget.CommonBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalizedActivity extends BaseActivity implements CommonBar.CommonBarInterface,
        RequestController.RequestCallInterface{

    @BindView(R.id.ap_cb) CommonBar commonBar;
    @BindView(R.id.ap_et_content) EditText et_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalized);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initEvent() {
        commonBar.setBarInterface(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void disConnected() {

    }

    @Override
    public void connected() {

    }

    @Override
    public void disNetConnected() {

    }

    @Override
    public void netConnected() {

    }

    @Override
    public void checkLeftIcon() {
        finish();
    }

    @Override
    public void checkRightIcon() {

    }

    @OnClick(R.id.ap_tv_submit) void submit(){
        String content = et_content.getText().toString();
        if(StringUtil.isEmpty(content)){
            ConfigUtil.showToask(this, GlobalConstants.NULL_MSG);
            return;
        }
        if(content.length() > 100){
            ConfigUtil.showToask(this, "超过100字");
            return;
        }
        super.progressDialog.show();
        CommonRequest request = new CommonRequest();
        request.setSignature(content);
        RequestController.getInstance().init(this)
                .addRequest(RequestController.PROFILE, request)
                .addCallInterface(this)
                .build();
    }

    @Override
    public void success(CommonResponse response) {
        super.progressDialog.dismiss();
        if(response != null) {
            if (UserMessageManager.isUserInfo(this)) {
                UserMessageManager.deleteUserInfo(this);
            }
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setAppKey(GlobalParams.userInfoDTO.getAppKey());
            userInfoDTO.setBirthday(response.getBirthday());
            userInfoDTO.setIcon(response.getIcon());
            userInfoDTO.setName(response.getName());
            userInfoDTO.setSignature(response.getSignature());
            userInfoDTO.setSex(response.getSex());
            userInfoDTO.setMic(GlobalParams.userInfoDTO.isMic());
            userInfoDTO.setPhone(response.getPhone());
            GlobalParams.userInfoDTO = userInfoDTO;
            String user = GsonManager.getInstance().toJson(userInfoDTO);
            UserMessageManager.saveUserInfo(this, user);
            finish();
        }
    }

    @Override
    public void error(CommonResponse response) {
        super.progressDialog.dismiss();
    }

    @Override
    public void fail(String error) {
        super.progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RequestController.getInstance().removeParams();
    }
}
