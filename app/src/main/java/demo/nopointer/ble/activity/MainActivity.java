package demo.nopointer.ble.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import demo.nopointer.ble.R;
import npble.nopointer.ble.scan.ScanListener;
import npble.nopointer.device.BleDevice;
import npble.nopointer.log.NpBleLog;


public class MainActivity extends Activity implements ScanListener {

        public static String mac = "0C:B2:B7:53:39:D2";

    private TextView textBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
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
//                startActivity(new Intent(MainActivity.this,BleActivity.class));
            }
        });

        startActivity(new Intent(MainActivity.this,BleActivity.class));
    }


    @Override
    protected void onDestroy() {
        NpBleLog.e("清理掉了app");
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
    }

    //    adb shell dumpsys activity | grep -i run
//    plugin.voip.ui.VideoActivity
//    plugin.voip.ui.VideoActivity

}
