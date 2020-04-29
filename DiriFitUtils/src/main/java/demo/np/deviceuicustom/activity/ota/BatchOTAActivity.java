package demo.np.deviceuicustom.activity.ota;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.ArrayList;
import java.util.HashSet;
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

    @BindView(R.id.device_ota_result_tv)
    TextView device_ota_result_tv;

    //OTA管理器
    private OTAManager otaManager = OTAManager.getInstance();

    public static List<BleDevice> otaList = new ArrayList<>();

    /**
     * 成功列表
     */
    private List<BleDevice> successList = new ArrayList<>();
    /**
     * 失败列表
     */
    private List<BleDevice> failureList = new ArrayList<>();
    /**
     * 疑似成功列表（OTA成功，但为正常关机）
     */
    private List<BleDevice> suspectedSuccessList = new ArrayList<>();

    private StringBuilder stringBuilder = new StringBuilder();

    private boolean isOTA = false;


    @Override
    public int loadLayout() {
        return R.layout.activity_bacth_ota;
    }

    @Override
    public void initView() {
        super.initView();
        titleBar.setTitle("OTA");

        titleBar.setLeftViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOTA) {
                    sureExitOTA();
                }
            }
        });
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
                        npRectProgressView.setProgress(singleProgress);
                    }
                });
            }

            @Override
            public void onDeviceSuccess(BleDevice bleDevice) {
                successList.add(bleDevice);
            }

            @Override
            public void onDeviceFailure(BleDevice bleDevice) {
                failureList.add(bleDevice);
            }

            @Override
            public void onOTATaskFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isOTA = false;
                        npRectProgressView.setProgress(1);
                        start_ota_btn.setEnabled(true);
                        stop_ota_btn.setEnabled(false);
                        text_info_tv.setText("OTA 完成");

                        showOTAResult();
                    }
                });
            }
        });
    }

    /**
     * OTA结果
     */
    private void showOTAResult() {
        HashSet<String> macList = new HashSet<>();
        stringBuilder = new StringBuilder();
        stringBuilder.append("成功的设备:").append("[" + successList.size() + "]").append("\n ");
        for (BleDevice bleDevice : successList) {
            macList.add(bleDevice.getMac());
            stringBuilder.append(bleDevice.getName()).append(" : ").append(bleDevice.getMac()).append("\n");
        }
        stringBuilder.append("\n");
        stringBuilder.append("失败的设备:").append("[" + failureList.size() + "]").append("\n");
        for (BleDevice bleDevice : failureList) {
            macList.add(bleDevice.getMac());
            stringBuilder.append(bleDevice.getName()).append(" : ").append(bleDevice.getMac()).append("\n");
        }

        //收集疑似成功的设备
        for (BleDevice bleDevice : otaList) {
            if (macList.contains(bleDevice.getMac())) continue;
            suspectedSuccessList.add(bleDevice);

        }

        stringBuilder.append("\n");
        stringBuilder.append("疑似成功的设备（OTA完成，但未成功关机）:").append("[" + suspectedSuccessList.size() + "]").append("\n");
        for (BleDevice bleDevice : suspectedSuccessList) {
            stringBuilder.append(bleDevice.getName()).append(" : ").append(bleDevice.getMac()).append("\n");
        }

        device_ota_result_tv.setText(stringBuilder.toString());

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
        isOTA = true;
        count_progress_tv.setText((1) + "/" + otaList.size());
        npRectProgressView.setProgress(0);
        text_info_tv.setText("正在OTA....");
        device_ota_result_tv.setText("");
        failureList.clear();
        successList.clear();
        suspectedSuccessList.clear();

        List<BleDevice> bleDevices = new ArrayList<>();
        bleDevices.addAll(otaList);
//        bleDevices.add(new BleDevice("MM39", "12:AB:68:01:93:50"));
//        bleDevices.add(new BleDevice("", "12:AB:68:01:93:50"));
//        bleDevices.add(new BleDevice("", "12:AB:68:01:93:50"));
//        bleDevices.add(new BleDevice("", "12:AB:68:01:93:50"));
        otaManager.setOtaList(bleDevices);
        otaManager.setBinPath(selectBinPath);
        otaManager.startOTA(this);
    }

    void sureExitOTA() {
        new QMUIDialog.MessageDialogBuilder(this)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .setMessage("确定要停止OTA吗？")
                .addAction(getString(R.string.continue_upload), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, getString(R.string.give_up), QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        OTAManager.getInstance().stopOTA();
                        finish();
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isOTA) {
                sureExitOTA();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
