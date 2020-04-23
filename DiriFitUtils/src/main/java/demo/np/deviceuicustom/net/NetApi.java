package demo.np.deviceuicustom.net;

import demo.np.deviceuicustom.net.entity.Data;
import demo.np.deviceuicustom.net.entity.FirmwareInfoEntity;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NetApi {


    /**
     * 查询服务器上的固件
     *
     * @param name
     */
    @POST("common/firmwareUpdate")
    @FormUrlEncoded
    public Call<Data<FirmwareInfoEntity>> queryNetFirmwareInfo(@Field("name") String name);




}
