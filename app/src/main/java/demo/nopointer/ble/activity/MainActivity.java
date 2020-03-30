package demo.nopointer.ble.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import demo.nopointer.ble.R;
import demo.nopointer.ble.activity.ble.scan.ScanActivity;


public class MainActivity extends Activity {

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
    }

    /**
     * 普通点击
     *
     * @param view
     */
    public void normalFunction(View view) {
        startActivity(new Intent(this, ScanActivity.class));
    }

    /**
     * OTA功能
     *
     * @param view
     */
    public void otaFunction(View view) {
        startActivity(new Intent(this, OTAActivity.class));
    }

}
