package com.zgy.translate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zgy.translate.R;
import com.zgy.translate.adapters.interfaces.BluetoothDeviceAdapterInterface;
import com.zgy.translate.domains.dtos.BluetoothDeviceDTO;

import java.util.List;

/**
 * Created by zhouguangyue on 2017/11/28.
 */

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.DeviceViewHolder> {

    private Context mContext;
    private DeviceViewHolder deviceViewHolder = null;
    private List<BluetoothDeviceDTO> deviceDTOList;
    private BluetoothDeviceAdapterInterface adapterInterface;

    public BluetoothDeviceAdapter(Context context, List<BluetoothDeviceDTO> deviceDTOs, BluetoothDeviceAdapterInterface adapterInterface){
        mContext = context;
        deviceDTOList = deviceDTOs;
        this.adapterInterface = adapterInterface;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceViewHolder(LayoutInflater.from(mContext.getApplicationContext())
                .inflate(R.layout.item_bluetooth_device, parent, false));
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        BluetoothDeviceDTO deviceDTO = deviceDTOList.get(position);
        if(deviceDTO.getDevice_name() == null){
            holder.name.setText(deviceDTO.getDevice_address());
        }else{
            holder.name.setText(deviceDTO.getDevice_name());
        }

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterInterface.bongDevice(deviceDTO, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return deviceDTOList.size();
    }



    class DeviceViewHolder extends RecyclerView.ViewHolder{
        TextView name;

        private DeviceViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.ibd_tv_deviceName);
        }

    }


}
