package org.pesc.sdk.util;

import javax.xml.bind.DatatypeConverter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by gcrim on 5/4/15.
 */
public class DateConverter {
    private static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat MONTH_DAY_FORMAT = new SimpleDateFormat("--MM-DD");

    public static Date parseDate(String s) {
        return DatatypeConverter.parseDate(s).getTime();
    }

    public static Date parseTime(String s) {
        return DatatypeConverter.parseTime(s).getTime();
    }

    public static Date parseDateTime(String s) {
        return DatatypeConverter.parseDateTime(s).getTime();
    }

    public static Date parseYearMonth(String s) {
        Date date=null;
        try {
            date = YEAR_MONTH_FORMAT.parse(s);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
        return date;
    }

    public static Date parseYear(String s) {
        Date date=null;
        try {
            date = YEAR_FORMAT.parse(s);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
        return date;
    }

    public static String parseMonthDay(String s) {
        return s;
    }

    public static String printDate(Date dt) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dt);
        return DatatypeConverter.printDate(cal);
    }

    public static String printTime(Date dt) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dt);
        return DatatypeConverter.printTime(cal);
    }

    public static String printDateTime(Date dt) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dt);
        return DatatypeConverter.printDateTime(cal);
    }

    public static String printYearMonth(Date dt) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dt);
        return YEAR_MONTH_FORMAT.format(cal.getTime());
    }

    public static String printYear(Date dt) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(dt);
        return YEAR_FORMAT.format(cal.getTime());
    }

    public static String printMonthDay(String dt) {
        return dt;
    }
}
