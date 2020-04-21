package demo.nopointer.ble.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import demo.nopointer.ble.R;
import demo.nopointer.ble.activity.ble.scan.ScanActivity;
import demo.nopointer.ble.base.activity.TitleActivity;
import npBase.BaseCommon.util.NpAppBaseUtils;
import npLog.nopointer.core.NpLog;


public class MainActivity extends TitleActivity {



    @Override
    public int loadLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        super.initView();

        titleBar.setTitle(R.string.app_name_main);
        titleBar.setLeftText(NpAppBaseUtils.getVersionName(this));
        titleBar.setLeftViewOnClickListener(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
        NpLog.initLog("npBle/bleLog","log");
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
