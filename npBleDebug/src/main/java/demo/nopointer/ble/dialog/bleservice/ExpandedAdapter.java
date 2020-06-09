package demo.nopointer.ble.dialog.bleservice;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.yanzhenjie.recyclerview.ExpandableAdapter;
import com.yanzhenjie.recyclerview.ExpandableAdapter.ViewHolder;

import java.util.HashMap;
import java.util.List;

import demo.nopointer.ble.R;
import npble.nopointer.constant.BaseUUIDs;

public class ExpandedAdapter extends ExpandableAdapter<ExpandableAdapter.ViewHolder> {
    private String lastUUid = null;
    private LayoutInflater mInflater;
    private List<BleServiceBean> trainHistoryBeanList;

    public ExpandedAdapter(Context paramContext) {
        this.mInflater = LayoutInflater.from(paramContext);
    }

    public void bindChildHolder(@NonNull ExpandableAdapter.ViewHolder holder, int paramInt1, int paramInt2) {
        BleServiceBean localBleServiceBean = trainHistoryBeanList.get(paramInt1).getCharaBeanList().get(paramInt2);
        ((ChildHolder) holder).setData(localBleServiceBean);
        if (localBleServiceBean.getUuid().equalsIgnoreCase(this.lastUUid)) {
            holder.itemView.setBackgroundResource(R.color.bar_grey);
        }
        holder.itemView.setBackgroundResource(R.color.white);
    }

    public void bindParentHolder(@NonNull ExpandableAdapter.ViewHolder paramViewHolder, int paramInt) {
        ((ParentHolder) paramViewHolder).setData((BleServiceBean) this.trainHistoryBeanList.get(paramInt));
    }

    public int childItemCount(int paramInt) {
        List localList = ((BleServiceBean) this.trainHistoryBeanList.get(paramInt)).getCharaBeanList();
        if (localList == null) {
            return 0;
        }
        return localList.size();
    }

    public int childItemViewType(int paramInt1, int paramInt2) {
        return super.childItemViewType(paramInt1, paramInt2);
    }

    public ExpandableAdapter.ViewHolder createChildHolder(@NonNull ViewGroup paramViewGroup, int paramInt) {
        return new ChildHolder(this.mInflater.inflate(R.layout.item_expand_child, paramViewGroup, false), this);
    }

    public ExpandableAdapter.ViewHolder createParentHolder(@NonNull ViewGroup paramViewGroup, int paramInt) {
        return new ParentHolder(this.mInflater.inflate(R.layout.item_expand_parent, paramViewGroup, false), this);
    }

    public int parentItemCount() {
        if (this.trainHistoryBeanList == null) {
            return 0;
        }
        return this.trainHistoryBeanList.size();
    }

    public int parentItemViewType(int paramInt) {
        return super.parentItemViewType(paramInt);
    }

    public void setGroupList(List<BleServiceBean> paramList) {
        this.trainHistoryBeanList = paramList;
    }

    public void setLastUUid(String paramString) {
        this.lastUUid = paramString;
    }

    static class ChildHolder extends ExpandableAdapter.ViewHolder {
        @BindView(R.id.chara_name_tv)
        TextView chara_name_tv;
        @BindView(R.id.chara_type_tv)
        TextView chara_type_tv;
        @BindView(R.id.chara_uuid_tv)
        TextView chara_uuid_tv;

        public ChildHolder(@NonNull View paramView, ExpandableAdapter paramExpandableAdapter) {
            super(paramView, paramExpandableAdapter);
            ButterKnife.bind(this, paramView);
        }

        public void setData(BleServiceBean bleServiceBean) {
            String charaName = "UnKnown characteristic";
            if (BaseUUIDs.getAttributes().containsKey(bleServiceBean.getUuid())) {
                charaName = BaseUUIDs.getAttributes().get(bleServiceBean.getUuid());
            }
            this.chara_name_tv.setText(charaName);
            this.chara_uuid_tv.setText(bleServiceBean.getUuid());
            StringBuilder stringBuilder = new StringBuilder();

            if ((bleServiceBean.getType() & 0x02) == 0x02) {
                stringBuilder.append("可读,");
            }

            if (((bleServiceBean.getType() & 0x04) == 0x04) || ((bleServiceBean.getType() & 0x08) == 0x08)) {
                stringBuilder.append("可写,");
            }
            if (((bleServiceBean.getType() & 0x10) == 0x10) || (bleServiceBean.getType() & 0x20) == 0x20) {
                stringBuilder.append("可通知,");
            }
            if (stringBuilder.length() > 1) {
                stringBuilder =stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            this.chara_type_tv.setText(stringBuilder.toString());
        }
    }

    static class ParentHolder extends ExpandableAdapter.ViewHolder {
        @BindView(R.id.service_name_tv)
        TextView service_name_tv;
        @BindView(R.id.service_uuid_tv)
        TextView service_uuid_tv;
        @BindView(R.id.train_item_parent_state_iv)
        ImageView train_item_parent_state_iv;

        public ParentHolder(@NonNull View paramView, ExpandableAdapter paramExpandableAdapter) {
            super(paramView, paramExpandableAdapter);
            ButterKnife.bind(this, paramView);
        }

        public void setData(BleServiceBean paramBleServiceBean) {
            String str = "UnKnown service";
            if (BaseUUIDs.getAttributes().containsKey(paramBleServiceBean.getUuid())) {
                str = (String) BaseUUIDs.getAttributes().get(paramBleServiceBean.getUuid());
            }
            this.service_name_tv.setText(str);
            this.service_uuid_tv.setText(paramBleServiceBean.getUuid());
            this.train_item_parent_state_iv.setSelected(paramBleServiceBean.isExpanded());
        }
    }
}
