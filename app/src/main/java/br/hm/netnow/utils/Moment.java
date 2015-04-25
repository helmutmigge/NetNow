package br.hm.netnow.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by helmutmigge on 23/04/2015.
 */
public class Moment {

    private final static DateFormat dateFormat;
    private final static DateFormat dateFormatHourMinute;

    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormatHourMinute = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    }

    private final long startMilliseconds;
    private final long endMilliseconds;

    public static final int HOUR = Calendar.HOUR;

    private Moment(long startMilliseconds, long endMilliseconds) {
        this.startMilliseconds = startMilliseconds;
        this.endMilliseconds = endMilliseconds;
    }

    public long getStartMilliseconds() {
        return startMilliseconds;
    }

    public long getEndMilliseconds() {
        return endMilliseconds;
    }


    public String formatStartAsISO() {
        return formatDateLongAsISO(startMilliseconds);
    }

    public String formatEndAsISO() {
        return formatDateLongAsISO(endMilliseconds);
    }


    public static String formatDateLongAsISO(long milliseconds) {
        return dateFormat.format(new Date(milliseconds));
    }

    public static long formatDateISOHourMinuteAsLong(String iso)
            throws ParseException {
        return dateFormatHourMinute.parse(iso).getTime();
    }

    public static Moment instanceMoment(long milliseconds, int range, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        calendar.add(field, range);
        return new Moment(milliseconds, calendar.getTimeInMillis());
    }
}
