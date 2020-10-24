package demo.nopointer.npDemo.scan;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import demo.nopointer.npDemo.R;
import demo.nopointer.npDemo.base.adapter.BaseRecycleAdapter;
import demo.nopointer.npDemo.base.adapter.BaseRecycleTag;
import npble.nopointer.device.BleDevice;

public abstract class DeviceListAdapter extends BaseRecycleAdapter<BleDevice, DeviceListAdapter.ViewHolder> {


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
        holder.deviceRssiTv.setText(data.getRssi()+"db");
        //添加点击事件
        holder.deviceConnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回调给调用者处理
                onItemConnClick(data);
            }
        });
    }

    protected abstract void onItemConnClick(BleDevice bluetoothDevice);


    public static class ViewHolder extends BaseRecycleTag {

        @BindView(R.id.device_name_tv)
        TextView deviceNameTv;

        @BindView(R.id.device_mac_tv)
        TextView deviceMacTv;

        @BindView(R.id.device_conn_btn)
        TextView deviceConnBtn;

        @BindView(R.id.device_rssi_tv)
        TextView deviceRssiTv;


        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


}
