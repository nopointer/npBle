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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.WriteRequest;
import no.nordicsemi.android.ble.callback.BeforeCallback;
import no.nordicsemi.android.ble.callback.FailCallback;
import no.nordicsemi.android.ble.callback.SuccessCallback;
import no.nordicsemi.android.ble.callback.WriteProgressCallback;
import no.nordicsemi.android.ble.data.Data;
import npble.nopointer.ble.conn.callbacks.NpBleCallback;
import npble.nopointer.ble.conn.callbacks.NpDataReceivedCallback;
import npble.nopointer.ble.conn.callbacks.NpDataSentCallback;
import npble.nopointer.ble.conn.callbacks.NpFailCallback;
import npble.nopointer.ble.conn.callbacks.NpSuccessCallback;
import npble.nopointer.ble.scan.BleScanner;
import npble.nopointer.ble.scan.ScanListener;
import npble.nopointer.device.BleDevice;
import npble.nopointer.exception.NpBleUUIDNullException;
import npble.nopointer.log.NpBleLog;
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
        mustUUIDList = new HashSet<>();
        if (bleStateReceiver == null) {
            bleStateReceiver = new BleStateReceiver();
        }
        bleStateReceiver.startListen(context);
        setGattCallbacks(npBleCallback);


    }

    /**
     * 发起连接请求的mac地址
     */
    private String connRequestMac = null;


    /**
     * 有的破手机 完全没有回调，比如努比亚的破手机，需要每3s做一次连接断开的检测
     */
    private Handler connCheckHandler = new Handler();

    /**
     * 单位秒，更新连接状态的间隔
     */
    private int connCheckIntervalSecond = 5;

    private Runnable connCheckRunner = new Runnable() {
        @Override
        public void run() {
            if (TextUtils.isEmpty(connRequestMac)) {
                NpBleLog.log("connRequestMac 为空 null，不检测");
                if (connCheckHandler != null) {
                    NpBleLog.log("connCheckHandler 为空 null");
                    connCheckHandler.removeCallbacksAndMessages(null);
                }
            } else {

                boolean isConnected = isConnected();
                boolean isInConnList = isInConnList();
                NpBleLog.log("isConnected():" + isConnected + "///isInConnList():" + isInConnList);

                if (isConnected && isInConnList) {
                    NpBleLog.log("真正意义上的连接了");
                } else {
                    NpBleLog.log("破手机 识别不了状态，误认为还连接着");
                    if (npBleCallback != null) {
                        try {
                            BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(connRequestMac);
                            npBleCallback.onDeviceDisconnected(bluetoothDevice);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (connCheckHandler != null) {
                    connCheckHandler.postDelayed(this, connCheckIntervalSecond * 1000);
                }
            }

        }
    };


    /**
     * 设置刷新连接状态的间隔  单位秒
     * 不能低于3，不能高于30 （如果不符合的话，就是用默认5）
     *
     * @param connCheckIntervalSecond
     */
    public void setConnCheckIntervalSecond(int connCheckIntervalSecond) {
        if (connCheckIntervalSecond < 3 || connCheckIntervalSecond > 30) {
            connCheckIntervalSecond = 5;
        }

        this.connCheckIntervalSecond = connCheckIntervalSecond;
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
        NpBleLog.logLibBleLog(priority + "," + message);
    }


    protected void stopListen(Context context) {
        if (bleStateReceiver != null) {
            bleStateReceiver.stopListen(context);
        }
    }


    //某个特征uuid是否可以存在重复的情况
    private String repeatChartUUID = null;
    //某个特征uuid是否可以存在重复的情况 实际重复的次数（默认情况是都是1，除非实际项目里面有多个重复的）
    private int repeatChartUUIDCount = 1;

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
            NpBleLog.log("此时已经不是时序了");
            return;
        }
        taskIndex++;
        NpBleLog.log("task:" + taskIndex + "/" + taskCount + "、、、");
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
            NpBleLog.log("任务完成");
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
        BleUtil.connDeviceList(getContext());
        if (!verifyConnBefore(mac)) {
            return;
        }
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
        NpBleLog.log("=====>手动处理断开");
        isHandDisConn = true;
        isConnectIng = false;
        connRequestMac = null;
        if (connCheckHandler != null) {
            connCheckHandler.removeCallbacksAndMessages(null);
        }
        if (mBluetoothGatt != null && isConnected()&&isInConnList()) {
            NpBleLog.log("已经在连接中，就不发出拦截请求了，直接断开");
            disconnect().enqueue();
        } else {
            NpBleLog.log("没有在连接中，发出拦截请求即连接后立马断开）");
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

        connRequestMac = bluetoothDevice.getAddress();

        connCheckHandler.postDelayed(connCheckRunner, connCheckIntervalSecond * 1000);

        NpBleLog.log("当前实际发出连接请求的设备是:" + new Gson().toJson(new String[]{bluetoothDevice.getAddress(), bluetoothDevice.getName()}));
        boolIsInterceptConn = false;
        isHandDisConn = false;
        if (!TextUtils.isEmpty(bluetoothDevice.getName())) {
            if (mBluetoothGatt != null) {
                NpBleLog.log("已经有过设备缓存信息,刷新后,开始连接");
//                refreshDeviceCache().enqueue();
            }
            String phoneBrand = android.os.Build.BRAND;
            if (
                    phoneBrand.equalsIgnoreCase("OPPO") ||
                            phoneBrand.equalsIgnoreCase("VIVO")
            ) {
                BleScanner.getInstance().startScan();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BleScanner.getInstance().stopScan();
                    }
                }, 15 * 1000);
            }
            withBleConnState(NpBleConnState.CONNECTING);
            connect(bluetoothDevice)
                    .retry(3, 500)
                    .useAutoConnect(false)
                    .enqueue();
        } else {
            withBleConnState(NpBleConnState.CONNECTING);
            NpBleLog.log("名称为空，需要开启一下扫描来缓存一下设备名称");
            hadScanDeviceFlag = true;
            BleScanner.getInstance().registerScanListener(new ScanListener() {
                @Override
                public void onScan(BleDevice bleDevice) {
                    NpBleLog.log("hadScanDeviceFlag=====>" + hadScanDeviceFlag + "///扫描到的设备:" + new Gson().toJson(bleDevice));
                    NpBleLog.log("bleDevice=====>" + bleDevice.getMac() + "///" + bluetoothDevice.getAddress());
                    if (hadScanDeviceFlag) {
                        if (bleDevice != null && bleDevice.getMac().equalsIgnoreCase(bluetoothDevice.getAddress())) {
                            BleScanner.getInstance().unRegisterScanListener(this);
                            hadScanDeviceFlag = false;
                            //扫描到设备，移除扫描的超时处理
                            handler.removeCallbacksAndMessages(null);
                            BleScanner.getInstance().stopScan();
                            NpBleLog.log("扫描到设备了，停止扫描，然后再连接");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    connect(bluetoothDevice)
                                            .retry(3, 500)
                                            .useAutoConnect(false)
                                            .enqueue();
                                }
                            }, 3000);
                        }
                    }
                }

                @Override
                public void onFailure(int code) {
                    NpBleLog.log("onScanFailed====>" + code);
                }
            });
            BleScanner.getInstance().startScan();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //如果过了30秒后，还是没有扫描到设备的话，就采取直连的方式
                    BleScanner.getInstance().stopScan();
                    NpBleLog.log("扫描设备超时，停止扫描，然后再连接");

                    if (hadScanDeviceFlag) {
                        hadScanDeviceFlag = false;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                connect(bluetoothDevice)
                                        .retry(3, 500)
                                        .useAutoConnect(false)
                                        .enqueue();
                            }
                        }, 2000);
                    }
                }
            }, 15 * 1000);
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
            NpBleLog.log("initialize===>");
            if (isHandDisConn) {
                NpBleLog.log("有拦截请求，需要断开");
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
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                requestConnectionPriority(ConnectionPriorityRequest.CONNECTION_PRIORITY_HIGH).enqueue();
//            }
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

            NpBleLog.log("====================================");
            NpBleLog.log("====================================");

            int totalCharaCount = 0;//总特征数量
            int flagChartCount = 0;//被标记的UUID数量

            HashSet<String> tmpUUidList = new HashSet<>();
            for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
                NpBleLog.log("service UUID:" + bluetoothGattService.getUuid());
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                    NpBleLog.log("chara UUID:" + bluetoothGattCharacteristic.getUuid());
                    if (mustUUIDList != null) {
                        for (UUID uuid : mustUUIDList) {
                            if (uuid.toString().equalsIgnoreCase(bluetoothGattCharacteristic.getUuid().toString())) {
                                tmpUUidList.add(uuid.toString());
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(repeatChartUUID) && repeatChartUUID.equalsIgnoreCase(bluetoothGattCharacteristic.getUuid().toString())) {
                        flagChartCount++;
                    }
                    totalCharaCount++;
                }
            }
            NpBleLog.log("====================================");
            NpBleLog.log("====================================");


            if (totalCharaCount == 0) {
                NpBleLog.log("扫描服务特征为0，断开");
                return false;
            }

            //如果唯一UUID不为空，
            if (!TextUtils.isEmpty(repeatChartUUID)) {
                if (flagChartCount != repeatChartUUIDCount) {
                    NpBleLog.log("特征uuid重复数量错误，断开");
                    return false;
                }
            }


            NpBleLog.log("验证设备所需的uuid列表===>" + new Gson().toJson(mustUUIDList));
            if (mustUUIDList == null || mustUUIDList.size() < 0) return true;
            NpBleLog.log("tmpUUidList:" + tmpUUidList.size());
            NpBleLog.log("totalCharaCount:" + totalCharaCount);
            if (tmpUUidList.size() == mustUUIDList.size()) {
                return true;
            } else {
                NpBleLog.log("uuid对不上，情况不对");
                isHandDisConn = true;
                return false;
            }
        }
    };


    private NpBleCallback npBleCallback = new NpBleCallback() {
        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            NpBleLog.log("onDeviceConnecting : " + device.getAddress());
            withBleConnState(NpBleConnState.CONNECTING);
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            NpBleLog.log("onDeviceConnected : " + device.getAddress());
            isConnectIng = false;

            withBleConnState(NpBleConnState.CONNECTED);
            onBleDeviceConnected();
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            NpBleLog.log("onDeviceDisconnecting : " + device.getAddress());
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            String mac = device.getAddress();
            NpBleLog.log("onDeviceDisconnected : " + mac + " /// " + isHandDisConn);
            isConnectIng = false;


            if (isHandDisConn) {
                withBleConnState(NpBleConnState.HANDDISCONN);
                NpBleLog.log("onDeviceDisconnected : withBleConnState(NpBleConnState.HANDDISCONN)");
                onHandDisConnected();
                NpBleLog.log("onDeviceDisconnected : onHandDisConnected");
            } else {
                NpBleLog.log("onDeviceDisconnected : refreshDeviceCache().enqueue() start");
                refreshDeviceCache().enqueue();
                NpBleLog.log("onDeviceDisconnected : refreshDeviceCache().enqueue() end");
                withBleConnState(NpBleConnState.CONNEXCEPTION);
                NpBleLog.log("onDeviceDisconnected : withBleConnState(NpBleConnState.CONNEXCEPTION)");
                onConnException();
                NpBleLog.log("onDeviceDisconnected : onConnException");
            }
            isHandDisConn = false;

        }

        @Override
        public void onLinkLossOccurred(@NonNull BluetoothDevice device) {
            NpBleLog.log("onLinkLossOccurred : " + device.getAddress());
        }

        @Override
        public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {
            NpBleLog.log("onServicesDiscovered : " + device.getAddress());
            onDiscoveredServices(mBluetoothGatt);
        }

        @Override
        public void onDeviceReady(@NonNull BluetoothDevice device) {
            NpBleLog.log("onDeviceReady : " + device.getAddress());
        }


        @Override
        public void onBondingRequired(@NonNull BluetoothDevice device) {
            NpBleLog.log("onBondingRequired : " + device.getAddress());
        }

        @Override
        public void onBonded(@NonNull BluetoothDevice device) {
            NpBleLog.log("onBonded : " + device.getAddress());
        }

        @Override
        public void onBondingFailed(@NonNull BluetoothDevice device) {
            NpBleLog.log("onBondingFailed : " + device.getAddress());
        }

        @Override
        public void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode) {
            NpBleLog.log("onError : " + device.getAddress());
        }

        @Override
        public void onDeviceNotSupported(@NonNull BluetoothDevice device) {
            NpBleLog.log("onDeviceNotSupported : " + device.getAddress());
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
     * 写特征数据 不要写回调监听
     *
     * @param serviceUUId
     * @param uuid
     * @throws NpBleUUIDNullException
     */
    protected void writeCharacteristicWithOutCallback(UUID serviceUUId, UUID uuid, byte[] data) throws NpBleUUIDNullException {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid);
        bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        bluetoothGattCharacteristic.setValue(data);
        writeCharacteristicWithOutCallback(bluetoothGattCharacteristic);
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
     * 写特征数据 无响应写
     *
     * @param serviceUUId
     * @param uuid
     * @throws NpBleUUIDNullException
     */
    protected void writeCharacteristicWithOutResponseWithOutCallback(UUID serviceUUId, UUID uuid, byte[] data) throws NpBleUUIDNullException {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid);
        bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        bluetoothGattCharacteristic.setValue(data);
        writeCharacteristicWithOutCallback(bluetoothGattCharacteristic);
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
                NpBleLog.log("onDataSent : " + uuid.toString() + "{ enableNotifications }");
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
                NpBleLog.log("onDataSent : " + uuid.toString() + "{ disableNotifications }");
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
                NpBleLog.log("Write : " + uuid.toString() + "{ " + BleUtil.byte2HexStr(data.getValue()) + " }");
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
     * 写特征数据 不要回调
     *
     * @param bluetoothGattCharacteristic
     */
    private void writeCharacteristicWithOutCallback(final BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        byte[] data = bluetoothGattCharacteristic.getValue();
        writeCharacteristic(bluetoothGattCharacteristic, data).enqueue();
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
     * 手动断开设备
     */
    protected void onHandDisConnected() {

    }

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
     * Ble设备连接上了
     */

    protected void onBleDeviceConnected() {

    }


    /**
     * 是否允许出现重复的UUID，针对部分华为手机，这个目前只是在定性阶段 还没完全确定下来 慎用
     * <p>
     * 后面发现有的设备里面本来就有同名的uuid存在的情况，所以此函数增加2个参数，用于记录某个uuid真实的次数
     *
     * @param uuid
     * @param allowCount
     * @return
     */
    protected void setRepeatChartUUID(String uuid, int allowCount) {
        this.repeatChartUUID = uuid;
        if (allowCount <= 1) {
            allowCount = 1;
        }
        this.repeatChartUUIDCount = allowCount;
    }


    /**
     * 系统的蓝牙打开
     */
    public void onBleOpen() {
        isConnectIng = false;
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

            NpBleLog.log("BleStateReceiver 广播的action:===>" + action);

            if (action.equals(ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        NpBleLog.log("当前系统蓝牙正在打开......");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        NpBleLog.log("当前系统蓝牙处于开启状态");
                        onBleOpen();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        NpBleLog.log("当前系统蓝牙正在关闭......");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        NpBleLog.log("当前系统蓝牙处于关闭状态");
                        onBleClose();
                        break;
                }
            } else {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null) {
                    NpBleLog.log("设备为空，不需要往后执行......");
                    return;
                }
                NpBleLog.log("跟本次广播相关的设备===>" + device.getName() + "/" + device.getAddress());

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
            NpBleLog.log("verifyConnBefore，蓝牙没有打开呢！");
            return false;
        }

        //2.判断mac地址的正确性
        if (!BleUtil.isRightBleMacAddress(mac)) {
            NpBleLog.log("verifyConnBefore，mac地址都不对,地址要注意大写,且不能为空！！！！！");
            return false;
        }

        //3.判断当前的连接动作，是不是在连接
        if (isConnectIng) {
            NpBleLog.log("verifyConnBefore，ble-当前已经发出了连接请求，还没响应，不需要再发送这次请求");
//            withBleConnState(NpBleConnState.CONNECTING);
            return false;
        }

        //4.判断连接状态
        if (isConnected()&&isInConnList()) {
            NpBleLog.log("verifyConnBefore，已经是连接的，，不需要花里胡哨的了");
            return false;
        }


        return true;
    }

    /**
     * 是否正在连接中
     *
     * @return
     */
    protected boolean isConnectIng() {
        return isConnectIng;
    }

    /**
     * 是否是手动断开连接
     *
     * @return
     */
    protected boolean isHandDisConn() {
        return isHandDisConn;
    }


    /**
     * 设备是否连接上了
     *
     * @return
     */
    protected boolean isDeviceConnected() {
        return isConnected() && isInConnList();
    }


    /**
     * 是否在连接队列中
     *
     * @return
     */
    protected boolean isInConnList() {
        if (TextUtils.isEmpty(connRequestMac)) return false;
        List<BluetoothDevice> connDeviceList = BleUtil.connDeviceList(getContext());
        if (connDeviceList != null) {
            NpBleLog.log("当前系统连接的设备数量有:" + connDeviceList.size());
            int index = -1, size = connDeviceList.size();

            for (int i = 0; i < size; i++) {
                BluetoothDevice bluetoothDevice = connDeviceList.get(i);
                if (bluetoothDevice.getAddress().equalsIgnoreCase(connRequestMac)) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                NpBleLog.log("在连接队列中");
                return true;
            } else {
                NpBleLog.log("不在连接队列中");
                return false;
            }
        }
        return false;
    }
}
