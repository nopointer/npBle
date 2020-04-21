package npBase.BaseCommon.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import npBase.BaseCommon.widget.DateBarBean;

/**
 */

public class DateUtils {
    //每周开始日期
    public final static SimpleDateFormat yyyyMMddSmp = new SimpleDateFormat("yyyy-MM-dd");
    
    public final static SimpleDateFormat endDateSimpleDateFormat = new SimpleDateFormat("MM-dd");
    //月
    public final static SimpleDateFormat monthDateSimpleDateFormat = new SimpleDateFormat("yyyy-MM");
    //年
    public final static SimpleDateFormat yearDateSimpleDateFormat = new SimpleDateFormat("yyyy");
    //完整
    public final static SimpleDateFormat defaultDateSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //时分秒
    public final static SimpleDateFormat hmsSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");


    /**
     * 获取这个月有多少天
     *
     * @param argDate
     * @return
     * @throws ParseException
     */
    public static int getMonthDayByDate(String argDate) throws ParseException {
        if (TextUtils.isEmpty(argDate)) {
            return 30;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(yyyyMMddSmp.parse(argDate));

        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据年-月-日，获取今天是周几
     *
     * @param argDate
     * @return
     */
    public static int getWeekByDate(String argDate) {
        if (TextUtils.isEmpty(argDate)) {
            return -1;
        }

        int result = -1;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(yyyyMMddSmp.parse(argDate));
            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    //周天
                    result = 7;
                    break;
                case 2:
                    result = 1;
                    break;
                case 3:
                    result = 2;
                    break;
                case 4:
                    result = 3;
                    break;
                case 5:
                    result = 4;
                    break;
                case 6:
                    result = 5;
                    break;
                case 7:
                    result = 6;
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 获取今天的年-月-日
     *
     * @return
     */
    public static String getNowDateString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));

        return yyyyMMddSmp.format(calendar.getTime());
    }


    /**
     * 获取天
     *
     * @param argIndex
     * @return
     */
    public static DateBarBean getDayDateBarBean(Date argDate, int argIndex) {
        DateBarBean result = new DateBarBean();
        if (argIndex == 0) {
            result.setStartDate(defaultDateSimpleDateFormat.format(getDayStart(argDate)));
            result.setEndDate(defaultDateSimpleDateFormat.format(getDayEnd(argDate)));
            result.setTitle(yyyyMMddSmp.format(getDayStart(argDate)));
        } else {
            result.setStartDate(defaultDateSimpleDateFormat.format(getDayStartByIndex(argDate, argIndex)));
            result.setEndDate(defaultDateSimpleDateFormat.format(getDayEndByIndex(argDate, argIndex)));
            result.setTitle(yyyyMMddSmp.format(getDayStartByIndex(argDate, argIndex)));
        }

        result.setType(DateBarBean.TYPE_DAY);
        return result;
    }


    /**
     * 获取周
     *
     * @param argDate  当前日期
     * @param argIndex 如果为0表示本周，-1为上周，1为下周
     * @return
     */
    public static DateBarBean getWeekDateBarBean(Date argDate, int argIndex) {
        DateBarBean result = new DateBarBean();
        //拼标题
        result.setStartDate(yyyyMMddSmp.format(getWeekStartDate(argDate, argIndex)));
        result.setEndDate(endDateSimpleDateFormat.format(getWeekEndDate(argDate, argIndex)));
        result.setTitle(result.getStartDate() + " ~ " + result.getEndDate());
        //重新算完整的数据
        result.setStartDate(defaultDateSimpleDateFormat.format(getWeekStartDate(argDate, argIndex)));
        result.setEndDate(defaultDateSimpleDateFormat.format(getWeekEndDate(argDate, argIndex)));
        result.setType(DateBarBean.TYPE_WEEK);
        return result;
    }

    /**
     * 获取月
     *
     * @param argDate
     * @param argIndex
     * @return
     */
    public static DateBarBean getMonthDateBarBean(Date argDate, int argIndex) {
        DateBarBean result = new DateBarBean();
        result.setStartDate(defaultDateSimpleDateFormat.format(getMonthStartDate(argDate, argIndex)));
        result.setEndDate(defaultDateSimpleDateFormat.format(getMonthEndDate(argDate, argIndex)));
        result.setTitle(monthDateSimpleDateFormat.format(getMonthStartDate(argDate, argIndex)));
        result.setType(DateBarBean.TYPE_MONTH);
        return result;
    }

    /**
     * 获取年
     *
     * @param argDate
     * @param argIndex
     * @return
     */
    public static DateBarBean getYearDateBarBean(Date argDate, int argIndex) {
        DateBarBean result = new DateBarBean();
        result.setStartDate(defaultDateSimpleDateFormat.format(getYearStartDate(argDate, argIndex)));
        result.setEndDate(defaultDateSimpleDateFormat.format(getYearEndStartDate(argDate, argIndex)));
        result.setTitle(yearDateSimpleDateFormat.format(getYearStartDate(argDate, argIndex)));
        result.setType(DateBarBean.TYPE_YEAR);
        return result;
    }

    /**
     * 获取当天的开始时间
     */
    private static Date getDayStart(Date argDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(argDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当天的结束时间
     */
    private static Date getDayEnd(Date argDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(argDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取指定天数的开始时间
     *
     * @param argIndex
     * @return
     */
    private static Date getDayStartByIndex(Date argDate, int argIndex) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(getDayStart(argDate));
        calendar.add(Calendar.DAY_OF_MONTH, argIndex);
        return calendar.getTime();
    }

    /**
     * 获取指定天数的开始时间
     *
     * @param argIndex
     * @return
     */
    private static Date getDayEndByIndex(Date argDate, int argIndex) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(getDayEnd(argDate));
        calendar.add(Calendar.DAY_OF_MONTH, argIndex);
        return calendar.getTime();
    }


    /**
     * 获取本周一
     */
    private static Date getWeekStartDate(Date argDate, int argIndex) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(argDate);
        // 获得当前日期是一个星期的第几天
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) {
            day = 7;
        }
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -day + 1 + argIndex * 7);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return calendar.getTime();
    }

    /**
     * 获取本周天
     *
     * @param argDate
     * @return
     */
    private static Date getWeekEndDate(Date argDate, int argIndex) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(argDate);
        // 获得当前日期是一个星期的第几天
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) {
            day = 7;
        }
        calendar.add(Calendar.DATE, -day + 7 + argIndex * 7);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        return calendar.getTime();
    }

    /**
     * 获取指定月份开始时间
     *
     * @param argDate
     * @param argIndex
     * @return
     */
    private static Date getMonthStartDate(Date argDate, int argIndex) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(argDate), getNowMonth(argDate) + argIndex, 1);
        return getDayStartTime(calendar.getTime());
    }

    /**
     * 获取指定月份结束时间
     *
     * @param argDate
     * @param argIndex
     * @return
     */
    private static Date getMonthEndDate(Date argDate, int argIndex) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(argDate), getNowMonth(argDate) + argIndex, 1);
        int day = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(getNowYear(argDate), getNowMonth(argDate) + argIndex, day);
        return getDayEndTime(calendar.getTime());
    }

    /**
     * 获取本年的开始时间
     *
     * @param argDate
     * @param argIndex
     * @return
     */
    public static Date getYearStartDate(Date argDate, int argIndex) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, getNowYear(argDate) + argIndex);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 1);
        return getDayStartTime(calendar.getTime());
    }


    /**
     * 获取本年的结束时间
     *
     * @param argDate
     * @param argIndex
     * @return
     */
    public static Date getYearEndStartDate(Date argDate, int argIndex) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, getNowYear(argDate) + argIndex);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DATE, 31);
        return getDayEndTime(calendar.getTime());
    }


    /**
     * 获取今年是哪一年
     *
     * @return
     */
    private static Integer getNowYear(Date argDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(argDate);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取本月是哪一月
     *
     * @param argDate
     * @return
     */
    private static int getNowMonth(Date argDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(argDate);
        return calendar.get(Calendar.MONTH);
    }

    /**
     * 获取某个日期的开始时间
     *
     * @param argDate
     * @return
     */
    private static Date getDayStartTime(Date argDate) {
        Calendar calendar = Calendar.getInstance();
        if (null != argDate) calendar.setTime(argDate);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取某个日期的结束时间
     *
     * @param argDate
     * @return
     */
    private static Date getDayEndTime(Date argDate) {
        Calendar calendar = Calendar.getInstance();
        if (null != argDate) calendar.setTime(argDate);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获取指定日期中这个月的天数
     *
     * @param date
     * @return
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


    /**
     * 根据时间戮获取HH:mm:ss
     * @param time 秒为单位
     * @return
     */
    public static String getHMSString(long time){
        return hmsSimpleDateFormat.format(new Date(time*1000));
    }

    /**
     * 秒钟转成00:00:00时间格式
     *
     * @param time
     * @return
     */
    public static String secondToTimeString(int time) {
        String timeStr;
        int hour;
        int minute;
        int second;
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }


    /**
     * 秒钟转成00:00:00时间格式
     *
     * @param time
     * @return
     */
    public static String secondToTimeString2(int time) {
        String timeStr;
        int hour;
        int minute;
        int second;
        if (time <= 0) {
            return "0'00''";
        }else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + "'" + unitFormat(second) + "''";
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99'59'59''";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + "'" + unitFormat(minute) + "'" + unitFormat(second) + "''";
            }
        }
        return timeStr;
    }

    /**
     * 补位
     *
     * @param i
     * @return
     */
    private static String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }


    /**
     * 计算相对给定的日期的天数的日期，1表示后一天的数据 ，-1表示前一天的数据
     *
     * @param date
     * @param after
     * @return
     */
    public static Date getTheDayAfterDate(Date date, int after) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, after);
        return c.getTime();
    }

    /**
     * 计算相对给定的日期的天数的日期，1表示后一天的数据 ，-1表示前一天的数据
     *
     * @param date
     * @param after
     * @return
     */
    public static Date getTheHourAfterDate(Date date, int after) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR, after);
        return c.getTime();
    }

    /**
     * 计算相对给定的日期的天数的日期，1表示后一天的数据 ，-1表示前一天的数据
     *
     * @param date
     * @param after
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

}
