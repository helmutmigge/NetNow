package br.hm.netnow.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.hm.netnow.R;

/**
 * Created by helmutmigge on 21/04/2015.
 */
public class Utility {

    public static long getLastFetchSchedule(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        long last_fetch_schedule_default = -1;
        return preferences.getLong(context.getString(R.string.pref_last_fetch_schedule_key), last_fetch_schedule_default);
    }

    public static void setLastFetchSchedule(Context context,long lastFechtSchedule){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(context.getString(R.string.pref_last_fetch_schedule_key),lastFechtSchedule);
        editor.commit();

    }


    public static int getCityIdSetting(Context ctx) {
        return 27;

    }

    public static long getTimeServerSetting(Context context) {
        return System.currentTimeMillis();

    }

    public static boolean isLand(Context context) {
        return context.getResources().getBoolean(R.bool.isLand);
    }

    public static int convertDpToPx(Context context,int dpValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue*scale + 0.5f);
    }

    public static String formatMillisecoundsToHourMinute(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }
}
