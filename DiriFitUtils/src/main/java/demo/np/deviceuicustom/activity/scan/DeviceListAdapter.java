package demo.np.deviceuicustom.activity.scan;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import demo.np.deviceuicustom.R;
import demo.np.deviceuicustom.base.adapter.BaseRecycleAdapter;
import demo.np.deviceuicustom.base.adapter.BaseRecycleTag;
import npble.nopointer.device.BleDevice;

public abstract class DeviceListAdapter extends BaseRecycleAdapter<BleDevice, DeviceListAdapter.ViewHolder> {
    public DeviceListAdapter(Context paramContext, List<BleDevice> paramList) {
        super(paramContext, paramList);
    }

    public void handDataAndView(ViewHolder paramViewHolder, final BleDevice paramBleDevice, int paramInt) {
        paramViewHolder.deviceNameTv.setText(paramBleDevice.getName());
        paramViewHolder.deviceMacTv.setText(paramBleDevice.getMac());
        TextView localTextView = paramViewHolder.deviceRssiTv;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramBleDevice.getRssi());
        localStringBuilder.append("db");
        localTextView.setText(localStringBuilder.toString());
        paramViewHolder.deviceConnBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View paramAnonymousView) {
                DeviceListAdapter.this.onItemConnClick(paramBleDevice);
            }
        });
    }

    public ViewHolder instanceTag(View paramView) {
        return new ViewHolder(paramView);
    }

    public int loadItemView() {
        return R.layout.item_scan_device_info;
    }

    protected abstract void onItemConnClick(BleDevice paramBleDevice);

    public static class ViewHolder extends BaseRecycleTag {
        @BindView(R.id.device_conn_btn)
        TextView deviceConnBtn;
        @BindView(R.id.device_mac_tv)
        TextView deviceMacTv;
        @BindView(R.id.device_name_tv)
        TextView deviceNameTv;
        @BindView(R.id.device_rssi_tv)
        TextView deviceRssiTv;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}

