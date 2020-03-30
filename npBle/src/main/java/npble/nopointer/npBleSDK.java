package npble.nopointer;

import android.content.Context;

import npble.nopointer.ble.scan.BleScanner;

public class npBleSDK {

    /**
     * 初始化蓝牙
     *
     * @param context
     */
    public static void initSDK(Context context) {
        BleScanner.init(context);
    }



    public static void setScanLog(boolean enable) {
        BleScanner.isShowScanLog = enable;
    }


}
