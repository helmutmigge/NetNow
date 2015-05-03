package br.hm.netnow;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.hm.netnow.data.NetNowContract;
import br.hm.netnow.data.NetNowContract.ChannelEntry;
import br.hm.netnow.data.NetNowContract.ScheduleDetailView;
import br.hm.netnow.data.NetNowContract.ScheduleEntry;
import br.hm.netnow.data.NetNowContract.ShowEntry;
import br.hm.netnow.data.ScheduleDetailHelper;
import br.hm.netnow.utils.Utility;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RememberService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_REMEMBER_NOTIFICATION = "br.hm.netnow.action.REMEMBER_NOTIFICATION_ACTION";
    private static final String ACTION_REMEMBER_ALARM = "br.hm.netnow.action.REMEMBER_ALARM_ACTION";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionRememberNotification(Context context, int scheduleId) {
        Intent intent = new Intent(context, RememberService.class);
        intent.setAction(ACTION_REMEMBER_NOTIFICATION);
        intent.putExtra(ScheduleEntry._ID, scheduleId);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionRememberAlarm(Context context) {
        Intent intent = new Intent(context, RememberService.class);
        intent.setAction(ACTION_REMEMBER_ALARM);
        context.startService(intent);
    }

    public RememberService() {
        super("NotificationRememberService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REMEMBER_NOTIFICATION.equals(action)) {
                final int scheduleId = intent.getIntExtra(ScheduleEntry._ID, -1);

                handleActionRememberNotification(scheduleId);
            } else if (ACTION_REMEMBER_ALARM.equals(action)) {
                handleActionRememberAlarm();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRememberNotification(int scheduleId) {
        String scheduleTitle = null;
        String[] scheduleMessage = null;
        {
            String channelName = null;
            long scheduleStartDate = 0;
            String[] projection = new String[]{
                    ShowEntry.COLUMN_SHOW_TITLE
                    , ScheduleEntry.COLUMN_SCHEDULE_START_DATE
                    , ChannelEntry.COLUMN_CHANNEL_NAME
            };

            final int COLUMN_SHOW_TITLE_INDEX = 0;
            final int COLUMN_SCHEDULE_STAR_INDEX = 1;
            final int COLUMN_CHANNEL_INDEX = 2;

            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(ScheduleDetailView.buildScheduleDetailUri(scheduleId), projection, null, null, null);
                if (cursor.moveToNext()) {
                    scheduleTitle = cursor.getString(COLUMN_SHOW_TITLE_INDEX);
                    channelName = cursor.getString(COLUMN_CHANNEL_INDEX);
                    scheduleStartDate = cursor.getLong(COLUMN_SCHEDULE_STAR_INDEX);

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            SimpleDateFormat format = new SimpleDateFormat(getString(R.string.schedule_detal_start_date_format));
            Date startDate = new Date(scheduleStartDate);
            String dateFormatStr = format.format(startDate);

            scheduleMessage = new String[]{
                    dateFormatStr
                    ,channelName};

        }

        if (scheduleTitle != null) {
            Intent intentScheduleDetailActivity = new Intent(this, ScheduleDetailActivity.class);
            intentScheduleDetailActivity.putExtra(NetNowContract.ScheduleEntry._ID, scheduleId);


            PendingIntent pendingIntent = PendingIntent.getActivity(this, scheduleId, intentScheduleDetailActivity, 0);

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setAutoCancel(true);
            builder.setContentTitle(scheduleTitle);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentIntent(pendingIntent);
            builder.setVibrate(new long[]{150, 300, 150, 600});

            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();

            for (String message : scheduleMessage){
                style.addLine(message);
            }
            builder.setStyle(style);

            Notification notification = builder.build();
            notificationManager.notify(scheduleId, notification);
            ScheduleDetailHelper.updateRemember(this, scheduleId, false);
        }

    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRememberAlarm() {
        Cursor cursor = null;
        try {
            String[] projection = new String[]{
                    ScheduleEntry._ID
                    , ScheduleEntry.COLUMN_SCHEDULE_START_DATE
            };

            String selection = ScheduleEntry.COLUMN_SCHEDULE_REMEMBER + " != 0";

            cursor = getContentResolver().query(ScheduleEntry.CONTENT_URI, projection, selection, null, null);
            while (cursor.moveToNext()) {
                int scheduleId = cursor.getInt(0);
                long scheduleStartDate = cursor.getLong(1);
                long scheduleRemeberDate = Utility.getScheduleRememberDate(scheduleStartDate);
                Utility.registryAlarmNotifyRemember(this, scheduleId, scheduleRemeberDate);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
