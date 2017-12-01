package com.zgy.translate.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private List<BluetoothDevice> deviceDTOList;
    private BluetoothDeviceAdapterInterface adapterInterface;

    public BluetoothDeviceAdapter(Context context, List<BluetoothDevice> deviceDTOs, BluetoothDeviceAdapterInterface adapterInterface){
        mContext = context;
        deviceDTOList = deviceDTOs;
        this.adapterInterface = adapterInterface;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_bluetooth_device, parent, false));
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        BluetoothDevice device = deviceDTOList.get(position);
        if(device.getName() == null){
            holder.name.setText(device.getAddress());
        }else{
            holder.name.setText(device.getName());
        }

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterInterface.bongDevice(device, holder.getAdapterPosition());
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
