package com.zgy.translate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zgy.translate.R;

/**
 * Created by zhouguangyue on 2017/12/19.
 */

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private Context mContext;

    public QuestionAdapter(Context context){
        mContext = context;
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QuestionViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_question, parent, false));
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder{
        TextView title, content;

        private QuestionViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.iq_tv_title);
            content = itemView.findViewById(R.id.iq_tv_content);
        }
    }

}
