package demo.nopointer.ble.bleModule;

import java.util.UUID;


/**
 * * uuid 的配置文件
 */
public interface BleUUIDCfg {

    //主服务service
    UUID U_SER = UUID.fromString("6E40FC00-B5A3-F393-E0A9-E50E24DCCA9E");
    //通知特征
    UUID U_notify = UUID.fromString("6E40FC21-B5A3-F393-E0A9-E50E24DCCA9E");
    //写特征
    UUID U_write = UUID.fromString("6E40FC20-B5A3-F393-E0A9-E50E24DCCA9E");

    public static final String ycBleTag = "ycBleTag";


}
