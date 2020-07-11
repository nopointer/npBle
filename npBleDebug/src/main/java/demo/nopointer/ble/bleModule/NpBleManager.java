package demo.nopointer.ble.bleModule;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import demo.nopointer.ble.MainApplication;
import demo.nopointer.ble.bleModule.bean.CharaBean;
import demo.nopointer.ble.dialog.bleservice.BleServiceBean;
import demo.nopointer.ble.utils.ToastHelper;
import npble.nopointer.ble.conn.NpBleAbsConnManager;
import npble.nopointer.exception.NpBleUUIDNullException;
import npble.nopointer.log.NpBleLog;
import npble.nopointer.util.BleUtil;

public class NpBleManager extends NpBleAbsConnManager {
    private static NpBleManager instance = null;
    BleDataProcessingUtils bleDataProcessingUtils = null;
    private List<BleServiceBean> bleServiceBeanList = new ArrayList();
    private BleDataReceiveListener dataReceiveListener;
    private HashSet<String> deviceAllUuids = new HashSet();
    private CharaBean readNotifyUuid;
    private CharaBean writeUuid;


    private NpBleManager(Context paramContext) {
        super(paramContext);
        this.bleServiceBeanList.clear();
    }

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

    public List<BleServiceBean> getBleServiceBeanList() {
        return this.bleServiceBeanList;
    }

    public boolean isSelectReadOrNotifyUUID() {
        if ((this.readNotifyUuid != null) && (this.readNotifyUuid.getServiceUUId() != null) && (this.readNotifyUuid.getCharaUUid() != null)) {
            return true;
        }
        ToastHelper.getToastHelper().show("请先选择需要监听或者读取的uuid");
        return false;
    }

    public void loadCfg() {
    }

    protected void onBeforeWriteData(UUID uuid, byte[] data) {
        NpBleLog.log(uuid.toString() + "写指令之前:" + BleUtil.byte2HexStr(data));
    }

    protected void onConnException() {
        if (isHandDisConn()) {
            NpBleLog.log("这是手动断开的，不处理");
            return;
        }
        NpBleLog.log("连接异常，重连");
    }

    protected void onDataReceive(byte[] paramArrayOfByte, UUID paramUUID) {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("onDataReceive====>");
        localStringBuilder.append(BleUtil.byte2HexStr(paramArrayOfByte));
        NpBleLog.log(localStringBuilder.toString());
        if (this.dataReceiveListener != null) {
            this.dataReceiveListener.onReceiveData(paramUUID.toString(), paramArrayOfByte);
        }
        this.bleDataProcessingUtils.handResponseData(paramUUID, paramArrayOfByte);
    }

    protected void onDataWriteFail(UUID uuid, byte[] data, int code) {
        NpBleLog.log(uuid.toString() + "onDataWriteFail:" + BleUtil.byte2HexStr(data) + "///" + code);
    }

    protected void onDataWriteSuccess(UUID uuid, byte[] data) {
        NpBleLog.log(uuid.toString() + "onDataWriteSuccess:" + BleUtil.byte2HexStr(data));
    }

    public void onDiscoveredServices(BluetoothGatt paramBluetoothGatt) {
        super.onDiscoveredServices(paramBluetoothGatt);
        this.bleServiceBeanList.clear();
        this.deviceAllUuids.clear();
        if (paramBluetoothGatt != null) {
            List<BluetoothGattService> bluetoothGattServiceList = paramBluetoothGatt.getServices();

            for (BluetoothGattService service : bluetoothGattServiceList) {
                BleServiceBean serviceBean = new BleServiceBean(service.getUuid().toString());
                deviceAllUuids.add(serviceBean.getUuid());

                List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
                ArrayList charaList = new ArrayList();

                for (BluetoothGattCharacteristic characteristic : characteristicList) {
                    BleServiceBean charaBean = new BleServiceBean(characteristic.getUuid().toString());
                    this.deviceAllUuids.add(charaBean.getUuid());
                    charaBean.setType(characteristic.getProperties());
                    charaList.add(charaBean);
                }
                serviceBean.setCharaBeanList(charaList);
                this.bleServiceBeanList.add(serviceBean);
            }
        }
        NpBleLog.log("获取到了设备的uuid");
    }

    protected void onFinishTaskAfterConn() {
        NpBleLog.log("onFinishTaskAfterConn===>时序任务完成");
    }

    public void readCharaData() {
        if ((this.readNotifyUuid != null) && (this.readNotifyUuid.getServiceUUId() != null) && (this.readNotifyUuid.getCharaUUid() != null)) {
            try {
                readCharacteristic(this.readNotifyUuid.getServiceUUId(), this.readNotifyUuid.getCharaUUid());
                return;
            } catch (NpBleUUIDNullException localNpBleUUIDNullException) {
                localNpBleUUIDNullException.printStackTrace();
                return;
            }
        }
        ToastHelper.getToastHelper().show("请先选择需要监听或者读取的uuid");
    }

    public void setDataReceiveListener(BleDataReceiveListener paramBleDataReceiveListener) {
        this.dataReceiveListener = paramBleDataReceiveListener;
    }

    public void setReadNotifyUuid(CharaBean paramCharaBean) {
        if ((paramCharaBean != null) && (this.deviceAllUuids.contains(paramCharaBean.getServiceUUId().toString())) && (this.deviceAllUuids.contains(paramCharaBean.getCharaUUid().toString()))) {
            this.readNotifyUuid = paramCharaBean;
            return;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("自动设置上次的读或者通知uuid");
        localStringBuilder.append(paramCharaBean.toString());
        NpBleLog.log(localStringBuilder.toString());
        NpBleLog.log("应该是遇到了同名称的别家设备");
    }

    public void setWriteUuid(CharaBean paramCharaBean) {
        if ((paramCharaBean != null) && (this.deviceAllUuids.contains(paramCharaBean.getServiceUUId().toString())) && (this.deviceAllUuids.contains(paramCharaBean.getCharaUUid().toString()))) {
            this.writeUuid = paramCharaBean;
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("自动设置上次的写uuid");
            localStringBuilder.append(paramCharaBean.toString());
            NpBleLog.log(localStringBuilder.toString());
            return;
        }
        NpBleLog.log("应该是遇到了同名称的别家设备");
    }

    public void startReadNotifyUuid() {
        if ((this.readNotifyUuid != null) && (this.readNotifyUuid.getServiceUUId() != null) && (this.readNotifyUuid.getCharaUUid() != null)) {
            try {
                setNotificationCallback(this.readNotifyUuid.getServiceUUId(), this.readNotifyUuid.getCharaUUid());
                enableNotifications(this.readNotifyUuid.getServiceUUId(), this.readNotifyUuid.getCharaUUid());
                return;
            } catch (NpBleUUIDNullException localNpBleUUIDNullException) {
                localNpBleUUIDNullException.printStackTrace();
                return;
            }
        }
        ToastHelper.getToastHelper().show("请先选择需要监听或者读取的uuid");
    }

    public void stopReadNotifyUuid() {
        if ((this.readNotifyUuid != null) && (this.readNotifyUuid.getServiceUUId() != null) && (this.readNotifyUuid.getCharaUUid() != null)) {
            try {
                removeNotificationCallback(this.readNotifyUuid.getServiceUUId(), this.readNotifyUuid.getCharaUUid());
                disableNotifications(this.readNotifyUuid.getServiceUUId(), this.readNotifyUuid.getCharaUUid());
                return;
            } catch (NpBleUUIDNullException localNpBleUUIDNullException) {
                localNpBleUUIDNullException.printStackTrace();
                return;
            }
        }
        ToastHelper.getToastHelper().show("请先选择需要监听或者读取的uuid");
    }

    public void taskSuccess() {
        try {
            Thread.sleep(20L);
        } catch (InterruptedException localInterruptedException) {
            localInterruptedException.printStackTrace();
        }
        nextTask();
    }

    public void writeData(byte[] paramArrayOfByte) {
        if ((this.writeUuid != null) && (this.writeUuid.getServiceUUId() != null) && (this.writeUuid.getCharaUUid() != null)) {
            try {
                writeCharacteristicWithOutResponse(this.writeUuid.getServiceUUId(), this.writeUuid.getCharaUUid(), paramArrayOfByte);
                return;
            } catch (NpBleUUIDNullException e) {
                e.printStackTrace();
                return;
            }
        }
        ToastHelper.getToastHelper().show("请先选择需要写数据的蓝牙特征uuid");
    }

    public static abstract interface BleDataReceiveListener {
        public abstract void onReceiveData(String paramString, byte[] paramArrayOfByte);
    }
}
