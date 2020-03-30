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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import demo.nopointer.ble.R;
import demo.nopointer.ble.activity.OTAActivity;
import demo.nopointer.ble.base.TitleBar;
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

    @BindView(R.id.titleBar)
    TitleBar titleBar;

    @BindView(R.id.deviceList)
    RecyclerView deviceListView;


    private List<BleDevice> bluetoothDeviceList = new ArrayList<>();
    private DeviceListAdapter deviceListAdapter = null;
    private List<String> scanMacList = new ArrayList<>();

    private BluetoothDevice bluetoothDevice;

    /**
     * 操作企图 类型，可能是连接可能是ota
     */
    private int type;


    private NpBleManager npBleManager = NpBleManager.getInstance();

    @Override
    public int loadLayout() {
        return R.layout.activity_scan;
    }

    @Override
    public void initView() {
        super.initView();
        titleBar.setTitle("设备列表");
        titleBar.setRightText("扫描");
        type = getIntent().getIntExtra("type", 1);

        BleScanner.getInstance().setBleDeviceFilter(MyDeviceFilter.getInstance());
        BleScanner.getInstance().registerScanListener(this);

        titleBar.setRightViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BleScanner.getInstance().isScan()) {
                    BleScanner.getInstance().registerScanListener(ScanActivity.this);
                    BleScanner.getInstance().startScan();
                    titleBar.setRightText("停止");
                } else {
                    BleScanner.getInstance().unRegisterScanListener(ScanActivity.this);
                    BleScanner.getInstance().stopScan();
                    titleBar.setRightText("扫描");

                }
            }
        });
        deviceListAdapter = new DeviceListAdapter(this, bluetoothDeviceList) {
            @Override
            protected void onItemConnClick(BleDevice bleDevice) {

                if (type == 2) {
                    Intent intent = new Intent(ScanActivity.this, OTAActivity.class);
                    intent.putExtra("name", bleDevice.getName());
                    intent.putExtra("mac", bleDevice.getMac());
                    startActivity(intent);
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
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

    @Override
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

    NpBleConnCallback bleConnCallback = new NpBleConnCallback() {
        @Override
        public void onConnState(final NpBleConnState bleConnState) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (bleConnState) {
                        case CONNECTED:
                            dismissLoadingDialog();
                            if (bluetoothDevice != null) {
//                                BleScanner.getInstance().stopScan();
//                                Intent intent = new Intent(ScanActivity.this, DeviceFindMoreDialActivity.class);
//                                intent.putExtra("name", bluetoothDevice.getName());
//                                intent.putExtra("mac", bluetoothDevice.getAddress());
//                                startActivity(intent);
                            }
                            break;
                        case CONNEXCEPTION:
                        case HANDDISCONN:
                        case PHONEBLEANR:
                            ToastHelper.getToastHelper().show("连接失败");
                            dismissLoadingDialog();
                            break;
                    }
                }
            });
        }


    };

    private void setConnCallback() {
        //连接状态回调
        npBleManager.registerConnCallback(bleConnCallback);
    }

    @Override
    public void onScan(BleDevice bluetoothDevice) {
        NpLog.eAndSave(bluetoothDevice.getMac());
        Message message = handler.obtainMessage();
        message.obj = bluetoothDevice;
        handler.sendMessage(message);
    }

    @Override
    public void onFailure(int code) {

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg == null || msg.obj == null) return;
            BleDevice bluetoothDevice = (BleDevice) msg.obj;
            if (bluetoothDevice == null) return;

//            if ("CZWHTX_DFU".equals(bluetoothDevice.getName())) {
            if ("HTX_DFU".equals(bluetoothDevice.getName())) {
                //扫描到已经进入OTA模式的设备
//                BleScanner.getInstance().stopScan();
//                Intent intent = new Intent(ScanActivity.this, DeviceFindMoreDialActivity.class);
//                intent.putExtra("name", bluetoothDevice.getName());
//                intent.putExtra("mac", bluetoothDevice.getMac());
//                intent.putExtra("isOtaMode", true);
//                startActivity(intent);
            } else {
                //扫描到设备
                if (!scanMacList.contains(bluetoothDevice.getMac())) {
                    scanMacList.add(bluetoothDevice.getMac());
                    bluetoothDeviceList.add(bluetoothDevice);

                    Collections.sort(bluetoothDeviceList, new Comparator<BleDevice>() {
                        @Override
                        public int compare(BleDevice o1, BleDevice o2) {
                            return o2.getRssi() - o1.getRssi();
                        }
                    });

                    deviceListAdapter.notifyDataSetChanged();
                } else {
                    int index = scanMacList.indexOf(bluetoothDevice.getMac());
                    if (index != -1) {
                        bluetoothDeviceList.set(index, bluetoothDevice);
                        deviceListAdapter.notifyItemChanged(index);
                    }
                }
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            jump2OpenBleSetting();
        }
    }

    private void jump2OpenBleSetting() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 111);
    }


    @Override
    protected void onPause() {
        super.onPause();
        BleScanner.getInstance().unRegisterScanListener(this);
    }
}
