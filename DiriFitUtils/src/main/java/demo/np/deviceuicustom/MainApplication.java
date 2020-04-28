package demo.np.deviceuicustom;

import npBase.BaseCommon.base.application.NpBaseApplication;
import npBase.BaseCommon.util.sharedpreferences.SaveObjectUtils;
import npLog.nopointer.core.NpLog;
import npble.nopointer.npBleSDK;


public class MainApplication extends NpBaseApplication {



    public static MainApplication mainApplication = null;


    @Override
    protected void initCfgAfterCreate() {
        mainApplication = this;
        npBleSDK.initSDK(this);
        mainApplication = this;
        SaveObjectUtils.init(this);
        NpLog.setLogFileMaxSizeByM(0.5F);
        NpLog.setEnableShowCurrentLogFileSize(false);
    }


    public static MainApplication getMainApplication() {
        return mainApplication;
    }





}
