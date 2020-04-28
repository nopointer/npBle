package demo.np.deviceuicustom.activity.scan;

import npble.nopointer.device.BleDevice;

/**
 * 选择设备
 */
public class ScanBleDevice extends BleDevice {
    private boolean isSelect;

    public ScanBleDevice(String name, String mac) {
        super(name, mac);
    }



    public ScanBleDevice(BleDevice bleDevice) {
        super(bleDevice.getName(), bleDevice.getMac());
    }




    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "ScanBleDevice{" +
                "isSelect=" + isSelect +
                '}';
    }
}
