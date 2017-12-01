package com.zgy.translate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zgy.translate.R;
import com.zgy.translate.base.BaseFragment;

import butterknife.ButterKnife;

/**
 * Created by zhouguangyue on 2017/12/1.
 */

public class BleBluetoothManagerFragment extends BaseFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ble_bluetooth_manager, container, false);
        ButterKnife.bind(this, view);
        return view;
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
