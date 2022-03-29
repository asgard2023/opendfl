package org.ccs.opendfl.core.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DeseDateUtil {
    private static Date add(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * Adds a number of hours to a date returning a new object.
     * The original {@code Date} is unchanged.
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@code Date} with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addHours(final Date date, final int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }


    /**
     * 取时间的对应收益时间，即5点到5点，所以当前时间往前推5小时
     *
     * @param date
     * @return
     */
    public static Date getIncomeDate(Date date) {
        if (date != null) {
            date = addHours(date, -5);
        }
        return date;
    }

    public static int getTimeInt(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) * 10000 + calendar.get(Calendar.MINUTE) * 100 + calendar.get(Calendar.SECOND);
    }
}
