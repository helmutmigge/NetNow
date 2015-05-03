package br.hm.netnow;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import br.hm.netnow.data.NetNowContract;
import br.hm.netnow.utils.Utility;

public class NotificationRememberReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(NotificationRememberReceiver.class.getName(), NotificationRememberReceiver.class.getName());
        int scheduleId = intent.getIntExtra(NetNowContract.ScheduleEntry._ID,-1);
        RememberService.startActionRememberNotification(context,scheduleId);

    }
}
