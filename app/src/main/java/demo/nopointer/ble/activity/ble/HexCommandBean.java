package demo.nopointer.ble.activity.ble;

import npble.nopointer.util.BleUtil;

public class HexCommandBean {
    private String hex;
    private boolean isSelect;
    private long time;

    public HexCommandBean() {
    }

    public HexCommandBean(String paramString) {
        this.hex = paramString;
    }

    public HexCommandBean(byte[] paramArrayOfByte) {
        this.hex = BleUtil.byte2HexStr(paramArrayOfByte);
    }

    public String getHex() {
        return this.hex;
    }

    public long getTime() {
        return this.time;
    }

    public boolean isSelect() {
        return this.isSelect;
    }

    public void setHex(String paramString) {
        this.hex = paramString;
    }

    public void setSelect(boolean paramBoolean) {
        this.isSelect = paramBoolean;
    }

    public void setTime(long paramLong) {
        this.time = paramLong;
    }

    @Override
    public String toString() {
        return "HexCommandBean{" +
                "hex='" + hex + '\'' +
                ", isSelect=" + isSelect +
                ", time=" + time +
                '}';
    }
}