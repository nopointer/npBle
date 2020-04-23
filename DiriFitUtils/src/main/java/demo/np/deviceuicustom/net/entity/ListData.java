package demo.np.deviceuicustom.net.entity;

import java.util.List;

public class ListData<T> extends D {
    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
