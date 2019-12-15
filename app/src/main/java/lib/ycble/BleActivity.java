package lib.ycble;

import android.app.Activity;
import android.os.Bundle;

import lib.ycble.ble.NpBleManager;

public class BleActivity extends Activity {


    public static final String macForXinCore = "CF:56:48:42:07:E2";
//    public static final String macForXinCore = "13:5E:C1:10:07:73";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NpBleManager.getInstance(this).connDevice(macForXinCore);

    }
}
