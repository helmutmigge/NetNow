package br.hm.netnow;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import br.hm.netnow.data.NetNowSQLHelper;

/**
 * Created by helmutmigge on 21/04/2015.
 */
public class DBAndroidTestCase extends AndroidTestCase {
    public void testCreateDb() {
        for (String database : mContext.databaseList()){
           boolean success =  mContext.deleteDatabase(database);
            Log.i("database","delete ["+database+"]" + success);
        }
        SQLiteDatabase db = new NetNowSQLHelper(mContext).getReadableDatabase();

        //assertEquals(true, db.isOpen());


    }
}
