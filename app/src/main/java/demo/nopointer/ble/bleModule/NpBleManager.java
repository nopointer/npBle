package demo.nopointer.ble.bleModule;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.UUID;

import demo.nopointer.ble.MainApplication;
import demo.nopointer.ble.activity.BleActivity;
import npLog.nopointer.core.NpLog;
import npble.nopointer.ble.conn.NpBleAbsConnManager;
import npble.nopointer.exception.NpBleUUIDNullException;
import npble.nopointer.ota.absimpl.xc.no.nordicsemi.android.BleManagerCallbacks;
import npble.nopointer.util.BleUtil;

public class NpBleManager extends NpBleAbsConnManager implements BleUUIDCfg {
    /**
     * The manager constructor.
     * <p>
     * After constructing the manager, the callbacks object must be set with
     * {@link #setGattCallbacks(BleManagerCallbacks)}.
     * <p>
     * To connect a device, call {@link #connect(BluetoothDevice)}.
     *
     * @param context the context.
     */
    private NpBleManager(Context context) {
        super(context);
        bleDataProcessingUtils = new BleDataProcessingUtils(this);
    }

    /**
     * 数据解析工具
     */
    BleDataProcessingUtils bleDataProcessingUtils = null;

    @Override
    public void loadCfg() {
        try {
            setNotificationCallback(U_SER, U_notify);
            enableNotifications(U_SER, U_notify);
//            addTask(createWriteTask(U_SER, U_write, new byte[]{0x14}));
//            addTask(createWriteTask(U_SER, U_write, new byte[]{0x51, 0x01}));

            addTask(createWriteTask(U_SER, U_write, new byte[]{0x13, 20, 2, 29}));
//            addTask(createWriteTask(U_SER, U_write, new byte[]{0x51, 0x01}));

            addTask(createWriteTaskWithOutResp(U_SER, U_write, new byte[]{0x13, 20, 2, 29}));
            addTask(createWriteTaskWithOutResp(U_SER, U_write, new byte[]{0x14}));
            addTask(createWriteTaskWithOutResp(U_SER, U_write, new byte[]{0x51, 0x01}));
//            writeCharacteristic(U_SER, U_write, new byte[]{0x51, 0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            addTask(createWriteTask(U_SER, U_write, new byte[]{0x13, 19, 12, 15}));
//            byte[] data = createPushMsgContent("哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哦嗯嗯嗯123你好你好任务我偶然和维护费艾特内容给如果以隔热清入关前二个偶尔end", 2);
//            writeCharacteristicWithMostPack(U_SER, U_write, data, 0, data.length, new WriteProgressCallback() {
//                @Override
//                public void onPacketSent(@NonNull BluetoothDevice device, @Nullable byte[] data, int index) {
//                    try {
//                        ycBleLog.e("onPacketSent ： " + BleUtil.byte2HexStr(data) + "///" + index);
//                        writeCharacteristicWithMostPack(U_SER, U_write, data, (index + 1) * 20, 4, this);
//                    } catch (BleUUIDNullException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            writeCharacteristic(U_SER,U_write,new byte[]{0x13,19,12,14});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x13,19,12,13});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x13,19,12,12});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
            disconnect();
        }
    }


    /**
     * 写数据
     *
     * @param data
     */
    public void writeData(byte[] data) {
        try {
            writeCharacteristic(U_SER, U_write, data);
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDataReceive(byte[] data, UUID uuid) {
        NpLog.eAndSave("onDataReceive====>" + BleUtil.byte2HexStr(data));
        bleDataProcessingUtils.handResponseData(uuid, data);
    }

    /**
     * 连接异常
     */
    @Override
    protected void onConnException() {
        if (isHandDisConn()) {
            NpLog.eAndSave("这是手动断开的，不处理");
        } else {
            NpLog.eAndSave("连接异常，重连");
            connDevice(BleActivity.macForXinCore);
        }
    }

    @Override
    protected void onDataWriteSuccess(UUID uuid, byte[] data) {
        NpLog.eAndSave("onDataWriteSuccess===>" + BleUtil.byte2HexStr(data));
    }

    @Override
    protected void onDataWriteFail(UUID uuid, byte[] data, int status) {
        NpLog.eAndSave("onDataWriteFail===>" + BleUtil.byte2HexStr(data));
    }

    @Override
    protected void onFinishTaskAfterConn() {
        NpLog.eAndSave("onFinishTaskAfterConn===>时序任务完成");
    }


    private static NpBleManager instance = null;

    public static NpBleManager getInstance() {
        synchronized (Void.class) {
            if (instance == null) {
                synchronized (Void.class) {
                    if (instance == null) {
                        instance = new NpBleManager(MainApplication.getMainApplication());
                        instance.setMustUUID(U_write);
                    }
                }
            }
        }
        return instance;
    }


    public void taskSuccess() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        nextTask();
    }

    @Override
    protected void onBeforeWriteData(UUID uuid, byte[] data) {
        NpLog.eAndSave("写指令之前:" + BleUtil.byte2HexStr(data));
        bleDataProcessingUtils.onBeforeWriteData(uuid, data);
    }
}
