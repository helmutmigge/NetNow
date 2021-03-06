package br.hm.netnow.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.hm.netnow.data.NetNowContract.ChannelEntry;
import br.hm.netnow.data.NetNowContract.CategoryEntry;
import br.hm.netnow.data.NetNowContract.ScheduleEntry;
/**
 * Created by helmutmigge on 21/04/2015.
 */
public class NetNowSQLHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "dbNetNow";
    public static final int DATABASE_VERSION = 1;

    public NetNowSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createCategoryTable(db);
        createChannelTable(db);
        createScheduleTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createCategoryTable(SQLiteDatabase db){
        final String SQL_CREATE_CATEGORY_TABLE =
                "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                        CategoryEntry._ID + " INTEGER," +
                        CategoryEntry.COLUMN_CATEGORY_NAME + " TEXT NOT NULL," +
                        "PRIMARY KEY (" + CategoryEntry._ID + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
    }

    public void createChannelTable(SQLiteDatabase db){
        final String SQL_CREATE_CHANNEL_TABLE =
                "CREATE TABLE " + ChannelEntry.TABLE_NAME + " (" +
                        ChannelEntry._ID + " INTEGER," +
                        ChannelEntry.COLUMN_CITY_ID + " INTEGER NOT NULL," +
                        ChannelEntry.COLUMN_CATEGORY_ID + " INTEGER NULL," +
                        ChannelEntry.COLUMN_CHANNEL_ST + " TEXT NOT NULL," +
                        ChannelEntry.COLUMN_CHANNEL_IMAGE + " BLOB NULL," +
                        ChannelEntry.COLUMN_CHANNEL_NUMBER + " INTEGER NOT NULL," +
                        ChannelEntry.COLUMN_CHANNEL_NAME + " TEXT NOT NULL,"+

                        //Configura o chave estrangeira para categoria
                        " FOREIGN KEY (" + ChannelEntry.COLUMN_CATEGORY_ID + ") REFERENCES " +
                        CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + "), " +

                        "PRIMARY KEY (" + ChannelEntry._ID + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_CHANNEL_TABLE);
    }

    public void createScheduleTable(SQLiteDatabase db){
        final String SQL_CREATE_SHEDULE_TABLE =
                "CREATE TABLE " + ScheduleEntry.TABLE_NAME + " (" +
                        ScheduleEntry._ID + " INTEGER," +
                        ScheduleEntry.COLUMN_CITY_ID + " INTEGER NOT NULL," +
                        ScheduleEntry.COLUMN_CHANNEL_ID + " INTEGER NOT NULL," +
                        ScheduleEntry.COLUMN_SCHEDULE_END_DATE + " INTEGER NOT NULL," +
                        ScheduleEntry.COLUMN_SCHEDULE_START_DATE + " INTEGER NOT NULL," +
                        ScheduleEntry.COLUMN_SCHEDULE_ST + " TEXT NOT NULL," +
                        ScheduleEntry.COLUMN_SCHEDULE_GENRE + " TEXT NOT NULL,"+
                        ScheduleEntry.COLUMN_SCHEDULE_TITLE + " TEXT NOT NULL," +
                        ScheduleEntry.COLUMN_SHOW_ID + " INTEGER NOT NULL," +

                        //Configura o chave estrangeira para canal
                        " FOREIGN KEY (" + ScheduleEntry.COLUMN_CHANNEL_ID + ") REFERENCES " +
                        ChannelEntry.TABLE_NAME + " (" + ChannelEntry._ID + "), " +

                        "PRIMARY KEY (" + ScheduleEntry._ID + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_SHEDULE_TABLE);
    }
}


