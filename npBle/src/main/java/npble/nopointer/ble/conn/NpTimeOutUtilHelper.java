package npble.nopointer.ble.conn;


/**
 * 超时工具类
 */
public class NpTimeOutUtilHelper {

    //发送的数据
    private String strData;
    //延时多少毫秒后判断，是否是超时
    private int milliSecond;
    //重发指令次数
    private int retryCount = 0;

    public NpTimeOutUtilHelper(String strData, int milliSecond) {
        this.strData = strData;
        this.milliSecond = milliSecond;
    }

    public String getStrData() {
        return strData;
    }

    public int getMilliSecond() {
        return milliSecond;
    }

    public void setStrData(String strData) {
        this.strData = strData;
    }

    public void setMilliSecond(int milliSecond) {
        this.milliSecond = milliSecond;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
