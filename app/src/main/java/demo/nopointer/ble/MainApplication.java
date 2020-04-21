package demo.nopointer.ble;

import android.app.Application;
import android.os.Handler;

import demo.nopointer.ble.database.DbCfgUtil;
import npBase.BaseCommon.util.sharedpreferences.SaveObjectUtils;
import npLog.nopointer.core.NpLog;
import npble.nopointer.npBleSDK;


public class MainApplication extends Application {


    private static final int NOTIFICATION_TITLE_TYPE = 9;
    private static final int NOTIFICATION_CONTENT_TYPE = 10;


    public static MainApplication mainApplication = null;

    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        npBleSDK.initSDK(this);
        mainApplication = this;

        npBleSDK.initSDK(this);
        mainApplication = this;
        DbCfgUtil.getDbCfgUtil().init(this);
        SaveObjectUtils.init(this);
        NpLog.setLogFileMaxSizeByM(0.5F);
        NpLog.setEnableShowCurrentLogFileSize(true);

    }

    public static MainApplication getMainApplication() {
        return mainApplication;
    }





}
