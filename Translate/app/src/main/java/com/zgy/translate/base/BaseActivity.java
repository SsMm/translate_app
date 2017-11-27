package com.zgy.translate.base;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zgy.translate.controllers.ActivityController;


/**
 * Created by zhou on 2017/4/27.
 */

public abstract class BaseActivity extends AppCompatActivity{



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
    }

    public void init(){
        initView();
        initEvent();
        initData();
    }

    public abstract void initView();
    public abstract void initEvent();
    public abstract void initData();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);

    }


}
