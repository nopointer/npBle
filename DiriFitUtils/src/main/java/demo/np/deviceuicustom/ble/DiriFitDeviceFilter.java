package demo.np.deviceuicustom.ble;


import npble.nopointer.ble.scan.BleDeviceFilter;
import npble.nopointer.device.BleDevice;
import npble.nopointer.util.BleUtil;


/**
 * Created by nopointer on 2018/8/1.
 * 我的设备过滤器
 */

public class DiriFitDeviceFilter extends BleDeviceFilter<BleDevice> {


    private DiriFitDeviceFilter() {
    }

    private static DiriFitDeviceFilter instance = new DiriFitDeviceFilter();

    public static DiriFitDeviceFilter getInstance() {
        return instance;
    }



    public static final String filterStr = "FF0A00";

    @Override
    public synchronized boolean filter(BleDevice bleDevice) {
        if (bleDevice == null) return false;
        if (bleDevice.getScanBytes() == null) return false;

        String hexStr = BleUtil.byte2HexStr(bleDevice.getScanBytes());
        if (hexStr.length() > 32) {
            if (hexStr.contains(filterStr)) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }


}
