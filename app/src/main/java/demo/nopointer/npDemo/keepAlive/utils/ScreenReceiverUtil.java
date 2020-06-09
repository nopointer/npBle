package demo.nopointer.npDemo.keepAlive.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ScreenReceiverUtil {
    private Context mContext;
    private SreenBroadcastReceiver mScreenReceiver;
    private SreenStateListener mStateReceiverListener;

    public ScreenReceiverUtil(Context mContext) {
        this.mContext = mContext;
    }

    public void setScreenReceiverListener(SreenStateListener mStateReceiverListener) {
        this.mStateReceiverListener = mStateReceiverListener;
        // 动态启动广播接收器
        this.mScreenReceiver = new SreenBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver, filter);
    }

    public void stopScreenReceiverListener() {
        mContext.unregisterReceiver(mScreenReceiver);
    }


    public interface SreenStateListener {

        void onSreenOn();


        void onSreenOff();


        void onUserPresent();
    }


    public class SreenBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (mStateReceiverListener == null) {
                return;
            }
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                mStateReceiverListener.onSreenOn();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                mStateReceiverListener.onSreenOff();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                mStateReceiverListener.onUserPresent();
            }
        }
    }
}