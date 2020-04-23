package demo.np.deviceuicustom.net.entity;

import java.io.Serializable;

public class FirmwareInfoEntity implements Serializable {
    private String fileName;
    private String fileSize;
    private String url;
    private String versionCode;
    private String versionLog;

    public String getFileName() {
        return this.fileName;
    }

    public String getFileSize() {
        return this.fileSize;
    }

    public String getUrl() {
        return this.url;
    }

    public String getVersionCode() {
        return this.versionCode;
    }

    public String getVersionLog() {
        return this.versionLog;
    }

    public void setFileName(String paramString) {
        this.fileName = paramString;
    }

    public void setFileSize(String paramString) {
        this.fileSize = paramString;
    }

    public void setUrl(String paramString) {
        this.url = paramString;
    }

    public void setVersionCode(String paramString) {
        this.versionCode = paramString;
    }

    public void setVersionLog(String paramString) {
        this.versionLog = paramString;
    }

    @Override
    public String toString() {
        return "FirmwareInfoEntity{" +
                "fileName='" + fileName + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", url='" + url + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", versionLog='" + versionLog + '\'' +
                '}';
    }
}
