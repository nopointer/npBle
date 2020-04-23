package demo.np.deviceuicustom.activity.scan;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import demo.np.deviceuicustom.R;
import demo.np.deviceuicustom.activity.ota.BatchOTAActivity;
import demo.np.deviceuicustom.base.TitleActivity;
import demo.np.deviceuicustom.ble.MyDeviceFilter;
import demo.np.deviceuicustom.sharedpreferences.SharedPrefereceFilter;
import demo.np.deviceuicustom.sharedpreferences.bean.FilterBean;
import npBase.BaseCommon.absimpl.NpEditTextWatchImpl;
import npLog.nopointer.core.NpLog;
import npPermission.nopointer.core.RequestPermissionInfo;
import npble.nopointer.ble.scan.BleScanner;
import npble.nopointer.ble.scan.ScanListener;
import npble.nopointer.device.BleDevice;

/**
 * 设备扫描界面
 */
public class ScanActivity extends TitleActivity implements ScanListener {

    private List<BleDevice> bluetoothDeviceList = new ArrayList();
    private DeviceListAdapter deviceListAdapter = null;
    @BindView(R.id.deviceListView)
    RecyclerView deviceListView;

    @BindView(R.id.name_filter_ed)
    EditText name_filter_ed;//名称过滤

    @BindView(R.id.mac_filter_ed)
    EditText mac_filter_ed;//mac过滤

    @BindView(R.id.all_choice_checkbox)
    CheckBox all_choice_checkbox;//全部选中/不选中

    @BindView(R.id.start_btn)
    Button start_btn;//开始按钮


    public int loadLayout() {
        return R.layout.activity_scan;
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg == null || msg.obj == null) return;
            BleDevice bluetoothDevice = (BleDevice) msg.obj;
            if (bluetoothDevice == null) return;

            if ("HTX_DFU".equals(bluetoothDevice.getName())) {
                return;
            }
            if (!scanMacList.contains(bluetoothDevice.getMac())) {
                scanMacList.add(bluetoothDevice.getMac());
                bluetoothDeviceList.add(bluetoothDevice);
                deviceListAdapter.notifyDataSetChanged();
                return;
            }
            int i = scanMacList.indexOf(bluetoothDevice.getMac());
            if (i != -1) {
                bluetoothDeviceList.set(i, bluetoothDevice);
                deviceListAdapter.notifyItemChanged(i);
            }
            return;

        }
    };
    private List<String> scanMacList = new ArrayList();
    private int type;

    private void jump2OpenBleSetting() {
        startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 111);
    }


    public void initView() {
        super.initView();
        titleBar.setTitle("设备列表");
        titleBar.setRightText("扫描");
        type = getIntent().getIntExtra("type", 1);
        BleScanner.getInstance().setBleDeviceFilter(MyDeviceFilter.getInstance());
        BleScanner.getInstance().registerScanListener(this);
        titleBar.setRightViewOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (!BleScanner.getInstance().isScan()) {
                    BleScanner.getInstance().registerScanListener(ScanActivity.this);
                    BleScanner.getInstance().startScan();
                    titleBar.setRightText("停止");
                    return;
                }
                BleScanner.getInstance().unRegisterScanListener(ScanActivity.this);
                BleScanner.getInstance().stopScan();
                titleBar.setRightText("扫描");
            }
        });
        deviceListAdapter = new DeviceListAdapter(this, bluetoothDeviceList);

        deviceListView.setLayoutManager(new LinearLayoutManager(this));
        deviceListView.setAdapter(deviceListAdapter);
        requestPermission(loadPermissionsConfig());

        loadFilter();
    }


    protected RequestPermissionInfo loadPermissionsConfig() {
        RequestPermissionInfo requestPermissionInfo = new RequestPermissionInfo();
        requestPermissionInfo.setPermissionTitle(getResources().getString(R.string.request_permission_title));
        requestPermissionInfo.setPermissionMessage(getResources().getString(R.string.request_permission_message_for_main));
        requestPermissionInfo.setPermissionCancelText(getResources().getString(R.string.cancel));
        requestPermissionInfo.setPermissionSureText(getResources().getString(R.string.ok));

        requestPermissionInfo.setAgainPermissionTitle(getResources().getString(R.string.request_permission_title));
        requestPermissionInfo.setAgainPermissionMessage(getResources().getString(R.string.request_permission_message_for_main));
        requestPermissionInfo.setAgainPermissionCancelText(getResources().getString(R.string.cancel));
        requestPermissionInfo.setAgainPermissionSureText(getResources().getString(R.string.ok));
        requestPermissionInfo.setPermissionArr(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        });
        return requestPermissionInfo;
    }

    public void onFailure(int paramInt) {
    }

    protected void onPause() {
        super.onPause();
        BleScanner.getInstance().unRegisterScanListener(this);
    }

    protected void onResume() {
        super.onResume();
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            jump2OpenBleSetting();
            return;
        }
        if (!BleScanner.getInstance().isScan()) {
            BleScanner.getInstance().registerScanListener(this);
            BleScanner.getInstance().startScan();
            titleBar.setRightText("停止");
        }
    }

    @Override
    public void onScan(BleDevice paramBleDevice) {
        NpLog.eAndSave(paramBleDevice.getMac());
        Message localMessage = handler.obtainMessage();
        localMessage.obj = paramBleDevice;
        handler.sendMessage(localMessage);
    }

    FilterBean filterBean = null;

    private void loadFilter() {
        filterBean = SharedPrefereceFilter.read();
        if (filterBean != null) {
            if (!TextUtils.isEmpty(filterBean.getName())) {
                name_filter_ed.setText(filterBean.getName());
            }
            if (!TextUtils.isEmpty(filterBean.getMac())) {
                mac_filter_ed.setText(filterBean.getMac());
            }
        }


        NpEditTextWatchImpl npEditTextWatch = new NpEditTextWatchImpl() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterBean == null) {
                    filterBean = new FilterBean();
                }
                filterBean.setName(name_filter_ed.getText().toString());
                filterBean.setMac(mac_filter_ed.getText().toString());
                SharedPrefereceFilter.save(filterBean);

                MyDeviceFilter.setFilterStr(filterBean.getName(), filterBean.getMac());
            }
        };

        mac_filter_ed.addTextChangedListener(npEditTextWatch);
        name_filter_ed.addTextChangedListener(npEditTextWatch);
    }

    @OnClick(R.id.start_btn)
    void click(View view) {
        switch (view.getId()) {
            //开始按钮
            case R.id.start_btn:
                startActivity(BatchOTAActivity.class);
                break;
        }
    }
}
