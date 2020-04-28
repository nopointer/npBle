package demo.np.deviceuicustom.ble.ota;

import android.content.Context;
import android.os.Handler;

import java.util.List;

import demo.np.deviceuicustom.ble.NpBleManager;
import npBase.BaseCommon.util.log.LogUtil;
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

    private NpBleManager npBleManager = NpBleManager.getInstance();


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
    private long timeDelay = 12 * 1000;

    /**
     * 当前设备OTA的索引
     */
    private int currentOTADeviceIndex = 0;


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
        npBleManager.setOtaMode(true);

        next(false);
    }


    /**
     * 下一个设备
     *
     * @param isNeedPowerOff 是否需要关机上一个设备
     */
    private void next(boolean isNeedPowerOff) {
        if (otaList == null || otaList.size() < 1) {
            LogUtil.e("设备列表为空");
            return;
        }
        if (currentOTADeviceIndex >= otaList.size()) {
            LogUtil.e("任务完成");
            return;
        }
        NpLog.e("========currentOTADeviceIndex================");
        NpLog.e("||                                          ");
        NpLog.e("||     上一个设备需要关机与否:" + isNeedPowerOff + "///" + currentOTADeviceIndex);
        NpLog.e("||                                          ");
        NpLog.e("========currentOTADeviceIndex================");
        if (isNeedPowerOff) {
            powerOff();
        } else {
            ota();
        }
    }

    /**
     * ota
     */
    private void ota() {
        BleDevice bleDevice = otaList.get(currentOTADeviceIndex);
        if (otaTaskCallback != null) {
            otaTaskCallback.onDeviceProgress(currentOTADeviceIndex, otaList.size());
        }
        TeLinkOTAHelper.getInstance().startOTA(context, bleDevice.getMac(), binPath, new NpOtaCallback() {
            @Override
            public void onFailure(int code, String message) {
                delayedNext(false);
            }

            @Override
            public void onSuccess() {
                delayedNext(true);
            }

            @Override
            public void onProgress(int progress) {
                NpLog.e("OTA 单个进度:" + progress);
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
     * 关机
     */
    private void powerOff() {
        npBleManager.setOnWriteCallback(new NpBleManager.OnWriteCallback() {
            @Override
            public void onDataWriteSuccess(byte[] data) {
                delayedOta();
            }

            @Override
            public void onDataWriteFail(byte[] data) {
                delayedOta();
            }
        });
        BleDevice bleDevice = otaList.get(currentOTADeviceIndex);
        npBleManager.connDevice(bleDevice.getMac());
    }


    /**
     * 延时OTA，设备关机后，也需要时间开机
     */
    private void delayedOta() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                next(false);
            }
        }, 6000);
    }

    /**
     * 延时下一个
     *
     * @param isNeedPower
     */
    private void delayedNext(boolean isNeedPower) {
        currentOTADeviceIndex++;
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                next(isNeedPower);
            }
        }, timeDelay);
    }


    /**
     * 结束OTA
     */
    public void stopOTA() {

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
