package com.zgy.translate.activitys;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.controllers.ActivityController;
import com.zgy.translate.controllers.RequestController;
import com.zgy.translate.domains.dtos.UserInfoDTO;
import com.zgy.translate.domains.request.CommonRequest;
import com.zgy.translate.domains.response.CommonResponse;
import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.managers.GlideImageManager;
import com.zgy.translate.managers.GsonManager;
import com.zgy.translate.managers.UserMessageManager;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.RedirectUtil;
import com.zgy.translate.widget.CommonBar;

import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MySettingActivity extends BaseActivity implements CommonBar.CommonBarInterface, CompoundButton.OnCheckedChangeListener,
        RequestController.RequestCallInterface{

    @BindView(R.id.ams_cb) CommonBar commonBar;
    @BindView(R.id.ams_tv_name) TextView tv_name;
    @BindView(R.id.ams_tv_per) TextView tv_per;
    @BindView(R.id.ams_civ_headerIcon) CircleImageView civ_headerIcon;
    @BindView(R.id.ams_cb_choose) CheckBox cb_choose;

    private boolean isExit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {
    }

    @Override
    public void initEvent() {
        commonBar.setBarInterface(this);
        cb_choose.setOnCheckedChangeListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void checkLeftIcon() {
        finish();
    }

    @Override
    public void checkRightIcon() {

    }

    @Override
    public void blueOff() {

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
    protected void onResume() {
        super.onResume();
        if(GlobalParams.userInfoDTO == null){
            GlobalParams.userInfoDTO = UserMessageManager.getUserInfo(this);
        }
        showUser(GlobalParams.userInfoDTO);
    }

    @OnClick(R.id.ams_rl_baseMsg) void msg(){
        RedirectUtil.redirect(this, MyMsgActivity.class);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.ams_cb_choose:
                UserInfoDTO dto = UserMessageManager.getUserInfo(this);
                if(cb_choose.isChecked()){
                    dto.setMic(true);
                }else{
                    dto.setMic(false);
                }
                UserMessageManager.deleteUserInfo(this);
                String re = GsonManager.getInstance().toJson(dto);
                GlobalParams.userInfoDTO = dto;
                UserMessageManager.saveUserInfo(this, re);
                break;
        }
    }

    @OnClick(R.id.ams_cn_share) void share(){
        showShare();
    }

    @OnClick(R.id.ams_cn_question) void question(){
        RedirectUtil.redirect(this, QuestionActivity.class);
    }

    @OnClick(R.id.ams_cn_about) void about(){
        RedirectUtil.redirect(this, AboutActivity.class);
    }

    @OnClick(R.id.ams_tv_exit) void exi(){
        isExit = true;
        super.progressDialog.show();
        CommonRequest request = new CommonRequest();
        request.setPhone(UserMessageManager.getUserInfo(this).getPhone());
        RequestController.getInstance().init(this)
                .addRequest(RequestController.LOGOUT, request)
                .addCallInterface(this)
                .build();
    }

    private static final String TEXT = "我有一副超棒的蓝牙耳机，跟专用的翻译APP结合使用，很智能，功能多，操作方便，推荐给你。";
    private static final String TITLE = "蓝牙智能耳机翻译APP";
    private static final String URL = "http://www.sharesdk.cn";

    /**一键分享*/
    private void showShare() {
        UMImage image = new UMImage(MySettingActivity.this, GlobalParams.userInfoDTO.getIcon());
        UMWeb web = new UMWeb(URL);
        web.setTitle(TITLE);
        web.setDescription(TEXT);
        web.setThumb(image);
        new ShareAction(MySettingActivity.this).withMedia(web)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ,
                        SHARE_MEDIA.QZONE, SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        Log.i("onStart", "onStart");
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        ConfigUtil.showToask(MySettingActivity.this, "分享成功");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        ConfigUtil.showToask(MySettingActivity.this, "分享失败" + throwable.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        ConfigUtil.showToask(MySettingActivity.this, "取消分享");
                    }
                }).open();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void success(CommonResponse response) {
        super.progressDialog.dismiss();
        if(isExit){
            UserMessageManager.deleteUserInfo(this);
            GlobalParams.userInfoDTO = null;
            ActivityController.finishActivity();
            RedirectUtil.redirect(this, LoginActivity.class);
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

    private void showUser(UserInfoDTO dto){
        if(dto.getName() != null){
            tv_name.setText(dto.getName());
        }else{
            tv_name.setText("用户");
        }
        if(dto.getSignature() != null){
            tv_per.setVisibility(View.VISIBLE);
            tv_per.setText(dto.getSignature());
        }else{
            tv_per.setVisibility(View.GONE);
        }
        if(dto.getIcon() != null){
            GlideImageManager.showURLDownloadImage(this, dto.getIcon(), civ_headerIcon);
        }
        if(dto.isMic()){
            cb_choose.setChecked(true);
        }else{
            cb_choose.setChecked(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
        RequestController.getInstance().removeParams();
    }
}
