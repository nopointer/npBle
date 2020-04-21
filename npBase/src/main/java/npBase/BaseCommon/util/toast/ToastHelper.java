package npBase.BaseCommon.util.toast;

import android.app.Activity;
import android.widget.Toast;

public class ToastHelper {

    private ToastHelper() {
    }

    private Activity activity;

    private static ToastHelper toastHelper = new ToastHelper();

    public static ToastHelper getToastHelper() {
        return toastHelper;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }


    Toast toast = null;

    public void show(final String msg) {
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
                } else {
                    toast.setDuration(Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });
    }

    public void show(final int msg) {
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
                } else {
                    toast.setDuration(Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });
    }

}
