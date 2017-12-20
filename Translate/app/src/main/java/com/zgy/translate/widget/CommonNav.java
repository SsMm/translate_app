package com.zgy.translate.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgy.translate.R;

/**
 * Created by zhouguangyue on 2017/12/20.
 */

public class CommonNav extends LinearLayout {

    private TextView right_title;

    public CommonNav(Context context) {
        super(context);
        init(context, null);
    }

    public CommonNav(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonNav);
        String leftTitle = typedArray.getString(R.styleable.CommonNav_nav_leftTitle);
        boolean showRight = typedArray.getBoolean(R.styleable.CommonNav_nav_show_right, false);
        String rightTitle = typedArray.getString(R.styleable.CommonNav_nav_rightTitle);

        typedArray.recycle();

        LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.widget_common_nav, this);

        TextView left_title = findViewById(R.id.wcn_tv_leftTitle);
        right_title = findViewById(R.id.wcn_tv_rightTitle);

        if(showRight){
            right_title.setVisibility(VISIBLE);
            right_title.setText(rightTitle);
        }else {
            right_title.setVisibility(GONE);
        }

        left_title.setText(leftTitle);
    }

    public void setRightTitle(String title){
        right_title.setVisibility(VISIBLE);
        right_title.setText(title);
    }


}
