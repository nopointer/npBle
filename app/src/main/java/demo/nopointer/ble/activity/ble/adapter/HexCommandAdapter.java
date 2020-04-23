package demo.nopointer.ble.activity.ble.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import demo.nopointer.ble.R;
import demo.nopointer.ble.activity.ble.HexCommandBean;
import demo.nopointer.ble.base.adapter.BaseRecycleAdapter;
import demo.nopointer.ble.base.adapter.BaseRecycleTag;
import npBase.BaseCommon.absimpl.NpEditTextWatchImpl;


public abstract class HexCommandAdapter extends BaseRecycleAdapter<HexCommandBean, HexCommandAdapter.ViewHolder> {
    public HexCommandAdapter(Context paramContext, List<HexCommandBean> paramList) {
        super(paramContext, paramList);
    }

    public List<HexCommandBean> getSelectCommand() {
        ArrayList localArrayList = new ArrayList();
        if ((this.dataList != null) && (this.dataList.size() > 0)) {
            Iterator localIterator = this.dataList.iterator();
            while (localIterator.hasNext()) {
                HexCommandBean localHexCommandBean = (HexCommandBean) localIterator.next();
                if ((localHexCommandBean.isSelect()) && (!TextUtils.isEmpty(localHexCommandBean.getHex()))) {
                    localArrayList.add(localHexCommandBean);
                }
            }
        }
        return localArrayList;
    }

    public void handDataAndView(final ViewHolder paramViewHolder, final HexCommandBean paramHexCommandBean, final int paramInt) {
        paramViewHolder.hex_select_checkbox.setChecked(paramHexCommandBean.isSelect());
        paramViewHolder.hex_string_edit.setText(paramHexCommandBean.getHex());
        paramViewHolder.hex_string_edit.addTextChangedListener(new NpEditTextWatchImpl() {
            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
                paramHexCommandBean.setHex(paramViewHolder.hex_string_edit.getText().toString());
                HexCommandAdapter.this.onHexCommandChange(paramHexCommandBean, paramInt);
            }
        });
        paramViewHolder.hex_send_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                paramHexCommandBean.setHex(paramViewHolder.hex_string_edit.getText().toString());
                HexCommandAdapter.this.onSendCommand(paramHexCommandBean, paramInt);
            }
        });
        paramViewHolder.hex_select_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean) {
                paramHexCommandBean.setSelect(paramAnonymousBoolean);
                HexCommandAdapter.this.onHexCommandChange(paramHexCommandBean, paramInt);
            }
        });
    }

    public ViewHolder instanceTag(View paramView) {
        return new ViewHolder(paramView);
    }

    public int loadItemView() {
        return R.layout.item_hex_command;
    }

    protected abstract void onHexCommandChange(HexCommandBean paramHexCommandBean, int paramInt);

    protected abstract void onSendCommand(HexCommandBean paramHexCommandBean, int paramInt);

    public static class ViewHolder extends BaseRecycleTag {
        @BindView(R.id.hex_select_checkbox)
        CheckBox hex_select_checkbox;
        @BindView(R.id.hex_send_btn)
        Button hex_send_btn;
        @BindView(R.id.hex_string_edit)
        EditText hex_string_edit;

        public ViewHolder(View view) {
            super(view);
            setIsRecyclable(false);
        }
    }


}
