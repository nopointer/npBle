
package lib.ycble.ipc;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

import npble.nopointer.log.ycBleLog;

public class IPCControllerFactory {

    private static final String TAG = "[IPC_S][IPCControllerFactory]";

    private static final int MSG_NEW = 1;

    private ArrayList<IPCControllerEx.IPCController> controllers = new ArrayList<IPCControllerEx.IPCController>(6);

    // init in main thread
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NEW:
                    controllers.clear();
                    for (int i = 0; i < 6; i++) {
                        IPCControllerEx.IPCController controller = new IPCControllerEx.IPCController(i);
                        controllers.add(i, controller);
                        ycBleLog.e( "[MSG_NEW] " + controllers.get(i));
                    }
                    break;
                default:
                    return;
            }
        }
    };

    private static IPCControllerFactory sInstance;

    private IPCControllerFactory() {
    }

    public static synchronized IPCControllerFactory getInstance() {
        if (sInstance == null) {
            sInstance = new IPCControllerFactory();
        }
        return sInstance;
    }

    public void init() {
        ycBleLog.e( "init");
        mHandler.sendEmptyMessage(MSG_NEW);
    }

    public synchronized IPCControllerEx.IPCController getIPCController() {
        if (controllers == null) {
            ycBleLog.e( "[getIPCController] null");
            return null;
        }
        ycBleLog.e( "[getIPCController] " + controllers.size() + " = " + controllers);
        mHandler.sendEmptyMessageDelayed(MSG_NEW, 200);
        if (controllers.size() > 0) {
            IPCControllerEx.IPCController controller = controllers.remove(0);
            return controller;
        } else {
            return null;
        }
    }
}
