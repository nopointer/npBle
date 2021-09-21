package demo.nopointer.npDemo.keepAlive.activity;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.WeakReference;

public class ScreenManager {
    private static final String TAG = ScreenManager.class.getSimpleName();
    private static ScreenManager sInstance;
    private Context mContext;
    private WeakReference<Activity> mActivity;

    private ScreenManager(Context mContext) {
        this.mContext = mContext;
    }

    public static ScreenManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ScreenManager(context);
        }
        return sInstance;
    }

    /**
     * 获得SinglePixelActivity的引用
     *
     * @param activity
     */
    public void setSingleActivity(Activity activity) {
        mActivity = new WeakReference<>(activity);
    }

    /**
     * 启动SinglePixelActivity
     */
    public void startActivity() {
//        Intent intent = new Intent(mContext, Bg1PxActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(intent);
    }

    /**
     * 结束SinglePixelActivity
     */
    public void finishActivity() {
        if (mActivity != null) {
            Activity activity = mActivity.get();
            if (activity != null) {
                activity.finish();
            }
        }
    }
}
