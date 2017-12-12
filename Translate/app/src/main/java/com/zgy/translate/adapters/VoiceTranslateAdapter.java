package com.zgy.translate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.zgy.translate.adapters.interfaces.VoiceTranslateAdapterInterface;
import com.zgy.translate.domains.dtos.VoiceTransDTO;

import java.util.List;

/**
 * Created by zhouguangyue on 2017/12/12.
 */

public class VoiceTranslateAdapter extends RecyclerView.Adapter<VoiceTranslateAdapter.VoiceTranslateViewHolder>{

    private Context mContext;
    private List<VoiceTransDTO> transDTOs;
    private VoiceTranslateAdapterInterface adapterInterface;

    public VoiceTranslateAdapter(Context context, List<VoiceTransDTO> dtos, VoiceTranslateAdapterInterface inter){
        mContext = context;
        transDTOs = dtos;
        adapterInterface = inter;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public VoiceTranslateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VoiceTranslateViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return transDTOs.size();
    }

    class VoiceTranslateViewHolder extends RecyclerView.ViewHolder{

        public VoiceTranslateViewHolder(View itemView) {
            super(itemView);
        }
    }

}
