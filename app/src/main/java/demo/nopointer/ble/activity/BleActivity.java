package demo.nopointer.ble.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import demo.nopointer.ble.R;
import demo.nopointer.ble.bleModule.NpBleManager;

public class BleActivity extends Activity {


    //    public static final String macForXinCore = "E3:E7:10:FB:C0:17";
//    public static final String macForXinCore = "CF:56:48:42:07:E2";
//    public static final String macForXinCore = "13:5E:C1:10:07:73";
    public static final String macForXinCore = "C4:B8:DC:6D:B8:9D";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NpBleManager.getInstance().connDevice(macForXinCore);

        findViewById(R.id.textBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NpBleManager.getInstance().disConnectDevice();
            }
        });

    }
}
