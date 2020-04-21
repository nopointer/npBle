package npBase.BaseCommon.util.phone;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by nopointer on 2018/8/8.
 * 手机功能状态
 */

public class PhoneFunctionUtil {


    /**
     * 是否开启了gps定位
     *
     * @param context
     * @return
     */
    public static boolean isLocationModeOpen(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * 是否开启了gps定位
     *
     * @param context
     * @return
     */
    public static boolean isLocationModeOpenWith23(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return isLocationModeOpen(context);
    }

    /**
     * 跳转到设置定位的界面
     *
     * @param context
     */
    public static void jump2LocationSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
