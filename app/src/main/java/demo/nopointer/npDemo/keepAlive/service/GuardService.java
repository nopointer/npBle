package demo.nopointer.npDemo.keepAlive.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;


import demo.nopointer.npDemo.keepAlive.KeepHelper;
import demo.nopointer.npDemo.keepAlive.utils.KeepLog;
import demo.nopointer.npDemo.keepAlive.utils.ServiceAliveUtils;
import npBase.BaseCommon.util.log.LogUtil;

public class GuardService extends Service implements KeepHelper.KeepHelpCallback {

    private String TAG = "GuardService";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.e("GuardService->onCreate()");
        NotifyUtils.sendNotify(this, this);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            boolean isGuardAlive = ServiceAliveUtils.isServiceAlice(GuardService.this, BgCoreService.class);
            KeepLog.e(TAG, "GuardService:判断BgCoreService是否还活着:" + isGuardAlive);
            if (!isGuardAlive) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(GuardService.this, BgCoreService.class));
                } else {
                    startService(new Intent(GuardService.this, BgCoreService.class));
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            KeepLog.e(TAG, "GuardService:断开连接" + name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(GuardService.this, KeepService.class));
            } else {
                startService(new Intent(GuardService.this, KeepService.class));
            }
            bindService(new Intent(GuardService.this, KeepService.class), serviceConnection, BIND_AUTO_CREATE);
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        KeepLog.e(TAG, "onBind?");
        return new KeepAliveConnection.Stub() {

        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KeepLog.e(TAG, "onStartCommand?");
        startAndBindKeep();
        return START_STICKY;
    }

    /**
     * 启动并且绑定keep服务
     */
    private void startAndBindKeep() {

        boolean isServiceAlive = ServiceAliveUtils.isServiceAlice(GuardService.this, KeepService.class);
        if (!isServiceAlive) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, KeepService.class));
            } else {
                startService(new Intent(this, KeepService.class));
            }
        }
        bindService(new Intent(this, KeepService.class), serviceConnection, BIND_AUTO_CREATE);
        KeepHelper.getInstance().registerCallback(this);
    }

    @Override
    public void onStopBgService() {
        try {
            stopService(new Intent(this, KeepService.class));
            unbindService(serviceConnection);
            KeepHelper.getInstance().unRegisterCallback(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
