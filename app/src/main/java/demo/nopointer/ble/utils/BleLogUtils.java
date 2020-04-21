package demo.nopointer.ble.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BleLogUtils {
    private static boolean enableShowCurrentLogFileSize = false;
    private static boolean isShowTime = false;
    private static float logFileMaxSizeByM;
    private static String mLogDir = "npBle";
    private static String mLogFileName = "log";
    private static SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    static {
        logFileMaxSizeByM = 2.0F;
    }

    private static void appendFileHeader(String content, String srcPath) throws Exception {
        RandomAccessFile src = new RandomAccessFile(srcPath, "rw");
        int srcLength = (int) src.length();
        byte[] buff = new byte[srcLength];
        src.read(buff, 0, srcLength);
        src.seek(0);
        byte[] header = content.getBytes("utf-8");
        src.write(header);
        src.seek(header.length);
        src.write(buff);
        src.close();
    }

    public static void clearLogFile() {
        File file = new File(Environment.getExternalStorageDirectory(), getFilePath());
        if (file.exists()) {
            Log.e("成功删除文件", file.getAbsolutePath());
            file.delete();
        }
    }

    public static File getBleLogFileDir() {
        initDirAndFileName();
        return new File(Environment.getExternalStorageDirectory(), getFilePath());
    }

    private static String getFilePath() {
        initDirAndFileName();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(mLogDir);
        localStringBuilder.append("/");
        localStringBuilder.append(mLogFileName);
        localStringBuilder.append(".txt");
        return localStringBuilder.toString();
    }

    private static void initDirAndFileName() {
        if (TextUtils.isEmpty(mLogDir)) {
            mLogDir = "npLog";
        }
        if (TextUtils.isEmpty(mLogFileName)) {
            mLogFileName = "log";
        }
        if (logFileMaxSizeByM <= 0.0F) {
            logFileMaxSizeByM = 2.0F;
        }
        if (logFileMaxSizeByM >= 5.0F) {
            logFileMaxSizeByM = 5.0F;
        }
    }

    public static void initLog() {
        initLog(null, null);
    }

    public static void initLog(String paramString1, String paramString2) {
        mLogDir = paramString1;
        mLogFileName = paramString2;
        initDirAndFileName();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("初始化log管理器");
        localStringBuilder.append(paramString1);
        localStringBuilder.append("/");
        localStringBuilder.append(paramString2);
        Log.e("initLog", localStringBuilder.toString());
    }

    public static void save(String paramString) {
        String str = smp.format(new Date());
        try {
            if (isShowTime) {
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append(str);
                localStringBuilder.append("  ");
                localStringBuilder.append(paramString);
                writeFile(localStringBuilder.toString());
            } else {
                writeFile(paramString);
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setEnableShowCurrentLogFileSize(boolean paramBoolean) {
        enableShowCurrentLogFileSize = paramBoolean;
    }

    public static void setIsShowTime(boolean paramBoolean) {
        isShowTime = paramBoolean;
    }

    public static void setLogDir(String paramString) {
        mLogDir = paramString;
    }

    public static void setLogFileMaxSizeByM(float paramFloat) {
        logFileMaxSizeByM = paramFloat;
    }

    public static void setLogFileName(String paramString) {
        mLogFileName = paramString;
    }

    public static void writeFile(String paramString) {
        for (; ; ) {
            try {
                Object localObject = new File(Environment.getExternalStorageDirectory(), mLogDir);
                if (!((File) localObject).exists()) {
                    ((File) localObject).mkdirs();
                }
                StringBuilder localStringBuilder1 = new StringBuilder();
                localStringBuilder1.append(mLogFileName);
                localStringBuilder1.append(".txt");
                localObject = new File((File) localObject, localStringBuilder1.toString());
                boolean bool = ((File) localObject).exists();
                if (!bool) {
                    try {
                        new BufferedWriter(new FileWriter((File) localObject, true));
                    } catch (IOException localIOException) {
                        localIOException.printStackTrace();
                    }
                } else {
                    if (enableShowCurrentLogFileSize) {
                        StringBuilder localStringBuilder2 = new StringBuilder();
                        localStringBuilder2.append(((File) localObject).length());
                        localStringBuilder2.append("");
                        Log.e("size:", localStringBuilder2.toString());
                    }
                    if ((float) ((File) localObject).length() > logFileMaxSizeByM * 1024.0F * 1024.0F) {
                        clearLogFile();
                        writeFile(paramString);
                        return;
                    }
                    try {
                        localObject = new BufferedWriter(new FileWriter((File) localObject, true));
                        ((BufferedWriter) localObject).write(paramString);
                        ((BufferedWriter) localObject).newLine();
                        ((BufferedWriter) localObject).flush();
                        ((BufferedWriter) localObject).close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
            } finally {
            }
        }
    }
}