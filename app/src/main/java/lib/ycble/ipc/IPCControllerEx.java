
package lib.ycble.ipc;

import com.mediatek.wearable.Controller;
import com.mediatek.wearable.WearableManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;

import npble.nopointer.log.ycBleLog;

public class IPCControllerEx {

    private static final String TAG = "[IPC_S][IPCControllerEx]";

    public static final int INIT_OK = 0;

    public static final int INIT_HAS_SAME_CONTROLLER_ID = -1;

    private IPCController mIPCController;

    /**
     * compatible with CA*AK IPC API
     * CMD type default = CMD_8
     */
    IPCControllerEx(int cmd_type, String tagName, IControllerInternalCallback callback)
            throws IllegalArgumentException {
        ycBleLog.e( "[IPCControllerEx] cmd_type: " + cmd_type
                + " tagName: " + tagName);
        if (cmd_type != 8 && cmd_type != 9) {
            cmd_type = 8;
        }
        mIPCController = IPCControllerFactory.getInstance().getIPCController();
        mIPCController.setControllerTag(tagName);
        mIPCController.setCmdType(cmd_type);
        HashSet<String> receivers = new HashSet<String>();
        receivers.add(tagName);
        mIPCController.setReceiverTags(receivers);
        mIPCController.setCallback(callback);

        WearableManager.getInstance().addController(mIPCController);
    }

    public int initController() {
        mIPCController.init();
        return INIT_OK;
    }

    public void tearDown() {
        WearableManager.getInstance().removeController(mIPCController);
        mIPCController.tearDown();
    }

    /**
     * @param data
     * @return
     */
    public void sendBytes(String cmd, byte[] data, int priority) {
        if (data == null || data.length == 0) {
            ycBleLog.e( "[sendBytes] null data");
            return;
        }
        ycBleLog.e( "[sendBytes] cmd : " + cmd + " data Length : " + data.length + " priority : "
                + priority);

        try {
            mIPCController.send(cmd, data, false, false, priority);
        } catch (Exception e) {
            ycBleLog.e( "sendBytes Exception: " + e);
        }
    }

    /**
     * IPCController internal callback, same as IControllerCallback
     */
    public interface IControllerInternalCallback {

        void onConnectionStateChange(String tagname, int state);

        void onBytesReceived(String tagname, byte[] dataBuffer);
    }

    /**
     * IPCController extends Controller
     */
    static class IPCController extends Controller {

        private String mIndex;

        private IControllerInternalCallback mCallback;

        /**
         * compatible with CA*AK IPC API CMD type default = CMD_8
         */
        IPCController(int index) throws IllegalArgumentException {
            super("temp", CMD_8);
            Calendar cal = Calendar.getInstance();
            long time = cal.getTimeInMillis();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
            mIndex = dateFormat.format(time) + "/" + index;
            ycBleLog.e( "[IPCController] mIndex: " + mIndex);
        }

        public void setCallback(IControllerInternalCallback callback) {
            mCallback = callback;
        }

        /**
         * @param data
         * @return
         */
        public void sendBytes(String cmd, byte[] data, int priority) {
            if (data == null || data.length == 0) {
                return;
            }

            try {
                super.send(cmd, data, false, false, priority);
            } catch (Exception e) {
                ycBleLog.e( "sendBytes Exception: " + e);
            }
        }

        /**
         * @param dataBuffer
         */
        public void onReceive(byte[] dataBuffer) {
            if (dataBuffer == null || dataBuffer.length == 0) {
                ycBleLog.e( "[onReceive] null  dataBuffer");
                return;
            }
            ycBleLog.e( "[onByteReceive] dataBuffer Length : " + dataBuffer.length);
            mCallback.onBytesReceived(getControllerTag(), dataBuffer);
        }

        /**
         * @param linkerName
         * @param oldState
         * @param newState
         */
        public void onConnectionStateChange(int state) {
            ycBleLog.e( "[onConnectionStateChange] state: " + state);
            mCallback.onConnectionStateChange(getControllerTag(), state);
            return;
        }

        @Override
        public String toString() {
            String str = "[ControllerTag] " + getControllerTag() + "; [Index] " + mIndex;
            return str;
        }
    }
}
