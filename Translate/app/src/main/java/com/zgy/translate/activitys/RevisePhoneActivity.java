package com.zgy.translate.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.widget.CommonBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RevisePhoneActivity extends BaseActivity implements CommonBar.CommonBarInterface{

    @BindView(R.id.arp_cb) CommonBar commonBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_phone);
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
}
