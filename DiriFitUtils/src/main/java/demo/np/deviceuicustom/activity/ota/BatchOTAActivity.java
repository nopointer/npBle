package demo.np.deviceuicustom.activity.ota;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import demo.np.deviceuicustom.R;
import demo.np.deviceuicustom.activity.firmware.FirmwarelistActivity;
import demo.np.deviceuicustom.base.TitleActivity;
import demo.np.deviceuicustom.ble.ota.OTAManager;
import npble.nopointer.device.BleDevice;
import npwidget.nopointer.progress.NpRectProgressView;

/**
 * 批量OTA
 */
public class BatchOTAActivity extends TitleActivity {

    @BindView(R.id.select_btn_tv)
    TextView select_btn_tv;//当前选择的固件

    //当前选择的固件路径
    private String selectBinPath = null;

    @BindView(R.id.start_ota_btn)
    Button start_ota_btn;//开始按钮

    @BindView(R.id.stop_ota_btn)
    Button stop_ota_btn;//停止按钮

    @BindView(R.id.npRectProgressView)
    NpRectProgressView npRectProgressView;//进度

    @BindView(R.id.count_progress_tv)
    TextView count_progress_tv;//设备进度

    @BindView(R.id.current_device_tv)
    TextView current_device_tv;//当前设备

    @BindView(R.id.text_info_tv)
    TextView text_info_tv;//显示文本进度

    //OTA管理器
    private OTAManager otaManager = OTAManager.getInstance();


    @Override
    public int loadLayout() {
        return R.layout.activity_bacth_ota;
    }

    @Override
    public void initView() {
        super.initView();
        titleBar.setTitle("OTA");

        npRectProgressView.setUseRoundMode(true);
        npRectProgressView.setBgColor(0xFFCCCCCC);
        npRectProgressView.setProgressColor(getResources().getColor(R.color.colorPrimary));
        npRectProgressView.setProgress(0.0f);


        refreshSelectBin();

        otaManager.setOtaTaskCallback(new OTAManager.OTATaskCallback() {
            @Override
            public void onDeviceProgress(int currentIndex, int totalDeviceCount) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        count_progress_tv.setText((currentIndex + 1) + "/" + totalDeviceCount);
                        StringBuilder stringBuilder = new StringBuilder();
                        BleDevice bleDevice = otaManager.getOtaList().get(currentIndex);
                        stringBuilder.append(bleDevice.getName());
                        stringBuilder.append(":");
                        stringBuilder.append(bleDevice.getMac());
                        current_device_tv.setText(stringBuilder.toString());
                    }
                });
            }

            @Override
            public void onProgress(float singleProgress, float totalProgress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        npRectProgressView.setProgress(totalProgress);
                    }
                });
            }

            @Override
            public void onDeviceSuccess(BleDevice bleDevice) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onDeviceFailure(BleDevice bleDevice) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onOTATaskFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }


    @OnClick({R.id.select_bin_btn, R.id.start_ota_btn, R.id.stop_ota_btn})
    void click(View view) {
        switch (view.getId()) {
            //选择固件
            case R.id.select_bin_btn: {
                Intent intent = new Intent(this, FirmwarelistActivity.class);
                startActivityForResult(intent, 100);
            }
            break;

            //开始OTA
            case R.id.start_ota_btn:
                start_ota_btn.setEnabled(false);
                stop_ota_btn.setEnabled(true);

                startOTA();
                break;

            //结束OTA
            case R.id.stop_ota_btn:
                start_ota_btn.setEnabled(true);
                stop_ota_btn.setEnabled(false);
                break;
        }
    }

    //刷新显示的固件
    private void refreshSelectBin() {
        if (TextUtils.isEmpty(selectBinPath)) {
            select_btn_tv.setText("当前选择的固件:无");
            start_ota_btn.setEnabled(false);
            stop_ota_btn.setEnabled(false);
        } else {
            select_btn_tv.setText("当前选择的固件:" + selectBinPath);
            start_ota_btn.setEnabled(true);
            stop_ota_btn.setEnabled(false);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                selectBinPath = data.getStringExtra("binPath");
                refreshSelectBin();
            }
        }
    }


    private void startOTA() {
        List<BleDevice> bleDevices = new ArrayList<>();
        bleDevices.add(new BleDevice("", "12:AB:68:01:93:50"));
        bleDevices.add(new BleDevice("", "12:AB:68:01:93:50"));
        bleDevices.add(new BleDevice("", "12:AB:68:01:93:50"));
        bleDevices.add(new BleDevice("", "12:AB:68:01:93:50"));
        otaManager.setOtaList(bleDevices);
        otaManager.setBinPath(selectBinPath);
        otaManager.startOTA(this);
    }

}
