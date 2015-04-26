package br.hm.netnow;

import android.database.Cursor;
import android.os.AsyncTask;
import android.test.AndroidTestCase;
import android.util.Log;

import br.hm.netnow.data.NetNowContract;
import br.hm.netnow.task.FetchScheduleTask;
import br.hm.netnow.utils.Utility;

/**
 * Created by helmutmigge on 21/04/2015.
 */
public class RemoteChannelTestCase extends AndroidTestCase {
    /*
    public void testquery() throws IOException, JSONException {
        RemoteChannel repository = new RemoteChannel();
        repository.queryById(27, 494, new JSONCallback() {
            @Override
            public void processRow(Object json, int rowNum) {
                Log.i("test", json.toString());
            }
        });
    }
*/
    public void testScheaduleTask() throws InterruptedException {
        FetchScheduleTask task = new FetchScheduleTask(getContext());
        long timeServer = Utility.getTimeServerSetting(getContext());
        task.execute("27", Long.toString(timeServer));
        while (AsyncTask.Status.RUNNING == task.getStatus()) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                Log.e(RemoteChannelTestCase.class.getName(), e.getMessage(), e);
                throw e;
            }
        }
        String sort = NetNowContract.ChannelEntry.COLUMN_CHANNEL_NUMBER + " ASC";
        String[] projection = new String[]{NetNowContract.ChannelEntry.COLUMN_CHANNEL_NUMBER, NetNowContract.ChannelEntry.COLUMN_CHANNEL_NAME};
        Cursor cursor = getContext().getContentResolver().query(NetNowContract.ChannelEntry.CONTENT_URI, projection, null, null, sort);
        while (cursor.moveToNext()) {
            Log.i("row", cursor.getString(0) + "-" + cursor.getString(1));
        }
    }

}
