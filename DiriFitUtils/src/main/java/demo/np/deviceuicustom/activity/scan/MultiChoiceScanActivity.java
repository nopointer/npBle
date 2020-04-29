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
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import demo.np.deviceuicustom.R;
import demo.np.deviceuicustom.activity.ota.BatchOTAActivity;
import demo.np.deviceuicustom.base.TitleActivity;
import demo.np.deviceuicustom.ble.EmptyDeviceFilter;
import demo.np.deviceuicustom.sharedpreferences.SharedPrefereceFilter;
import demo.np.deviceuicustom.sharedpreferences.bean.FilterBean;
import npBase.BaseCommon.absimpl.NpEditTextWatchImpl;
import npBase.BaseCommon.util.toast.ToastHelper;
import npLog.nopointer.core.NpLog;
import npPermission.nopointer.core.RequestPermissionInfo;
import npble.nopointer.ble.scan.BleScanner;
import npble.nopointer.ble.scan.ScanListener;
import npble.nopointer.device.BleDevice;

/**
 * 设备扫描界面(可以多选设备)
 */
public class MultiChoiceScanActivity extends TitleActivity implements ScanListener {

    /**
     * 扫描到的设备列表
     */
    private List<BleDevice> scanBleDeviceList = new ArrayList();

    /**
     * 适配器所需要显示的列表
     */
    private List<BleDevice> adapterDataList = new ArrayList<>();
    private MultiChoiceDeviceListAdapter deviceListAdapter = null;
    @BindView(R.id.deviceListView)
    RecyclerView deviceListView;

    @BindView(R.id.name_filter_ed)
    EditText name_filter_ed;//名称过滤

    @BindView(R.id.mac_filter_ed)
    EditText mac_filter_ed;//mac过滤

    @BindView(R.id.all_select_btn)
    Button all_select_btn;//全部选中/不选中

    //是否是全选
    private boolean isAllSelect = false;

    @BindView(R.id.start_btn)
    Button start_btn;//开始按钮

    private FilterBean filterBean = null;

    public int loadLayout() {
        return R.layout.activity_scan_multi;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg == null || msg.obj == null) return;
            BleDevice bleDevice = (BleDevice) msg.obj;
            if (bleDevice == null) return;

            if (!scanMacList.contains(bleDevice.getMac())) {
                scanMacList.add(bleDevice.getMac());
                scanBleDeviceList.add(bleDevice);
                filterDevice(filterBean);
                return;
            }
            int i = scanMacList.indexOf(bleDevice.getMac());
            if (i != -1) {
                scanBleDeviceList.get(i).setRssi(bleDevice.getRssi());
                filterDevice(filterBean);
            }
            return;

        }
    };
    private List<String> scanMacList = new ArrayList();
    private int type;

    private void jump2OpenBleSetting() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 111);
    }


    public void initView() {
        super.initView();
        titleBar.setTitle("设备列表");
        titleBar.setRightText("扫描");
        type = getIntent().getIntExtra("type", 1);
        BleScanner.getInstance().setBleDeviceFilter(EmptyDeviceFilter.getInstance());
        BleScanner.getInstance().registerScanListener(this);
        titleBar.setRightViewOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (!BleScanner.getInstance().isScan()) {
                    BleScanner.getInstance().registerScanListener(MultiChoiceScanActivity.this);
                    scanCoding();
                    titleBar.setRightText("停止");
                    return;
                }
                BleScanner.getInstance().unRegisterScanListener(MultiChoiceScanActivity.this);
                BleScanner.getInstance().stopScan();
                titleBar.setRightText("扫描");
            }
        });
        deviceListAdapter = new MultiChoiceDeviceListAdapter(this, adapterDataList) {
            @Override
            protected void onSelectStateChange(int position) {
                BleScanner.getInstance().stopScan();
                titleBar.setRightText("扫描");
                if (deviceListAdapter.isAllChoiceMode()) {
                    isAllSelect = true;
                    all_select_btn.setText("全不选");
                } else {
                    isAllSelect = false;
                    all_select_btn.setText("全选");
                }
                updateStartBtnState();
            }
        };

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
        BleScanner.getInstance().stopScan();
    }

    protected void onResume() {
        super.onResume();
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            jump2OpenBleSetting();
            return;
        }
        if (!BleScanner.getInstance().isScan()) {
            BleScanner.getInstance().registerScanListener(this);
            scanCoding();
        }
    }

    @Override
    public void onScan(BleDevice paramBleDevice) {
        NpLog.eAndSave(paramBleDevice.getMac());
        Message localMessage = handler.obtainMessage();
        localMessage.obj = paramBleDevice;
        handler.sendMessage(localMessage);
    }

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
                filterDevice(filterBean);
            }
        };

        mac_filter_ed.addTextChangedListener(npEditTextWatch);
        name_filter_ed.addTextChangedListener(npEditTextWatch);
    }

    @OnClick({R.id.start_btn, R.id.all_select_btn})
    void click(View view) {
        switch (view.getId()) {
            //开始按钮
            case R.id.start_btn: {
                List<Integer> integerList = deviceListAdapter.getSelectIndexList();
                if (integerList != null && integerList.size() > 0) {
                    List<BleDevice> bleDevices = new ArrayList<>();
                    for (int in : integerList) {
                        bleDevices.add(adapterDataList.get(in));
                    }
                    BatchOTAActivity.otaList =bleDevices;
                    startActivity(BatchOTAActivity.class);
                } else {
                    ToastHelper.getToastHelper().show("请至少选择一个设备");
                }
            }
            break;
            case R.id.all_select_btn:
                isAllSelect = !isAllSelect;
                BleScanner.getInstance().stopScan();
                titleBar.setRightText("扫描");
                if (isAllSelect) {
                    all_select_btn.setText("全不选");
                    deviceListAdapter.allChoice();
                } else {
                    all_select_btn.setText("全选");
                    deviceListAdapter.allNotChoice();
                }
                updateStartBtnState();
                break;
        }
    }


    /**
     * 过滤设备
     *
     * @param filterBean
     */
    void filterDevice(FilterBean filterBean) {

        boolean isEmptyFilter = filterBean == null || (TextUtils.isEmpty(filterBean.getName()) && TextUtils.isEmpty(filterBean.getMac()));

        if (isEmptyFilter) {
            adapterDataList.clear();
            adapterDataList.addAll(scanBleDeviceList);
        } else {
            List<BleDevice> bleDevices = new ArrayList<>();
            if (scanBleDeviceList != null && scanBleDeviceList.size() > 0) {
                for (BleDevice bleDevice : scanBleDeviceList) {

                    boolean isFilterName = !TextUtils.isEmpty(filterBean.getName()) && bleDevice.getName().toUpperCase().contains(filterBean.getName().toUpperCase());
                    boolean isFilterMac = !TextUtils.isEmpty(filterBean.getMac()) && bleDevice.getMac().toUpperCase().contains(filterBean.getMac().toUpperCase());

                    if (!TextUtils.isEmpty(filterBean.getName()) && !TextUtils.isEmpty(filterBean.getMac())) {
                        if (isFilterName && isFilterMac) {
                            bleDevices.add(bleDevice);
                        }
                    } else {
                        if (isFilterName || isFilterMac) {
                            bleDevices.add(bleDevice);
                        }
                    }
                }
            }
            adapterDataList.clear();
            adapterDataList.addAll(bleDevices);
        }
        deviceListAdapter.notifyDataSetChanged();
    }

    /**
     * 扫描，需要重置
     */
    private void scanCoding() {
        BleScanner.getInstance().startScan();
        titleBar.setRightText("停止");

        scanBleDeviceList.clear();
        adapterDataList.clear();
        isAllSelect = false;
        all_select_btn.setText("全选");
        scanMacList.clear();
        deviceListAdapter.notifyDataSetChanged();
    }

    /**
     * 更新开始按钮的状态
     */
    private void updateStartBtnState() {
        List<Integer> integerList = deviceListAdapter.getSelectIndexList();
        if (integerList != null && integerList.size() > 0) {
            start_btn.setEnabled(true);
        } else {
            start_btn.setEnabled(false);
        }
    }


}
