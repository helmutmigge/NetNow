package br.hm.netnow.data;

import android.content.ContentUris;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by helmutmigge on 21/04/2015.
 */
public class NetNowContract {

    public static final String CONTENT_AUTHORITY = "br.hm.netnow";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_CHANNEL = "channel";
    public static final String PATH_SCHEDULE = "schedule";
    public static final String PATH_SHOW = "show";
    public static final String PATH_VIEW = "view";

    public static final class CategoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();


        public static final String TABLE_NAME = "category";
        public static final String COLUMN_CATEGORY_NAME = "category_name";

        public static Uri buildCategoryUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class ChannelEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHANNEL).build();

        public static final String TABLE_NAME = "channel";
        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_CITY_ID = "city_id";
        public static final String COLUMN_CHANNEL_NAME = "channel_name";
        public static final String COLUMN_CHANNEL_ST = "channel_st";
        public static final String COLUMN_CHANNEL_NUMBER = "channel_number";
        public static final String COLUMN_CHANNEL_IMAGE = "channel_image";
        public static Uri buildChannelUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ScheduleEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHEDULE).build();

        public static final String TABLE_NAME = "schedule";
        public static final String COLUMN_SCHEDULE_END_DATE = "schedule_end_date";
        public static final String COLUMN_SCHEDULE_START_DATE = "schedule_start_date";

        public static final String COLUMN_CHANNEL_ID = "channel_id";
        public static final String COLUMN_SHOW_ID = "show_id";

        public static Uri buildScheduleUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String TABLE_WITH_SHOW = ScheduleEntry.TABLE_NAME  + " INNER JOIN "
                + ShowEntry.TABLE_NAME + " ON "+ ScheduleEntry.TABLE_NAME + "." + ScheduleEntry.COLUMN_SHOW_ID + " = " +ShowEntry.TABLE_NAME +"."+ ShowEntry._ID;
    }

    public static final class ShowEntry implements  BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHOW).build();

        public static final String TABLE_NAME = "show";

        public static final String COLUMN_SHOW_DIRECTOR = "show_director";
        public static final String COLUMN_SHOW_RATING = "show_rating";
        public static final String COLUMN_SHOW_TITLE_ST = "show_title_st";
        public static final String COLUMN_SHOW_ORIGINAL_TITLE = "show_original_title";
        public static final String COLUMN_SHOW_TITLE = "show_title";
        public static final String COLUMN_SHOW_DESCRIPTION = "show_description";
        public static final String COLUMN_SHOW_SUBGENUS = "show_subgenus";
        public static final String COLUMN_SHOW_GENRE = "show_genre";
        public static final String COLUMN_SHOW_DURATION_MINUTES = "show_duration_minutes";
        public static final String COLUMN_SHOW_CONTENT_RATING = "show_content_rating";
        public static final String COLUMN_SHOW_CAST = "show_cast";

        public static Uri buildShowUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    public static final class ScheduleDetailView implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIEW).appendPath(PATH_SCHEDULE).build();

        public static String TABLES = ScheduleEntry.TABLE_NAME + " INNER JOIN " + ChannelEntry.TABLE_NAME + " ON "
                + ScheduleEntry.TABLE_NAME+ "."+  ScheduleEntry.COLUMN_CHANNEL_ID +" = " + ChannelEntry.TABLE_NAME + "."+ ChannelEntry._ID
                + " INNER JOIN " + ShowEntry.TABLE_NAME + " ON " + ScheduleEntry.TABLE_NAME+ "."+ ScheduleEntry.COLUMN_SHOW_ID + " = "+ShowEntry.TABLE_NAME+ "."+ ShowEntry._ID;

        public static final String WHERE_WHITN_ID =
                ScheduleEntry.TABLE_NAME+"."+ScheduleEntry._ID + " = ? ";

        public static Uri buildScheduleDetailUri(int id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static int getIdFromUri(Uri uri){
            return Integer.parseInt(uri.getLastPathSegment());
        }
    }
}
