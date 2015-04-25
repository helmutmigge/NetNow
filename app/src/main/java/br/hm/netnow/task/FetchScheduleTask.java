package br.hm.netnow.task;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import br.hm.netnow.commons.JSONCallback;
import br.hm.netnow.data.NetNowContract.CategoryEntry;
import br.hm.netnow.data.NetNowContract.ChannelEntry;
import br.hm.netnow.data.NetNowContract.ScheduleEntry;
import br.hm.netnow.remote.RemoteChannel;
import br.hm.netnow.remote.RemoteSchedule;
import br.hm.netnow.utils.Moment;

/**
 * Created by helmutmigge on 22/04/2015.
 */
public class FetchScheduleTask extends AsyncTask<String, Void, Void> {

    private final Context context;
    private final RemoteChannel remoteChannel;
    private final RemoteSchedule remoteSchedule;

    public FetchScheduleTask(Context context) {
        this.context = context;
        this.remoteChannel = new RemoteChannel();
        this.remoteSchedule = new RemoteSchedule();
    }

    @Override
    protected Void doInBackground(String... params) {
        final int idCity = Integer.parseInt(params[0]);
        final long currentTimeMillis = Long.parseLong(params[1]);
        final int maxChannel = 300;
        final int maxSchedule = 10000;
        final Moment moment = Moment.instanceMoment(currentTimeMillis, 6, Moment.HOUR);

        JSONCallback channelCallback = new ChannelCallBack();
        JSONCallback scheduleCallBack = new ScheduleCallBack();

        try {
            cleanScheduleBeforeDate(moment.getStartMilliseconds());

            remoteChannel.query(idCity, maxChannel, channelCallback);

            remoteSchedule.query(idCity, maxSchedule, moment, scheduleCallBack);
        } catch (Exception e) {
            Log.e(FetchScheduleTask.class.getName(), e.getMessage(), e);
            throw new RuntimeException(e);
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

    private class ScheduleCallBack implements JSONCallback<Void> {

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
                contentValues.put(ScheduleEntry.COLUMN_CITY_ID, cityId);
                contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_END_DATE, scheduleEndDate);
                contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_START_DATE, scheduleStartDate);
                contentValues.put(ScheduleEntry.COLUMN_SHOW_ID, showId);
                contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_ST, scheduleSt);
                contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_TITLE, scheduleTitle);
                contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_GENRE, scheduleGenre);

                addSchedule(contentValues);
            } catch (ParseException e) {
                Log.e(FetchScheduleTask.class.getName(), e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        public void addSchedule(ContentValues values) {
            FetchScheduleTask.this.context.getContentResolver().insert(ScheduleEntry.CONTENT_URI, values);
        }

    }


    private class ChannelCallBack implements JSONCallback<Void> {
        @Override
        public void processRow(Object json, int rowNum) throws JSONException {
            JSONObject jsonObject = (JSONObject) json;

            //Category
            final long categoryId = jsonObject.getLong(RemoteChannel.FIELD_CATOGORY_ID);
            final String categoryName = jsonObject.getString(RemoteChannel.FIELD_CATOGORY_NAME);
            addCategory(categoryId, categoryName);


            //Channel
            String channelSt = jsonObject.getString(RemoteChannel.FIELD_CHANNEL_ST);
            long channelId = jsonObject.getLong(RemoteChannel.FIELD_CHANNEL_ID);
            byte[] image = FetchScheduleTask.this.remoteChannel.image(channelSt, Long.toString(channelId));

            ContentValues contentValues = new ContentValues();
            contentValues.put(ChannelEntry.COLUMN_CHANNEL_ST, channelSt);
            contentValues.put(ChannelEntry._ID, channelId);
            contentValues.put(ChannelEntry.COLUMN_CITY_ID, jsonObject.getLong(RemoteChannel.FIELD_CITY_ID));
            contentValues.put(ChannelEntry.COLUMN_CHANNEL_NUMBER, jsonObject.getInt(RemoteChannel.FIELD_CHANNEL_NUMBER));
            contentValues.put(ChannelEntry.COLUMN_CHANNEL_NAME, jsonObject.getString(RemoteChannel.FIELD_CHANNEL_NAME));
            contentValues.put(ChannelEntry.COLUMN_CHANNEL_IMAGE, image);
            addChannel(contentValues);
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
