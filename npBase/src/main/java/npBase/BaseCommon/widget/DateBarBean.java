package npBase.BaseCommon.widget;

/**
 */

public class DateBarBean {
    public static final int TYPE_DAY = 0;
    public static final int TYPE_WEEK = 1;
    public static final int TYPE_MONTH = 2;
    public static final int TYPE_YEAR = 3;

    //显示的标题
    private String title;
    //开始时间
    private String startDate;
    //结束时间
    private String endDate;
    //类型
    private int type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "DateBarBean{" +
                "title='" + title + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", type=" + type +
                '}';
    }
}
