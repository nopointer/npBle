package npBase.BaseCommon.util;

import android.content.Context;
import android.os.Vibrator;

/**
 */

public class VibratorUtils {

    //上下文
    private Context context;
    //实例
    private static VibratorUtils instance;
    //振动器
    private Vibrator vibrator;

    private VibratorUtils(Context argContext){
        context = argContext;
        vibrator =  (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
    }

    public static VibratorUtils getInstance(Context argContext){
        if(instance == null){
            instance = new VibratorUtils(argContext);
        }

        return instance;
    }

    /**
     * 多振动
     * @param argData
     * @parm argRepeat  是否循环振动，-1为不循环
     */
    public  void vibrator(long[] argData, int argRepeat){
        if(vibrator != null && vibrator.hasVibrator()){
            vibrator.vibrate(argData,argRepeat);
        }
    }

    /**
     * 取消振动
     */
    public void cancelVibrator(){
        if(vibrator != null){
            vibrator.cancel();
        }
    }
}
