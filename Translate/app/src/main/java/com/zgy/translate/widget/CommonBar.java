package com.zgy.translate.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgy.translate.R;
import com.zgy.translate.managers.GlideImageManager;

/**
 * Created by zhouguangyue on 2017/12/19.
 */

public class CommonBar extends LinearLayout {

    private CommonBarInterface barInterface;

    public CommonBar(Context context) {
        super(context);
        init(context, null);
    }

    public CommonBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet set){
        TypedArray typedArray = context.obtainStyledAttributes(set, R.styleable.CommonBar);
        String title = typedArray.getString(R.styleable.CommonBar_bar_title);
        boolean show_left = typedArray.getBoolean(R.styleable.CommonBar_show_left, false);
        int left_icon = typedArray.getResourceId(R.styleable.CommonBar_left_icon, 0);
        boolean show_right = typedArray.getBoolean(R.styleable.CommonBar_show_right, false);
        int right_icon = typedArray.getResourceId(R.styleable.CommonBar_right_icon, 0);

        typedArray.recycle();

        LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.widget_common_bar, this);

        ImageView leftIcon = findViewById(R.id.wcb_leftIcon);
        ImageView rightIcon = findViewById(R.id.wcb_rightIcon);
        TextView centerTitle = findViewById(R.id.wcb_title);

        if(show_left){
            leftIcon.setVisibility(VISIBLE);
            //GlideImageManager.showResImage(context, left_icon, leftIcon);
        }else{
            leftIcon.setVisibility(GONE);
        }

        leftIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                barInterface.checkLeftIcon();
            }
        });

        if(show_right){
            rightIcon.setVisibility(VISIBLE);
            GlideImageManager.showResImage(context, right_icon, rightIcon);
        }else{
            rightIcon.setVisibility(GONE);
        }

        rightIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                barInterface.checkRightIcon();
            }
        });

        centerTitle.setText(title);

    }

    public void setBarInterface(CommonBarInterface barInterface){
        this.barInterface = barInterface;
    }



    public interface CommonBarInterface{
        void checkLeftIcon();
        void checkRightIcon();
    }




}
