package demo.nopointer.npDemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.Enumeration;

import dalvik.system.DexFile;
import demo.nopointer.npDemo.ble.NpBleManager;
import demo.nopointer.npDemo.scan.ScanActivity;
import npLog.nopointer.core.NpLog;
import npble.nopointer.ble.conn.NpBleConnCallback;
import npble.nopointer.ble.conn.NpBleConnState;
import npble.nopointer.util.BleUtil;
//import  org.apache.commons.lang.*;


public class MainActivity extends Activity implements NpBleConnCallback {


    private String mac ="A4:C1:38:7A:67:4F";
    private TextView my_device;

    private TextView conn_state_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA
            }, 100);
        }
        my_device = findViewById(R.id.my_device);
        conn_state_tv = findViewById(R.id.conn_state_tv);

        NpLog.initLog("npDemo", "log",this);

        findViewById(R.id.textBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScanActivity.class));
            }
        });
//        startActivity(new Intent(this,BaseCameraTakePhotoActivity.class));
//        NpBleManager.getInstance().registerConnCallback(this);
//
//        NpBleManager.getInstance().disConnectDevice();

//        debug();

        NpBleManager.getInstance().connDevice(mac);

        NpLog.e("原始数据:"+BleUtil.byte2HexStr("ABCDEF".getBytes()));

    }


    //
    @Override
    protected void onResume() {
        super.onResume();
//        NpBleLog.log("MainActivity-->onResume()");
        NpBleManager.getInstance().registerConnCallback(this::onConnState);
        onConnState(NpBleManager.getInstance().getBleConnState());



//        BleDevice bleDevice = SharedPrefereceDevice.read();
//        if (bleDevice == null || TextUtils.isEmpty(bleDevice.getMac())) {
//            my_device.setText("无");
//        } else {
//            my_device.setText(bleDevice.getName() + " /// " + bleDevice.getMac());
////            NpBleManager.getInstance().connDevice(bleDevice.getMac());
//            NpBleManager.getInstance().connDevice(mac);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NpBleManager.getInstance().unRegisterConnCallback(this::onConnState);
    }

    @Override
    public void onConnState(NpBleConnState bleConnState) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bleConnState == NpBleConnState.CONNECTING) {
                    conn_state_tv.setText("连接中");
                } else if (bleConnState == NpBleConnState.CONNECTED) {
                    conn_state_tv.setText("已连接");
                } else {
                    conn_state_tv.setText("未连接");
                }
            }
        });
    }


    String path = null;

    void debug() {
        try {
            path = getPackageManager().getApplicationInfo(
                    "demo.nopointer.npDemo", 0).sourceDir;//获得某个程序的APK路径
        } catch (PackageManager.NameNotFoundException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        try {
            DexFile df = new DexFile(path);//传如APK实例一个dexfile
            Enumeration<String> s = df.entries();
            while (s.hasMoreElements()) {//遍历出所有类
                String string = (String) s.nextElement();
                Log.e("test", string);
            }
            Log.i("test", df.getName() + "");
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            Log.e("test", "error" + e1.getMessage());
            e1.printStackTrace();
        }
    }
}
