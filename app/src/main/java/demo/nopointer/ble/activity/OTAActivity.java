package demo.nopointer.ble.activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import demo.nopointer.ble.R;
import npLog.nopointer.core.NpLog;
import npble.nopointer.ota.NpFirmType;
import npble.nopointer.ota.NpOtaHelper;
import npble.nopointer.ota.callback.NpOtaCallback;

public class OTAActivity extends Activity {


    private String filePath = "/storage/emulated/0/npBle/V5HD_HI02_V_1_0_9.bin";
    //    private String mac = "C4:B8:DC:6D:B8:9D";
    private String mac = "D2:92:7C:6B:8B:7A";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota);

        NpOtaHelper.getInstance().startOTA(this, filePath, mac, NpFirmType.HTX, new NpOtaCallback() {
            @Override
            public void onFailure(int code, String message) {

            }

            @Override
            public void onSuccess() {
                NpLog.e("OTA成功");
            }

            @Override
            public void onProgress(int progress) {
                NpLog.e("进度:" + progress + "%");
            }
        });
    }
}
