package npble.nopointer.ble.conn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.ConnectionPriorityRequest;
import no.nordicsemi.android.ble.WriteRequest;
import no.nordicsemi.android.ble.callback.BeforeCallback;
import no.nordicsemi.android.ble.callback.FailCallback;
import no.nordicsemi.android.ble.callback.SuccessCallback;
import no.nordicsemi.android.ble.callback.WriteProgressCallback;
import no.nordicsemi.android.ble.data.Data;
import npLog.nopointer.core.NpLog;
import npble.nopointer.ble.conn.callbacks.NpBleCallback;
import npble.nopointer.ble.conn.callbacks.NpDataReceivedCallback;
import npble.nopointer.ble.conn.callbacks.NpDataSentCallback;
import npble.nopointer.ble.conn.callbacks.NpFailCallback;
import npble.nopointer.ble.conn.callbacks.NpSuccessCallback;
import npble.nopointer.ble.scan.BleScanner;
import npble.nopointer.ble.scan.ScanListener;
import npble.nopointer.device.BleDevice;
import npble.nopointer.exception.NpBleUUIDNullException;
import npble.nopointer.util.BleUtil;

import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;
import static android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED;

/**
 * 抽象的连接管理对象
 */
public abstract class NpBleAbsConnManager extends BleManager<NpBleCallback> {


    /**
     * The manager constructor.
     * <p>
     * After constructing the manager, the callbacks object must be set with
     * //     * {@link
     * <p>
     * To connect a device, call {@link #connect(BluetoothDevice)}.
     *
     * @param context the context.
     */
    public NpBleAbsConnManager(@NonNull Context context) {
        super(context);
        if (bleStateReceiver == null) {
            bleStateReceiver = new BleStateReceiver();
        }
        bleStateReceiver.startListen(context);
        setGattCallbacks(npBleCallback);
    }

    /**
     * 蓝牙状态接收器
     */
    protected BleStateReceiver bleStateReceiver = null;

    /**
     * 连接后的任务时序是否已经完成
     */
    private boolean hasAfterConnectedTaskEnd = false;

    public boolean isHasAfterConnectedTaskEnd() {
        return hasAfterConnectedTaskEnd;
    }

    /**
     * 是否请求了连接，因为蓝牙的连接需要一个过程（并不是马上就能连接成功），所以需要一个标志位做判断
     */
    private boolean isConnectIng = false;


    /**
     * 是否是手动断开
     */
    private boolean isHandDisConn = false;

    protected boolean isHandDisConn() {
        return isHandDisConn;
    }

    /**
     * 是否拦截中途拦截蓝牙的连接
     */
    private boolean boolIsInterceptConn = false;


    private BluetoothGatt mBluetoothGatt = null;

    /**
     * 设备的连接状态
     */
    private NpBleConnState bleConnState = null;

    public NpBleConnState getBleConnState() {
        return bleConnState;
    }


    @Override
    public void log(int priority, @NonNull String message) {
//        super.log(priority, message);
        NpLog.i(priority + "," + message);
    }

    /**
     * 当前任务的索引
     */
    private int taskIndex = -1;
    /**
     * 任务栈的数量
     */
    private int taskCount = 0;
    /**
     * 请求列表
     */
    private List<NpBleTask> requestTaskList = new ArrayList<>();

    protected void nextTask() {
        if (hasAfterConnectedTaskEnd) {
            NpLog.eAndSave("此时已经不是时序了");
            return;
        }
        taskIndex++;
        NpLog.eAndSave("task:" + taskIndex + "/" + taskCount + "、、、");
        if (taskIndex < taskCount) {
            NpBleTask bleTask = requestTaskList.get(taskIndex);


            if (bleTask.getRequest() instanceof WriteRequest) {
                WriteRequest writeRequest = (WriteRequest) bleTask.getRequest();
                writeRequest
                        .before(new BeforeCallback() {
                            @Override
                            public void onRequestStarted(@NonNull BluetoothDevice device) {
                                onBeforeWriteData(bleTask.getUuid(), bleTask.getData());
                            }
                        })
                        .done(new SuccessCallback() {
                            @Override
                            public void onRequestCompleted(@NonNull BluetoothDevice device) {
                                onDataWriteSuccess(bleTask.getUuid(), bleTask.getData());
                            }
                        }).fail(new FailCallback() {
                    @Override
                    public void onRequestFailed(@NonNull BluetoothDevice device, int status) {
                        onDataWriteFail(bleTask.getUuid(), bleTask.getData(), status);
                        nextTask();
                    }
                }).enqueue();
            }
        } else {
            hasAfterConnectedTaskEnd = true;
            NpLog.eAndSave("任务完成");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                requestConnectionPriority(ConnectionPriorityRequest.CONNECTION_PRIORITY_BALANCED).enqueue();
//            }
            onFinishTaskAfterConn();
        }
    }


    /**
     * 添加任务
     *
     * @param bleTask
     */
    public void addTask(NpBleTask bleTask) {
        requestTaskList.add(bleTask);
    }

    /**
     * 写数据之前的回调 ，可以在这里自定义超时处理或者其他特殊指令的处理
     *
     * @param uuid
     * @param data
     */
    protected abstract void onBeforeWriteData(UUID uuid, byte[] data);


    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }


    /**
     * 连接设备
     *
     * @param mac
     */
    public void connDevice(String mac) {
        if (!verifyConnBefore(mac)) {
            return;
        }
        isHandDisConn = false;
        BluetoothDevice bluetoothDevice = BleUtil.getBluetoothDevice(mac);
        if (bluetoothDevice != null) {
            isConnectIng = true;
            connectCode(bluetoothDevice);
        }
    }

    /**
     * 手动断开连接,同时也做成拦截（防止蓝牙在发出连接请求后，后续连接上的情况）
     */
    public void disConnectDevice() {
        NpLog.eAndSave("=====>手动断开指令");
        isHandDisConn = true;
        isConnectIng = false;
        if (mBluetoothGatt != null && isConnected()) {
            NpLog.eAndSave("已经在连接中，就不发出拦截请求了，直接断开");
            disconnect().enqueue();
        } else {
            NpLog.eAndSave("没有在连接中，发出拦截请求即连接后立马断开）");
            boolIsInterceptConn = true;
        }
    }

    /**
     * 扫描后的连接标志位
     */
    private boolean hadScanDeviceFlag = true;

    private Handler handler = new Handler();

    /**
     * 连接设备
     *
     * @param bluetoothDevice
     */
    protected void connectCode(final BluetoothDevice bluetoothDevice) {
        NpLog.eAndSave("当前实际发出连接请求的设备是:" + new Gson().toJson(new String[]{bluetoothDevice.getAddress(), bluetoothDevice.getName()}));
        boolIsInterceptConn = false;
        isHandDisConn = false;

        if (!TextUtils.isEmpty(bluetoothDevice.getName())) {
            if (mBluetoothGatt != null) {
                NpLog.eAndSave("已经有过设备缓存信息,刷新后,开始连接");
//                refreshDeviceCache().enqueue();
            }
            connect(bluetoothDevice)
                    .retry(3, 300)
                    .useAutoConnect(false)
                    .enqueue();
        } else {
            NpLog.eAndSave("名称为空，需要开启一下扫描来缓存一下设备名称");
            hadScanDeviceFlag = true;
            BleScanner.getInstance().registerScanListener(new ScanListener() {
                @Override
                public void onScan(BleDevice bleDevice) {
                    NpLog.eAndSave("hadScanDeviceFlag=====>" + hadScanDeviceFlag + "///扫描到的设备:" + new Gson().toJson(bleDevice));
                    if (hadScanDeviceFlag) {
                        if (bleDevice != null && bleDevice.getMac().equalsIgnoreCase(bluetoothDevice.getAddress())) {
                            BleScanner.getInstance().unRegisterScanListener(this);
                            hadScanDeviceFlag = false;
                            //扫描到设备，移除扫描的超时处理
                            handler.removeCallbacksAndMessages(null);
                            BleScanner.getInstance().stopScan();
                            NpLog.eAndSave("扫描到设备了，停止扫描，然后再连接");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    connect(bluetoothDevice)
                                            .retry(3, 100)
                                            .useAutoConnect(false)
                                            .enqueue();
                                }
                            }, 2000);
                        }
                    }
                }

                @Override
                public void onFailure(int code) {
                    NpLog.eAndSave("onScanFailed====>" + code);
                }
            });
            BleScanner.getInstance().startScan();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //如果过了30秒后，还是没有扫描到设备的话，就采取直连的方式
                    if (hadScanDeviceFlag) {
                        hadScanDeviceFlag = false;
                        BleScanner.getInstance().stopScan();
                        NpLog.eAndSave("扫描设备超时，停止扫描，然后再连接");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                connect(bluetoothDevice)
                                        .retry(3, 100)
                                        .useAutoConnect(false)
                                        .enqueue();
                            }
                        }, 1200);
                    }
                }
            }, 30 * 1000);
        }
    }


    /**
     * 蓝牙连接结果的回调
     */
    private HashSet<NpBleConnCallback> bleBleConnCallbackHashSet = new HashSet();

    /**
     * 注册连接回调
     *
     * @param connCallback
     */
    public void registerConnCallback(NpBleConnCallback connCallback) {
        if (!bleBleConnCallbackHashSet.contains(connCallback)) {
            bleBleConnCallbackHashSet.add(connCallback);
        }
    }

    /**
     * 注销连接回调
     *
     * @param connCallback
     */
    public void unRegisterConnCallback(NpBleConnCallback connCallback) {
        if (bleBleConnCallbackHashSet.contains(connCallback)) {
            bleBleConnCallbackHashSet.remove(connCallback);
        }
    }

    /**
     * 必须的特征uuid
     */
    private HashSet<UUID> mustUUIDList = null;

    /**
     * 设置设备的uuid 用来区分是否是本项目的设备
     *
     * @param uuids
     */
    protected void setMustUUID(UUID... uuids) {
        if (mustUUIDList == null) {
            mustUUIDList = new HashSet<>();
        }
        if (mustUUIDList != null && uuids != null) {
            for (UUID uuid : uuids) {
                mustUUIDList.add(uuid);
            }
        }
    }

    protected void addMustUUID(UUID uuid) {
        if (mustUUIDList == null) {
            mustUUIDList = new HashSet<>();
        }
        mustUUIDList.add(uuid);
    }

    protected void clearMustUUID() {
        if (mustUUIDList != null) {
            mustUUIDList.clear();
        }
    }


    @Override
    protected boolean shouldClearCacheWhenDisconnected() {
        return true;
    }


    /**
     * BluetoothGatt callbacks object.
     */
    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        /**
         * 这个函数就是在扫描完服务后回调的函数
         */
        @Override
        protected void initialize() {
            NpLog.eAndSave("initialize===>");
            if (isHandDisConn) {
                NpLog.eAndSave("有拦截请求，需要断开");
                disconnect().enqueue();
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (requestTaskList == null) {
                requestTaskList = new ArrayList<>();
            }
            requestTaskList.clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requestConnectionPriority(ConnectionPriorityRequest.CONNECTION_PRIORITY_HIGH).enqueue();
            }
            clearQueue();
            loadCfg();
            taskIndex = -1;
            taskCount = 0;
            if (requestTaskList != null && requestTaskList.size() > 0) {
                taskCount = requestTaskList.size();
            }

            if (taskCount > 0) {
                hasAfterConnectedTaskEnd = false;
                nextTask();
            } else {
                hasAfterConnectedTaskEnd = true;
                onFinishTaskAfterConn();
            }
        }

        @Override
        protected void onDeviceDisconnected() {
            mBluetoothGatt = null;
        }


        @Override
        public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
            mBluetoothGatt = gatt;
            NpLog.eAndSave("验证设备所需的uuid列表===>" + new Gson().toJson(mustUUIDList));
            if (mustUUIDList == null || mustUUIDList.size() < 0) return true;
            int count = 0;
            for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
                NpLog.eAndSave("service UUID:" + bluetoothGattService.getUuid());
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                    NpLog.eAndSave("chara UUID:" + bluetoothGattCharacteristic.getUuid());
                    for (UUID uuid : mustUUIDList) {
                        if (uuid.equals(bluetoothGattCharacteristic.getUuid())) {
                            count++;
                        }
                    }
                }
            }
            if (count == mustUUIDList.size()) {
                return true;
            } else {
                NpLog.eAndSave("uuid对不上，情况不对");
                isHandDisConn = true;
                return false;
            }
        }


    };


    private NpBleCallback npBleCallback = new NpBleCallback() {
        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            NpLog.eAndSave("onDeviceConnecting : " + device.getAddress());
            withBleConnState(NpBleConnState.CONNECTING);
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            NpLog.eAndSave("onDeviceConnected : " + device.getAddress());
            withBleConnState(NpBleConnState.CONNECTED);
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            NpLog.eAndSave("onDeviceDisconnecting : " + device.getAddress());
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            NpLog.eAndSave("onDeviceDisconnected : " + device.getAddress());
            if (isHandDisConn) {
                withBleConnState(NpBleConnState.HANDDISCONN);
            } else {
                isConnectIng = false;
                refreshDeviceCache().enqueue();
                onConnException();
                withBleConnState(NpBleConnState.CONNEXCEPTION);
            }
            isHandDisConn = false;
        }

        @Override
        public void onLinkLossOccurred(@NonNull BluetoothDevice device) {
            NpLog.eAndSave("onLinkLossOccurred : " + device.getAddress());
        }

        @Override
        public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {
            NpLog.eAndSave("onServicesDiscovered : " + device.getAddress());
            if (mBluetoothGatt != null) {
                for (BluetoothGattService bluetoothGattService : mBluetoothGatt.getServices()) {
                    NpLog.eAndSave("service UUID:" + bluetoothGattService.getUuid());
                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                        NpLog.eAndSave("chara UUID:" + bluetoothGattCharacteristic.getUuid());
                    }
                }
            }
            onDiscoveredServices(mBluetoothGatt);
        }

        @Override
        public void onDeviceReady(@NonNull BluetoothDevice device) {
            NpLog.eAndSave("onDeviceReady : " + device.getAddress());
        }


        @Override
        public void onBondingRequired(@NonNull BluetoothDevice device) {
            NpLog.eAndSave("onBondingRequired : " + device.getAddress());
        }

        @Override
        public void onBonded(@NonNull BluetoothDevice device) {
            NpLog.eAndSave("onBonded : " + device.getAddress());
        }

        @Override
        public void onBondingFailed(@NonNull BluetoothDevice device) {
            NpLog.eAndSave("onBondingFailed : " + device.getAddress());
        }

        @Override
        public void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode) {
            NpLog.eAndSave("onError : " + device.getAddress());
        }

        @Override
        public void onDeviceNotSupported(@NonNull BluetoothDevice device) {
            NpLog.eAndSave("onDeviceNotSupported : " + device.getAddress());
            onNotSupportDevice();
        }

    };


    /**
     * 处理连接后的时序，用户根据需求添加一系列的同步指令，比如同步时间，打开通知，读取数据等等,不需要的话就不管
     */
    protected abstract void loadCfg();


    /**
     * 处理具体的接收到的数据的逻辑，交给具体的实现类去完成
     *
     * @param data
     * @param uuid
     */
    protected abstract void onDataReceive(final byte[] data, final UUID uuid);


    /**
     * 读取特征
     *
     * @param serviceUUId
     * @param uuid
     * @throws NpBleUUIDNullException
     */
    protected void readCharacteristic(UUID serviceUUId, UUID uuid) throws NpBleUUIDNullException {
        readCharacteristic(BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid)).with(new NpDataReceivedCallback(uuid) {
            @Override
            public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data, UUID uuid) {
                onDataReceive(data.getValue(), uuid);
            }
        }).enqueue();
    }

    /**
     * 写特征数据
     *
     * @param serviceUUId
     * @param uuid
     * @throws NpBleUUIDNullException
     */
    protected void writeCharacteristic(UUID serviceUUId, UUID uuid, byte[] data) throws NpBleUUIDNullException {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid);
        bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        bluetoothGattCharacteristic.setValue(data);
        writeCharacteristic(bluetoothGattCharacteristic);
    }

    /**
     * 写特征数据 无响应写
     *
     * @param serviceUUId
     * @param uuid
     * @throws NpBleUUIDNullException
     */
    protected void writeCharacteristicWithOutResponse(UUID serviceUUId, UUID uuid, byte[] data) throws NpBleUUIDNullException {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid);
        bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        bluetoothGattCharacteristic.setValue(data);
        writeCharacteristic(bluetoothGattCharacteristic);
    }


    /**
     * 写特征数据，可以写多包数据
     *
     * @param serviceUUId
     * @param uuid
     * @throws NpBleUUIDNullException
     */
    protected WriteRequest writeCharacteristicWithMostPack(UUID serviceUUId, UUID uuid, byte[] data, int offset, final int length, WriteProgressCallback writeProgressCallback) throws NpBleUUIDNullException {
        return writeCharacteristic(BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid), data, offset, length).split(writeProgressCallback);
    }

    /**
     * 写特征数据，可以写多包数据
     *
     * @param serviceUUId
     * @param uuid
     * @throws NpBleUUIDNullException
     */
    protected WriteRequest writeCharacteristicWithMostPackWithOutResp(UUID serviceUUId, UUID uuid, byte[] data, int offset, final int length, WriteProgressCallback writeProgressCallback) throws NpBleUUIDNullException {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid);
        bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        return writeCharacteristic(bluetoothGattCharacteristic, data, offset, length).split(writeProgressCallback);
    }

    /**
     * 设置监听
     *
     * @param serviceUUId
     * @param uuid
     * @throws NpBleUUIDNullException
     */
    protected void setNotificationCallback(UUID serviceUUId, UUID uuid) throws NpBleUUIDNullException {
        setNotificationCallback(BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid)).with(new NpDataReceivedCallback(uuid) {
            @Override
            public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data, UUID uuid) {
                onDataReceive(data.getValue(), uuid);
            }
        });
    }


    /**
     * 设置监听
     *
     * @param serviceUUId
     * @param uuid
     * @throws NpBleUUIDNullException
     */
    protected void removeNotificationCallback(UUID serviceUUId, UUID uuid) throws NpBleUUIDNullException {
        setNotificationCallback(BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid)).with(null);
    }

    /**
     * 使能通知
     *
     * @param serviceUUId
     * @param uuid
     * @throws NpBleUUIDNullException
     */
    protected void enableNotifications(UUID serviceUUId, UUID uuid) throws NpBleUUIDNullException {
        enableNotifications(BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid)).with(new NpDataSentCallback(uuid) {
            @Override
            public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data, UUID uuid) {
                NpLog.eAndSave("onDataSent : " + uuid.toString() + "{ enableNotifications }");
            }
        }).enqueue();
    }

    /**
     * 使不能通知
     *
     * @param serviceUUId
     * @param uuid
     * @throws NpBleUUIDNullException
     */
    protected void disableNotifications(UUID serviceUUId, UUID uuid) throws NpBleUUIDNullException {
        disableNotifications(BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid)).with(new NpDataSentCallback(uuid) {
            @Override
            public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data, UUID uuid) {
                NpLog.eAndSave("onDataSent : " + uuid.toString() + "{ disableNotifications }");
            }
        }).enqueue();
    }


    /**
     * 写特征数据
     *
     * @param bluetoothGattCharacteristic
     */
    private void writeCharacteristic(final BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        UUID uuid = bluetoothGattCharacteristic.getUuid();
        byte[] data = bluetoothGattCharacteristic.getValue();
        writeCharacteristic(bluetoothGattCharacteristic, data).with(new NpDataSentCallback(uuid) {
            @Override
            public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data, UUID uuid) {
                NpLog.eAndSave("Write : " + uuid.toString() + "{ " + BleUtil.byte2HexStr(data.getValue()) + " }");
            }
        }).before(new BeforeCallback() {
            @Override
            public void onRequestStarted(@NonNull BluetoothDevice device) {
                onBeforeWriteData(uuid, data);
            }
        }).done(new NpSuccessCallback(uuid, data) {
            @Override
            public void onRequestCompleted(UUID uuid, byte[] data) {
                onDataWriteSuccess(uuid, data);
            }
        }).fail(new NpFailCallback(uuid, data) {
            @Override
            public void onRequestFailed(UUID uuid, byte[] data, int status) {
                onDataWriteFail(uuid, data, status);
            }
        }).enqueue();
    }

    /**
     * 创建一个使能通知请求，但是不会执行（等待调用），用于里连接后的时序
     *
     * @param serviceUUId
     * @param uuid
     * @throws NpBleUUIDNullException
     */
    public NpBleTask createEnableNotificationsTask(UUID serviceUUId, UUID uuid) throws NpBleUUIDNullException {
        WriteRequest writeRequest = null;
        try {
            writeRequest = enableNotifications(BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid));
            return NpBleTask.createEnableNotifyTask(writeRequest, uuid);
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 创建一个写任务的请求，并不会写数据（等待调用），用于里连接后的时序
     *
     * @param serviceUUId
     * @param uuid
     * @param data
     * @return
     */
    protected NpBleTask createWriteTask(UUID serviceUUId, UUID uuid, byte data[]) {
        WriteRequest writeRequest = null;
        try {
            writeRequest = writeCharacteristic(BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid), data);
            return NpBleTask.createWriteTask(writeRequest, uuid, data);
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 创建一个写任务的请求，并不会写数据（等待调用），用于里连接后的时序
     *
     * @param serviceUUId
     * @param uuid
     * @param data
     * @return
     */
    protected NpBleTask createWriteTaskWithOutResp(UUID serviceUUId, UUID uuid, byte data[]) {
        WriteRequest writeRequest = null;
        try {
            BluetoothGattCharacteristic bluetoothGattCharacteristic = BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid);
            bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            writeRequest = writeCharacteristic(bluetoothGattCharacteristic, data);
            return NpBleTask.createWriteTask(writeRequest, uuid, data);
        } catch (NpBleUUIDNullException e) {
            e.printStackTrace();
            return null;
        }
    }

    final void withBleConnState(NpBleConnState connState) {
        bleConnState = connState;
        for (NpBleConnCallback connCallback : bleBleConnCallbackHashSet) {
            connCallback.onConnState(connState);
        }
    }


    /**
     * 连接异常
     */
    protected abstract void onConnException();

    /**
     * 数据写成功
     *
     * @param uuid
     * @param data
     */
    protected abstract void onDataWriteSuccess(UUID uuid, byte[] data);

    /**
     * 数据写失败
     *
     * @param uuid
     * @param data
     * @param status
     */
    protected abstract void onDataWriteFail(UUID uuid, byte[] data, int status);

    /**
     * 连接成功后的时序任务完成
     */
    protected abstract void onFinishTaskAfterConn();

    /**
     * 不是本项目所支持的设备
     */
    protected void onNotSupportDevice() {
    }


    /**
     * 系统的蓝牙打开
     */
    public void onBleOpen() {

    }

    /**
     * 系统蓝牙关闭
     */
    public void onBleClose() {
        isHandDisConn = true;
        isConnectIng = false;
        refreshDeviceCache().enqueue();
        BleScanner.getInstance().stopScan();
    }

    /**
     * 设备的服务被检测到
     *
     * @param bluetoothGatt
     */
    public void onDiscoveredServices(BluetoothGatt bluetoothGatt) {

    }


    public class BleStateReceiver extends BroadcastReceiver {

        private BleStateReceiver() {
        }


        /**
         * 创建一个蓝牙状态的过滤器
         *
         * @return
         */
        private IntentFilter createSateFilter() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            return intentFilter;
        }

        /**
         * 开始监听mac地址
         *
         * @param context
         */
        public void startListen(Context context) {
            try {
                if (context != null) {
                    context.registerReceiver(this, createSateFilter());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 停止监听
         *
         * @param context
         */
        public void stopListen(Context context) {
            try {
                if (context != null) {
                    context.unregisterReceiver(this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onReceive(final Context context, Intent intent) {

            String action = intent.getAction();

            NpLog.eAndSave("BleStateReceiver 广播的action:===>" + action);

            if (action.equals(ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        NpLog.eAndSave("当前系统蓝牙正在打开......");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        NpLog.eAndSave("当前系统蓝牙处于开启状态");
                        onBleOpen();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        NpLog.eAndSave("当前系统蓝牙正在关闭......");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        NpLog.eAndSave("当前系统蓝牙处于关闭状态");
                        onBleClose();
                        break;
                }
            } else {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null) {
                    NpLog.eAndSave("设备为空，不需要往后执行......");
                    return;
                }
                NpLog.eAndSave("跟本次广播相关的设备===>" + device.getName() + "/" + device.getAddress());

                if (action == ACTION_ACL_DISCONNECTED) {

                }
            }
        }
    }


    /**
     * 验证是否可以连接
     *
     * @return
     */
    private boolean verifyConnBefore(String mac) {
        //1.判断蓝牙状态
        if (!BleUtil.isBLeEnabled()) {
            NpLog.eAndSave("verifyConnBefore，蓝牙没有打开呢！");
            return false;
        }
        //2.判断连接状态
        if (isConnected()) {
            NpLog.eAndSave("verifyConnBefore，已经是连接的，，不需要花里胡哨的了");
            return false;
        }
        //3.判断mac地址的正确性
        if (!BleUtil.isRightBleMacAddress(mac)) {
            NpLog.eAndSave("verifyConnBefore，mac地址都不对,地址要注意大写,且不能为空！！！！！");
            return false;
        }
        //4.判断当前的连接是否已经回应了
        if (isConnectIng) {
            NpLog.eAndSave("verifyConnBefore，ble-当前已经发出了连接请求，还没响应，不需要再发送这次请求");
//            withBleConnState(NpBleConnState.CONNECTING);
            return false;
        }
        return true;
    }

}
