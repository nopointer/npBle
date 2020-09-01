package demo.np.deviceuicustom.ble.ota;

import android.content.Context;

import java.util.UUID;

import demo.np.deviceuicustom.ble.BleSomeCfg;
import demo.np.deviceuicustom.ble.utils.DevDataUtils;
import npble.nopointer.ble.conn.NpBleAbsConnManager;
import npble.nopointer.exception.NpBleUUIDNullException;
import npble.nopointer.log.NpBleLog;
import npble.nopointer.util.BleUtil;

/**
 * 蓝牙交互对象
 */
public class BlePowerManager extends NpBleAbsConnManager implements BleSomeCfg {

    public BlePowerManager(Context context) {
        super(context);
        stopListen(context);
    }

    //是否已经写下去了关机指令
    private boolean isWritePowderOffCommand = false;


    private OnPowerCallback onPowerCallback = null;

    public void setOnPowerCallback(OnPowerCallback onPowerCallback) {
        this.onPowerCallback = onPowerCallback;
    }

    @Override
    protected void onBeforeWriteData(UUID uuid, byte[] data) {

    }

    @Override
    protected void loadCfg() {
        write(DevDataUtils.powderOff());
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

    }

    @Override
    protected void onConnException() {
        if (onPowerCallback != null) {
            if (isWritePowderOffCommand) {
                onPowerCallback.onPowerOffSuccess();
            } else {
                onPowerCallback.onPowerOffFail();
            }
        }
    }

    @Override
    protected void onDataWriteSuccess(UUID uuid, byte[] data) {
        NpBleLog.log("onDataWriteSuccess:" + uuid.toString() + "///" + BleUtil.byte2HexStr(data));
        if (uuid.equals(dataWriteUUID) && BleUtil.byte2HexStr(data).equalsIgnoreCase("AB0005FFFE800001")) {
            NpBleLog.log("关机指令发送完成了");
            isWritePowderOffCommand = true;
        }

    }

    @Override
    protected void onDataWriteFail(UUID uuid, byte[] data, int status) {
        NpBleLog.log("onDataWriteSuccess:" + uuid.toString() + "///" + BleUtil.byte2HexStr(data));
        if (uuid.equals(dataWriteUUID) && BleUtil.byte2HexStr(data).equalsIgnoreCase("AB0005FFFE800001")) {
            NpBleLog.log("关机指令发送失败了");
            isWritePowderOffCommand = false;
        }
    }

    @Override
    protected void onFinishTaskAfterConn() {

    }

    public interface OnPowerCallback {
        void onPowerOffSuccess();

        void onPowerOffFail();
    }


}
