package demo.nopointer.ble.dialog.bleservice;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import demo.nopointer.ble.R;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.List;

public abstract class BleServiceListDialog extends Dialog {

    private List<BleServiceBean> bleServiceBeanList = new ArrayList();
    private ExpandedAdapter expandedAdapter;

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        public void onItemClick(View view, int position) {


            //根据原position判断该item是否是parent item
            if (expandedAdapter.isParentItem(position)) {
                // 换取parent position
                final int parentPosition = expandedAdapter.parentItemPosition(position);

                // 判断parent是否打开了二级菜单
                if (expandedAdapter.isExpanded(parentPosition)) {
                    bleServiceBeanList.get(parentPosition).setExpanded(false);
                    expandedAdapter.notifyParentChanged(parentPosition);
                    // 关闭该parent下的二级菜单
                    expandedAdapter.collapseParent(parentPosition);
                } else {
                    bleServiceBeanList.get(parentPosition).setExpanded(true);
                    expandedAdapter.notifyParentChanged(parentPosition);
                    // 打开该parent下的二级菜单
                    expandedAdapter.expandParent(parentPosition);
                }
            } else {
                // 换取parent position
                int parentPosition = expandedAdapter.parentItemPosition(position);
                // 换取child position
                int childPosition = expandedAdapter.childItemPosition(position);

                try {
                    BleServiceBean bleServiceBean = bleServiceBeanList.get(parentPosition).getCharaBeanList().get(childPosition);
                    if (BleServiceListDialog.this.type == 0) {
                        if (((bleServiceBean.getType() & 0x4) != 4) && ((bleServiceBean.getType() & 0x8) != 8)) {
                            Toast.makeText(BleServiceListDialog.this.getContext(), "该UUID 不能进行'写数据'操作！", 0).show();
                        } else {
                            BleServiceListDialog.this.onCharaSelect(bleServiceBean.getUuid(), bleServiceBean.getUuid());
                            BleServiceListDialog.this.dismiss();
                        }
                    } else if (BleServiceListDialog.this.type == 1) {
                        if (((bleServiceBean.getType() & 0x2) != 2) && ((bleServiceBean.getType() & 0x10) != 16) && ((bleServiceBean.getType() & 0x20) != 32)) {
                            Toast.makeText(BleServiceListDialog.this.getContext(), "该UUID 不能进行'读数据或通知'操作！", 0).show();
                        } else {
                            BleServiceListDialog.this.onCharaSelect(bleServiceBean.getUuid(), bleServiceBean.getUuid());
                            BleServiceListDialog.this.dismiss();
                        }
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


//            
//            if (BleServiceListDialog.this.expandedAdapter.isParentItem(paramAnonymousInt)) {
//                paramAnonymousInt = BleServiceListDialog.this.expandedAdapter.parentItemPosition(paramAnonymousInt);
//                if (BleServiceListDialog.this.expandedAdapter.isExpanded(paramAnonymousInt)) {
//                    ((BleServiceBean) BleServiceListDialog.this.bleServiceBeanList.get(paramAnonymousInt)).setExpanded(false);
//                    BleServiceListDialog.this.expandedAdapter.notifyParentChanged(paramAnonymousInt);
//                    BleServiceListDialog.this.expandedAdapter.collapseParent(paramAnonymousInt);
//                } else {
//                    ((BleServiceBean) BleServiceListDialog.this.bleServiceBeanList.get(paramAnonymousInt)).setExpanded(true);
//                    BleServiceListDialog.this.expandedAdapter.notifyParentChanged(paramAnonymousInt);
//                    BleServiceListDialog.this.expandedAdapter.expandParent(paramAnonymousInt);
//                }
//                return;
//            }
//            int i = BleServiceListDialog.this.expandedAdapter.parentItemPosition(paramAnonymousInt);
//            paramAnonymousInt = BleServiceListDialog.this.expandedAdapter.childItemPosition(paramAnonymousInt);
//
        }
    };
    @BindView(R.id.swipeRecyclerView)
    SwipeRecyclerView swipeRecyclerView;
    private int type = -1;

    public BleServiceListDialog(@NonNull Context paramContext) {
        super(paramContext);
    }

    protected abstract void onCharaSelect(String paramString1, String paramString2);

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.dialog_ble_service_list);
        findViewById(R.id.dialog_close).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                BleServiceListDialog.this.dismiss();
            }
        });
        ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        this.swipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.swipeRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(getContext(), R.color.bar_grey), QMUIDisplayHelper.getScreenWidth(getContext()), QMUIDisplayHelper.dp2px(getContext(), 1)));
        this.expandedAdapter = new ExpandedAdapter(getContext());
        this.expandedAdapter.setGroupList(this.bleServiceBeanList);
        this.swipeRecyclerView.setOnItemClickListener(this.onItemClickListener);
        this.swipeRecyclerView.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            public void onItemClick(SwipeMenuBridge paramAnonymousSwipeMenuBridge, int paramAnonymousInt) {
                if (!BleServiceListDialog.this.expandedAdapter.isParentItem(paramAnonymousInt)) {
                    BleServiceListDialog.this.expandedAdapter.parentItemPosition(paramAnonymousInt);
                    BleServiceListDialog.this.expandedAdapter.childItemPosition(paramAnonymousInt);
                }
            }
        });
        this.swipeRecyclerView.setAdapter(this.expandedAdapter);
    }

    public void showWithChoiceData(List<BleServiceBean> paramList, int paramInt, String paramString) {
        show();
        this.type = paramInt;
        this.bleServiceBeanList.clear();
        this.bleServiceBeanList.addAll(paramList);
        this.expandedAdapter.setLastUUid(paramString);
        this.expandedAdapter.notifyDataSetChanged();
    }
}
