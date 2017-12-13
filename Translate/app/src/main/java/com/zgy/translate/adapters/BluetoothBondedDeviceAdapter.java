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
import com.zgy.translate.utils.StringUtil;

import java.util.List;

/**
 * Created by zhouguangyue on 2017/12/13.
 */

public class BluetoothBondedDeviceAdapter extends RecyclerView.Adapter<BluetoothBondedDeviceAdapter.BluetoothBondedViewHolder> {
    public static final String CON_STATE = "connection";
    public static final String Bon_STATE = "bonded";

    private Context mContext;
    private List<BluetoothBondedDeviceDTO> deviceDTOs;
    private BluetoothBondedDeviceAdapterInterface adapterInterface;


    public BluetoothBondedDeviceAdapter(Context context, List<BluetoothBondedDeviceDTO> deviceDTOs,
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
        BluetoothBondedDeviceDTO dto = deviceDTOs.get(position);
        if(dto.getState().equals(Bon_STATE)){
            holder.state.setText("");
        }else{
            holder.state.setText("已连接");
        }
        if(StringUtil.isEmpty(dto.getDevice().getName())){
            holder.name.setText(dto.getDevice().getAddress());
        }else{
            holder.name.setText(dto.getDevice().getName());
        }

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterInterface.bondedToConnection(holder.getAdapterPosition(), dto.getDevice());
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
            name = (TextView) itemView.findViewById(R.id.ibbd_tv_bondedName);
            state = (TextView) itemView.findViewById(R.id.ibbd_tv_bondedState);
        }
    }

}
