package br.hm.netnow;

import android.test.AndroidTestCase;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

import br.hm.netnow.commons.JSONCallback;
import br.hm.netnow.remote.RemoteSchedule;
import br.hm.netnow.utils.Moment;
import br.hm.netnow.utils.Utility;

/**
 * Created by helmutmigge on 21/04/2015.
 */
public class RemoteScheduleTestCase extends AndroidTestCase {
    public void testTimeServer() {
        RemoteSchedule repository = new RemoteSchedule();
        long currentTimeMillis = repository.currentTimeMillisOfTimeServer();
        assertNotNull(currentTimeMillis);
        Log.i(RemoteScheduleTestCase.class.getName(), Moment.formatDateLongAsISO(currentTimeMillis));
    }

    public void testSchedule() throws IOException, JSONException {
        for (String database : mContext.databaseList()) {
            boolean success = mContext.deleteDatabase(database);
            Log.i("database", "delete [" + database + "]" + success);
        }

        RemoteSchedule repository = new RemoteSchedule();
        long currentTimeMillis = repository.currentTimeMillisOfTimeServer();
        long currentMilliseconds = Utility.getTimeServerSetting(getContext());
        Moment moment = Moment.instanceMoment(currentMilliseconds, 6, Moment.HOUR);
        repository.query(27, 10000, moment, new JSONCallback() {
            @Override
            public void processRow(Object json, int rowNum) {
                Log.i("RemoteScheduleTestCase", json.toString());
            }
        });
    }
}
