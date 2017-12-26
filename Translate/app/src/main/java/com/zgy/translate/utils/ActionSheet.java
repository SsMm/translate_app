package com.zgy.translate.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgy.translate.R;


/**
 *
 */

public class ActionSheet extends Dialog {

    private LinearLayout itemContainer;
    private Context mContext;
    private LinearLayout layout_cancel;


    private ActionSheet(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        initView();
        regListeners();
    }

    public static ActionSheet create(Context context){
        return new ActionSheet(context, R.style.CustomDialog);
    }


    public ActionSheet setOptItems(OptItem... optItems){
        for (int i = 0 ; i < optItems.length ; i++){
            OptItem element = optItems[i];
            LinearLayout itemView = (LinearLayout) LayoutInflater.from(mContext.getApplicationContext())
                    .inflate(R.layout.item_actionsheet, null);
            TextView tx_content = itemView.findViewById(R.id.tx_content);
            View lineView = itemView.findViewById(R.id.view_actionSheet_line);
            if (element.getTxColor() != 0)
                tx_content.setTextColor(element.getTxColor());
            if (element.getTxSize() != 0)
                tx_content.setTextSize(element.getTxSize());
            tx_content.setText(element.getItemContent());
            if (element.getOnClickListener() != null)
                itemView.setOnClickListener(element.getOnClickListener());
            if (i == optItems.length-1){
                lineView.setVisibility(View.GONE);
            }
            itemContainer.addView(itemView);
        }
        return this;
    }

    private void initView(){
        LinearLayout contentView = (LinearLayout) LayoutInflater.from(mContext.getApplicationContext())
                .inflate(R.layout.layout_actionsheet, null);
        itemContainer = (LinearLayout)contentView.findViewById(R.id.layout_actionsheet_items_container);
        layout_cancel = (LinearLayout)contentView.findViewById(R.id.layout_cancel);
        setContentView(contentView);

        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.style.pop_animation);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
    }

    private void regListeners(){

        layout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActionSheet.this.isShowing()){
                    ActionSheet.this.dismiss();
                }
            }
        });
    }

    public void onMyDestroy(){
        layout_cancel.setOnClickListener(null);
        layout_cancel = null;
        if(itemContainer != null && itemContainer.getChildCount() != 0){
            itemContainer.removeAllViews();
            itemContainer = null;
        }

    }

}
