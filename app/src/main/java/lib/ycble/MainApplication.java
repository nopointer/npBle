package lib.ycble;

import android.app.Application;
import android.os.Handler;
import android.service.notification.StatusBarNotification;

import npble.nopointer.aider.MsgType;
import npble.nopointer.aider.PushAiderHelper;
import npble.nopointer.aider.callback.MsgCallback;
import npble.nopointer.core.ycBleSDK;
import npble.nopointer.log.ycBleLog;


public class MainApplication extends Application {


    private static final int NOTIFICATION_TITLE_TYPE = 9;
    private static final int NOTIFICATION_CONTENT_TYPE = 10;


    public static MainApplication mainApplication = null;

    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        ycBleSDK.initSDK(this);
        mainApplication = this;
        PushAiderHelper.getAiderHelper().setMsgReceiveCallback(new MsgCallback() {
            @Override
            public void onAppMsgReceive(String packName, MsgType msgType, String from, String msgContent) {
//                NotificationData notificationData = new NotificationData();
//                notificationData.setAppID("5");
//                notificationData.setMsgId(-452347470);
//                notificationData.setTickerText("debug");
//                notificationData.setPackageName("com.tencent.mm");
//                notificationData.setTextList(new String[]{"123", "456"});
////                            ArrayList<String> strings = new ArrayList<>();
////                            strings.add("fuck");
////                            strings.add("fuck");
////                            notificationData.setActionsList(strings);
//                notificationData.setTag("tag");
//                notificationData.setWhen(System.currentTimeMillis());
//                ycBleLog.e("我日" + new Gson().toJson(notificationData));
//                NotificationController.getInstance(MainApplication.this).sendNotfications(notificationData);
            }

            @Override
            public void onPhoneInComing(String phoneNumber, String contactName, int userHandResult) {

            }

            @Override
            public void onMessageReceive(String phoneNumber, String contactName, String messageContent) {
                ycBleLog.e("onMessageReceive===>"+phoneNumber+"//"+contactName+"///"+messageContent);
            }

            @Override
            public void onNotificationPost(StatusBarNotification sbn) {
                super.onNotificationPost(sbn);

//                ycBleLog.e("Notification Posted, " + "ID: " + sbn.getId() + ", Package: "
//                        + sbn.getPackageName());
//
//                ycBleLog.e("sdk version is " + android.os.Build.VERSION.SDK_INT);
//                if (android.os.Build.VERSION.SDK_INT < 18) {
//                    ycBleLog.e("Android platform version is lower than 18.");
//                    return;
//                }
//
//                Notification notification = (Notification) sbn.getNotification();
//
//                if (notification == null) {
//                    ycBleLog.e("Notification is null, return");
//                    return;
//                }
//                ycBleLog.e("packagename = " + sbn.getPackageName() + "tag = " + sbn.getTag() + "Id = " + sbn.getId());
//
//
//                int id = sbn.getId();
//                String tag = sbn.getTag();
//
//
//                NotificationData notificationData = new NotificationData();
//                String[] textArray = getNotificationText(notification);
//                String[] pageTextArray = getNotificationPageText(notification); //android 4.4w.2 support
//                if (pageTextArray != null && textArray != null) {
//                    textArray = concat(textArray, pageTextArray);
//                }
//                notificationData.setTextList(textArray);
//                try {
//                    ycBleLog.e("textlist = " + Arrays.toString(textArray));
//                } catch (Exception e) {
//                    ycBleLog.e("get textlist error");
//                }
//                notificationData.setGroupKey(getGroupKey(notification));
//                notificationData.setActionsList(getNotificationActions(notification));
//                notificationData.setPackageName(sbn.getPackageName());
//                notificationData.setAppID(Utils.getKeyFromValue(notificationData.getPackageName()));
//
//                if (!TextUtils.isEmpty(notification.tickerText)) {
//                    notificationData.setTickerText(notification.tickerText.toString());
//                } else {
//                    ycBleLog.e("get ticker is null or empty");
//                    notificationData.setTickerText("");
//                }
//                notificationData.setWhen(notification.when);
//                if (id == 0) { //Maybe some app's id is 0. like: hangouts(com.google.android.talk)
//                    id = 1 + (int) (Math.random() * 1000000);
//                    ycBleLog.e("the id is 0 and need create a random number : " + id);
//                }
//                notificationData.setMsgId(id);
//                notificationData.setTag(tag);
//
//
//                ycBleLog.e("我日" + new Gson().toJson(notificationData));
//                ycBleLog.e("connCode===>" + WearableManager.getInstance().getConnectState());
//
//                ycBleLog.e(" WearableManager.getInstance().isAvailable()==>" + WearableManager.getInstance().isAvailable());
////                ycBleLog.e(" WearableManager.getInstance().isAvailable()==>"+ WearableManager.getInstance().isAvailable());
//
////                && !var1.getControllerTag().equals("SyncTime")
//
//                NotificationController.getInstance(MainApplication.this).sendNotfications(notificationData);
            }
        });

//        IPCControllerFactory.getInstance().init();
//        NotificationController.getInstance(this).init();
//
//        boolean isSuccess = WearableManager.getInstance().init(true, getApplicationContext(), "demo", R.xml.wearable_config);
//        ycBleLog.e("WearableManager init " + isSuccess);
////        handler.postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
////                String activityName = am.getRunningTasks(1).get(0).topActivity.getClassName();
////                ycBleLog.e("activityName===>" + activityName);
////                handler.postDelayed(this, 1000);
////            }
////        }, 1000);
//
//        WearableManager manager = WearableManager.getInstance();
//        manager.addController(NotificationController.getInstance(this));
//
//        Map<Object, Object> applist = AppList.getInstance().getAppList();
//        if (applist.size() == 0) {
//            applist.put(AppList.MAX_APP, (int) AppList.CREATE_LENTH);
//            applist.put(AppList.CREATE_LENTH, AppList.BATTERYLOW_APPID);
//            applist.put(AppList.CREATE_LENTH, AppList.SMSRESULT_APPID);
//            AppList.getInstance().saveAppList(applist);
//        }

    }

    public static MainApplication getMainApplication() {
        return mainApplication;
    }





}
