//package demo.np.deviceuicustom.activity.scan;
//
//import android.Manifest;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.view.View.OnClickListener;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
//import butterknife.BindView;
//import demo.np.deviceuicustom.R;
//import demo.np.deviceuicustom.base.TitleActivity;
//import demo.np.deviceuicustom.ble.MyDeviceFilter;
//import npPermission.nopointer.core.RequestPermissionInfo;
//import npble.nopointer.ble.scan.BleScanner;
//import npble.nopointer.ble.scan.ScanListener;
//import npble.nopointer.device.BleDevice;
//
//public class ScanActivityBak extends TitleActivity implements ScanListener {
//
////    BleConnCallback bleConnCallback = new BleConnCallback() {
////        public void onConnState(final BleConnState paramAnonymousBleConnState) {
////            ScanActivity.this.runOnUiThread(new Runnable() {
////                public void run() {
////                    switch (ScanActivity
////                    .5.$SwitchMap$ycble$runchinaup$core$BleConnState[paramAnonymousBleConnState.ordinal()])
////                    {
////                        default:
////                            return;
////                        case 2:
////                        case 3:
////                        case 4:
////                            ToastHelper.getToastHelper().show("连接失败");
////                            ScanActivity.this.dismissLoadingDialog();
////                            return;
////                    }
////                    ScanActivity.this.dismissLoadingDialog();
////                    if (ScanActivity.this.bluetoothDevice != null) {
////                        BleScanner.getInstance().stopScan();
////                        Intent localIntent = new Intent(ScanActivity.this, DeviceFindMoreDialActivity.class);
////                        localIntent.putExtra("name", ScanActivity.this.bluetoothDevice.getName());
////                        localIntent.putExtra("mac", ScanActivity.this.bluetoothDevice.getAddress());
////                        ScanActivity.this.startActivity(localIntent);
////                    }
////                }
////            });
////        }
////    };
//
//
//    private BluetoothDevice bluetoothDevice;
//    private List<BleDevice> bluetoothDeviceList = new ArrayList();
//    private DeviceListAdapter deviceListAdapter = null;
//    @BindView(R.id.deviceList)
//    RecyclerView deviceListView;
//    private Handler handler = new Handler() {
//        public void handleMessage(Message paramAnonymousMessage) {
//            super.handleMessage(paramAnonymousMessage);
//            if (paramAnonymousMessage != null) {
//                if (paramAnonymousMessage.obj == null) {
//                    return;
//                }
////                paramAnonymousMessage = (BleDevice) paramAnonymousMessage.obj;
//                if (paramAnonymousMessage == null) {
//                    return;
//                }
////                if ("HTX_DFU".equals(paramAnonymousMessage.getName())) {
////                    BleScanner.getInstance().stopScan();
////                    Intent localIntent = new Intent(ScanActivity.this, DeviceFindMoreDialActivity.class);
////                    localIntent.putExtra("name", paramAnonymousMessage.getName());
////                    localIntent.putExtra("mac", paramAnonymousMessage.getMac());
////                    localIntent.putExtra("isOtaMode", true);
////                    ScanActivity.this.startActivity(localIntent);
////                    return;
////                }
////                if (!ScanActivity.this.scanMacList.contains(paramAnonymousMessage.getMac())) {
////                    ScanActivity.this.scanMacList.add(paramAnonymousMessage.getMac());
////                    ScanActivity.this.bluetoothDeviceList.add(paramAnonymousMessage);
////                    Collections.sort(ScanActivity.this.bluetoothDeviceList, new Comparator() {
////                        public int compare(BleDevice paramAnonymous2BleDevice1, BleDevice paramAnonymous2BleDevice2) {
////                            return paramAnonymous2BleDevice2.getRssi() - paramAnonymous2BleDevice1.getRssi();
////                        }
////                    });
////                    ScanActivity.this.deviceListAdapter.notifyDataSetChanged();
////                }
//                return;
//            }
//        }
//    };
//    private HashSet<String> scanMacList = new HashSet();
//
//    private int type;
//
//    private void jump2OpenBleSetting() {
//        startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 111);
//    }
//
//    private void setConnCallback() {
////        Np.getBleManager().registerConnCallback(this.bleConnCallback);
//    }
//
//
//    public void initView() {
//        super.initView();
//        this.titleBar.setTitle("设备列表");
//        this.titleBar.setRightText("扫描");
//        this.type = getIntent().getIntExtra("type", 1);
//        BleScanner.getInstance().setBleDeviceFilter(MyDeviceFilter.getInstance());
//        BleScanner.getInstance().registerScanListener(this);
//        this.titleBar.setRightViewOnClickListener(new OnClickListener() {
//            public void onClick(View paramAnonymousView) {
//                if (!BleScanner.getInstance().isScan()) {
//                    BleScanner.getInstance().registerScanListener(ScanActivityBak.this);
//                    BleScanner.getInstance().startScan();
//                    ScanActivityBak.this.titleBar.setRightText("停止");
//                    return;
//                }
//                BleScanner.getInstance().unRegisterScanListener(ScanActivityBak.this);
//                BleScanner.getInstance().stopScan();
//                ScanActivityBak.this.titleBar.setRightText("扫描");
//            }
//        });
//        this.deviceListAdapter = new DeviceListAdapter(this, this.bluetoothDeviceList) {
//            protected void onItemConnClick(BleDevice paramAnonymousBleDevice) {
////                if (ScanActivity.this.type == 2) {
////                    localObject = new Intent(ScanActivity.this, OTAActivity.class);
////                    ((Intent) localObject).putExtra("name", paramAnonymousBleDevice.getName());
////                    ((Intent) localObject).putExtra("mac", paramAnonymousBleDevice.getMac());
////                    ScanActivity.this.startActivity((Intent) localObject);
////                    return;
////                }
////                ScanActivity.this.runOnUiThread(new Runnable() {
////                    public void run() {
////                        ScanActivity.this.showLoadingDialog("连接中");
////                    }
////                });
////                ScanActivity.this.setConnCallback();
////                BleScanner.getInstance().stopScan();
////                ScanActivity.this.titleBar.setRightText("扫描");
////                Object localObject = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(paramAnonymousBleDevice.getMac());
////                ScanActivity.access$202(ScanActivity.this, (BluetoothDevice) localObject);
////                BleManager.getBleManager().connBleDevice(paramAnonymousBleDevice.getMac());
//            }
//        };
//        this.deviceListView.setLayoutManager(new LinearLayoutManager(this));
//        this.deviceListView.setAdapter(this.deviceListAdapter);
//        requestPermission(loadPermissionsConfig());
//    }
//
//    public int loadLayout() {
//        return R.layout.activity_scan;
//    }
//
//    protected RequestPermissionInfo loadPermissionsConfig() {
//        RequestPermissionInfo requestPermissionInfo = new RequestPermissionInfo();
//        requestPermissionInfo.setPermissionTitle(getResources().getString(R.string.request_permission_title));
//        requestPermissionInfo.setPermissionMessage(getResources().getString(R.string.request_permission_message_for_main));
//        requestPermissionInfo.setPermissionCancelText(getResources().getString(R.string.cancel));
//        requestPermissionInfo.setPermissionSureText(getResources().getString(R.string.ok));
//
//        requestPermissionInfo.setAgainPermissionTitle(getResources().getString(R.string.request_permission_title));
//        requestPermissionInfo.setAgainPermissionMessage(getResources().getString(R.string.request_permission_message_for_main));
//        requestPermissionInfo.setAgainPermissionCancelText(getResources().getString(R.string.cancel));
//        requestPermissionInfo.setAgainPermissionSureText(getResources().getString(R.string.ok));
//        requestPermissionInfo.setPermissionArr(new String[]{
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        });
//        return requestPermissionInfo;
//    }
//
//    public void onFailure(int paramInt) {
//    }
//
//    protected void onPause() {
//        super.onPause();
//        BleScanner.getInstance().unRegisterScanListener(this);
//    }
//
//    protected void onResume() {
//        super.onResume();
//        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
//            jump2OpenBleSetting();
//        }
//    }
//
//    public void onScan(BleDevice paramBleDevice) {
//        Message localMessage = this.handler.obtainMessage();
//        localMessage.obj = paramBleDevice;
//        this.handler.sendMessage(localMessage);
//    }
//}