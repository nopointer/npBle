package demo.nopointer.npDemo.sharedpreferences;


import npBase.BaseCommon.util.sharedpreferences.SaveObjectUtils;
import npble.nopointer.device.BleDevice;

public class SharedPrefereceDevice {
    public static void clear() {
        save(null);
    }

    public static BleDevice read() {
        return (BleDevice) SaveObjectUtils.getObject("my_device", BleDevice.class);
    }

    public static void save(BleDevice bleDevice) {
        SaveObjectUtils.setObject("my_device", bleDevice);
    }
}