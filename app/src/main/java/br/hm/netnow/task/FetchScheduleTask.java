package br.hm.netnow.task;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import br.hm.netnow.commons.JSONCallback;
import br.hm.netnow.data.ChannelHelper;
import br.hm.netnow.data.NetNowContract.CategoryEntry;
import br.hm.netnow.data.NetNowContract.ChannelEntry;
import br.hm.netnow.data.NetNowContract.ScheduleEntry;
import br.hm.netnow.data.NetNowContract.ShowEntry;
import br.hm.netnow.data.ShowHelper;
import br.hm.netnow.remote.RemoteChannel;
import br.hm.netnow.remote.RemoteSchedule;
import br.hm.netnow.remote.RemoteShow;
import br.hm.netnow.utils.Moment;
import br.hm.netnow.utils.Utility;

/**
 * Checa guia de programação
 * Created by helmutmigge on 22/04/2015.
 */
public class FetchScheduleTask extends AsyncTask<String, Void, Void> {

    private final Context context;
    private final RemoteChannel remoteChannel;
    private final RemoteSchedule remoteSchedule;
    private final RemoteShow remoteShow;

    public FetchScheduleTask(Context context) {
        this.context = context;
        this.remoteChannel = new RemoteChannel();
        this.remoteSchedule = new RemoteSchedule();
        this.remoteShow = new RemoteShow();
    }

    @Override
    protected Void doInBackground(String... params) {
        final int idCity = Integer.parseInt(params[0]);
        final long currentTimeMillis = Long.parseLong(params[1]);
        final int maxChannel = 300;
        final int maxSchedule = 10000;
        final Moment moment = Moment.instanceMoment(currentTimeMillis, 6, Moment.HOUR);

        JSONCallback channelCallback = new ChannelCallBack();
        ScheduleCallBack scheduleCallBack = new ScheduleCallBack();

        try {

            remoteChannel.query(idCity, maxChannel, channelCallback);

            remoteSchedule.query(idCity, maxSchedule, moment, scheduleCallBack);
            scheduleCallBack.flush();
            cleanScheduleBeforeDate(moment.getStartMilliseconds());
            Utility.setLastFetchSchedule(context, moment.getEndMilliseconds());
        } catch (Exception e) {
            Log.e(FetchScheduleTask.class.getName(), e.getMessage(), e);
        }

        return null;
    }

    private void cleanScheduleBeforeDate(long millisecounds) {
        String where = ScheduleEntry.COLUMN_SCHEDULE_END_DATE + " < ?";
        context.getContentResolver().delete(ScheduleEntry.CONTENT_URI
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
            contentValues.put(ShowEntry._ID, showId);
            contentValues.put(ShowEntry.COLUMN_SHOW_CAST, showCast);
            contentValues.put(ShowEntry.COLUMN_SHOW_CONTENT_RATING, showContentRating);
            contentValues.put(ShowEntry.COLUMN_SHOW_DESCRIPTION, showDescription);
            contentValues.put(ShowEntry.COLUMN_SHOW_DIRECTOR, showDirector);
            contentValues.put(ShowEntry.COLUMN_SHOW_DURATION_MINUTES, showDurationMinutes);
            contentValues.put(ShowEntry.COLUMN_SHOW_GENRE, showGenre);
            contentValues.put(ShowEntry.COLUMN_SHOW_TITLE, showTitle);
            contentValues.put(ShowEntry.COLUMN_SHOW_TITLE_ST, showTitleSt);
            contentValues.put(ShowEntry.COLUMN_SHOW_RATING, showRating);
            contentValues.put(ShowEntry.COLUMN_SHOW_ORIGINAL_TITLE, showOriginalTitle);
            contentValues.put(ShowEntry.COLUMN_SHOW_SUBGENUS, showSubgenus);
        }
    }

    private class ScheduleCallBack implements JSONCallback {
        private List<ContentValues> scheduleBuffer = new ArrayList<ContentValues>();
        private List<ContentValues> showBuffer = new ArrayList<ContentValues>();
        private ShowCallBack showCallBack = new ShowCallBack();

        @Override
        public void processRow(Object json, int rowNum) throws JSONException {
            JSONObject jsonObject = (JSONObject) json;
            try {
                long scheduleEndDate = Moment.formatDateISOHourMinuteAsLong(jsonObject.getString(RemoteSchedule.FIELD_END_DATE));
                long scheduleStartDate = Moment.formatDateISOHourMinuteAsLong(jsonObject.getString(RemoteSchedule.FIELD_START_DATE));
                int cityId = jsonObject.getInt(RemoteSchedule.FIELD_CITY_ID);
                String scheduleSt = jsonObject.getString(RemoteSchedule.FIELD_TITLE_ST);
                String scheduleTitle = jsonObject.getString(RemoteSchedule.FIELD_TITLE);
                String scheduleGenre = jsonObject.getString(RemoteSchedule.FIELD_GENRE);
                int scheduleId = jsonObject.getInt(RemoteSchedule.FIELD_SCHEDULE_ID);
                int showId = jsonObject.getInt(RemoteSchedule.FIELD_SHOW_ID);
                int channelId = jsonObject.getInt(RemoteSchedule.FIELD_CHANNEL_ID);

                ContentValues contentValues = new ContentValues();
                contentValues.put(ScheduleEntry._ID, scheduleId);
                contentValues.put(ScheduleEntry.COLUMN_CHANNEL_ID, channelId);

                contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_END_DATE, scheduleEndDate);
                contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_START_DATE, scheduleStartDate);
                contentValues.put(ScheduleEntry.COLUMN_SHOW_ID, showId);
                //contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_ST, scheduleSt);
                //contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_TITLE, scheduleTitle);
                //contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_GENRE, scheduleGenre);
                //contentValues.put(ScheduleEntry.COLUMN_CITY_ID, cityId);

                if (ShowHelper.isExist(context, showId)) {
                    addSchedule(contentValues);
                } else {
                    //scheduleBuffer.add(contentValues);
                    remoteShow.query(showId, showCallBack);
                    //showBuffer.add(showCallBack.contentValues);
                    addShow(showCallBack.contentValues);
                    addSchedule(contentValues);
                }
            } catch (IOException e) {
                Log.e(FetchScheduleTask.class.getName(), e.getMessage(), e);
            } catch (ParseException e) {
                Log.e(FetchScheduleTask.class.getName(), e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        public void addSchedule(ContentValues values) {
            FetchScheduleTask.this.context.getContentResolver().insert(ScheduleEntry.CONTENT_URI, values);
        }

        public void addSchedule(ContentValues[] values) {
            FetchScheduleTask.this.context.getContentResolver().bulkInsert(ScheduleEntry.CONTENT_URI, values);
        }

        public void addShow(ContentValues values) {
            FetchScheduleTask.this.context.getContentResolver().insert(ShowEntry.CONTENT_URI, values);
        }


        public void addShow(ContentValues[] values) {
            FetchScheduleTask.this.context.getContentResolver().bulkInsert(ShowEntry.CONTENT_URI, values);
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
        @Override
        public void processRow(Object json, int rowNum) throws JSONException {
            JSONObject jsonObject = (JSONObject) json;

            int channelId = jsonObject.getInt(RemoteChannel.FIELD_CHANNEL_ID);
            if (!ChannelHelper.isExist(context, channelId)) {
                //Category
                final long categoryId = jsonObject.getLong(RemoteChannel.FIELD_CATOGORY_ID);
                final String categoryName = jsonObject.getString(RemoteChannel.FIELD_CATOGORY_NAME);
                addCategory(categoryId, categoryName);


                //Channel
                String channelSt = jsonObject.getString(RemoteChannel.FIELD_CHANNEL_ST);
                int cityId = jsonObject.getInt(RemoteChannel.FIELD_CITY_ID);
                int channelNumber = jsonObject.getInt(RemoteChannel.FIELD_CHANNEL_NUMBER);
                String channelName = jsonObject.getString(RemoteChannel.FIELD_CHANNEL_NAME);
                byte[] image = FetchScheduleTask.this.remoteChannel.image(channelSt, Long.toString(channelId));

                ContentValues contentValues = new ContentValues();
                contentValues.put(ChannelEntry.COLUMN_CHANNEL_ST, channelSt);
                contentValues.put(ChannelEntry._ID, channelId);
                contentValues.put(ChannelEntry.COLUMN_CITY_ID, cityId);
                contentValues.put(ChannelEntry.COLUMN_CHANNEL_NUMBER, channelNumber);
                contentValues.put(ChannelEntry.COLUMN_CHANNEL_NAME, channelName);
                contentValues.put(ChannelEntry.COLUMN_CHANNEL_IMAGE, image);
                addChannel(contentValues);
            }
        }

        public void addChannel(ContentValues values) {
            FetchScheduleTask.this.context.getContentResolver().insert(ChannelEntry.CONTENT_URI, values);
        }

        public void addCategory(final long categoryId, final String categoryName) {
            ContentValues values = new ContentValues();

            values.put(CategoryEntry._ID, categoryId);
            values.put(CategoryEntry.COLUMN_CATEGORY_NAME, categoryName);

            FetchScheduleTask.this.context.getContentResolver().insert(CategoryEntry.CONTENT_URI, values);
        }
    }


}
