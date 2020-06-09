package demo.nopointer.npDemo.ble;

import android.os.Handler;
import android.support.v4.util.SimpleArrayMap;

import java.util.UUID;

import npLog.nopointer.core.NpLog;
import npble.nopointer.ble.conn.NpTimeOutUtilHelper;
import npble.nopointer.util.BleUtil;

/**
 * ble数据处理工具
 */
public class BleDataProcessingUtils {
    private NpBleManager bleManager;

    public BleDataProcessingUtils(NpBleManager bleManager) {
        this.bleManager = bleManager;
        retryMap = new SimpleArrayMap<>();
    }

    private NpTimeOutUtilHelper npTimeOutUtilHelper = null;

    private Handler handler = new Handler();

    private int currentRetryCount = 0;

    /**
     * 重新尝试次数
     */
    private SimpleArrayMap<String, Integer> retryMap = null;


    /**
     * 写之前的数据
     *
     * @param uuid
     * @param data
     */
    public void onBeforeWriteData(UUID uuid, byte[] data) {
        handler.removeCallbacksAndMessages(null);
        npTimeOutUtilHelper = new NpTimeOutUtilHelper(BleUtil.byte2HexStr(data), 3000);
        npTimeOutUtilHelper.setRetryCount(3);
        retryMap.put(npTimeOutUtilHelper.getStrData(), npTimeOutUtilHelper.getRetryCount());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentRetryCount++;
                if (retryMap.containsKey(npTimeOutUtilHelper.getStrData()) && retryMap.get(npTimeOutUtilHelper.getStrData()) < currentRetryCount) {
                    NpLog.eAndSave("已经超过尝试次数了！,超时");
                    if (!bleManager.isHasAfterConnectedTaskEnd()) {
                        bleManager.taskSuccess();
                    }
                    currentRetryCount = 0;
                } else {
                    NpLog.eAndSave("当前尝试次数" + currentRetryCount);
                    bleManager.writeData(BleUtil.hexStr2Byte(npTimeOutUtilHelper.getStrData()));
                }

            }
        }, npTimeOutUtilHelper.getMilliSecond());
    }
}
