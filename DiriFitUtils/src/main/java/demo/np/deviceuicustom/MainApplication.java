package demo.np.deviceuicustom;

import android.os.Handler;

import npBase.BaseCommon.base.application.NpBaseApplication;
import npBase.BaseCommon.util.sharedpreferences.SaveObjectUtils;
import npLog.nopointer.core.NpLog;
import npble.nopointer.npBleSDK;


public class MainApplication extends NpBaseApplication {



    public static MainApplication mainApplication = null;

    private Handler handler = new Handler();

    @Override
    protected void initCfgAfterCreate() {
        mainApplication = this;
        npBleSDK.initSDK(this);
        mainApplication = this;
//        DbCfgUtil.getDbCfgUtil().init(this);
        SaveObjectUtils.init(this);
        NpLog.setLogFileMaxSizeByM(0.5F);
        NpLog.setEnableShowCurrentLogFileSize(true);
    }


    public static MainApplication getMainApplication() {
        return mainApplication;
    }





}
