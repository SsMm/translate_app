package com.zgy.translate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgy.translate.R;
import com.zgy.translate.adapters.interfaces.VoiceTranslateAdapterInterface;
import com.zgy.translate.domains.dtos.VoiceTransDTO;

import java.util.List;

/**
 * Created by zhouguangyue on 2017/12/12.
 */

public class VoiceTranslateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final String FROM_PHONE = "phone";
    public static final String FROM_BLUE = "blue";

    private static final int PHONE_TYPE = 0;
    private static final int BLUE_TYPE = 1;

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
        VoiceTransDTO dto = transDTOs.get(position);
        if(FROM_PHONE.equals(dto.getLagType())){
            return PHONE_TYPE;
        }else{
            return BLUE_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case PHONE_TYPE:
                return new VoiceTranPhoneViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_tran_phone, parent, false));
            case BLUE_TYPE:
                return new VoiceTranBlueViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_tran_bluetooth, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VoiceTransDTO dto = transDTOs.get(position);
        if(holder instanceof VoiceTranPhoneViewHolder){
            ((VoiceTranPhoneViewHolder) holder).src.setText(dto.getLanSrc());
            ((VoiceTranPhoneViewHolder) holder).dst.setText(dto.getLanDst());
            ((VoiceTranPhoneViewHolder) holder).tts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterInterface.goTTS(dto.getLanDst());
                }
            });
        }else if(holder instanceof VoiceTranBlueViewHolder){
            ((VoiceTranBlueViewHolder) holder).src.setText(dto.getLanSrc());
            ((VoiceTranBlueViewHolder) holder).dst.setText(dto.getLanDst());
            ((VoiceTranBlueViewHolder) holder).tts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterInterface.goTTS(dto.getLanDst());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return transDTOs.size();
    }


    private class VoiceTranPhoneViewHolder extends RecyclerView.ViewHolder{
        TextView src, dst;
        ImageView tts;
        private VoiceTranPhoneViewHolder(View itemView) {
            super(itemView);
            src = itemView.findViewById(R.id.itp_tv_src);
            dst = itemView.findViewById(R.id.itp_tv_dst);
            tts = itemView.findViewById(R.id.itp_iv_goTTS);
        }
    }

    private class VoiceTranBlueViewHolder extends RecyclerView.ViewHolder{
        TextView src, dst;
        ImageView tts;
        private VoiceTranBlueViewHolder(View itemView) {
            super(itemView);
            src = itemView.findViewById(R.id.itb_tv_src);
            dst = itemView.findViewById(R.id.itb_tv_dst);
            tts = itemView.findViewById(R.id.itb_iv_goTTS);
        }
    }

}
