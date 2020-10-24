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

/**
 * 保存service
 */
public class KeepService extends Service implements KeepHelper.KeepHelpCallback {

    private String TAG = "keepAlive";


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.e("KeepService->onCreate()");
        NotifyUtils.sendNotify(this, this);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            boolean isGuardAlive = ServiceAliveUtils.isServiceAlice(KeepService.this, BgCoreService.class);
            KeepLog.e(TAG, "KeepService:判断BgCoreService是否还活着:" + isGuardAlive);
            if (!isGuardAlive) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(KeepService.this, BgCoreService.class));
                } else {
                    startService(new Intent(KeepService.this, BgCoreService.class));
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            KeepLog.e(TAG, "KeepService:断开连接" + name);
            startAndBindGuard();
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
        KeepLog.e(TAG, "onStartCommand?" + this);
        startAndBindGuard();
        return START_STICKY;
    }


    /**
     * 启动并且绑定guard服务
     */
    private void startAndBindGuard() {
        boolean isServiceAlive = ServiceAliveUtils.isServiceAlice(KeepService.this, GuardService.class);
        if (!isServiceAlive) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LogUtil.e("startForegroundService()>GuardService.class");
                startForegroundService(new Intent(KeepService.this, GuardService.class));
            } else {
                startService(new Intent(KeepService.this, GuardService.class));
            }
        }
        bindService(new Intent(KeepService.this, GuardService.class), serviceConnection, BIND_AUTO_CREATE);
        KeepHelper.getInstance().registerCallback(this);
    }

    @Override
    public void onStopBgService() {
        try {
            stopService(new Intent(this, GuardService.class));
            unbindService(serviceConnection);
            KeepHelper.getInstance().unRegisterCallback(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
