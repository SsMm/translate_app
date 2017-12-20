package com.zgy.translate.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zgy.translate.R;
import com.zgy.translate.base.BaseActivity;

import butterknife.ButterKnife;

public class FindPawActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_paw);
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
}
