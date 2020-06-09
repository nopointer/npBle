package demo.nopointer.ble.activity.ble.scan;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import demo.nopointer.ble.R;
import demo.nopointer.ble.activity.BleActivity;
import demo.nopointer.ble.activity.OTAActivity;
import demo.nopointer.ble.base.activity.TitleActivity;
import demo.nopointer.ble.bleModule.MyDeviceFilter;
import demo.nopointer.ble.bleModule.NpBleManager;
import demo.nopointer.ble.utils.ToastHelper;
import npLog.nopointer.core.NpLog;
import npPermission.nopointer.core.RequestPermissionInfo;
import npble.nopointer.ble.conn.NpBleConnCallback;
import npble.nopointer.ble.conn.NpBleConnState;
import npble.nopointer.ble.scan.BleScanner;
import npble.nopointer.ble.scan.ScanListener;
import npble.nopointer.device.BleDevice;

public class ScanActivity extends TitleActivity implements ScanListener {

    NpBleConnCallback bleConnCallback = new NpBleConnCallback() {
        public void onConnState(final NpBleConnState npBleConnState) {
            runOnUiThread(new Runnable() {
                public void run() {
                    switch (npBleConnState) {
                        case CONNEXCEPTION:
                        case HANDDISCONN:
                        case PHONEBLEANR:
                            ToastHelper.getToastHelper().show("连接失败");
                            dismissLoadingDialog();
                            return;

                        case CONNECTED:
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    dismissLoadingDialog();
                                    if (bluetoothDevice != null) {
                                        BleScanner.getInstance().stopScan();
                                        Intent localIntent = new Intent(ScanActivity.this, BleActivity.class);
                                        localIntent.putExtra("bleDevice", new BleDevice(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                                        startActivity(localIntent);
                                    }
                                }
                            }, 1000);
                            break;
                    }
                }
            });
        }
    };
    private BluetoothDevice bluetoothDevice;
    private List<BleDevice> bluetoothDeviceList = new ArrayList();
    private DeviceListAdapter deviceListAdapter = null;
    @BindView(R.id.deviceListView)
    RecyclerView deviceListView;

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
    private NpBleManager npBleManager = NpBleManager.getInstance();
    private List<String> scanMacList = new ArrayList();
    private int type;

    private void jump2OpenBleSetting() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 111);
    }

    private void setConnCallback() {
        npBleManager.registerConnCallback(bleConnCallback);
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
        deviceListAdapter = new DeviceListAdapter(this, bluetoothDeviceList) {
            protected void onItemConnClick(BleDevice bleDevice) {
                if (type == 2) {
                    Intent intent = new Intent(ScanActivity.this, OTAActivity.class);
                    intent.putExtra("name", bleDevice.getName());
                    intent.putExtra("mac", bleDevice.getMac());
                    startActivity(intent);
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        showLoadingDialog("连接中");
                    }
                });
                setConnCallback();
                BleScanner.getInstance().stopScan();
                titleBar.setRightText("扫描");

                BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bleDevice.getMac());
                ScanActivity.this.bluetoothDevice = bluetoothDevice;

                npBleManager.connDevice(bleDevice.getMac());
            }
        };
        deviceListView.setLayoutManager(new LinearLayoutManager(this));
        deviceListView.setAdapter(deviceListAdapter);
        requestPermission(loadPermissionsConfig());
    }

    public int loadLayout() {
        return R.layout.activity_scan;
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
        npBleManager.unRegisterConnCallback(bleConnCallback);
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

    public void onScan(BleDevice paramBleDevice) {
        NpLog.eAndSave(paramBleDevice.getMac());
        Message localMessage = handler.obtainMessage();
        localMessage.obj = paramBleDevice;
        handler.sendMessage(localMessage);
    }
}
