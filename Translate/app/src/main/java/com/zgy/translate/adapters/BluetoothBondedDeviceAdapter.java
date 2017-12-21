package com.zgy.translate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zgy.translate.R;
import com.zgy.translate.adapters.interfaces.BluetoothBondedDeviceAdapterInterface;
import com.zgy.translate.domains.dtos.BluetoothBondedDeviceDTO;
import com.zgy.translate.domains.dtos.BluetoothSocketDTO;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.utils.StringUtil;

import java.util.List;

/**
 * Created by zhouguangyue on 2017/12/13.
 */

public class BluetoothBondedDeviceAdapter extends RecyclerView.Adapter<BluetoothBondedDeviceAdapter.BluetoothBondedViewHolder> {
    public static final String CON_STATE = "connection";
    public static final String CONING_STATE = "connecting";
    public static final String Bon_STATE = "bonded";

    private Context mContext;
    private List<BluetoothSocketDTO> deviceDTOs;
    private BluetoothBondedDeviceAdapterInterface adapterInterface;


    public BluetoothBondedDeviceAdapter(Context context, List<BluetoothSocketDTO> deviceDTOs,
                                        BluetoothBondedDeviceAdapterInterface adapterInterface){
        mContext = context;
        this.deviceDTOs = deviceDTOs;
        this.adapterInterface = adapterInterface;
    }

    @Override
    public BluetoothBondedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new BluetoothBondedViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_bluetooth_bonded_device, parent, false));
    }

    @Override
    public void onBindViewHolder(BluetoothBondedViewHolder holder, int position) {
        BluetoothSocketDTO dto = deviceDTOs.get(position);
        if(Bon_STATE.equals(dto.getState())){
            holder.state.setVisibility(View.GONE);
        }else if(CONING_STATE.equals(dto.getState())){
            holder.state.setVisibility(View.VISIBLE);
            holder.state.setText(GlobalConstants.STATE_CONNECTING);
        }else if(CON_STATE.equals(dto.getState())){
            holder.state.setVisibility(View.VISIBLE);
            holder.state.setText(GlobalConstants.STATE_CONNECTED);
        }
        if(StringUtil.isEmpty(dto.getmBluetoothDevice().getName())){
            holder.name.setText(dto.getmBluetoothDevice().getAddress());
        }else{
            holder.name.setText(dto.getmBluetoothDevice().getName());
        }

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterInterface.bondedToConnection(holder.getAdapterPosition(), dto.getmBluetoothDevice());
            }
        });

    }

    @Override
    public int getItemCount() {
        return deviceDTOs.size();
    }


    class BluetoothBondedViewHolder extends RecyclerView.ViewHolder{
        TextView name, state;
        private BluetoothBondedViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ibbd_tv_bondedName);
            state = itemView.findViewById(R.id.ibbd_tv_bondedState);
        }
    }

}
