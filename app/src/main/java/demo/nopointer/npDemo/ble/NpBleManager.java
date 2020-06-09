package demo.nopointer.npDemo.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

import demo.nopointer.npDemo.MainApplication;
import no.nordicsemi.android.ble.ConnectionPriorityRequest;
import no.nordicsemi.android.ble.callback.WriteProgressCallback;
import npLog.nopointer.core.NpLog;
import npble.nopointer.ble.conn.NpBleAbsConnManager;
import npble.nopointer.exception.NpBleUUIDNullException;
import npble.nopointer.util.BleUtil;

/**
 * BLE 管理工具
 */
public class NpBleManager extends NpBleAbsConnManager implements BleSomeCfg {
    private static NpBleManager instance = null;

    public static NpBleManager getInstance() {
        synchronized (Void.class) {
            if (instance == null) {
                synchronized (Void.class) {
                    if (instance == null) {
                        instance = new NpBleManager(MainApplication.getMainApplication());
//                        instance.setMustUUID(dataWriteUUID);
                    }
                }
            }
        }
        return instance;
    }

    private NpBleManager(Context context) {
        super(context);
        bleDataProcessingUtils = new BleDataProcessingUtils(this);
    }

    private String mac = "A4:C1:38:7A:67:4F";

    /**
     * 数据解析工具
     */
    BleDataProcessingUtils bleDataProcessingUtils = null;

    private Handler handler = new Handler();


    @Override
    protected void onConnException() {
        NpLog.e("检测到断开:(isHandDisConn)" + isHandDisConn());
        if (!isHandDisConn()) {
            reConn();
        }
    }


    private void reConn() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connDevice(mac);
//                BleDevice bleDevice = SharedPrefereceDevice.read();
//                if (bleDevice != null && !TextUtils.isEmpty(bleDevice.getMac())) {
//                    NpLog.eAndSave("尝试重连");
////                    connDevice(bleDevice.getMac());
//                    connDevice(mac);
//                } else {
//                    disConnectDevice();
//                }
            }
        }, 3000);
    }

    @Override
    protected void loadCfg() {
        try {
//            setNotificationCallback(dataServiceUUID, dataNotifyUUID);
//            enableNotifications(dataServiceUUID, dataNotifyUUID);

            //同步时间
//            addTask(createWriteTaskWithOutResp(dataServiceUUID, dataWriteUUID, DevDataBaleUtils.getSystemTime()));
//            addTask(createWriteTaskWithOutResp(dataServiceUUID, dataWriteUUID, DevDataBaleUtils.getDeviceInfo()));
//            addTask(createWriteTaskWithOutResp(dataServiceUUID, dataWriteUUID, DevDataBaleUtils.syncPhoneSysLanguage()));


//

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onFinishTaskAfterConn() {
        NpLog.e("同步时序完成");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    NpLog.e("开始请求间隔更改");
                    ConnectionPriorityRequest connectionPriorityRequest =
                            requestConnectionPriority(ConnectionPriorityRequest.CONNECTION_PRIORITY_HIGH);
                    connectionPriorityRequest.enqueue();
//            requestConnectionPriority(ConnectionPriorityRequest.CONNECTION_PRIORITY_HIGH).enqueue();
//            requestConnectionPriority(ConnectionPriorityRequest.CONNECTION_PRIORITY_HIGH).enqueue();
//            requestConnectionPriority(ConnectionPriorityRequest.CONNECTION_PRIORITY_HIGH).enqueue();
                }
            }
        }, 5000);

    }

    @Override
    protected void onBeforeWriteData(UUID uuid, byte[] data) {
        bleDataProcessingUtils.onBeforeWriteData(uuid, data);
    }


    @Override
    protected void onDataReceive(byte[] data, UUID uuid) {
        NpLog.eAndSave(uuid + " 接收到数据: " + BleUtil.byte2HexStr(data));
//        bleDataProcessingUtils.onDataReceive(uuid, data);
    }


    @Override
    protected void onDataWriteSuccess(UUID uuid, byte[] data) {
        NpLog.eAndSave(uuid + " 写成功数据: " + BleUtil.byte2HexStr(data));
//        bleDataProcessingUtils.handWriteCallback(uuid, data);
    }

    @Override
    protected void onDataWriteFail(UUID uuid, byte[] data, int status) {
        NpLog.eAndSave(uuid + " 写失败数据: " + BleUtil.byte2HexStr(data));
    }


    protected void taskSuccess() {
        nextTask();
    }

    protected void writeData(byte[] data) {
        try {
            writeCharacteristic(dataServiceUUID, dataWriteUUID, data);
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
        }
    }


    /**
     * 写多包数据
     *
     * @param multiData
     * @throws NpBleUUIDNullException
     */
    private void writeCharacteristicWithMostPack(byte[] multiData, boolean isSyncContacts) throws NpBleUUIDNullException {
        writeCharacteristicWithMostPack(dataServiceUUID, dataWriteUUID, multiData, 0, multiData.length, new WriteProgressCallback() {
            @Override
            public void onPacketSent(@NonNull BluetoothDevice device, @Nullable byte[] data, int index) {
                try {
                    NpLog.eAndSave("onPacketSent ： " + npble.nopointer.util.BleUtil.byte2HexStr(data) + "///" + index);
                    writeCharacteristicWithMostPack(dataServiceUUID, dataWriteUUID, data, (index + 1) * 20, data.length, this);
                } catch (NpBleUUIDNullException e) {
                    e.printStackTrace();
                }
            }
        }).enqueue();
    }

    /**
     * 发送指令
     *
     * @param data
     */
    public void sendCommand(byte[] data) {
        try {
            writeCharacteristicWithOutResponse(dataServiceUUID, dataWriteUUID, data);
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connDevice(String mac) {
        BluetoothGatt bluetoothGatt = BleUtil.getBluetoothDevice(mac).connectGatt(getContext(), false, new BluetoothGattCallback() {
        });
        NpLog.eAndSave("bluetoothGatt" + bluetoothGatt.getDevice().toString());
        bluetoothGatt.disconnect();
        refreshDeviceCache().enqueue();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NpBleManager.super.connDevice(mac);
            }
        }, 2000);

    }
}
