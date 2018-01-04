package com.zgy.translate.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zgy.translate.R;
import com.zgy.translate.adapters.QuestionAdapter;
import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.controllers.RequestController;
import com.zgy.translate.widget.CommonBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionActivity extends BaseActivity implements CommonBar.CommonBarInterface{

    @BindView(R.id.aq_rv_questionList)
    RecyclerView rv_question;
    @BindView(R.id.aq_cb)
    CommonBar cb_ComBar;

    private QuestionAdapter questionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {
        cb_ComBar.setBarInterface(this);

        rv_question.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        questionAdapter = new QuestionAdapter(this);
        rv_question.setAdapter(questionAdapter);

    }

    @Override
    public void initEvent() {

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
    public void connected() {

    }

    @Override
    public void disNetConnected() {

    }

    @Override
    public void netConnected() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RequestController.getInstance().removeParams();
    }
}
