package npble.nopointer.ota;

import android.content.Context;

import no.nordicsemi.android.dfu.DfuBaseService;
import npLog.nopointer.core.NpLog;
import npble.nopointer.ota.absimpl.freqchip.FreqchipOTAHelper;
import npble.nopointer.ota.absimpl.htx.HTXOTAHelper;
import npble.nopointer.ota.absimpl.nordic.DfuHelper;
import npble.nopointer.ota.absimpl.telink.TeLinkOTAHelper;
import npble.nopointer.ota.absimpl.ti.TIOTAHelper;
import npble.nopointer.ota.absimpl.xc.XcOTAImpl;
import npble.nopointer.ota.callback.NpOtaCallback;

/**
 * OTA 助手
 */
public class NpOtaHelper {

    private static final NpOtaHelper ourInstance = new NpOtaHelper();

    public static NpOtaHelper getInstance() {
        return ourInstance;
    }

    /**
     * 设置DfuService
     */
    public Class<? extends DfuBaseService> dfuBaseService;

    public void setDfuBaseService(Class<? extends DfuBaseService> dfuBaseService) {
        this.dfuBaseService = dfuBaseService;
    }

    private NpOtaHelper() {
    }

//    public void startOTA(Context context, String filePath, String mac, NpFirmType firmType, OTACallback otaCallback) {
//        NpLog.eAndSave("startOTA======>");
//        NpLog.eAndSave("firmType======>" + firmType);
//        NpLog.eAndSave("filePath======>" + filePath);
//        NpLog.eAndSave("otaCallback======>" + otaCallback);
//        startOTA(context, filePath, mac, firmType, otaCallback);
//    }

    public void startOTA(Context context, String filePath, String mac,  NpFirmType firmType, NpOtaCallback otaCallback) {
        switch (firmType) {
            //nordic的ota 也是默认的ota
            case NORDIC:
                DfuHelper.getDfuHelper().start(context, filePath, mac,"otaName", otaCallback, dfuBaseService);
                break;
            case HTX://汉天下的OTA
                NpLog.eAndSave("开始汉天下的ota======>");
                HTXOTAHelper htxotaHelper = HTXOTAHelper.getInstance();
                htxotaHelper.setAppFilePath(filePath);
                htxotaHelper.setDeviceMac(mac);
                htxotaHelper.setOtaCallback(otaCallback);
                htxotaHelper.startOTA(context);
                break;
            case TELINK:
                TeLinkOTAHelper.getInstance().startOTA(context, mac, filePath, otaCallback);
                break;
            case FREQCHIP:
                FreqchipOTAHelper.getInstance().startOTA(context, mac, filePath, otaCallback);
                break;
            case TI:
                TIOTAHelper.getInstance().startOTA(context, mac, filePath, otaCallback);
                break;
            case XR:
                new XcOTAImpl().startOTA(context, mac, filePath, otaCallback);
                break;
            default:
                NpLog.eAndSave("暂无合适的固件");
                break;
        }
    }


    public void startOTAForTi(Context context, byte[] imageBytes, String mac, NpOtaCallback otaCallback) {
        TIOTAHelper.getInstance().startOTA(context, mac, imageBytes, otaCallback);
    }

    public void stopOTAForTi() {
        TIOTAHelper.getInstance().stopOTA();
    }


    /**
     * 释放资源
     */
    public void free() {
        HTXOTAHelper.getInstance().free();
    }

}
