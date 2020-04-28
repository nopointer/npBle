package demo.np.deviceuicustom.ble.imageTransport;

import java.io.File;

/**
 * 表盘图片数据
 */
public class DialImageBean {

    //图片/二进制文件的路径
    private String imagePath = null;
    //图片宽 默认240
    private int imageWidth = 240;
    //图片高 默认240
    private int imageHeight = 240;
    //总共的字节数，这个需要根据颜色配置计算得出
    private long totalByte = 0;
    //单包数的长度
    private int singlePckDataLen = 18;
    //要传输的图片的包数，这个需要根据单包数据传参的字节数计算得出
    private long totalBytePckCount = 0;

    //色彩配置 默认RGB_565
    private ColorCfg colorCfg = ColorCfg.RGB_556;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public ColorCfg getColorCfg() {
        return colorCfg;
    }

    public void setColorCfg(ColorCfg colorCfg) {
        this.colorCfg = colorCfg;
    }

    public int getSinglePckDataLen() {
        return singlePckDataLen;
    }

    public void setSinglePckDataLen(int singlePckDataLen) {
        this.singlePckDataLen = singlePckDataLen;
    }


    public long getTotalByte() {
        return totalByte;
    }

    public long getTotalBytePckCount() {
        return totalBytePckCount;
    }

    /**
     * 计算数据
     */
    public void calculationData() {
        switch (colorCfg) {
            //2个byte表示一个像素点，所以长度是
            case RGB_556:
            default:
                totalByte = imageHeight * imageWidth * 2;
                break;
            case ARGB_8888:
                totalByte = imageHeight * imageWidth * 4;
                break;
            case BIN_FILE:
                totalByte = new File(imagePath).length();
                break;
        }
        totalBytePckCount = totalByte / singlePckDataLen;
        //如果长度不能被整除 那就多一包数据咯
        if (totalByte % singlePckDataLen != 0) {
            totalBytePckCount += 1;
        }
    }
}
