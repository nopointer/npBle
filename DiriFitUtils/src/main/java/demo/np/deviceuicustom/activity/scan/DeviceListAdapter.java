package demo.np.deviceuicustom.activity.scan;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import demo.np.deviceuicustom.R;
import demo.np.deviceuicustom.base.adapter.BaseRecycleAdapter;
import demo.np.deviceuicustom.base.adapter.BaseRecycleTag;
import npble.nopointer.device.BleDevice;

/**
 * 设备列表
 */
public class DeviceListAdapter extends BaseRecycleAdapter<BleDevice, DeviceListAdapter.ViewHolder> {

    private HashMap<String, BleDevice> bleDeviceHashMap = new HashMap<>();


    /**
     * 全选
     */
    public void allChoice() {
        if (bleDeviceHashMap == null) {
            bleDeviceHashMap = new HashMap<>();
        }
        for (BleDevice bleDevice : dataList) {
            bleDeviceHashMap.put(bleDevice.getMac(), bleDevice);
        }
        notifyDataSetChanged();
    }

    /**
     * 全不选
     */
    public void allNotChoice() {
        if (bleDeviceHashMap != null) {
            bleDeviceHashMap.clear();
        }
        notifyDataSetChanged();
    }

    public DeviceListAdapter(Context context, List<BleDevice> dataList) {
        super(context, dataList);
    }

    @Override
    public int loadItemView() {
        return R.layout.item_scan_device_info;
    }

    @Override
    public ViewHolder instanceTag(View convertView) {
        return new ViewHolder(convertView);
    }

    @Override
    public void handDataAndView(ViewHolder holder, final BleDevice data, int position) {
        holder.deviceNameTv.setText(data.getName());
        holder.deviceMacTv.setText(data.getMac());
        holder.deviceRssiTv.setText(data.getRssi() + "db");
        holder.device_checkbox.setChecked(bleDeviceHashMap.containsKey(data.getMac()));

        holder.device_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bleDeviceHashMap.put(data.getMac(), data);
                } else {
                    bleDeviceHashMap.remove(data.getMac());
                }
            }
        });
    }


    public static class ViewHolder extends BaseRecycleTag {

        @BindView(R.id.device_name_tv)
        TextView deviceNameTv;

        @BindView(R.id.device_mac_tv)
        TextView deviceMacTv;

        @BindView(R.id.device_checkbox)
        CheckBox device_checkbox;

        @BindView(R.id.device_rssi_tv)
        TextView deviceRssiTv;


        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


}
