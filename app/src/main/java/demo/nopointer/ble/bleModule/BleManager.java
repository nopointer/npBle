//package demo.nopointer.ble.bleModule;
//
//import android.content.Context;
//import android.os.Build;
//import android.os.Handler;
//import android.support.annotation.NonNull;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import demo.nopointer.ble.MainApplication;
//import npble.nopointer.ble.conn.NpBleAbsConnManager;
//import npble.nopointer.log.NpBleLog;
//import npble.nopointer.util.BleUtil;
//
//import static demo.nopointer.ble.bleModule.BleUUIDCfg.U_write;
//
///**
// * Created by nopointer on 2017/11/18.
// */
//
//public abstract class BleManager extends NpBleAbsConnManager implements BleCfg {
//
//
//    //线程池
//    private ExecutorService cachedThreadPool = Executors.newFixedThreadPool(20);
//
//    private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
//
//
//    public static List<String> fuckPhoneModelList = new ArrayList<>();
//
//
//    static {
//        //辣鸡华为手机，LDN-AL00
//        fuckPhoneModelList.add("LDN-AL00");
//        fuckPhoneModelList.add("HRY-AL00Ta");
//    }
//
//    /**
//     * The manager constructor.
//     * <p>
//     * After constructing the manager, the callbacks object must be set with
//     * //     * {@link
//     * <p>
//     * To connect a device, call {@link #connect(BluetoothDevice)}.
//     *
//     * @param context the context.
//     * @param context
//     */
//    public BleManager(@NonNull Context context) {
//        super(context);
////        bleDataProcessor = BleDataProcessor.getInstance();
////        bleDataProcessor.setNpBleManager(this);
//    }
//
//
//    private static BleManager instance = null;
//
//
//    public static BleManager getInstance() {
//        synchronized (Void.class) {
//            if (instance == null) {
//                synchronized (Void.class) {
//                    if (instance == null) {
//                        instance = new BleManager(MainApplication.getMainApplication());
//                        instance.setMustUUID(U_write);
//                    }
//                }
//            }
//        }
//        return instance;
//    }
//
////    //步数解析对象
////    private DevStepUtil devStepUtil = DevStepUtil.gteInstance();
////
////    //睡眠解析对象
////    private DevSleepUtil devSleepUtil = DevSleepUtil.getInstance();
////
////    //检测数据解析对象
////    private DevMeasureUtil devMeasureUtil = DevMeasureUtil.gteInstance();
//
//    //设备电量
//    private int batteryInt = -1;
//
//    //获取设备电量
//    public int getBatteryInt() {
//        return batteryInt;
//    }
//
//    //设备功能列表
////    private DeviceFunction deviceFunction = new DeviceFunction();
//
//    //获取设备功能列表
////    public DeviceFunction getDeviceFunction() {
////        return deviceFunction;
////    }
//
//    //是否是获取了电量，因为电量上报会干扰数据的同步，所以在获取电量的时候，才认为是同步时序的任务，其他时候不作为任务结束的判断
//    private boolean isGetBattery = false;
//
//    //是否是连接后的第一个动作，获取必要的数据和同步历史数据
//    private boolean isAfterConnFirstTask = false;
//
//    private boolean boolIsSyncData = false;
//
//
//    private Handler handler = new Handler();
//
//
//    long[] longs = new long[]{3000, 800, 2000, 500, 2000, 300};
//
//    //最多连接异常几次后，提示
//    private static final int intMaxLostCount = 1;
//    //当前属于第几次异常
//    private int intLostCount = 0;
//
//    //设备信息
////    private DeviceInfoEntity deviceInfoEntity = null;
//
////    public DeviceInfoEntity getDeviceInfoEntity() {
////        return deviceInfoEntity;
////    }
//
//    //是否是在同步历史数据
//    private boolean isSyncHistoryData = false;
//
//    @Override
//    public void loadCfg() {
//
////        devStepUtil.clearReceiveBuffer();
//        //添加任务
////        addBleUnitTask(BleUnitTask.createEnableNotify(dataServiceUUID, dataNotifyUUID, "打开通知"));
////        addBleUnitTask(BleUnitTask.createWrite(dataServiceUUID, dataWriteUUID, DevDataUtils.choiceDevUIType(2), "获取电量"));
////        addBleUnitTask(BleUnitTask.createWrite(U_SER, U_write, DevDataUtil.currentTime(), "同步时间"));
////        addBleUnitTask(BleUnitTask.createWrite(U_SER, U_write, DevDataUtil.createFirmware(), "获取固件版本"));
////        addBleUnitTask(BleUnitTask.createWrite(U_SER, U_write, DevDataUtil.arrBytePackData(SharedPrefereceSleepRemindLock.read())));
////        addBleUnitTask(BleUnitTask.createWrite(U_SER, U_write, DevDataUtil.devFunction(), "获取手环功能"));
//    }
//
//    //连接异常
//    @Override
//    public void onConnException() {
////        devStepUtil.clearReceiveBuffer();
//        batteryInt = -1;
////        deviceInfoEntity = null;
////        setSyncFinish(true);
//        NpBleLog.e("连接异常，开始重新连接");
//        reConn();
//    }
//
//    @Override
//    protected void onDataWriteSuccess(UUID uuid, byte[] data) {
//
//    }
//
//    @Override
//    protected void onDataWriteFail(UUID uuid, byte[] data, int status) {
//
//    }
//
//
//    /**
//     * 蓝牙打开的回调
//     */
//    @Override
//    public synchronized void onBleOpen() {
//        intLostCount = 0;
////        deviceInfoEntity = null;
//        isSyncHistoryData = false;
//        NpBleLog.e("系统蓝牙打开，开始重新连接");
//        reConn();
//    }
//
//
//    private void reConn() {
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                connDevice(MainActivity.macForXinCore);
//            }
//        }, 1200);
//    }
//
//    //连接成功
////    @Override
////    public void onConnectSuccess() {
////        devStepUtil.clearReceiveBuffer();
////        intLostCount = 0;
////        isAfterConnFirstTask = true;
////        isGetBattery = false;
////    }
//
//    //手动断开
////    @Override
////    public void onHandDisConn() {
////        devStepUtil.clearReceiveBuffer();
////        batteryInt = -1;
////        deviceInfoEntity = null;
////        isSyncHistoryData = false;
////        ycBleLog.e("手动断开的设备");
////    }
//
//    //步数
//    private static final int TYPE_STEP = 1;
//    //睡眠
//    private static final int TYPE_SLEEP = 2;
//    //检测
//    private static final int TYPE_MEASURE = 3;
//
//    //同步数据的类型 1步数，2睡眠，3测量
//    private int dataType = TYPE_STEP;
//    //同步天数
//    private int syncIndex = -1;
//
//    /**
//     * 某个数据需要请求多少次,也就是需要同步多久的数据 最多七天 根据app保存的最后一条数据来看的
//     */
//    private int needQueryDayCount = 0;
//
//    //同步任务完成后
//    @Override
//    public void onFinishTaskAfterConn() {
//        isAfterConnFirstTask = false;
//        isSyncHistoryData = true;
//        NpBleLog.e("同步时序任务结束");
//
//        NpBleLog.e("基本指令同步完成....");
//        NpBleLog.e("先拿到今天的步数....");
////
////        try {
////            writeData(dataServiceUUID, dataWriteUUID, DevDataUtils.controlDevUI(1));
////        } catch (BleUUIDNullException e) {
////            e.printStackTrace();
////        }
////
////        handler.postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                devImageUtils.start();
////            }
////        }, 1000 * 21);
//
////        ycBleLog.e("先罗列一下设备功能列表:" + deviceFunction.toString());
//
////        if (deviceFunction.isSupportStep()) {
////            needQueryDayCount = devStepUtil.needQueryDayDataCount();
////            dataType = TYPE_STEP;
////        } else {
////            if (deviceFunction.isSupportSleep()) {
////                needQueryDayCount = devSleepUtil.needQueryDayDataCount();
////                dataType = TYPE_SLEEP;
////            } else {
////                if (deviceFunction.isSupportHr() || deviceFunction.isSupportOx() || deviceFunction.isSupportBlood()) {
////                    dataType = TYPE_MEASURE;
////                }
////            }
////        }
//        syncIndex = -1;
////        syncDataWithFunction();
//    }
//
//
//    @Override
//    public void onDataReceive(final byte[] data, final UUID uuid) {
//        NpBleLog.e("接收到数据>>>:" + BleUtil.byte2HexStr(data));
//
////        if (uuid.equals(imageDataNotifyUUID)) {
////            int index = BleUtil.byte2IntLR(data[1], data[2]);
////            devImageUtils.withNext(index);
////        }
//
////        onHandData(data);
//    }
//
//    public void writeData(final byte[] data) {
//        NpBleLog.e("准备写指令<<<" + BleUtil.byte2HexStr(data));
//        //如果没有连接 或者正在同步数据，结束刷新
//        if (!isConnected() || isAfterConnFirstTask) {
//            NpBleLog.e(">没有连接,或者正在数据同步历史");
//            return;
//        } else if (isConnected() && !isAfterConnFirstTask) {
////            privateWriteData(data);
//        } else if (isAfterConnFirstTask) {
//            NpBleLog.e(">数据正在同步,不能交互");
//        }
//    }
//
//    //多包数据
//    private List<byte[]> listMultiPckData = new ArrayList<>();
//    //多包数据的索引
//    private int intMultiPckDataIndex = -1;
//    //是否正在写多包数据
//    private boolean boolIsMultiWriteDataIng = false;
//
//    private Handler handlerMulti = new Handler();
//
//    //清除写多包数据的标志位
//    private synchronized void clearMultiFlag() {
//        intMultiPckDataIndex = -1;
//        listMultiPckData.clear();
//        boolIsMultiWriteDataIng = false;
//    }
//
//    //写多包数据的下一包数据
//    private synchronized void nextPckDataForMultiData() {
//        intMultiPckDataIndex++;
//        if (intMultiPckDataIndex < listMultiPckData.size()) {
//            writeData(listMultiPckData.get(intMultiPckDataIndex));
//        } else {
//            handlerMulti.removeCallbacksAndMessages(null);
//            clearMultiFlag();
//        }
//    }
//
//    //修改某些数据的状态
//    private void reUpdateSomeStataAndFlag(byte[] data) {
//        int flag = data[0] & 0xff;
//        if (flag == 0x13) {
//            //同步步数的时候，要清空之前的数据
////            devStepUtil.clearReceiveBuffer();
//        } else if (flag == 0x15) {
//            //同步睡眠的时候，要清空之前的数据
//        } else if (flag == 0x16) {
////            allHistoryMeasureData.clear();
//        } else if (flag == 0x31) {
//            //实时上报数据，打开
//        } else if (BleUtil.byte2HexStr(data).equalsIgnoreCase("1d0155aa")) {
//            NpBleLog.e("OTA 指令" + BleUtil.byte2HexStr(data));
//        }
//    }
//
//    //处理通知上报的数据
//    private void onHandData(final byte[] data) {
//        int flag = data[0] & 0xff;
//
//        switch (flag) {
//            case 0x81://同步时间回应
//            {
//                NpBleLog.e("时间同步成功了");
//            }
//            break;
//            case 0x82: //闹钟或者久坐提醒 消息推送开关 回应
//            {
//                NpBleLog.e("闹钟或者久坐提醒同步成功了");
//            }
//            break;
//            case 0x83:  //设备功能列表,单包数据
//            {
////                deviceFunction = DevDataUtil.getDevFunctionList(data);
////                for (DeviceFunction.DeviceFunctionCallback deviceFunctionCallback : deviceFunctionCallbackHashSet) {
////                    deviceFunctionCallback.onGetFunction(deviceFunction);
////                }
////                ycBleLog.e("获取到设备功能列表了+" + deviceFunction.toString());
//            }
//            break;
//            case 0x85: //天气回应
//            {
//                NpBleLog.e("天气数据写成功了");
//            }
//            break;
//            case 0x93: //步数数据解析,多包数据
//            {
//                cachedThreadPool.execute(new Runnable() {
//                    @Override
//                    public void run() {
////                        devStepUtil.receiveData(data);
////                        //处理结束数据的标识，回收超时处理
////                        if ((data[4] & 0xff) == 0xff) {
////                            taskSuccess();
////                            if (isSyncHistoryData) {
////                                //如果是在同步历史数据的时候，才能自动循环同步数据
////                                syncDataWithFunction();
////                            }
////                        }
//                    }
//                });
//            }
//            break;
//            case 0x94:  //电量，单包数据
//            {
//                batteryInt = BleUtil.byte2IntLR(data[1]);
////                BatteryHelper.getBatteryHelper().setBattery(batteryInt);
//                if (isGetBattery) {
////                    taskSuccess();
//                }
//            }
//            break;
//            case 0xB2:
//            case 0xB3: //实时步数上报，总的数据
//            {
////                devStepUtil.getTotalStepEntity(data);
////                taskSuccess();
//            }
//            break;
//            case 0x53: //查找/停止查找 手机
//            {
//                handFindMusic(BleUtil.byte2IntLR(data[1]) == 1);
////                taskSuccess();
//            }
//            break;
//            case 0x9F: //固件信息，版本
//            {
////                deviceInfoEntity = DevDataUtil.getDeviceInfo(data);
////                ycBleLog.e("固件版本:" + deviceInfoEntity.toString());
////                taskSuccess();
//            }
//            break;
//            case 0xF3: //来电拒绝
//            {
////                MainApplication.getApp().sendBroadcast(new Intent(BleReceiver.actionEndCall));
//            }
//            break;
//            case 0xA2: {
//                NpBleLog.e("拍照指令>>>");
////                sendAction(ActionCfg.takePhotoAction);
////                taskSuccess();
//            }
//            break;
//            case 0xD2: //拍照或者取消拍照
//            {
//                switch (BleUtil.byte2IntLR(data[1])) {
//                    case 0://APP退出拍照界面（disable）
////                        sendAction(ActionCfg.exitTakePhotoForApp);
//                        break;
//                    case 1://APP进入拍照界面（enable）
//                        break;
//                }
////                taskSuccess();
//            }
//            break;
//            case 0xE0: //检测状态 ，开始，停止
//            {
////                devMeasureUtil.getMeasureState(data);
////                taskSuccess();
//            }
//            break;
//            case 0xE1: //实时检测数据
//            {
////                devMeasureUtil.getRealMeasureEntity(data);
////                taskSuccess();
//            }
//            break;
//            case 0xEE: //数据响应错误了
//            {
//                if (BleUtil.byte2HexStr(data).equalsIgnoreCase("EEEEEEEE")) {
//                    NpBleLog.e("数据响应错误了，不知道是个什么鬼情况");
//                }
//            }
//            break;
//            default:
//                NpBleLog.e("默认task成功的标志");
////                taskSuccess();
//                break;
//        }
//    }
//
//    //寻找手机
//    private void handFindMusic(boolean isFindEnable) {
////        VibratorUtils.getInstance(MainApplication.getMainApplication()).vibrator(longs, -1);
////        //如果在后台运行的话 就发送通知
////        if (!AppBaseUtils.isForeground(MainApplication.getMainApplication())) {
////            NotifyUtils.sendFindNotify(MainApplication.getMainApplication());
////        } else {
////            //如果在后台运行的话 就toast 震动
////            ToastHelper.getToastHelper().show(R.string.device_is_fond_phone);
////        }
//
//    }
//
////    private void uploadMeasure(List<MeasureHistoryData> measureHistoryDatas) {
////
////        for (MeasureHistoryData measureHistoryData : measureHistoryDatas) {
////            if (measureHistoryData.getHr() != 0) {
////                dataHelper.uploadHr(measureHistoryData);
////            }
////            if (measureHistoryData.getOx() != 0) {
////                dataHelper.uploadOx(measureHistoryData);
////            }
////            if (measureHistoryData.getBdH() != 0 && measureHistoryData.getBdL() != 0) {
////                dataHelper.uploadBd(measureHistoryData);
////            }
////        }
////
////    }
//
//    //回调写处理标志位，在写回调里面（某些写操作不会有数据上报作为响应）
//    private final void handWithWriteDataFlag(byte[] data) {
//        int flag = data[0] & 0xff;
//        if (flag == 0x73) {
////            taskSuccess();
//            nextPckDataForMultiData();
//        } else if (flag == 0x11) {
////            taskSuccess();
//        } else if (flag == 0x1D) {
////            taskSuccess();
//        } else if (flag == 0x13) {
////            devStepUtil.clearReceiveBuffer();
//        } else if (flag == 0x60) {
//            NpBleLog.e("开启或者停止了测量");
////            taskSuccess();
//        }
//    }
////
////
////    /***
////     * 超时的指令
////     */
////    @Override
////    public void onResponseTimeOut(byte[] command) {
////        super.onResponseTimeOut(command);
////        if (BleUtil.byte2HexStr(command).equalsIgnoreCase("0300")) {
//////            deviceFunction = new DeviceFunction();
//////            for (DeviceFunction.DeviceFunctionCallback deviceFunctionCallback : deviceFunctionCallbackHashSet) {
//////                deviceFunctionCallback.onGetFunction(deviceFunction);
//////            }
////        }
////        ycBleLog.e("--超时的指令是:" + BleUtil.byte2HexStr(command));
////    }
//
//
//    //是否是可以交互数据
//    private boolean boolCanRWN() {
//        if (!isConnected() || isAfterConnFirstTask || boolIsSyncData) {
//            if (!isConnected()) {
//                NpBleLog.e("没有连接");
//            } else if (isAfterConnFirstTask) {
//                NpBleLog.e("在同步任务时序");
//            }
//            return false;
//        }
//        return true;
//    }
//
//    //发送广播
//    private void sendAction(String action) {
////        MainApplication.getMainApplication().sendBroadcast(new Intent(action));
//    }
//
//
//
//
//    static {
//        //辣鸡华为手机，LDN-AL00
////        fuckPhoneModelList.add("LDN-AL00");
////        fuckPhoneModelList.add("HRY-AL00Ta");
//        fuckPhoneModelList.add("LLD-AL00");
//    }
//
////    public void connBleDevice(final String mac) {
////        //手机型号
////        String phoneModel = Build.MODEL;
////        if (!fuckPhoneModelList.contains(phoneModel)) {
////            super.connDevice(mac);
////        } else {
////            if (isConn()) {
////                connDevice(mac);
////                return;
////            } else {
////                ycBleLog.e("由于这个手机底层蓝牙资源释放不及时，故此需要特殊处理一下");
////                connDevice(mac);
////                handler.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        disConn();
////                        handler.postDelayed(new Runnable() {
////                            @Override
////                            public void run() {
////                                connDevice(mac);
////                            }
////                        }, 3000);
////                    }
////                }, 1000);
////            }
////        }
////    }
//
//
//    public void connBleDevice(final String mac) {
//        //手机型号
//        String phoneModel = Build.MODEL;
//        if (!fuckPhoneModelList.contains(phoneModel)) {
//            super.connDevice(mac);
//        } else {
//            if (isConnected()) {
//                connDevice(mac);
//                return;
//            } else {
//                connDevice(mac);
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
////                        disConn();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                connDevice(mac);
//                            }
//                        }, 3000);
//                    }
//                }, 100);
//            }
//        }
//    }
//}
