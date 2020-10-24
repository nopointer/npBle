package demo.nopointer.npDemo.keepAlive;

import android.content.Context;
import android.content.Intent;
import android.os.Build;


import java.util.HashSet;

import demo.nopointer.npDemo.keepAlive.service.GuardService;
import demo.nopointer.npDemo.keepAlive.service.KeepService;

public class KeepHelper {

    private KeepHelper() {
    }

    private static KeepHelper instance = new KeepHelper();

    public static KeepHelper getInstance() {
        return instance;
    }

    public void startKeep(Context context) {
        Intent intentKeepService = new Intent(context, KeepService.class);
        Intent intentGuardService = new Intent(context, GuardService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentKeepService);
            context.startForegroundService(intentGuardService);
        } else {
            context.startService(intentKeepService);
            context.startService(intentGuardService);
        }
    }


    public void stopKeep() {
        notifyStopService();
    }

    private HashSet<KeepHelpCallback> callbacks = new HashSet<>();

    public void registerCallback(KeepHelpCallback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    public void unRegisterCallback(KeepHelpCallback callback) {
        if (callbacks.contains(callback)) {
            callbacks.remove(callback);
        }
    }


    /**
     * 通知关闭service
     */
    private void notifyStopService() {
        for (KeepHelpCallback callback : callbacks) {
            callback.onStopBgService();
        }
    }

    public interface KeepHelpCallback {

        void onStopBgService();

    }


}
