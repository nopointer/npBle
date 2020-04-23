package demo.np.deviceuicustom.sharedpreferences.bean;

import java.io.Serializable;

/**
 * 过滤对象
 */
public class FilterBean implements Serializable {
    private String name;
    private String mac;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
