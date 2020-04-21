package npBase.BaseCommon.base.activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 */

public class ActivityManager {

    private List<Activity> activityList = new ArrayList<>();
    //单例
    private static ActivityManager instance = new ActivityManager();

    /**
     * 获取实例
     * @return
     */
    public static ActivityManager getInstance(){
        return instance;
    }

    /**
     * 添加一个页面
     * @param argActivity
     */
    public void putActivity(Activity argActivity){
        if(activityList != null){
            activityList.add(argActivity);
        }
    }

    /**
     * 移除一个页面
     * @param argActivity
     */
    public void removeActivity(Activity argActivity){
        if(activityList != null){
            activityList.remove(argActivity);
        }
    }


    /**
     * 关闭指定页面
     * @param argActivity
     */
    public void close(Activity argActivity){
        if(argActivity != null && !argActivity.isFinishing()){
            argActivity.finish();
        }
    }

    /**
     * 关闭所有页面
     */
    public void closeAll(){
        if(activityList.isEmpty()) return;

        for (int i = 0; i < activityList.size(); i++) {
            close(activityList.get(i));
        }
        activityList.clear();

    }
}
