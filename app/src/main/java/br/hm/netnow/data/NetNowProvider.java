package br.hm.netnow.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class NetNowProvider extends ContentProvider {

    private static final int CATEGORY = 100;
    private static final int CHANNEL = 200;
    private static final int SCHEDULE = 300;
    private static final int SCHEDULE_DETAIL_WITH_ID = 350;
    private static final int SHOW = 400;


    private NetNowSQLHelper netNowSQLHelper;
    private static UriMatcher uriMatcher;

    static     {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(NetNowContract.CONTENT_AUTHORITY, NetNowContract.PATH_CATEGORY, CATEGORY);
        uriMatcher.addURI(NetNowContract.CONTENT_AUTHORITY, NetNowContract.PATH_CHANNEL, CHANNEL);
        uriMatcher.addURI(NetNowContract.CONTENT_AUTHORITY, NetNowContract.PATH_SCHEDULE, SCHEDULE);
        uriMatcher.addURI(NetNowContract.CONTENT_AUTHORITY, NetNowContract.PATH_SHOW, SHOW);
        uriMatcher.addURI(NetNowContract.CONTENT_AUTHORITY, NetNowContract.PATH_VIEW + "/" +NetNowContract.PATH_SCHEDULE + "/#", SCHEDULE_DETAIL_WITH_ID);
    }

    public NetNowProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = netNowSQLHelper.getWritableDatabase();
        int affectedRows = 0;
        final int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case -1:
                throw new UnsupportedOperationException("Not yet implemented");

            case SCHEDULE: {
                affectedRows = db.delete(NetNowContract.ScheduleEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            }
        }
        if (selection == null || affectedRows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri insertedUri = null;

        final int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = netNowSQLHelper.getWritableDatabase();
        switch (uriType) {
            case -1:
                throw new UnsupportedOperationException("Not yet implemented");
            case CATEGORY: {
                long _id = db.insertOrThrow(NetNowContract.CategoryEntry.TABLE_NAME, null, values);
                insertedUri =
                        NetNowContract.CategoryEntry.buildCategoryUri(_id);
                break;
            }
            case CHANNEL: {
                long _id = db.insertOrThrow(NetNowContract.ChannelEntry.TABLE_NAME, null, values);
                insertedUri =
                        NetNowContract.ChannelEntry.buildChannelUri(_id);
                break;
            }
            case SCHEDULE: {
                long _id = db.insertOrThrow(NetNowContract.ScheduleEntry.TABLE_NAME, null, values);
                insertedUri =
                        NetNowContract.ScheduleEntry.buildScheduleUri(_id);
                break;
            }
            case SHOW: {
                long _id = db.insertOrThrow(NetNowContract.ShowEntry.TABLE_NAME, null, values);
                insertedUri =
                        NetNowContract.ShowEntry.buildShowUri(_id);
                break;
            }
        }

        if (insertedUri != null) {
            getContext().getContentResolver().notifyChange(insertedUri, null);
        }
        return insertedUri;
    }

    @Override
    public boolean onCreate() {
        netNowSQLHelper = new NetNowSQLHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor = null;
        final int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case -1:
                throw new UnsupportedOperationException("Not yet implemented");
            case CHANNEL: {
                cursor = netNowSQLHelper.getReadableDatabase().query(
                        NetNowContract.ChannelEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case SCHEDULE: {
                cursor = netNowSQLHelper.getReadableDatabase().query(
                        NetNowContract.ScheduleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case SHOW: {
                cursor = netNowSQLHelper.getReadableDatabase().query(
                        NetNowContract.ShowEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case SCHEDULE_DETAIL_WITH_ID: {
                int id = NetNowContract.ScheduleDetailView.getIdFromUri(uri);
                String[] args = new String[]{
                        Integer.toString(id)
                };

                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                queryBuilder.setTables(NetNowContract.ScheduleDetailView.TABLES);
                cursor = queryBuilder.query(
                        netNowSQLHelper.getReadableDatabase()
                        ,projection
                        ,NetNowContract.ScheduleDetailView.WHERE_WHITN_ID
                        ,args,null,null,sortOrder);
                break;
            }


        }
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
