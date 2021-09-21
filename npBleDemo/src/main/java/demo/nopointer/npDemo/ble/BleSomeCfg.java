package demo.nopointer.npDemo.ble;

import java.util.UUID;

/**
 * 静态变量
 */
public interface BleSomeCfg {


    /**
     * 主服务service
     */
//    UUID dataServiceUUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9F");
    UUID dataServiceUUID = UUID.fromString("6E40FC00-B5A3-F393-E0A9-E50E24DCCA9E");

    /**
     * 数据写特征
     */
//    UUID dataWriteUUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9F");
    UUID dataWriteUUID = UUID.fromString("6E40FC20-B5A3-F393-E0A9-E50E24DCCA9E");


    /**
     * 数据通知特征
     */
//    UUID dataNotifyUUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9F");
    UUID dataNotifyUUID = UUID.fromString("6E40FC21-B5A3-F393-E0A9-E50E24DCCA9E");


    /**
     * 表盘图片写数据特征
     */
    UUID imageDataWriteUUID = UUID.fromString("6e400005-b5a3-f393-e0a9-e50e24dcca9e");

    /**
     * 表盘图片设备端端通知数据特征
     */
    UUID imageDataNotifyUUID = UUID.fromString("6e400006-b5a3-f393-e0a9-e50e24dcca9e");

    /**
     * 计步统计
     */
    int STEP_COUNT = 0x000A;


    /**
     * 测试数据，包括心率 血压 血氧 App端测试的 或者设备端测试的
     */
    int MEASURE_DATA_FLAG = 0XFF02;


    /**
     * 下拉刷新
     */
    int SYNC_REFRESH = 0xFF0A;


    /**
     * 睡眠监控
     */
    int SLEEP_MONITOR = 0xFF0B;


    /**
     * 消息提醒
     */
    int MESSAGE_PUSH_ENBALE = 0xFF0C;


    /**
     * 消息内容推送
     */
    int MESSAGE_CONTENT_PUSH = 0xFF0D;


    /**
     * 闹钟设置
     */
    int CLOCK_SETTING = 0xFF0E;


    /**
     * 用户信息
     */
    int USER_INFO = 0xFF0F;

    /**
     * 久坐提醒
     */
    int LONG_SIT = 0XFF10;


    /**
     * 勿扰提醒
     */
    int NOT_REMIND = 0xFF11;

    /**
     * 抬手亮屏
     */
    int HAND_LIGHT = 0xFF12;

    /**
     * 相机控制
     */
    int CAMERA_CONTROL = 0xFF14;

    /**
     * 寻找手机
     */
    int FIND_PHONE = 0xFF16;

    /**
     * 同步系统语言
     */
    int SYNC_LANGUAGE = 0xFF18;

    /**
     * 设备电量
     */
    int DEVICE_BATTERY = 0xFF20;

    /**
     * 设备信息
     */
    int DEVICE_INFO = 0xFF21;


    /**
     * 同步时间的指令
     */
    int SYNC_TIME = 0xFF22;

    /**
     * 恢复出厂设置
     */
    int RESET_SYSTEM = 0xFF26;
    /**
     * 天气预报
     */
    int WEATHER_INFO = 0xFF27;

    /**
     * 音乐控制
     */
    int MUSIC_CONTROL = 0xFF28;

    /**
     * 选择表盘样式
     */
    int CHOICE_DEVICE_UI_TYPE = 0xFF29;

    /**
     * 全天候测量
     */
    int ALL_DAY_MEASURE_HR = 0xFF2A;

    /**
     * 全天候测量的心率数据
     */
    int ALL_DAY_MEASURE_HR_HISTORY = 0xFF2B;

    /**
     * 寻找手环
     */
    int FIND_DEVICE = 0xFF2C;


    /**
     * 天气设置
     */
    int WEATHER_CFG = 0xFF31;


    /**
     * 表盘设置
     */
    int DEVICE_UI_SETTING = 0xFF32;

    /**
     * 设置表盘ui
     */
    int STE_DEVICE_UI = 0xFF33;


    /**
     * 结束通话
     */
    int END_CALL = 0xFF34;


    /**
     * 同步通讯录
     */
     int SYNC_CONTACTS = 0xFF35;

    /**
     * 音频名称
     */
     int AUDIO_NAME = 0xFF36;

    /**
     * 结束同步通讯录
     */
     int STOP_SYNC_CONTACTS = 0xFF38;

    /**
     * 开始同步通讯录
     */
     int START_SYNC_CONTACTS = 0xFF39;


}
