package lib.ycble;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import npble.nopointer.aider.PushAiderHelper;
import npble.nopointer.ble.scan.ScanListener;
import npble.nopointer.device.BleDevice;
import npble.nopointer.log.ycBleLog;


public class MainActivity extends Activity implements ScanListener {


    //    public static String mac = "0C:B2:B7:53:39:D2";
//    public static String macForHTX = "D2:92:7C:6B:8B:7A";

    String file1Path = "/storage/emulated/0/Download/Bluetooth/OTA_TEST1.bin";
    String file2Path = "/storage/emulated/0/Download/Bluetooth/OTA_TEST2.bin";

    String file3Path = "/storage/emulated/0/Bluetooth/MagicSwitch__OAD.bin";
    String pathForHTX = "/sdcard/otaHelper/firmware/V5HD_H_20191024.bin";


//    public static final String macForXinCore = "E3:06:05:E7:9C:52";
    public static final String macForXinCore = "CF:56:48:42:07:E2";

    public static final String pathForXinCore = "/storage/emulated/0/Bluetooth/xincore_band.bin";

    private TextView textBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG
            }, 100);
        }
        textBtn = findViewById(R.id.textBtn);
        textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushAiderHelper.getAiderHelper().goToSettingNotificationAccess(MainActivity.this);
//                startActivity(new Intent(MainActivity.this,BleActivity.class));
            }
        });

        startActivity(new Intent(MainActivity.this,BleActivity.class));
//        BleScanner.getInstance().registerScanListener(this);
//        BleScanner.getInstance().startScan();

//        mac = "20:17:98:F7:7F:E5";
//        OTAHelper.getInstance().startOTA(this, file2Path, mac, null, FirmType.HTX, new OTACallback() {
//            @Override
//            public void onFailure(int code, String message) {
//                ycBleLog.e("onFailure==>" + code + "///" + message);
//            }
//
//            @Override
//            public void onSuccess() {
//                ycBleLog.e("onSuccess==>");
//            }
//
//            @Override
//            public void onProgress(int progress) {
//                ycBleLog.e("onProgress==>" + progress);
//            }
//        });
//        startService(new Intent(this, BgService.class));


//        BleDevice bleDevice = new BleDevice("W28", macForXinCore);
////
//        OTAHelper.getInstance().startOTA(this, pathForXinCore, bleDevice, FirmType.XC, new OTACallback() {
//
//
//            @Override
//            public void onFailure(int code, String message) {
//                ycBleLog.e("onFailure===ota失败" + message);
//            }
//
//            @Override
//            public void onSuccess() {
//                ycBleLog.e("onSuccess===ota成功");
//            }
//
//            @Override
//            public void onProgress(int progress) {
//                ycBleLog.e("progress===>ota进度" + progress);
//            }
//        });

//        BlePhoneSysUtil.releaseAllScanClient();
//        BlePhoneSysUtil.refreshBleAppFromSystem(this,"lib.ycble.demo");
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


//        NpBleManager.getBleManager().connBleDevice(macForXinCore);

//        startService(new Intent(this, MainBackLiveService.class));
//        BleScanner.getBleScaner().startScan();

//        NpBleManager.getBleManager().connDevice(mac);


//        ycBleLog.e("MTK mode==>" + WearableManager.getInstance().getWorkingMode());
//
//
//        Set<BluetoothDevice> tmpList = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
//
//
//        BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("40:3D:A0:01:62:61");
//        ycBleLog.e("name===>" + bluetoothDevice.getName());
//
//        WearableManager.getInstance().setRemoteDevice(bluetoothDevice);
//        ycBleLog.e("[wearable][onCreate], BTNoticationApplication WearableManager connect!///" + WearableManager.getInstance().getWorkingMode());
//        WearableManager.getInstance().connect();
    }


    @Override
    protected void onDestroy() {
        ycBleLog.e("清理掉了app");
        super.onDestroy();
//        BleScanner.getInstance().stopScan();
//        PushAiderHelper.getAiderHelper().stop(this);
    }

    @Override
    public void onScan(BleDevice bleDevice) {
//        ycBleLog.e(bleDevice.toString());
    }

    @Override
    public void onFailure(int code) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        PushAiderHelper.getAiderHelper().registerCallAndSmsReceiver(this);
//        PushAiderHelper.getAiderHelper().startListeningForNotifications(this);
        if (PushAiderHelper.getAiderHelper().isNotifyEnable(this)) {
            PushAiderHelper.getAiderHelper().startListeningForNotifications(this);
            textBtn.setText("已开启");

        } else {
            textBtn.setText("未开启");
        }
    }

    //    adb shell dumpsys activity | grep -i run
//    plugin.voip.ui.VideoActivity
//    plugin.voip.ui.VideoActivity

}
