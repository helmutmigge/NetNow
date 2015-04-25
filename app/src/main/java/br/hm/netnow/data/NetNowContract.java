package br.hm.netnow.data;

import android.content.ContentUris;
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
        public static final String COLUMN_CITY_ID = "city_id";
        public static final String COLUMN_SCHEDULE_ST = "schedule_st";
        public static final String COLUMN_SCHEDULE_TITLE = "schedule_title";
        public static final String COLUMN_SCHEDULE_GENRE = "schedule_genre";
        public static final String COLUMN_CHANNEL_ID = "channel_id";
        public static final String COLUMN_SHOW_ID = "show_id";

        public static Uri buildScheduleUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
