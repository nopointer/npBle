package demo.np.deviceuicustom.activity.firmware;

import java.io.Serializable;

public class PathEntity implements Serializable {
    private String dialBinPath;
    private String otaBinPath;

    public String getDialBinPath() {
        return this.dialBinPath;
    }

    public String getOtaBinPath() {
        return this.otaBinPath;
    }

    public void setDialBinPath(String paramString) {
        this.dialBinPath = paramString;
    }

    public void setOtaBinPath(String paramString) {
        this.otaBinPath = paramString;
    }

    @Override
    public String toString() {
        return "PathEntity{" +
                "dialBinPath='" + dialBinPath + '\'' +
                ", otaBinPath='" + otaBinPath + '\'' +
                '}';
    }
}



/* Location:           C:\Users\Administrator\Desktop\AndroidKiller_v1.3.1\bin\dex2jar\classes-dex2jar.jar

 * Qualified Name:     demo.np.deviceuicustom.device.PathEntity

 * JD-Core Version:    0.7.0.1

 */