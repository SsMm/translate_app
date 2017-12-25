package com.zgy.translate.activitys;


import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.RedirectUtil;
import com.zgy.translate.utils.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.al_et_phoneNum) EditText et_phoneNum; //手机号
    @BindView(R.id.al_et_phonePaw) EditText et_phonePaw; //密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {

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

    @OnClick(R.id.al_tv_login) void login(){
        String num = et_phoneNum.getText().toString();
        String paw = et_phonePaw.getText().toString();
        if(StringUtil.isEmpty(num) || StringUtil.isEmpty(paw)){
            ConfigUtil.showToask(this, "信息不能为空");
        }else{

        }

    }

    @OnClick(R.id.al_tv_register) void register(){
        RedirectUtil.redirect(this, RegisterActivity.class);
    }

    /**忘记密码*/
    @OnClick(R.id.al_ll_forgetMsg) void forget(){
        RedirectUtil.redirect(this, ForgetPawActivity.class);
    }


}
