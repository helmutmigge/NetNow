package br.hm.netnow.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.hm.netnow.R;
import br.hm.netnow.data.NetNowContract;

/**
 * Created by helmutmigge on 21/04/2015.
 */
public class Utility {

    public static long getScheduleRememberDate(long scheduleStartDate){
        //Avisar com 10 minutos de antecedencia
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(scheduleStartDate);
        c.add(Calendar.MINUTE, -10);
        return c.getTimeInMillis();

    }

    public static void registryAlarmNotifyRemember(Context context, int scheduleId, long scheduleRememberDate) {
        PendingIntent pendingIntent = createPendingIntentNotifyRemember(context, scheduleId);
        //test

        scheduleRememberDate = getTimeServerSetting(context) + (5000);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, scheduleRememberDate, pendingIntent);


    }

    public static void unregistryAlarmNotifyRemember(Context context, int scheduleId) {
        PendingIntent pendingIntent = createPendingIntentNotifyRemember(context, scheduleId);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private static PendingIntent createPendingIntentNotifyRemember(Context context, int scheduleId) {
        Intent intent = new Intent("br.hm.netnow.REMEMBER_ACTION");
        intent.putExtra(NetNowContract.ScheduleEntry._ID,scheduleId);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


    public static int getCityIdSetting(Context context) {
        return 27;

    }

    public static long getTimeServerSetting(Context context) {
        return System.currentTimeMillis();

    }

    public static boolean isLand(Context context) {
        return context.getResources().getBoolean(R.bool.isLand);
    }

    public static int convertDpToPx(Context context, int dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String formatMillisecoundsToHourMinute(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }
}
