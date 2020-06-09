package demo.nopointer.npDemo.keepAlive.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;


import demo.nopointer.npDemo.ble.NpBleManager;
import demo.nopointer.npDemo.keepAlive.activity.ScreenManager;
import demo.nopointer.npDemo.keepAlive.utils.KeepLog;
import demo.nopointer.npDemo.keepAlive.utils.ScreenReceiverUtil;
import npBase.BaseCommon.util.log.LogUtil;
import npble.nopointer.ble.conn.NpBleConnCallback;
import npble.nopointer.ble.conn.NpBleConnState;

/**
 * 后台核心运行service
 */
public class BgCoreService extends Service implements NpBleConnCallback {

    private static final String TAG = BgCoreService.class.getSimpleName();
    private BgCoreBinder bgCoreBinder;
    private ScreenReceiverUtil mScreenListener;
    private ScreenManager mScreenManager;

    private ScreenReceiverUtil.SreenStateListener mScreenListenerer = new ScreenReceiverUtil.SreenStateListener() {
        @Override
        public void onSreenOn() {
            mScreenManager.finishActivity();
            KeepLog.e(TAG, "关闭了1像素Activity");
        }

        @Override
        public void onSreenOff() {
            mScreenManager.startActivity();
            KeepLog.e(TAG, "打开了1像素Activity");
        }

        @Override
        public void onUserPresent() {
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.e("BgCoreService->onCreate()");
        NotifyUtils.sendNotify(this, NpBleManager.getInstance().getBleConnState());
//       注册锁屏广播监听器
        mScreenListener = new ScreenReceiverUtil(this);
        mScreenManager = ScreenManager.getInstance(this);
        mScreenListener.setScreenReceiverListener(mScreenListenerer);

        bgCoreBinder = new BgCoreBinder();


        NpBleManager.getInstance().registerConnCallback(this);
        onConnState(NpBleManager.getInstance().getBleConnState());

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return bgCoreBinder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    @Override
    public void onConnState(NpBleConnState NpBleConnState) {
        reOpenNotify(NpBleConnState);
    }


    /**
     * 重新打开常驻通知栏
     *
     * @param npBleConnState 设备当前的状态
     */
    private void reOpenNotify(NpBleConnState npBleConnState) {
        NotifyUtils.sendNotify(this, npBleConnState);
    }



    public void unRegister() {
        NpBleManager.getInstance().unRegisterConnCallback(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mScreenListener.stopScreenReceiverListener();
        unRegister();
        NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (mManager == null) {
            return;
        }
        mManager.cancel(NotifyUtils.notifyId);
    }

    public class BgCoreBinder extends Binder {

    }


}
