package demo.np.deviceuicustom.ble.ota;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

import demo.np.deviceuicustom.MainApplication;
import npLog.nopointer.core.NpLog;
import npble.nopointer.device.BleDevice;
import npble.nopointer.ota.absimpl.telink.TeLinkOTAHelper;
import npble.nopointer.ota.callback.NpOtaCallback;

/**
 * OTA  管理器
 */
public class OTAManager {


    private static final OTAManager ourInstance = new OTAManager();

    public static OTAManager getInstance() {
        return ourInstance;
    }

    private OTAManager() {
    }

    /**
     * 需要OTA的设备列表
     */
    private List<BleDevice> otaList = null;

    /**
     * 固件地址路径
     */
    private String binPath;

    private OTATaskCallback otaTaskCallback;


    private Context context;

    /**
     * 延时器
     */
    private Handler handler = new Handler();

    //延时时间执行（单位毫秒）
    private long timeDelay = 10 * 1000;

    /**
     * 当前设备OTA的索引
     */
    private int currentOTADeviceIndex = 0;

    private boolean isOTA = false;


    public void setOtaList(List<BleDevice> otaList) {
        this.otaList = otaList;
    }

    public void setBinPath(String binPath) {
        this.binPath = binPath;
    }

    public List<BleDevice> getOtaList() {
        return otaList;
    }

    public void setOtaTaskCallback(OTATaskCallback otaTaskCallback) {
        this.otaTaskCallback = otaTaskCallback;
    }

    /**
     * 开始OTA
     */
    public void startOTA(Context context) {
        this.context = context;
        if (!isOTA) {
            isOTA = true;
            currentOTADeviceIndex = 0;
            next();
        }
    }


    /**
     * 下一个设备
     */
    private void next() {
        if (otaList == null || otaList.size() < 1) {
            NpBleLog.log("设备列表为空");
            return;
        }
        if (currentOTADeviceIndex >= otaList.size()) {
            NpBleLog.log("任务完成");
            isOTA = false;
            currentOTADeviceIndex = 0;
            if (otaTaskCallback != null) {
                otaTaskCallback.onOTATaskFinish();
            }
            return;
        }
        NpBleLog.log("============================================");
        NpBleLog.log("||                                          ");
        NpBleLog.log("||      " + currentOTADeviceIndex);
        NpBleLog.log("||                                          ");
        NpBleLog.log("============================================");
        ota();
    }

    /**
     * ota
     */
    private void ota() {
        BleDevice bleDevice = otaList.get(currentOTADeviceIndex);
        if (otaTaskCallback != null) {
            otaTaskCallback.onDeviceProgress(currentOTADeviceIndex, otaList.size());
        }
        NpBleLog.log("开始OTA的设备"+bleDevice.getName()+"///"+ bleDevice.getMac());
        TeLinkOTAHelper.getInstance().startOTA(context, bleDevice.getMac(), binPath, new NpOtaCallback() {
            @Override
            public void onFailure(int code, String message) {
                if (otaTaskCallback != null) {
                    otaTaskCallback.onDeviceFailure(otaList.get(currentOTADeviceIndex));
                }
                delayedNextOta();
            }

            @Override
            public void onSuccess() {
                Looper.prepare();
                delayedPowerOff();
                Looper.loop();
            }

            @Override
            public void onProgress(int progress) {
                NpBleLog.log("OTA 单个进度:" + progress);
                if (otaTaskCallback != null) {
                    float singleProgress = progress / 100.0f;
                    float deviceProgress = (currentOTADeviceIndex + 1.0f) / (otaList.size());
                    float totalProgress = deviceProgress * singleProgress;
                    otaTaskCallback.onProgress(singleProgress, totalProgress);
                }
            }
        });
    }

    /**
     * 延时关机
     */
    private void delayedPowerOff() {
        handler.removeCallbacksAndMessages(null);
        BlePowerManager powerManager = new BlePowerManager(MainApplication.getMainApplication());
        powerManager.setOnPowerCallback(new BlePowerManager.OnPowerCallback() {
            @Override
            public void onPowerOffSuccess() {
                if (otaTaskCallback != null) {
                    otaTaskCallback.onDeviceSuccess(otaList.get(currentOTADeviceIndex));
                }
                delayedNextOta();
            }

            @Override
            public void onPowerOffFail() {
                delayedNextOta();
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BleDevice bleDevice = otaList.get(currentOTADeviceIndex);
                NpBleLog.log("开始关机的设备"+bleDevice.getName()+"///"+ bleDevice.getMac());
                powerManager.connDevice(bleDevice.getMac());
            }
        }, timeDelay);
    }


    /**
     * 延时OTA，设备关机后，也需要时间开机
     */
    private void delayedNextOta() {
        currentOTADeviceIndex++;
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                next();
            }
        }, timeDelay);
    }


    /**
     * 结束OTA
     */
    public void stopOTA() {
        isOTA = false;
        handler.removeCallbacksAndMessages(null);
        currentOTADeviceIndex = 0;
        otaList.clear();
        TeLinkOTAHelper.getInstance().stopOTA();
        otaTaskCallback = null;
    }


    /**
     * OTA的 回调接口
     */
    public interface OTATaskCallback {


        /**
         * 设备进度
         *
         * @param currentIndex
         * @param totalDeviceCount
         */
        void onDeviceProgress(int currentIndex, int totalDeviceCount);


        /**
         * 进度
         *
         * @param singleProgress      //单个设备的进度
         * @param totalProgress//总体进度
         */
        void onProgress(float singleProgress, float totalProgress);


        /**
         * ota成功
         *
         * @param bleDevice
         */
        void onDeviceSuccess(BleDevice bleDevice);


        /**
         * ota失败
         */
        void onDeviceFailure(BleDevice bleDevice);


        /**
         * OTA任务完成
         */
        void onOTATaskFinish();


    }


}
