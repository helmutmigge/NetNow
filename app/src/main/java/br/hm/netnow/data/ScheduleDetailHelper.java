package br.hm.netnow.data;

import android.database.Cursor;

import br.hm.netnow.data.NetNowContract.ChannelEntry;
import br.hm.netnow.data.NetNowContract.ScheduleEntry;
import br.hm.netnow.data.NetNowContract.ShowEntry;

/**
 * Created by helmutmigge on 28/04/2015.
 */
public class ScheduleDetailHelper {

    public static final String[] COLUMNS= new String[]{
            ScheduleEntry.TABLE_NAME+"."+ScheduleEntry._ID
            ,ScheduleEntry.TABLE_NAME+"."+ScheduleEntry.COLUMN_SCHEDULE_START_DATE
            ,ScheduleEntry.TABLE_NAME+"."+ScheduleEntry.COLUMN_SCHEDULE_END_DATE
            ,ChannelEntry.TABLE_NAME+"."+ChannelEntry.COLUMN_CHANNEL_NAME
            ,ShowEntry.TABLE_NAME+"."+ShowEntry.COLUMN_SHOW_TITLE
            ,ShowEntry.TABLE_NAME+"."+ShowEntry.COLUMN_SHOW_ORIGINAL_TITLE
            ,ShowEntry.TABLE_NAME+"."+ShowEntry.COLUMN_SHOW_CONTENT_RATING
            ,ShowEntry.TABLE_NAME+"."+ShowEntry.COLUMN_SHOW_GENRE
            ,ShowEntry.TABLE_NAME+"."+ShowEntry.COLUMN_SHOW_DURATION_MINUTES
            ,ShowEntry.TABLE_NAME+"."+ShowEntry.COLUMN_SHOW_SUBGENUS
            ,ShowEntry.TABLE_NAME+"."+ShowEntry.COLUMN_SHOW_RATING
            ,ShowEntry.TABLE_NAME+"."+ShowEntry.COLUMN_SHOW_DESCRIPTION
            ,ShowEntry.TABLE_NAME+"."+ShowEntry.COLUMN_SHOW_DIRECTOR
            ,ShowEntry.TABLE_NAME+"."+ShowEntry.COLUMN_SHOW_CAST
    };

    public static long getScheduleStartDate(Cursor cursor){ return cursor.getLong(1); }
    public static String getChannelName(Cursor cursor){
        return cursor.getString(3);
    }
    public static String getShowTitle(Cursor cursor){
        return cursor.getString(4);
    }
    public static String getShowOriginalTitle(Cursor cursor){
        return cursor.getString(5);
    }
    public static int getShowContentRating(Cursor cursor){
        return cursor.getInt(6);
    }
    public static String getShowGenre(Cursor cursor){
        return cursor.getString(7);
    }
    public static int getShowDurationMinutes(Cursor cursor){
        return cursor.getInt(8);
    }
    public static String getShowSubgenus(Cursor cursor){
        return cursor.getString(9);
    }
    public static int getShowRating(Cursor cursor){
        return cursor.getInt(10);
    }
    public static String getShowDescription(Cursor cursor){
        return cursor.getString(11);
    }
    public static String getShowDirector(Cursor cursor){
        return cursor.getString(12);
    }
    public static String getShowCast(Cursor cursor){
        return cursor.getString(13);
    }

}

