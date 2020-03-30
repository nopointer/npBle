package npble.nopointer.ota.absimpl.ti;

import android.content.Context;

import npble.nopointer.ota.callback.NpOtaCallback;

public class TIOTAHelper {

    TiOTAImpl tiOTA = null;
    private static final TIOTAHelper ourInstance = new TIOTAHelper();

    public static TIOTAHelper getInstance() {
        return ourInstance;
    }

    private TIOTAHelper() {
    }


    public void startOTA(Context context, String mac, final String filePath, final NpOtaCallback otaCallback) {
        if (tiOTA == null) {
            tiOTA = new TiOTAImpl(context);
        }
        tiOTA.setOtaCallback(otaCallback);
        tiOTA.setFilePath(filePath);
        tiOTA.startOTA(mac);
    }


    public void startOTA(Context context, String mac, final byte imageByes[], final NpOtaCallback otaCallback) {
        if (tiOTA == null) {
            tiOTA = new TiOTAImpl(context);
        }
        tiOTA.setOtaCallback(otaCallback);
        tiOTA.setImageByes(imageByes);
        tiOTA.startOTA(mac);
    }

    public void stopOTA() {
        if (tiOTA != null) {
            tiOTA.stopOTA();
            tiOTA = null;
        }
    }


}
