package demo.nopointer.ble.bleModule;


import android.os.Handler;
import android.support.v4.util.SimpleArrayMap;

import java.util.UUID;

import npble.nopointer.ble.conn.NpTimeOutUtilHelper;
import npble.nopointer.log.NpBleLog;
import npble.nopointer.util.BleUtil;

/**
 * ble 数据处理工具类
 */
public class BleDataProcessingUtils {

    private NpBleManager bleManager = null;

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
        npTimeOutUtilHelper = new NpTimeOutUtilHelper(BleUtil.byte2HexStr(data), 10000);
        npTimeOutUtilHelper.setRetryCount(3);
        retryMap.put(npTimeOutUtilHelper.getStrData(), npTimeOutUtilHelper.getRetryCount());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentRetryCount++;
                if (retryMap.containsKey(npTimeOutUtilHelper.getStrData()) && retryMap.get(npTimeOutUtilHelper.getStrData()) < currentRetryCount) {
                    NpBleLog.e("已经超过尝试次数了！,超时");
                    if (!bleManager.isHasAfterConnectedTaskEnd()) {
                        bleManager.taskSuccess();
                    }
                    currentRetryCount = 0;
                } else {
                    NpBleLog.e("当前尝试次数" + currentRetryCount);
                    bleManager.writeData(BleUtil.hexStr2Byte(npTimeOutUtilHelper.getStrData()));
                }

            }
        }, npTimeOutUtilHelper.getMilliSecond());
    }

    /**
     * 处理响应的数据标志位
     *
     * @param uuid
     * @param data
     */
    public void handResponseData(UUID uuid, byte[] data) {
        int flag = BleUtil.byte2IntLR(data[0]);
        handData(flag, data);//处理数据
        handFlag(flag, data);//处理接收到的数据标志位
    }


    /**
     * 处理响应的数据
     *
     * @param flag
     * @param data
     */
    private void handData(int flag, byte[] data) {

    }

    /**
     * 处理标志位
     *
     * @param data
     */
    private void handFlag(int flag, byte[] data) {
        switch (flag) {
            case 0x93://步数的多包响应数据,收到多包数据后,才算结束数据
                if (BleUtil.byte2IntLR(data[4]) == 0xFF) {
                    if (npTimeOutUtilHelper != null) {
                        npTimeOutUtilHelper = null;
                    }
                    resetTimeOutFlag();
                }
                break;

            case 0x94:
                if (npTimeOutUtilHelper != null && npTimeOutUtilHelper.getStrData().equalsIgnoreCase("14")) {
                    resetTimeOutFlag();
                }
                break;
            case 0xD1:
                resetTimeOutFlag();
                break;
        }
    }


    private void resetTimeOutFlag() {
        currentRetryCount = 0;
        bleManager.taskSuccess();
        handler.removeCallbacksAndMessages(null);
    }

}
