package demo.nopointer.npDemo.ble;



import android.text.TextUtils;

import npble.nopointer.ble.scan.BleDeviceFilter;
import npble.nopointer.device.BleDevice;


/**
 * Created by nopointer on 2018/8/1.
 * 我的设备过滤器
 */

public class MyDeviceFilter extends BleDeviceFilter<BleDevice> {


    private MyDeviceFilter() {
    }

    private static MyDeviceFilter instance = new MyDeviceFilter();

    public static MyDeviceFilter getInstance() {
        return instance;
    }



    //17 过滤数据的特殊字段
    private static final String filterStr = "FF1600";


    @Override
    public boolean filter(BleDevice bleDevice) {
        if (bleDevice == null || TextUtils.isEmpty(bleDevice.getName())||"null".equalsIgnoreCase(bleDevice.getName())) return false;
//        String string = BleUtil.byte2HexStr(bleDevice.getScanBytes());
//        if (string.length() < 36) return false;
//        if (string.contains(filterStr)) {
//            return true;
//        }
        return true;
    }


//    public boolean filter(BleDevice bleBaseDevice) {
//        if (bleBaseDevice == null) return false;
//        HashMap<String, String> advData = bleBaseDevice.getAdvData();
//        if (advData == null) return false;
//        if (advData.containsKey(BleDevice.adv_manufacturer_data)) {
//            String hexStr = advData.get(BleDevice.adv_manufacturer_data);
//            if (hexStr.length() > 4 && hexStr.startsWith("1600")) {
//                return true;
//            }
//        }
//        return false;
//    }


}
