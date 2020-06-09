package demo.nopointer.npDemo.keepAlive.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

import npBase.BaseCommon.util.log.LogUtil;


public class ServiceAliveUtils {

    /**
     * 判断某个service 是否还活着
     *
     * @param context
     * @param clazz
     * @return
     */
    public static boolean isServiceAlice(Context context, Class<?> clazz) {
        boolean isServiceRunning = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return true;
        }

        LogUtil.e("clazz.getName()"+clazz.getName());
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (clazz.getName().equals(service.service.getClassName())) {
                isServiceRunning = true;
            }
        }
        return isServiceRunning;
    }


    /**
     * 判断本应用是否存活
     * 如果需要判断本应用是否在后台还是前台用getRunningTask
     */
    public static boolean isAppALive(Context mContext, String packageName) {
        Log.e("isAppALive", packageName);
        boolean isAPPRunning = false;
        // 获取activity管理对象
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取所有正在运行的app
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        // 遍历，进程名即包名
        for (ActivityManager.RunningAppProcessInfo appInfo : appProcessInfoList) {
            if (packageName.equals(appInfo.processName)) {
                isAPPRunning = true;
                break;
            }
        }
        return isAPPRunning;
    }
}
