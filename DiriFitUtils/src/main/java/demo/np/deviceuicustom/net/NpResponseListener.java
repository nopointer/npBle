package demo.np.deviceuicustom.net;

import retrofit2.Call;
import retrofit2.Callback;

public abstract class NpResponseListener<T> implements Callback<T> {


    @Override
    public void onFailure(Call<T> call, Throwable t) {

    }
}
