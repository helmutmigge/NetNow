package br.hm.netnow.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import br.hm.netnow.R;
import br.hm.netnow.data.ChannelHelper;
import br.hm.netnow.data.NetNowContract;
import br.hm.netnow.data.ShowHelper;
import br.hm.netnow.sync.commons.JSONCallback;
import br.hm.netnow.sync.remote.RemoteChannel;
import br.hm.netnow.sync.remote.RemoteSchedule;
import br.hm.netnow.sync.remote.RemoteShow;
import br.hm.netnow.utils.Moment;
import br.hm.netnow.utils.Utility;

/**
 * Created by helmutmigge on 01/05/2015.
 */
public class NetnowSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final int UPDATE_NEXT_HOUR = 6;

    private static final int SYNC_INTERVAL = 60 * 60 * UPDATE_NEXT_HOUR;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL/UPDATE_NEXT_HOUR;

    public NetnowSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        final int cityId = Utility.getCityIdSetting(getContext());
        final long currentTimeMillis = Utility.getTimeServerSetting(getContext());

        final int maxChannel = 300;
        final int maxSchedule = 10000;
        final Moment moment = Moment.instanceMoment(currentTimeMillis, 6, Moment.HOUR);

        final RemoteChannel remoteChannel = new RemoteChannel();
        final RemoteSchedule remoteSchedule = new RemoteSchedule();

        JSONCallback channelCallback = new ChannelCallBack(remoteChannel);
        ScheduleCallBack scheduleCallBack = new ScheduleCallBack();

        try {

            remoteChannel.query(cityId, maxChannel, channelCallback);

            remoteSchedule.query(cityId, maxSchedule, moment, scheduleCallBack);
            scheduleCallBack.flush();
            cleanScheduleBeforeDate(moment.getStartMilliseconds());
        } catch (Exception e) {
            Log.e(NetnowSyncAdapter.class.getName(), e.getMessage(), e);
        }


    }

    private void cleanScheduleBeforeDate(long millisecounds) {
        String where = NetNowContract.ScheduleEntry.COLUMN_SCHEDULE_END_DATE + " < ?";
        getContext().getContentResolver().delete(NetNowContract.ScheduleEntry.CONTENT_URI
                , where
                , new String[]{
                Long.toString(millisecounds)});
    }

    private class ShowCallBack implements JSONCallback {
        public ContentValues contentValues;

        @Override
        public void processRow(Object json, int rowNum) throws JSONException {
            JSONObject jsonObject = (JSONObject) json;
            String showDirector = null;
            if (jsonObject.has(RemoteShow.FIELD_DIRECTOR)) {
                showDirector = jsonObject.getString(RemoteShow.FIELD_DIRECTOR);
            }

            String showCast = null;
            if (jsonObject.has(RemoteShow.FIELD_CAST)) {
                showCast = jsonObject.getString(RemoteShow.FIELD_CAST);
            }
            Integer showRating = null;
            if (jsonObject.has(RemoteShow.FIELD_RATING)) {
                showRating = jsonObject.getInt(RemoteShow.FIELD_RATING);
            }

            String showOriginalTitle =null;
            if (jsonObject.has(RemoteShow.FIELD_ORIGINAL_TITLE)) {
                showOriginalTitle = jsonObject.getString(RemoteShow.FIELD_ORIGINAL_TITLE);
            }

            String showTitleSt = jsonObject.getString(RemoteShow.FIELD_TITLE_ST);
            String showTitle = jsonObject.getString(RemoteShow.FIELD_TITLE);
            String showDescription = jsonObject.getString(RemoteShow.FIELD_DESCRIPTION);
            String showSubgenus = jsonObject.getString(RemoteShow.FIELD_SUBGENUS);
            String showGenre = jsonObject.getString(RemoteShow.FIELD_GENRE);
            int showDurationMinutes = jsonObject.getInt(RemoteShow.FIELD_DURATION_MINUTES);
            int showId = jsonObject.getInt(RemoteShow.FIELD_SHOW_ID);

            int showContentRating = jsonObject.getInt(RemoteShow.FIELD_CONTENT_RATING);

            contentValues = new ContentValues();
            contentValues.put(NetNowContract.ShowEntry._ID, showId);
            contentValues.put(NetNowContract.ShowEntry.COLUMN_SHOW_CAST, showCast);
            contentValues.put(NetNowContract.ShowEntry.COLUMN_SHOW_CONTENT_RATING, showContentRating);
            contentValues.put(NetNowContract.ShowEntry.COLUMN_SHOW_DESCRIPTION, showDescription);
            contentValues.put(NetNowContract.ShowEntry.COLUMN_SHOW_DIRECTOR, showDirector);
            contentValues.put(NetNowContract.ShowEntry.COLUMN_SHOW_DURATION_MINUTES, showDurationMinutes);
            contentValues.put(NetNowContract.ShowEntry.COLUMN_SHOW_GENRE, showGenre);
            contentValues.put(NetNowContract.ShowEntry.COLUMN_SHOW_TITLE, showTitle);
            contentValues.put(NetNowContract.ShowEntry.COLUMN_SHOW_TITLE_ST, showTitleSt);
            contentValues.put(NetNowContract.ShowEntry.COLUMN_SHOW_RATING, showRating);
            contentValues.put(NetNowContract.ShowEntry.COLUMN_SHOW_ORIGINAL_TITLE, showOriginalTitle);
            contentValues.put(NetNowContract.ShowEntry.COLUMN_SHOW_SUBGENUS, showSubgenus);
        }
    }

    private class ScheduleCallBack implements JSONCallback {
        private List<ContentValues> scheduleBuffer = new ArrayList<ContentValues>();
        private List<ContentValues> showBuffer = new ArrayList<ContentValues>();
        private ShowCallBack showCallBack = new ShowCallBack();
        private RemoteShow remoteShow = new RemoteShow();
        @Override
        public void processRow(Object json, int rowNum) throws JSONException {
            JSONObject jsonObject = (JSONObject) json;
            try {
                long scheduleEndDate = Moment.formatDateISOHourMinuteAsLong(jsonObject.getString(RemoteSchedule.FIELD_END_DATE));
                long scheduleStartDate = Moment.formatDateISOHourMinuteAsLong(jsonObject.getString(RemoteSchedule.FIELD_START_DATE));
//                int cityId = jsonObject.getInt(RemoteSchedule.FIELD_CITY_ID);
//                String scheduleSt = jsonObject.getString(RemoteSchedule.FIELD_TITLE_ST);
//                String scheduleTitle = jsonObject.getString(RemoteSchedule.FIELD_TITLE);
//                String scheduleGenre = jsonObject.getString(RemoteSchedule.FIELD_GENRE);
                int scheduleId = jsonObject.getInt(RemoteSchedule.FIELD_SCHEDULE_ID);
                int showId = jsonObject.getInt(RemoteSchedule.FIELD_SHOW_ID);
                int channelId = jsonObject.getInt(RemoteSchedule.FIELD_CHANNEL_ID);

                ContentValues contentValues = new ContentValues();
                contentValues.put(NetNowContract.ScheduleEntry._ID, scheduleId);
                contentValues.put(NetNowContract.ScheduleEntry.COLUMN_CHANNEL_ID, channelId);

                contentValues.put(NetNowContract.ScheduleEntry.COLUMN_SCHEDULE_END_DATE, scheduleEndDate);
                contentValues.put(NetNowContract.ScheduleEntry.COLUMN_SCHEDULE_START_DATE, scheduleStartDate);
                contentValues.put(NetNowContract.ScheduleEntry.COLUMN_SHOW_ID, showId);
                //contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_ST, scheduleSt);
                //contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_TITLE, scheduleTitle);
                //contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_GENRE, scheduleGenre);
                //contentValues.put(ScheduleEntry.COLUMN_CITY_ID, cityId);

                if (ShowHelper.isExist(getContext(), showId)) {
                    addSchedule(contentValues);
                } else {
                    //scheduleBuffer.add(contentValues);
                    remoteShow.query(showId, showCallBack);
                    //showBuffer.add(showCallBack.contentValues);
                    addShow(showCallBack.contentValues);
                    addSchedule(contentValues);
                }
            } catch (IOException e) {
                Log.e(NetnowSyncAdapter.class.getName(), e.getMessage(), e);
            } catch (ParseException e) {
                Log.e(NetnowSyncAdapter.class.getName(), e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        public void addSchedule(ContentValues values) {
            getContext().getContentResolver().insert(NetNowContract.ScheduleEntry.CONTENT_URI, values);
        }

        public void addSchedule(ContentValues[] values) {
            getContext().getContentResolver().bulkInsert(NetNowContract.ScheduleEntry.CONTENT_URI, values);
        }

        public void addShow(ContentValues values) {
            getContext().getContentResolver().insert(NetNowContract.ShowEntry.CONTENT_URI, values);
        }


        public void addShow(ContentValues[] values) {
            getContext().getContentResolver().bulkInsert(NetNowContract.ShowEntry.CONTENT_URI, values);
        }

        public void flush() {
            if (!showBuffer.isEmpty()) {
                ContentValues[] contentValueses = new ContentValues[showBuffer.size()];
                showBuffer.toArray(contentValueses);
                addShow(contentValueses);
                showBuffer = null;
            }

            if (!scheduleBuffer.isEmpty()) {
                ContentValues[] contentValueses = new ContentValues[scheduleBuffer.size()];
                scheduleBuffer.toArray(contentValueses);
                addSchedule(contentValueses);
                scheduleBuffer = null;
            }
        }


    }


    private class ChannelCallBack implements JSONCallback {

        private final RemoteChannel  remoteChannel;

        public ChannelCallBack(RemoteChannel remoteChannel){
            this.remoteChannel = remoteChannel;
        }

        @Override
        public void processRow(Object json, int rowNum) throws JSONException {
            JSONObject jsonObject = (JSONObject) json;

            int channelId = jsonObject.getInt(RemoteChannel.FIELD_CHANNEL_ID);
            if (!ChannelHelper.isExist(getContext(), channelId)) {
                //Category
                final long categoryId = jsonObject.getLong(RemoteChannel.FIELD_CATOGORY_ID);
                final String categoryName = jsonObject.getString(RemoteChannel.FIELD_CATOGORY_NAME);
                addCategory(categoryId, categoryName);


                //Channel
                String channelSt = jsonObject.getString(RemoteChannel.FIELD_CHANNEL_ST);
                int cityId = jsonObject.getInt(RemoteChannel.FIELD_CITY_ID);
                int channelNumber = jsonObject.getInt(RemoteChannel.FIELD_CHANNEL_NUMBER);
                String channelName = jsonObject.getString(RemoteChannel.FIELD_CHANNEL_NAME);
                byte[] image = remoteChannel.image(channelSt, Long.toString(channelId));

                ContentValues contentValues = new ContentValues();
                contentValues.put(NetNowContract.ChannelEntry.COLUMN_CHANNEL_ST, channelSt);
                contentValues.put(NetNowContract.ChannelEntry._ID, channelId);
                contentValues.put(NetNowContract.ChannelEntry.COLUMN_CITY_ID, cityId);
                contentValues.put(NetNowContract.ChannelEntry.COLUMN_CHANNEL_NUMBER, channelNumber);
                contentValues.put(NetNowContract.ChannelEntry.COLUMN_CHANNEL_NAME, channelName);
                contentValues.put(NetNowContract.ChannelEntry.COLUMN_CHANNEL_IMAGE, image);
                addChannel(contentValues);
            }
        }

        public void addChannel(ContentValues values) {
            getContext().getContentResolver().insert(NetNowContract.ChannelEntry.CONTENT_URI, values);
        }

        public void addCategory(final long categoryId, final String categoryName) {
            ContentValues values = new ContentValues();

            values.put(NetNowContract.CategoryEntry._ID, categoryId);
            values.put(NetNowContract.CategoryEntry.COLUMN_CATEGORY_NAME, categoryName);

            getContext().getContentResolver().insert(NetNowContract.CategoryEntry.CONTENT_URI, values);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }




    private static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        NetnowSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }


}
