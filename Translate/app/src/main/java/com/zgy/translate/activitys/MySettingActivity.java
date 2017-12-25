package com.zgy.translate.activitys;


import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.utils.RedirectUtil;
import com.zgy.translate.widget.CommonBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.hdodenhof.circleimageview.CircleImageView;

public class MySettingActivity extends BaseActivity implements CommonBar.CommonBarInterface, CompoundButton.OnCheckedChangeListener{

    @BindView(R.id.ams_cb) CommonBar commonBar;
    @BindView(R.id.ams_tv_name) TextView tv_name;
    @BindView(R.id.ams_tv_per) TextView tv_per;
    @BindView(R.id.ams_civ_headerIcon) CircleImageView civ_headerIcon;
    @BindView(R.id.ams_cb_choose) CheckBox cb_choose;


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
    public void disConnected() {

    }

    @Override
    public void disNetConnected() {

    }

    @Override
    public void netConnected() {

    }

    @OnClick(R.id.ams_rl_baseMsg) void msg(){
        RedirectUtil.redirect(this, MyMsgActivity.class);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.ams_cb_choose:
                if(cb_choose.isChecked()){

                }else{

                }
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

    @OnClick(R.id.ams_tv_exit) void exit(){

    }

    /**一键分享*/
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share_title));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }


}
