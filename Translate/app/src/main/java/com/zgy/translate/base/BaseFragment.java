package com.zgy.translate.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;


/**
 * Created by zhou on 2017/5/4.
 */

public abstract class BaseFragment extends Fragment{

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    public void init(){
        initView();
        initEvent();
        initData();
    }

    public abstract void initView();
    public abstract void initEvent();
    public abstract void initData();


}
