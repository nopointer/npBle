package demo.nopointer.npDemo;

import demo.nopointer.npDemo.keepAlive.KeepHelper;
import npBase.BaseCommon.base.application.NpBaseApplication;
import npBase.BaseCommon.util.sharedpreferences.SaveObjectUtils;
import npLog.nopointer.mail.SendMailUtil;


public class MainApplication extends NpBaseApplication {


    public static MainApplication mainApplication = null;


    private static final String PROCESS_NAME = "demo.nopointer.npDemo";

    @Override
    protected void initCfgAfterCreate() {

        String processName = getProcessName(this);
        if (processName.equalsIgnoreCase(PROCESS_NAME)) {
            mainApplication = this;
            SaveObjectUtils.init(this);
            SendMailUtil.setFromAdd("3343249301@qq.com");
            SendMailUtil.setFromPsw("davpgtmyazmbciij");

            KeepHelper.getInstance().startKeep(this);
        }
    }

    public static MainApplication getMainApplication() {
        return mainApplication;
    }


}
