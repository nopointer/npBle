package demo.np.deviceuicustom.ble;

import android.content.Context;
import android.os.Build;

import java.util.UUID;

import demo.np.deviceuicustom.MainApplication;
import demo.np.deviceuicustom.ble.imageTransport.DevImageUtils;
import demo.np.deviceuicustom.ble.utils.DevDataUtils;
import no.nordicsemi.android.ble.ConnectionPriorityRequest;
import npLog.nopointer.core.NpLog;
import npble.nopointer.ble.conn.NpBleAbsConnManager;
import npble.nopointer.exception.NpBleUUIDNullException;
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    requestConnectionPriority(ConnectionPriorityRequest.CONNECTION_PRIORITY_HIGH).enqueue();
                }
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
            int index = BleUtil.byte2IntLR(data[1], data[2]);
            NpLog.eAndSave("续传图片传输的索引:" + index);
            DevImageUtils.getInstance().withNext(index);
        }
    }

    @Override
    protected void onConnException() {

    }

    @Override
    protected void onDataWriteSuccess(UUID uuid, byte[] data) {
        NpLog.e("onDataWriteSuccess:" + uuid.toString() + "///" + BleUtil.byte2HexStr(data));
        if (uuid.equals(dataWriteUUID)) {
            if (isOtaMode && onWriteCallback != null) {
                onWriteCallback.onDataWriteSuccess(data);
            }
        } else if (uuid.equals(imageDataWriteUUID)) {
            NpLog.e("开始写下一包图片的数据");
            DevImageUtils.getInstance().next();
        }

    }

    @Override
    protected void onDataWriteFail(UUID uuid, byte[] data, int status) {
        NpLog.e("onDataWriteSuccess:" + uuid.toString() + "///" + BleUtil.byte2HexStr(data));
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
    public void writeImageData(byte[] data) {
        try {
            writeCharacteristicWithOutResponse(dataServiceUUID, imageDataWriteUUID, data);
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
        }
    }


    public interface OnWriteCallback {
        void onDataWriteSuccess(byte[] data);

        void onDataWriteFail(byte[] data);
    }


}
