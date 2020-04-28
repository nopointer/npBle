package demo.np.deviceuicustom.activity.scan;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import demo.np.deviceuicustom.R;
import demo.np.deviceuicustom.base.adapter.BaseRecycleAdapter;
import demo.np.deviceuicustom.base.adapter.BaseRecycleTag;
import npble.nopointer.device.BleDevice;

/**
 * 多选设备列表
 */
public class MultiChoiceDeviceListAdapter extends BaseRecycleAdapter<BleDevice, MultiChoiceDeviceListAdapter.ViewHolder> {


    //解决holder复用问题
    private List<Integer> selectIndexList = new ArrayList<>();


    public MultiChoiceDeviceListAdapter(Context context, List<BleDevice> dataList) {
        super(context, dataList);

    }


    /**
     * 全选
     */
    public void allChoice() {

        int count = dataList.size();
        for (int i = 0; i < count; i++) {
            selectIndexList.add(new Integer(i));
        }
        notifyDataSetChanged();
    }

    /**
     * 全不选
     */
    public void allNotChoice() {
        selectIndexList.clear();
        notifyDataSetChanged();
    }


    @Override
    public int loadItemView() {
        return R.layout.item_multi_choice_scan_device_info;
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

        Integer tag = new Integer(position);
        holder.device_checkbox.setTag(tag);
//        LogUtil.e("mac：" + data.getMac() + "///" + bleDeviceHashMap.containsKey(data.getMac()));

        holder.device_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!selectIndexList.contains(tag)) {
                        selectIndexList.add(tag);
                    }
                } else {
                    if (selectIndexList.contains(tag)) {
                        selectIndexList.remove(tag);
                    }
                }
            }
        });

        if (selectIndexList.contains(tag))
            holder.device_checkbox.setChecked(true);
        else {
            holder.device_checkbox.setChecked(false);
        }
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
