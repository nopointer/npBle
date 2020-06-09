package demo.nopointer.npDemo.keepAlive.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;


import demo.nopointer.npDemo.MainActivity;
import demo.nopointer.npDemo.MainApplication;
import demo.nopointer.npDemo.R;
import demo.nopointer.npDemo.keepAlive.utils.KeepLog;
import demo.nopointer.npDemo.sharedpreferences.SharedPrefereceDevice;
import npble.nopointer.ble.conn.NpBleConnState;
import npble.nopointer.device.BleDevice;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotifyUtils {

    public static final int notifyId = 0x559;


    public static void sendNotify(Context context, Service service) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //先关掉之前的通知栏
        notificationManager.cancel(notifyId);
        //通知栏标题
        String title = context.getResources().getString(R.string.app_name_main);
        String content = title;
        Notification notification = createAppNotification(context, null, title, content);
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(notifyId, notification);
        if (service != null) {
            service.startForeground(notifyId, notification);
        }
    }

    public static void sendNotify(Service service, NpBleConnState npBleConnState) {
        Context context = MainApplication.getMainApplication();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //先关掉之前的通知栏
        notificationManager.cancel(notifyId);
        PendingIntent pendingintent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        //是否是绑定了设备
        boolean hadBondDevice = false;
        BleDevice bleDevice = SharedPrefereceDevice.read();
        if (bleDevice != null && !TextUtils.isEmpty(bleDevice.getMac())) {
            hadBondDevice = true;
        }
        //通知栏标题
        String title = context.getResources().getString(R.string.app_name_main);
        String content = title;
        if (!hadBondDevice) {
            title += "（" + "未连接" + "）";
        } else {
            if (npBleConnState == NpBleConnState.CONNECTED) {
                title += "（" + "已连接" + " " + bleDevice.getName() + "）";
            } else if (npBleConnState == NpBleConnState.CONNECTING || npBleConnState == NpBleConnState.SEARCH_ING) {
                title += "（" + "正在连接" + "）";
            } else {
                title += "（" + "已断开" + "）";
            }
        }
        Notification notification = createAppNotification(context, pendingintent, title, content);
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(notifyId, notification);
        if (service != null) {
            service.startForeground(notifyId, notification);
        }
    }


    /**
     * 创建一个常驻通知栏
     *
     * @param context
     * @param intent
     * @param contentText
     * @param contentTitle
     * @return
     */
    static Notification createAppNotification(Context context, PendingIntent intent, String contentText, String contentTitle) {

        String packName = context.getPackageName();
        packName = packName.replace(".", "&");
        KeepLog.e("tag", packName);

        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String CHANNEL_ID = "channel_id_" + packName;
            final String CHANNEL_NAME = "channel_name_" + packName;
            NotificationManager mManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            /**
             * Oreo不用Priority了，用importance
             * IMPORTANCE_NONE 关闭通知
             * IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
             * IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
             * IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
             * IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
             */
            NotificationChannel notificationChannel = new
                    NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW); //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道， //通知才能正常弹出
            mManager.createNotificationChannel(notificationChannel);
            builder = new Notification.Builder(context, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
        }
        builder.setShowWhen(true);
        return builder.setContentText(contentText)
                .setContentTitle(contentTitle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(intent).build();
    }
}
