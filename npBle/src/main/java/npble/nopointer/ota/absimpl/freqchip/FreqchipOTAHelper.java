package npble.nopointer.ota.absimpl.freqchip;

import android.content.Context;

import npble.nopointer.ota.callback.NpOtaCallback;

/**
 * 富窝坤OTA
 */
public class FreqchipOTAHelper {
    private static final FreqchipOTAHelper ourInstance = new FreqchipOTAHelper();

    public static FreqchipOTAHelper getInstance() {
        return ourInstance;
    }

    private FreqchipOTAHelper() {
    }

    private FreqOTAImpl otaImpl = null;

    public void startOTA(Context context, String mac, String filePath, NpOtaCallback otaCallback) {
        otaImpl = new FreqOTAImpl(context);
        otaImpl.setFilePath(filePath);
        otaImpl.setOtaCallback(otaCallback);
        otaImpl.connDevice(mac);
    }
}
