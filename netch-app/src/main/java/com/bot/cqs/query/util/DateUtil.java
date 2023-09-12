
package com.bot.cqs.query.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.util.StringUtils;

/**
 * 日期轉換
 */
public class DateUtil {

    public static final int DATE_STYLE_FROM = 0;
    public static final int DATE_STYLE_TO = 1;
    public static final int DATE_STYLE_DEFAULT = 2;

    public static final String DATE_FIELD_SEPARATOR = "/";

    private static final DecimalFormat rocYearDecimalFormat = new DecimalFormat("##00");
    private static final SimpleDateFormat mmddDatetimeFormat = new SimpleDateFormat(DATE_FIELD_SEPARATOR + "MM" + DATE_FIELD_SEPARATOR + "dd HH:mm:ss");
    private static final SimpleDateFormat mmddDateFormat = new SimpleDateFormat(DATE_FIELD_SEPARATOR + "MM" + DATE_FIELD_SEPARATOR + "dd");
    private static final SimpleDateFormat mmDateFormat = new SimpleDateFormat(DATE_FIELD_SEPARATOR + "MM");
    private static final SimpleDateFormat yyyyMMddDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * 將西元年轉換成中華民國年的 "字串"。
     * 
     * @param date
     *            要轉換的日期。
     * @return 中華民國的日期格式字串 yyy/MM/dd HH:mm:ss。
     */
    public static String toRocDatetime(Date date) {

        if (date == null)
            return "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return rocYearDecimalFormat.format(calendar.get(Calendar.YEAR) - 1911) + mmddDatetimeFormat.format(date);
    }

    /**
     * 將西元年轉換成中華民國年的 "字串"。
     * 
     * @param date
     *            要轉換的日期。
     * @return 中華民國的日期格式字串 yyy/MM/dd。
     */
    public static String toRocDate(Date date) {

        if (date == null)
            return "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return rocYearDecimalFormat.format(calendar.get(Calendar.YEAR) - 1911) + mmddDateFormat.format(date);
    }

    /**
     * 將西元年轉換成民國年的格式。
     * 
     * @param date
     *            要被轉換的日期。
     * @return 中華民國的日期格式字串 yyy/MM。
     */
    public static String toRocYearMonth(Date date) {

        if (date == null)
            return "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return rocYearDecimalFormat.format(calendar.get(Calendar.YEAR) - 1911) + mmDateFormat.format(date);
    }

    /**
     * 將Date轉換成西元年+月+日 的 "字串"
     * 
     * @param date
     *            要轉換的日期。
     * @return 西元年的日期字串 格式為：yyyyMMdd
     */
    public static String toADDate(Date date) {

        if (date == null)
            return "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return yyyyMMddDateFormat.format(date);
    }

    /**
     * 計算是不是在週期時間內 週期最多<code>cyale</code>天，以迄日回推
     * 
     * @param startDate
     *            起始日期(起日)
     * @param endDate
     *            結束日期(迄日)
     * @param cyale
     *            週期天數
     * @return
     */
    public static boolean isInCyale(Date startDate, Date endDate, int cyale) {
        if (startDate == null || endDate == null)
            return true;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        int endDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(startDate);
        int startDay = calendar.get(Calendar.DAY_OF_YEAR);
        if (cyale >= endDay - startDay) {
            return true;
        }
        return false;
    }

    /**
     * 取得一個含 (西元)年月日時分秒 的 int array
     * 
     * @param date
     * @return
     */
    public static int[] getRocAttributes(Date date) {

        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int[] attr = new int[6];
        attr[0] = calendar.get(Calendar.YEAR);
        attr[1] = calendar.get(Calendar.MONTH) + 1;
        attr[2] = calendar.get(Calendar.DAY_OF_MONTH);
        attr[3] = calendar.get(Calendar.HOUR_OF_DAY);
        attr[4] = calendar.get(Calendar.MINUTE);
        attr[5] = calendar.get(Calendar.SECOND);

        return attr;
    }

    /**
     * 取得一日的最小時間
     * 
     * @param date
     * @return 該日零時
     */
    public static Date toMinTime(Date date) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 取得一日的最大時間
     * 
     * @param date
     * @return 該日 23 時 59 分 59 秒
     */
    public static Date toMaxTime(Date date) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.SECOND, -1);

        return calendar.getTime();
    }

    /**
     * 由民國年月日取得 java.util.Date 物件
     * 
     * @param yy
     * @param mm
     * @param dd
     * @return
     */
    public static Date getDate(String yy, String mm, String dd) {

        return getDate(yy, mm, dd, DATE_STYLE_DEFAULT);
    }

    /**
     * 由民國年月日取得 java.util.Date 物件
     * 
     * @param yy
     * @param mm
     * @param dd
     * @param style
     *            DATE_STYLE_FROM, DATE_STYLE_TO or DATE_STYLE_DEFAULT
     * @throws IllegalArgumentException
     *             if date yy, mm, dd invalid. For example 19xx-2-31.
     * @return
     */
    public static Date getDate(String yy, String mm, String dd, int style) {

        int year = stringToInt(yy);
        int month = stringToInt(mm);
        int day = stringToInt(dd);

        // 先西元年再說
        int y1 = year + 1911;
        int m1 = month;
        int d1 = day;

        Calendar calendar = Calendar.getInstance();
        calendar.set(y1, m1 - 1, d1);
        if ((calendar.get(Calendar.YEAR) != y1) || (calendar.get(Calendar.MONTH) != m1 - 1) || (calendar.get(Calendar.DAY_OF_MONTH) != d1))
            throw new IllegalArgumentException("Illegal date parameter");

        if (style == DATE_STYLE_FROM)
            return toMinTime(calendar.getTime());
        else if (style == DATE_STYLE_TO)
            return toMaxTime(calendar.getTime());
        else
            return calendar.getTime();

    }

    /**
     * 將民國年字串轉為 Date 物件
     * 
     * @param dateString
     *            YYY/MM/DD
     * @param style
     * @return
     */
    public static Date getDate(String dateString, int style) {

        if (!StringUtils.hasText(dateString))
            return null;

        StringTokenizer tokenizer = new StringTokenizer(dateString, DATE_FIELD_SEPARATOR);
        List<String> param = new ArrayList<String>();
        while (tokenizer.hasMoreTokens())
            param.add(tokenizer.nextToken());

        if (param.size() < 3)
            throw new IllegalArgumentException("Invalid date parameter");

        return getDate(param.get(0), param.get(1), param.get(2), style);
    }

    /**
     * 將民國年月字串轉為二個 Date 物件, 第一個是該月第一天, 第二個是該月最後一天
     * 
     * @param yymm
     * @return
     */
    public static Date[] getDateInterval(String yymm) {

        if (!StringUtils.hasText(yymm))
            return null;

        StringTokenizer tokenizer = new StringTokenizer(yymm, DATE_FIELD_SEPARATOR);
        List<String> param = new ArrayList<String>();
        while (tokenizer.hasMoreTokens())
            param.add(tokenizer.nextToken());

        if (param.size() < 2)
            throw new IllegalArgumentException("Invalid date parameter");

        Date from = getDate(param.get(0), param.get(1), "1", DATE_STYLE_FROM);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date to = toMaxTime(calendar.getTime());

        return new Date[] { from, to };
    }

    private static int stringToInt(String value) {

        if (value == null)
            throw new IllegalArgumentException("Null date parameter");

        return Integer.parseInt(value.trim());
    }

    /**
     * 取得現在系統時間，含日期、時間，以凌晨0點為基準。
     * 
     * @return 目前系統時間
     */
    public static long getToday() {

        Calendar changedDate = Calendar.getInstance();

        changedDate.set(Calendar.HOUR, 0);
        changedDate.set(Calendar.MINUTE, 0);
        changedDate.set(Calendar.SECOND, 0);
        changedDate.set(Calendar.MILLISECOND, 0);
        return changedDate.getTimeInMillis();
    }

}
