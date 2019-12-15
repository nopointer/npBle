package lib.ycble;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.mediatek.ctrl.notification.NotificationActions;
import com.mediatek.wearable.WearableManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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


    @SuppressLint("UseSparseArrays")
    @SuppressWarnings("unchecked")
    public String[] getNotificationText(Notification notification) {
        String[] textArray = null;
        RemoteViews remoteViews = notification.contentView;
        if (remoteViews == null) {
            textArray = new String[]{"", ""};
            ycBleLog.e("remoteViews is null, set title and content to be empty. ");
        } else {
            HashMap<Integer, String> text = new HashMap<Integer, String>();
            try {
                Class<?> remoteViewsClass = Class.forName(RemoteViews.class.getName());
                Field[] outerFields = remoteViewsClass.getDeclaredFields();
                ycBleLog.e("outerFields.length = " + outerFields.length);
                Field actionField = null;
                for (Field outerField : outerFields) {
                    if (outerField.getName().equals("mActions")) {
                        actionField = outerField;
                        break;
                    }
                }
                if (actionField == null) {
                    ycBleLog.e("actionField is null, return null");
                    return null;
                }
                actionField.setAccessible(true);
                ArrayList<Object> actions = (ArrayList<Object>) actionField.get(remoteViews);
                int viewId = 0;
                for (Object action : actions) {
                    /*
                     * Get notification tile and content
                     */
                    Field[] innerFields = action.getClass().getDeclaredFields();

                    // RemoteViews curr_action = (RemoteViews)action;
                    Object value = null;
                    Integer type = null;
                    for (Field field : innerFields) {
                        field.setAccessible(true);
                        if (field.getName().equals("value")) {
                            value = field.get(action);
                        } else if (field.getName().equals("type")) {
                            type = field.getInt(action);
                        } else if (field.getName().equals("methodName")) {
                            String method = (String) field.get(action);
                            if (method.equals("setProgress")) {
                                return null;
                            }
                        }
                    }

                    // If this notification filed is title or content, save it to
                    // text list
                    if ((type != null)
                            && ((type == NOTIFICATION_TITLE_TYPE) || (type == NOTIFICATION_CONTENT_TYPE))) {
                        if (value != null) {
                            viewId++;
                            text.put(viewId, value.toString());
                            if (viewId == 2) {
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                ycBleLog.e("getText ERROR");
            }

            textArray = text.values().toArray(new String[0]);
            if (textArray == null) {
                ycBleLog.e("get title and content from notification is null.Set it to be empty string.");
                textArray = new String[]{"", ""};
            } else {
                ycBleLog.e("textArray is " + Arrays.toString(textArray));
            }
        }
        String[] bigTextArray = new String[2];
        if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_340
                && android.os.Build.VERSION.SDK_INT >= 19) {//android 4.4
            //get bigtextstyle title and content
            String EXTRA_TITLE = "android.title";
            String EXTRA_TITLE_BIG = EXTRA_TITLE + ".big";
            String EXTRA_BIG_TEXT = "android.bigText";
            CharSequence mBigTitle = notification.extras.getCharSequence(EXTRA_TITLE_BIG);
            CharSequence mBigText = notification.extras.getCharSequence(EXTRA_BIG_TEXT);
            if (!TextUtils.isEmpty(mBigTitle)) {
                bigTextArray[0] = mBigTitle.toString();
            } else if (textArray != null && textArray.length > 0 && !TextUtils.isEmpty(textArray[0])) {
                bigTextArray[0] = textArray[0];
            } else {
                bigTextArray[0] = "";
            }

            if (!TextUtils.isEmpty(mBigText)) {
                bigTextArray[1] = mBigText.toString();
            } else if (textArray != null && textArray.length > 1 && !TextUtils.isEmpty(textArray[1])) {
                bigTextArray[1] = textArray[1];
            } else {
                bigTextArray[1] = "";
            }

        } else {
            bigTextArray = textArray;
            ycBleLog.e("Android platform is lower than android 4.4 and does not support bigtextstyle attribute.");
        }
        try {
            ycBleLog.e("getNotificationText(), text list = " + Arrays.toString(bigTextArray));
        } catch (Exception e) {
            ycBleLog.e("getNotificationText Exception");
        }
        return bigTextArray;
    }

    public String[] getNotificationPageText(Notification notification) {
        String[] textArray = null;
        // get title and content of Pages
        if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_340
                && android.os.Build.VERSION.SDK_INT >= 20) {//android 4.4w.2
            String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
            String KEY_PAGES = "pages";
            Bundle wearableBundle = notification.extras.getBundle(EXTRA_WEARABLE_EXTENSIONS);
            if (wearableBundle != null) {
                Notification[] pages = getNotificationArrayFromBundle(wearableBundle, KEY_PAGES);
                if (pages != null) {
                    ycBleLog.e("pages num = " + pages.length);
                    for (int i = 0; i < pages.length; i++) {
                        String[] pageTextArray = getNotificationText(pages[i]);
                        if (pageTextArray != null) {
                            if (i == 0) {
                                textArray = pageTextArray;
                            } else {
                                textArray = concat(textArray, pageTextArray);
                            }
                        }
                    }
                }
            }
        } else {
            ycBleLog.e("Android platform is lower than android 4.4w.2 and does not support page attribute.");
        }
        try {
            ycBleLog.e("getNotificationPageText(), text list = " + Arrays.toString(textArray));
        } catch (Exception e) {
            ycBleLog.e("getNotificationPageText Exception");
        }
        return textArray;
    }

    public Notification[] getNotificationArrayFromBundle(Bundle bundle, String key) {
        Parcelable[] array = bundle.getParcelableArray(key);
        if (array instanceof Notification[] || array == null) {
            return (Notification[]) array;
        }
        Notification[] typedArray = Arrays.copyOf(array, array.length,
                Notification[].class);
        bundle.putParcelableArray(key, typedArray);
        return typedArray;
    }

    public String[] concat(String[] first, String[] second) {
        String[] result = new String[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public ArrayList<NotificationActions> getNotificationActions(Notification notification) {
        ArrayList<NotificationActions> actionsList = new ArrayList<NotificationActions>();
        if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_340) {
            try {
                // get contentIntent field(The intent to execute when the expanded status entry is clicked.)
                Field mContentIntentField = Notification.class.getDeclaredField("contentIntent");
                if (mContentIntentField != null) {
                    mContentIntentField.setAccessible(true);
                    PendingIntent contentIntent = (PendingIntent) mContentIntentField.get(notification);
                    // the contentIntent maybe is null, if the contentIntent is null do not add it to actionsList
                    if (contentIntent != null) {
                        NotificationActions notificationAction = new NotificationActions();
                        notificationAction.setActionId(String.valueOf(0)); // always is 0
                        notificationAction.setActionTitle(getString(R.string.app_name));
                        notificationAction.setActionIntent(contentIntent);
                        actionsList.add(notificationAction);
                    } else {
                        ycBleLog.e("contentIntent is null.");
                    }
                } else {
                    ycBleLog.e("get contentIntent field failed.");
                }

                if (android.os.Build.VERSION.SDK_INT >= 19) {//android 4.4
                    Field mActionField = Notification.class.getDeclaredField("actions");// get Action[] field
                    if (mActionField != null) {
                        mActionField.setAccessible(true);
                        Object[] actions = (Object[]) mActionField.get(notification);
                        int index = 1;
                        if (actions != null) {
                            for (Object action : actions) {
                                Field[] innerFields = action.getClass().getDeclaredFields();
                                NotificationActions notificationAction = new NotificationActions();
                                for (Field field : innerFields) {
                                    field.setAccessible(true);
                                    if (field.getType().getName()
                                            .equals(java.lang.CharSequence.class.getName())) {
                                        // get Action title
                                        CharSequence title = (CharSequence) field.get(action);
                                        notificationAction.setActionTitle(title.toString());
                                        ycBleLog.e(
                                                "action title = "
                                                        + notificationAction.getActionTitle());
                                    } else if (field.getType().getName()
                                            .equals(android.app.PendingIntent.class.getName())) {
                                        // get Action PendingIntent
                                        PendingIntent intent = (PendingIntent) field.get(action);
                                        notificationAction.setActionIntent(intent);
                                        ycBleLog.e("pendingintent = "
                                                + notificationAction.getActionIntent().toString());
                                    }
                                }
                                notificationAction.setActionId(String.valueOf(index));
                                actionsList.add(notificationAction);
                                index++;
                            }
                            ycBleLog.e("action size = " + actionsList.size());
                        }
                    } else {
                        ycBleLog.e("get Action field failed.");
                        return null;
                    }
                } else {
                    ycBleLog.e("Android platform is lower than android 4.4 and does not support gction attribute.");
                }
            } catch (NoSuchFieldException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return actionsList;
    }

    public String getGroupKey(Notification notification) {
        String groupKey = "";
        if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_340
                && android.os.Build.VERSION.SDK_INT >= 20) {//android 4.4w.2
            groupKey = notification.getGroup();
        } else {
            ycBleLog.e("Android platform is lower than android 4.4w.2 and does not support group attribute.");
        }
        ycBleLog.e("groupKey = " + groupKey);
        return groupKey;
    }

}
