package demo.np.deviceuicustom.ble;

import android.content.Context;

import java.util.UUID;

import demo.np.deviceuicustom.MainApplication;
import demo.np.deviceuicustom.ble.imageTransport.DevImageUtils;
import demo.np.deviceuicustom.ble.utils.DevDataUtils;
import npble.nopointer.ble.conn.NpBleAbsConnManager;
import npble.nopointer.exception.NpBleUUIDNullException;
import npble.nopointer.log.NpBleLog;
import npble.nopointer.util.BleUtil;

/**
 * 蓝牙交互对象
 */
public class NpBleManager extends NpBleAbsConnManager implements BleSomeCfg {

    private static NpBleManager instance = null;


    public static NpBleManager getInstance() {
        try {
            if (instance == null) {
                try {
                    if (instance == null) {
                        instance = new NpBleManager(MainApplication.getMainApplication());
                    }
                } finally {
                }
            }
            return instance;
        } finally {
        }
    }


    private NpBleManager(Context paramContext) {
        super(paramContext);
        tmpIndex = 0;
    }


    //是否是ota模式
    private boolean isOtaMode = false;

    public void setOtaMode(boolean otaMode) {
        isOtaMode = otaMode;
    }


    private OnWriteCallback onWriteCallback = null;

    public void setOnWriteCallback(OnWriteCallback onWriteCallback) {
        this.onWriteCallback = onWriteCallback;
    }

    @Override
    protected void onBeforeWriteData(UUID uuid, byte[] data) {

    }


    public static int tmpIndex;

    @Override
    protected void loadCfg() {
        if (isOtaMode) {
            write(DevDataUtils.powderOff());
        } else {
            try {
                setNotificationCallback(dataServiceUUID, dataNotifyUUID);
                enableNotifications(dataServiceUUID, dataNotifyUUID);

                setNotificationCallback(dataServiceUUID, imageDataNotifyUUID);
                enableNotifications(dataServiceUUID, imageDataNotifyUUID);

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    requestConnectionPriority(ConnectionPriorityRequest.CONNECTION_PRIORITY_HIGH).enqueue();
//                }
            } catch (NpBleUUIDNullException e) {
                e.printStackTrace();
            }
        }
    }

    private void write(byte[] data) {
        try {
            writeCharacteristicWithOutResponse(dataServiceUUID, dataWriteUUID, data);
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDataReceive(byte[] data, UUID uuid) {
        if (uuid.equals(imageDataNotifyUUID)) {
            int index = BleUtil.byte2IntLR(data[1], data[2],data[3],data[4]);
            NpBleLog.log("续传图片传输的索引:" + index);
            DevImageUtils.getInstance().withNext(index);
        }
    }

    @Override
    protected void onConnException() {

    }

    @Override
    protected void onDataWriteSuccess(UUID uuid, byte[] data) {
        NpBleLog.log("onDataWriteSuccess:" + uuid.toString() + "///" + BleUtil.byte2HexStr(data));
        if (uuid.equals(dataWriteUUID)) {
            if (isOtaMode && onWriteCallback != null) {
                onWriteCallback.onDataWriteSuccess(data);
            }
        } else if (uuid.equals(imageDataWriteUUID)) {
//            NpBleLog.log("开始写下一包图片的数据" + tmpIndex);
            if (tmpIndex % 10 == 0) {
                for (int i = 0; i < 9; i++) {
//                    NpBleLog.log("连续写" + i);
                    DevImageUtils.getInstance().next(false);
                    tmpIndex++;
                }
                DevImageUtils.getInstance().next(true);
                tmpIndex++;
            }
        }

    }

    @Override
    protected void onDataWriteFail(UUID uuid, byte[] data, int status) {
        NpBleLog.log("onDataWriteSuccess:" + uuid.toString() + "///" + BleUtil.byte2HexStr(data));
        if (isOtaMode && onWriteCallback != null) {
            onWriteCallback.onDataWriteFail(data);
        }
    }

    @Override
    protected void onFinishTaskAfterConn() {

    }


    public void writeData(byte[] data) {
        write(data);
    }

    /**
     * 写图片数据
     *
     * @param data
     */
    public void writeImageData(byte[] data, boolean isNeedCallback) {
        try {

            if (isNeedCallback) {
                writeCharacteristicWithOutResponse(dataServiceUUID, imageDataWriteUUID, data);
            } else {
                writeCharacteristicWithOutResponseWithOutCallback(dataServiceUUID, imageDataWriteUUID, data);
            }

//            writeCharacteristicWithOutResponse(dataServiceUUID, imageDataWriteUUID, data);
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
        }
    }


    public interface OnWriteCallback {
        void onDataWriteSuccess(byte[] data);

        void onDataWriteFail(byte[] data);
    }


}
