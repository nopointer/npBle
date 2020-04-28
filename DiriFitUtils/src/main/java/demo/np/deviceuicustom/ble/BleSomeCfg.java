package demo.np.deviceuicustom.ble;

import java.util.UUID;

/**
 * 静态变量
 */
public interface BleSomeCfg {


    /**
     * 主服务service
     */
    UUID dataServiceUUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");

    /**
     * 数据写特征
     */
    UUID dataWriteUUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");


    /**
     * 数据通知特征
     */
    UUID dataNotifyUUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");


    /**
     * 表盘图片写数据特征
     */
    UUID imageDataWriteUUID = UUID.fromString("6e400005-b5a3-f393-e0a9-e50e24dcca9e");

    /**
     * 表盘图片设备端端通知数据特征
     */
    UUID imageDataNotifyUUID = UUID.fromString("6e400006-b5a3-f393-e0a9-e50e24dcca9e");

    /**
     * 测试数据，包括心率 血压 血氧 App端测试的 或者设备端测试的
     */
    public static final int MEASURE_DATA_FLAG = 0XFF02;

    /**
     * 同步时间的指令
     */
    public static final int SYNC_TIME = 0xFF22;



    /**
     * 选择表盘样式
     */
    public static final int CHOICE_DEVICE_UI_TYPE = 0xFF29;

    /**
     * 设备电量
     */
    public static final int DEVICE_BATTERY = 0xFF20;


    /**
     * 寻找手环
     */
    public static final int FIND_DEVICE = 0xFF2C;


    /**
     * 恢复出厂设置
     */
    public static final int RESET_SYSTEM = 0xFF26;


    /**
     * 消息提醒
     */
    public static final int MESSAGE_PUSH_ENBALE = 0xFF0C;


    /**
     * 消息内容推送
     */
    public static final int MESSAGE_CONTENT_PUSH = 0xFF0D;


    /**
     * 久坐提醒
     */
    public static final int LONG_SIT = 0XFF10;


    /**
     * 勿扰提醒
     */
    public static final int NOT_REMIND = 0xFF11;

    /**
     * 抬手亮屏
     */
    public static final int HAND_LIGHT = 0xFF12;


    /**
     * 下拉刷新
     */
    public static final int SYNC_REFRESH = 0xFF0A;

    /**
     * 设备信息
     */
    public static final int DEVICE_INFO = 0xFF21;


    /**
     * 全天候测量
     */
    public static final int ALL_DAY_MEASURE_HR = 0xFF2A;

    /**
     * 全天候测量的心率数据
     */
    int ALL_DAY_MEASURE_HR_HISTORY = 0xFF2B;

    /**
     * 同步系统语言
     */
    public static final int SYNC_LANGUAGE = 0xFF18;


    /**
     * 天气设置
     */
    public static final int WEATHER_CFG = 0xFF31;
    /**
     * 睡眠监控
     */
    public static final int SLEEP_MONITOR = 0xFF0B;


    /**
     * 闹钟设置
     */
    public static final int CLOCK_SETTING = 0xFF0E;


    /**
     * 相机控制
     */
    public static final int CAMERA_CONTROL = 0xFF14;

    /**
     * 音乐控制
     */
    public static final int MUSIC_CONTROL = 0xFF28;


    /**
     * 设置表盘ui
     */
    public static final int STE_DEVICE_UI = 0xFF33;

    /**
     * 表盘设置
     */
    public static final int DEVICE_UI_SETTING = 0xFF32;


    /**
     * 结束通话
     */
    public static final int END_CALL = 0xFF34;


    /**
     * 用户信息
     */
    int USER_INFO = 0xFF0F;

    /**
     * 天气预报
     */
    int WEATHER_INFO = 0xFF27;

    /**
     * 寻找手机
     */
    int FIND_PHONE = 0xFF16;


}
