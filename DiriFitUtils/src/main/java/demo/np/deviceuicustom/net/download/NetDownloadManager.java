package demo.np.deviceuicustom.net.download;

import android.support.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import npBase.BaseCommon.util.log.LogUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NetDownloadManager {

    private String domain = "";


    private static int sBufferSize = 1024*512;

    private static final NetDownloadManager ourInstance = new NetDownloadManager();

    public static NetDownloadManager getInstance() {
        return ourInstance;
    }

    private NetDownloadManager() {

        domain = "http://mlb2.app168.com/index.php/Home/";
    }

    public void download(String url, String filePath, DownloadListener downloadListener) {
        Retrofit mRetrofit = new Retrofit.Builder()
                //设置网络请求BaseUrl地址
                .baseUrl(domain)
                //设置数据解析器
                .build();
        DownloadApi api = mRetrofit.create(DownloadApi.class);
        url = url.replace(domain, "");
        Call<ResponseBody> call = api.downloadFile(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                //将Response写入到从磁盘中，详见下面分析
                //注意，这个方法是运行在子线程中的
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        writeResponseToDisk(filePath, response, downloadListener);
                    }
                }).start();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                downloadListener.onFail("网络错误～");
            }
        });
    }

    private static void writeResponseToDisk(String path, Response<ResponseBody> response, DownloadListener downloadListener) {
        //从response获取输入流以及总大小
        writeFileFromIS(new File(path), response.body().byteStream(), response.body().contentLength(), downloadListener);
    }


    //将输入流写入文件
    private static void writeFileFromIS(File file, InputStream is, long totalLength, DownloadListener downloadListener) {
        LogUtil.e("fileSize:" + totalLength);
        //开始下载
        downloadListener.onStart();
        //创建文件
        if (!file.exists()) {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                downloadListener.onFail("createNewFile IOException");
            }
        }

        OutputStream os = null;
        long currentLength = 0;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[sBufferSize];
            int len;
            while ((len = is.read(data, 0, sBufferSize)) != -1) {
                os.write(data, 0, len);
                currentLength += len;
                //计算当前下载进度
                downloadListener.onProgress((1.0f * currentLength) / totalLength);
            }
            //下载完成，并返回保存的文件路径
            downloadListener.onFinish(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            downloadListener.onFail("IOException");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


