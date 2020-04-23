package demo.np.deviceuicustom.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

import demo.np.deviceuicustom.net.entity.Data;
import demo.np.deviceuicustom.net.entity.FirmwareInfoEntity;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetManager {


    private Retrofit mRetrofit;

    private static final NetManager ourInstance = new NetManager();

    public static NetManager getInstance() {
        return ourInstance;
    }


    public static final String domainUrl = "http://mlb2.app168.com/index.php/Home/";


    private NetManager() {

        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        Gson gson = builder.create();

        ////步骤4:构建Retrofit实例
        mRetrofit = new Retrofit.Builder()
                //设置网络请求BaseUrl地址
                .baseUrl(domainUrl)
                //设置数据解析器
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }


    /**
     * 查询固件
     *
     * @param name
     * @param responseListener
     */
    public void queryNetFirmwareInfo(String name, NpResponseListener<Data<FirmwareInfoEntity>> responseListener) {
        NetApi api = mRetrofit.create(NetApi.class);
        //步骤6：对发送请求进行封装
        Call<Data<FirmwareInfoEntity>> jsonDataCall = api.queryNetFirmwareInfo(name);
        jsonDataCall.enqueue(responseListener);
    }






}
