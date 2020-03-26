package demo.nopointer.ble.bleModule;

import java.util.UUID;

/**
 * 静态变量
 */
public interface BleCfg {


    /**
     * 主服务service
     */
    UUID dataServiceUUID = UUID.fromString("6E40FC00-B5A3-F393-E0A9-E50E24DCCA9E");

    /**
     * 数据写特征
     */
    UUID dataWriteUUID = UUID.fromString("6E40FC20-B5A3-F393-E0A9-E50E24DCCA9E");


    /**
     * 数据通知特征
     */
    UUID dataNotifyUUID = UUID.fromString("6E40FC21-B5A3-F393-E0A9-E50E24DCCA9E");


    /**
     * 测试数据，包括心率 血压 血氧 App端测试的 或者设备端测试的
     */
    public static final int MEASURE_DATA_FLAG = 0XFF02;

}
