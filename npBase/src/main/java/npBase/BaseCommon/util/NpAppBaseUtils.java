package npBase.BaseCommon.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.List;
import java.util.Locale;

public class NpAppBaseUtils {





    //google play
    public static final String packageNameGooglePlay = "com.android.vending";
    //youtube
    public static final String packageNameYoutube = "com.google.android.youtube";
    //GMail
    public static final String packageNameGmail = "com.google.android.gm";
    //play Music
    public static final String packageNamePlayMusic = "com.google.android.music";


    private NpAppBaseUtils() {
    }

    /**
     * 判断app是否是在前台运行
     *
     * @param context
     * @return
     */
    public static boolean isForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName()) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取app版本名称
     *
     * @param context 上下文
     * @return 版本名称
     */
    public static String getVersionName(Context context) {

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取app版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    public static int getVersionCode(Context context) {

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return 手机IMEI
     */
    public static String getIMEI(Context ctx) {
        TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            return telephonyManager.getDeviceId();
        }
        return null;
    }


    /**
     * 获取手机的系统版本号
     *
     * @return
     * @link #Build.VERSION_CODES
     */
    public static int getPhoneSystemVersionCode() {
        return Build.VERSION.SDK_INT;
    }


    /**
     * 获取指定的meta数据
     *
     * @param context
     * @param key
     * @return
     */
    public static String getAppMetaData(Context context, String key) {
        if (context == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String channelNumber = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelNumber = applicationInfo.metaData.getString(key);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channelNumber;
    }



    /**
     * 判断某个app是否安装了
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkApkExist(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /**
     * 判断是否是原生的Rom
     *
     * @param context
     * @return
     */
    public static boolean isGoogleRom(Context context) {
        boolean playMusic = checkApkExist(context, packageNamePlayMusic);
        boolean youtube = checkApkExist(context, packageNameYoutube);
        boolean gmail = checkApkExist(context, packageNameGmail);
        boolean googleplay = checkApkExist(context, packageNameGooglePlay);
        return playMusic && youtube && gmail && googleplay;
    }
}
