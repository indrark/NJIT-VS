package com.njit.buddy.app.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Indraneel 4/11/2017
 */
public class DateUtil {

    public static String toTimeString(long timestamp) {
        Date date = new Date(timestamp);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String toDateString(long timestamp) {
        Date date = new Date(timestamp);
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static boolean isToday(long timestamp) {
        return isToday(new Date(timestamp));
    }

    public static boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTime(date);
        return isSameDay(today, target);
    }

    public static boolean isSameDay(Calendar c1, Calendar c2) {
        return (c1.get(Calendar.ERA) == c2.get(Calendar.ERA) &&
                c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR));
    }

}
