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
            ,ChannelEntry.TABLE_NAME+"."+ChannelEntry.COLUMN_CHANNEL_IMAGE
            ,ChannelEntry.TABLE_NAME+"."+ChannelEntry.COLUMN_CHANNEL_NUMBER
            ,ShowEntry.TABLE_NAME+"."+ShowEntry.COLUMN_SHOW_TITLE
            ,ShowEntry.TABLE_NAME+"."+ShowEntry.COLUMN_SHOW_ORIGINAL_TITLE
    };


    public static String getChannelName(Cursor cursor){
        return cursor.getString(3);
    }
    public static byte[] getChannelImage(Cursor cursor){
        return cursor.getBlob(4);
    }
    public static String getChannelNumber(Cursor cursor){
        return cursor.getString(5);
    }
    public static String getShowTitle(Cursor cursor){
        return cursor.getString(6);
    }
    public static String getShowOriginalTitle(Cursor cursor){
        return cursor.getString(7);
    }

}

