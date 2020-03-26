package npble.nopointer.ota.absimpl.nordic;

import android.content.Context;
import android.os.Build;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;
import npble.nopointer.log.NpBleLog;
import npble.nopointer.ota.OTAErrCode;
import npble.nopointer.ota.OTAState;
import npble.nopointer.ota.callback.OTACallback;

public class DfuHelper {

    private DfuHelper() {
    }

    public static DfuHelper dfuHelper = null;

    public static DfuHelper getDfuHelper() {
        synchronized (Void.class) {
            if (dfuHelper == null) {
                synchronized (Void.class) {
                    dfuHelper = new DfuHelper();
                }
            }
        }
        return dfuHelper;
    }

    private OTACallback otaCallback = null;

    public void start(Context context, String zipFilePath, String mac, String name, OTACallback otaCallback, Class dfuServiceImpl) {
        this.otaCallback = otaCallback;
        final DfuServiceInitiator starter = new DfuServiceInitiator(mac)
                .setDeviceName(name)
                .setKeepBond(false)
                .setForceDfu(true)
                .setPacketsReceiptNotificationsEnabled(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                .setPacketsReceiptNotificationsValue(12)
                .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);

        starter.setZip(zipFilePath);
        if (dfuServiceImpl == null) {
            NpBleLog.e("dfuServiceImpl cant be null");
            return;
        }
        starter.setForeground(false);
        starter.start(context, dfuServiceImpl);
        DfuServiceListenerHelper.registerProgressListener(context, mDfuProgressListener);
    }

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            NpBleLog.e("R.string.dfu_status_connecting");

            if (otaCallback != null) {
                otaCallback.onCurrentState(OTAState.connecting);
            }
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {

            NpBleLog.e("R.string.dfu_status_starting");

            if (otaCallback != null) {
                otaCallback.onCurrentState(OTAState.starting);
            }
        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
            NpBleLog.e("R.string.dfu_status_switching_to_dfu");

            if (otaCallback != null) {
                otaCallback.onCurrentState(OTAState.switching_to_dfu);
            }

        }

        //
        @Override
        public void onFirmwareValidating(final String deviceAddress) {
//            mProgressBar.setIndeterminate(true);
//            mTextPercentage.setText(R.string.dfu_status_validating);
            NpBleLog.e("R.string.dfu_status_validating");
        }

        //
        @Override
        public void onDeviceDisconnecting(final String deviceAddress) {
//            mProgressBar.setIndeterminate(true);
//            mTextPercentage.setText(R.string.dfu_status_disconnecting);
            NpBleLog.e("R.string.dfu_status_disconnecting");
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
            NpBleLog.e("R.string.dfu_status_completed");
            if (otaCallback != null) {
                otaCallback.onSuccess();
            }
        }

        @Override
        public void onDfuAborted(final String deviceAddress) {
            NpBleLog.e("R.string.dfu_status_aborted");

            if (otaCallback != null) {
                otaCallback.onFailure(OTAErrCode.NRF_ABORTED,"DfuAborted");
            }
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
            NpBleLog.e("R.string.dfu_uploading_percentage" + percent);
            if (otaCallback != null) {
                otaCallback.onProgress(percent);
            }
        }

        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
            NpBleLog.e("onError==" + error + ";" + message);
            if (otaCallback != null) {
                otaCallback.onFailure(error,message);
            }
        }
    };


}
