package demo.np.deviceuicustom.net.download;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface DownloadApi {


    @GET
    @Streaming
    @Headers({"Accept-Encoding:identity"})
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
