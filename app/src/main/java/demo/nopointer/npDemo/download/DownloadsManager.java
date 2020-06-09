package demo.nopointer.npDemo.download;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;


import java.io.File;
import java.io.IOException;

import demo.nopointer.npDemo.download.impl.DownloadManager;
import demo.nopointer.npDemo.download.impl.Downloads;


public class DownloadsManager {
    //上下文对象
    private static Context context;
    //下载管理器
    private static DownloadManager downloadManager;
    //回调接口
    private IDownloadCallbackListener listener;
    //下载数据改变
    private DownloadChangeObserver downloadChangeObserver;
    //当前下载ID
    private static long downloadId;
    //当前下载保存的文件名
    private String url = "";
    //当前下载的路径
    private String filePath;

    //单例
    private static DownloadsManager instance;

    private DownloadsManager(Context argContext) {
        context = argContext;
    }

    /**
     * 获取实例
     *
     * @param argContext
     * @return
     */
    public static DownloadsManager getInstance(Context argContext) {
        if (instance == null) {
            instance = new DownloadsManager(argContext);
        }
        if (downloadManager == null) {
            downloadManager = new DownloadManager(context.getContentResolver(), context.getPackageName());
        }
        return instance;
    }


    /**
     * 下载
     *
     * @param argUrl      下载文件的URL地址
     * @param argListener 下载回调接口
     * @return
     */
    public long startDownload(String argUrl, String argFilePath, IDownloadCallbackListener argListener) {
        try {
            if (argListener != null) {
                listener = argListener;
            }
            url = argUrl;
            filePath = argFilePath;
            //注册数据监听
            downloadChangeObserver = new DownloadChangeObserver();
            context.getContentResolver().registerContentObserver(Downloads.CONTENT_URI, true, downloadChangeObserver);
            //根据uri获取request请求对象
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(argUrl));
            //判断文件是否存在
            if(isFileExist(argFilePath)){
                deleteFile(argFilePath);
            }
            //设置下载路径
            request.setDestinationUri(Uri.fromFile(new File(argFilePath)));
            //设置不在通知栏显示下载
            request.setShowRunningNotification(false);
            //设置不显示下载界面
            request.setVisibleInDownloadsUi(false);
            //执行下载
            downloadId = downloadManager.enqueue(request);
            //回调
            listener.onStatusChange(url, downloadId, IDownloadCallbackListener.DOWNLOAD_START);

            return downloadId;
        } catch (Exception e) {
            e.printStackTrace();
            listener.onDownloadFailure(url, downloadId, DownloadManager.ERROR_FILE_URL_NOT_FOUND, "");
            return -1;
        }
    }

    /**
     * 判断当前下载的
     *
     * @return
     */
    public boolean isFileExist(String argFilePath) {
        return getFileExist(argFilePath);
    }


    /**
     * 取消当前下载
     *
     * @param argDownloadId
     * @return
     */
    public boolean cancelDownload(long argDownloadId) {
        try {
            if (argDownloadId == 0) {
                argDownloadId = downloadId;
            }
            //取消下载任务
            downloadManager.remove(argDownloadId);
            //删除临时文件
            if (getFileExist(filePath)) {
                deleteFile(filePath);
            }
            listener.onStatusChange(url, argDownloadId, IDownloadCallbackListener.DOWNLOAD_CANCEL);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 暂停下载
     *
     * @param argDownloadId
     * @return
     */
    public boolean pauseDownload(long argDownloadId) {
        try {
            if (argDownloadId == 0) {
                argDownloadId = downloadId;
            }
            downloadManager.pauseDownload(argDownloadId);

            listener.onStatusChange(url, argDownloadId, IDownloadCallbackListener.DOWNLOAD_PAUSE);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 继续下载
     *
     * @param argDownloadId
     * @return
     */
    public boolean resumeDownload(long argDownloadId) {
        try {
            if (argDownloadId == 0) {
                argDownloadId = downloadId;
            }
            downloadManager.resumeDownload(argDownloadId);

            listener.onStatusChange(url, argDownloadId, IDownloadCallbackListener.DOWNLOAD_RESUME);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 重新下载
     *
     * @param argDownloadId
     * @return
     */
    public boolean restartDownload(long argDownloadId) {
        try {
            if (argDownloadId == 0) {
                argDownloadId = downloadId;
            }
            downloadManager.restartDownload(argDownloadId);

            listener.onStatusChange(url, argDownloadId, IDownloadCallbackListener.DOWNLOAD_RESTART);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public String getFilePath() {
        return filePath;
    }

    /**
     * 获取下载路径
     * @return
     */
    public static String getDownloadPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 判断文件是否存在
     *
     * @param argFilePath 文件绝对路径
     * @return
     */
    private boolean getFileExist(String argFilePath) {
        File file = new File(argFilePath);
        if (file.isDirectory()) {
            return false;
        }
        boolean bool = file.exists();
        return bool;
    }


    private boolean deleteFile(String argFilePath) {
        File file = new File(argFilePath);
        return file.delete();
    }

    /**
     * 改文件名
     *
     * @param argOldFilepath 原文件名
     * @param argNewFilepath 新文件名
     * @return
     */
    private boolean reName(String argOldFilepath, String argNewFilepath) {
        File file = new File(argOldFilepath);

        if (!file.exists()) {
            return false;
        }

        File newfile = new File(argNewFilepath);

        if (newfile.exists()) {
            newfile.delete();
        }

        return file.renameTo(newfile);
    }

    /**
     * 修改指定文件的读写权限
     *
     * @param argFilePath
     * @return
     */
    private boolean modifyFilePermissions(String argFilePath) {
        try {
            Runtime.getRuntime().exec("chmod 777 " + argFilePath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            //刷新进度
            int[] bytesAndStatus = downloadManager.getBytesAndStatus(downloadId);
            handler.sendMessage(handler.obtainMessage(0, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]));
        }
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    int statusCode = (Integer) msg.obj;
                    //状态是否是下载中
                    if (DownloadManager.isDownloading(statusCode)) {

                        //回调下载字符数发生改变接口
                        listener.onDownloadSizeChange(url, downloadId, msg.arg1, msg.arg2, DownloadManager.getNotiPercent(msg.arg1, msg.arg2));

                    } else if (msg.arg1 == msg.arg2 && statusCode == DownloadManager.STATUS_SUCCESSFUL) {
                        //回调状态
                        listener.onStatusChange(url, downloadId, IDownloadCallbackListener.DOWNLOAD_SUCCESS);
                        //赋权限
                        modifyFilePermissions(filePath);
                        //回调下载完成
                        listener.onDownloadSuccess(url, downloadId, filePath);
                        //注销
                        context.getContentResolver().unregisterContentObserver(downloadChangeObserver);
                    } else if (statusCode == DownloadManager.STATUS_FAILED) {
                        //下载失败
                        listener.onStatusChange(url, downloadId, IDownloadCallbackListener.DOWNLOAD_FAILURE);

                        listener.onDownloadFailure(url, downloadId, downloadManager.getReason(downloadId), "");
                        //注销
                        context.getContentResolver().unregisterContentObserver(downloadChangeObserver);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
