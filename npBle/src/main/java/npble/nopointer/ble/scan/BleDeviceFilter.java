package npble.nopointer.ble.scan;

import android.bluetooth.BluetoothDevice;

import npble.nopointer.device.BleDevice;

/**
 * Created by nopointer on 2018/8/1.
 * 设备过滤器
 */

public abstract class BleDeviceFilter<T extends BleDevice> {
    public abstract boolean filter(T t);

    public T parserDevice(BluetoothDevice device, byte[] scanByteArr, int rssi) {
        return (T) BleDevice.parserFromScanData(device, scanByteArr, rssi);
    }

}
