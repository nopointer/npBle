package demo.np.deviceuicustom;

import npBase.BaseCommon.base.application.NpBaseApplication;
import npBase.BaseCommon.util.sharedpreferences.SaveObjectUtils;
import npLog.nopointer.core.NpLog;
import npble.nopointer.ble.scan.BleScanner;
import npble.nopointer.npBleSDK;


public class MainApplication extends NpBaseApplication {



    public static MainApplication mainApplication = null;


    @Override
    protected void initCfgAfterCreate() {
        mainApplication = this;
        npBleSDK.initSDK(this);
        BleScanner.setIsShowScanLog(false);
        mainApplication = this;
        SaveObjectUtils.init(this);
        NpLog.setLogFileMaxSizeByM(2F);
        NpLog.setEnableShowCurrentLogFileSize(false);
    }


    public static MainApplication getMainApplication() {
        return mainApplication;
    }





}
