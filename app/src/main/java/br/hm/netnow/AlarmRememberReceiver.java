package br.hm.netnow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmRememberReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        RememberService.startActionRememberAlarm(context);
     }
}
