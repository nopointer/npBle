package npble.nopointer.log;

import android.text.TextUtils;
import android.util.Log;

public class NpBleLog {


    /**
     * 是否允许log
     */
    public static boolean enableLog = true;


    /**
     * 是否允许lig的Log
     */
    public static boolean enableLibLog = false;


    //是否显示调用路径和行号
    public static boolean allowShowCallPathAndLineNumber = true;

    public static void logLibBleLog(String message) {
        if (!enableLibLog) return;
        if (mNpBleLogPrinter != null) {
            log(mNpBleLogPrinter.initTag(), message);
        } else {
            log("LibNpBleLog", message);
        }
    }

    public static void log(String message) {
        if (!enableLog) return;
        if (mNpBleLogPrinter != null) {
            log(mNpBleLogPrinter.initTag(), message);
        } else {
            log("NpBleLog", message);
        }
    }

    public static void log(String tag, String message) {
        if (TextUtils.isEmpty(tag)) {
            tag = "NpBleLog";
        }

        if (allowShowCallPathAndLineNumber) {
            StackTraceElement caller = getCallerStackTraceElement();
            message = "[" + getCallPathAndLineNumber(caller) + "]：" + message;
        }

        if (mNpBleLogPrinter == null) {
            Log.e(tag, message);
        } else {
            mNpBleLogPrinter.onLogPrint(tag, message);
        }
    }

    private static NpBleLogPrinter mNpBleLogPrinter;

    public static void setNpBleLogPrinter(NpBleLogPrinter npBleLogPrinter) {
        mNpBleLogPrinter = npBleLogPrinter;
    }

    public static interface NpBleLogPrinter {
        void onLogPrint(String message);

        void onLogPrint(String tag, String message);

        String initTag();
    }


    /**
     * 获取调用路径和行号
     *
     * @return
     */
    private static String getCallPathAndLineNumber(StackTraceElement caller) {
        String result = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        result = String.format(result, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        return result;
    }


    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[5];
    }

}
