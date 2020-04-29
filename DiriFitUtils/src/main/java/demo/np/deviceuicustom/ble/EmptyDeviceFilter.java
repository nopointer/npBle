package demo.np.deviceuicustom.ble;


import android.text.TextUtils;

import npble.nopointer.ble.scan.BleDeviceFilter;
import npble.nopointer.device.BleDevice;


/**
 * Created by nopointer on 2018/8/1.
 * 设备过滤器 空
 */

public class EmptyDeviceFilter extends BleDeviceFilter<BleDevice> {


    private EmptyDeviceFilter() {
    }

    private static EmptyDeviceFilter instance = new EmptyDeviceFilter();

    public static EmptyDeviceFilter getInstance() {
        return instance;
    }

    @Override
    public boolean filter(BleDevice bleDevice) {
        if (bleDevice == null || TextUtils.isEmpty(bleDevice.getName()) || "null".equalsIgnoreCase(bleDevice.getName()))
            return false;
        return true;
    }

}
