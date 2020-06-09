package demo.nopointer.npDemo.scan;

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
import demo.nopointer.npDemo.R;
import demo.nopointer.npDemo.base.activity.TitleActivity;
import demo.nopointer.npDemo.ble.MyDeviceFilter;
import demo.nopointer.npDemo.ble.NpBleManager;
import demo.nopointer.npDemo.sharedpreferences.SharedPrefereceDevice;
import npLog.nopointer.core.NpLog;
import npPermission.nopointer.core.RequestPermissionInfo;
import npble.nopointer.ble.scan.BleScanner;
import npble.nopointer.ble.scan.ScanListener;
import npble.nopointer.device.BleDevice;

public class ScanActivity extends TitleActivity implements ScanListener {

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
                npBleManager.disConnectDevice();
                SharedPrefereceDevice.save(bleDevice);
                finish();
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
    protected void onDestroy() {
        super.onDestroy();
        BleScanner.getInstance().stopScan();
    }

    public void onScan(BleDevice paramBleDevice) {
        NpLog.eAndSave(paramBleDevice.getMac());
        Message localMessage = handler.obtainMessage();
        localMessage.obj = paramBleDevice;
        handler.sendMessage(localMessage);
    }
}
