package npBase.BaseCommon.util.common;

import android.text.TextUtils;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import npBase.BaseCommon.util.log.LogUtil;

public class DateTimeUtils {

    private void DateFormatUtils() {
    }


    /**
     * 根据年月日获取时间戳，单位秒
     *
     * @param dateString
     * @return
     */
    public static long getTimestampWithSecondByDate(String dateString) {
        try {
            Date date = DateUtils.parseDate(dateString, new String[]{"yyyy-MM-dd"});
            return date.getTime() / 1000L;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据年月日获取时间戳，单位秒
     *
     * @param date
     * @return
     */
    public static long getTimestampWithSecondByDate(Date date) {
        return date.getTime() / 1000L;
    }


    /**
     * 根据时间戳获取年月日
     *
     * @param timestamp 时间戳 秒
     * @return
     */
    public static String getDateBySecond(long timestamp) {
        return DateFormatUtils.format(timestamp * 1000L, "yyyy-MM-dd");
    }

    /**
     * 根据时间戳获取年月日 时分
     *
     * @param timestamp 时间戳 秒
     * @return
     */
    public static String getDateBySecond2(long timestamp) {
        return DateFormatUtils.format(timestamp * 1000L, "yyyy-MM-dd HH:mm");
    }

    /**
     * 根据时间戳获取年月日 时分
     *
     * @param timestamp 时间戳 秒
     * @return
     */
    public static String getDateBySecond3(long timestamp) {
        return DateFormatUtils.format(timestamp * 1000L, "MM-dd HH:mm");
    }

    /**
     * 按指定的格式根据时间戳获取年月日
     *
     * @param timestamp
     * @param dateFormat 格式化
     * @return
     */
    public static String getDateBySecond(long timestamp, String dateFormat) {
        return DateFormatUtils.format(timestamp * 1000L, dateFormat);
    }


    /**
     * 按指定的格式根据时间戳获取年月日
     *
     * @param timestamp
     * @param dateFormat 格式化
     * @param locale     地区
     * @return
     */
    public static String getDateBySecond(long timestamp, String dateFormat, Locale locale) {
        return DateFormatUtils.format(timestamp * 1000L, dateFormat, locale);
    }

    /**
     * 按指定的格式根据时间戳获取年月日
     *
     * @param timestamp  时间戳 单位毫秒
     * @param dateFormat 格式化
     * @return
     */
    public static String getDateByMillisecond(long timestamp, String dateFormat) {
        return DateFormatUtils.format(timestamp, dateFormat);
    }

    /**
     * 根据时间戳获取年月日
     *
     * @param timestamp 时间戳 毫秒
     * @return
     */
    public static String getDateByMillisecond(long timestamp) {
        return DateFormatUtils.format(timestamp, "yyyy-MM-dd");
    }


    /**
     * 根据年月获取月的时间戳 单位秒
     *
     * @param year
     * @param month
     */
    public static long getYearMonthDay(int year, int month) {
        return getTimestampWithSecondByDate(String.format("%d-%02d-01", year, month));
    }


    /**
     * 根据出生年月日计算年龄
     *
     * @param birthTimeString yyyy-MM-dd 格式
     * @return
     */
    public static int getAgeFromBirthTime(String birthTimeString) {
        if (TextUtils.isEmpty(birthTimeString)) return 0;
        // 先截取到字符串中的年、月、日
        String strs[] = birthTimeString.trim().split("-");
        int selectYear = Integer.parseInt(strs[0]);
        // 得到当前时间的年、月、日
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        // 用当前年月日减去生日年月日
        int yearMinus = yearNow - selectYear;
        int age = yearMinus;// 先大致赋值
        return age;
    }

    /**
     * 根据出生年计算年龄
     *
     * @param birthYear yyyy 格式
     * @return
     */
    public static int getAgeFromBirthYear(String birthYear) {
        if (TextUtils.isEmpty(birthYear)) return 0;
        // 先截取到字符串中的年、月、日
        int selectYear = Integer.parseInt(birthYear);
        // 得到当前时间的年、月、日
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        // 用当前年月日减去生日年月日
        int yearMinus = yearNow - selectYear;
        int age = yearMinus;// 先大致赋值
        return age;
    }


    /**
     * 根据时间戳计算年龄
     *
     * @param birthTimeLong 时间戳 单位秒
     * @return
     */
    public static int getAgeFromBirthdayTime(long birthTimeLong) {
        Date date = new Date(birthTimeLong * 1000l);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String birthTimeString = format.format(date);
        return getAgeFromBirthTime(birthTimeString);
    }


    //按指定的格式解析时间字符串
    public static Date parserFromStr(String format, String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 按指定的格式解析时间字符串
     *
     * @param format
     * @param dateStr
     * @return 时间戳 单位秒
     */
    public static long parserFromStrReturnBySecond(String format, String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(dateStr).getTime() / 1000L;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //按指定格式解析时间
    public static String formatDate(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 按指定格式解析时间
     *
     * @param format
     * @param date
     * @return
     */
    public static String formatDate(String format, long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    //获取指定日期相差指定天数的日期
    public static Date getTheDayAfterDate(String formaat, String dateStr, int after) {
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat(formaat);
        Date date = null;
        try {
            date = yyyyMMdd.parse(dateStr);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, after);
            return c.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取指定日期的前后多少天
     *
     * @param date  指定的日期
     * @param after 正数表示多少天以后 负数表示多少天以前 0 表示指定的日期
     * @return
     */
    public static Date getTheDayAfterDate(Date date, int after) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, after);
        return c.getTime();
    }


    /**
     * 获取指定日期的前后多少小时
     *
     * @param date  指定的日期
     * @param after 正数表示多少小时以后 负数表示多少小时以前 0 表示指定的日期
     * @return
     */
    public static Date getTheHourAfterDate(Date date, int after) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR, after);
        return c.getTime();
    }

    /**
     * 获取指定日期的前后多少分钟
     *
     * @param date  指定的日期
     * @param after 正数表示多少分钟以后 负数表示多少分钟以前 0 表示指定的日期
     * @return
     */
    public static Date getTheMinuteAfterDate(Date date, int after) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, after);
        return c.getTime();
    }


    /**
     * 计算相对给定的日期的天数的日期，1表示后一天的数据 ，-1表示前一天的数据
     *
     * @param date
     * @param after
     * @return
     */
    public static Date getTheMonthAfterDate(Date date, int after) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, after);
        return c.getTime();
    }


    //计算两个日期相差多少天
    public static int daysBetween(Date one, Date two) {//86400000
        double difference = (one.getTime() - two.getTime()) / (24 * 60 * 60 * 1000f);
        return (int) difference;
    }

    //获取相对于当前周的周（上/下*周）
    public static long[] getWeekdays(int index) {
        long result[] = new long[7];
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 7 * index);
        setToFirstDay(calendar);
        for (int i = 0; i < 7; i++) {
            result[i] = getDayNumber(calendar);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return result;
    }

    //日期转星期
    public static int dayForWeek(String date) throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    //获取相对于当前月的月（上/下*月）
    public static Date afterMonthDate(int after) {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MONTH, after);
        return cal.getTime();
    }

    public static void main(String[] args) {
        long befo = 1522810281845l;
        long time = 1522714200 * 1000l;
//
        System.out.println(System.currentTimeMillis());

        System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(new Date(1523106000000l)));
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time)));
//        System.out.println(afterMonthDate(10));
    }

    //获取某个月的最后一天
    public static int getYearMonthAndDayCount(int year, int month) {
        int result[] = new int[3];
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, year);
        calendar.add(Calendar.MONTH, month);
        return calendar.getActualMaximum(Calendar.DATE);
    }

    private static final int FIRST_DAY = Calendar.MONDAY;

    private static void setToFirstDay(Calendar calendar) {
        while (calendar.get(Calendar.DAY_OF_WEEK) != FIRST_DAY) {
            calendar.add(Calendar.DATE, -1);
        }
    }

    private static long getDayNumber(Calendar calendar) {
        return calendar.getTimeInMillis();
//		return Integer.valueOf(new SimpleDateFormat("dd").format(calendar.getTime()));
    }

    public static int getLastDayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0); //输入类型为int类型
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static String getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        month -= 1;
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
        return new SimpleDateFormat("yyyy-MM-dd ").format(cal.getTime());
    }

    public static int[] currentYearMonth() {
        int result[] = new int[2];
        Date tempDate = new java.util.Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(tempDate);
        result[0] = cal.get(Calendar.YEAR);
        result[1] = cal.get(Calendar.MONTH) + 1;
//        Log.e("debug_date", result[0] + "__" + result[1]);
        return result;
    }

    /**
     * 把时间转成总分钟
     *
     * @param HHmmTime
     * @return
     */
    public static int HHmmTimeToIntValue(String HHmmTime) {
        if (HHmmTime.length() != 5) {
            LogUtil.e("时间格式不对，请使用HH:mm 格式的时间");
            return 0;
        }
        String[] tmpStringArr = HHmmTime.split(":");
        return Integer.valueOf(tmpStringArr[0]) * 60 + Integer.valueOf(tmpStringArr[1]);
    }

    /**
     * 把分钟转成时间
     *
     * @return
     */
    public static String intValueToHHmmTime(int totalTime) {
        return String.format("%02d:%02d", totalTime / 60, totalTime % 60);
    }


}
