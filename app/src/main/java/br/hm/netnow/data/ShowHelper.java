package br.hm.netnow.data;

import android.content.Context;
import android.database.Cursor;

import br.hm.netnow.data.NetNowContract.ShowEntry;

/**
 * Created by helmutmigge on 26/04/2015.
 */
public class ShowHelper {

    public static boolean isExist(Context context,int showId) {
        String where = ShowEntry._ID + " = ?";
        String[] args = new String[]{Integer.toString(showId)};
        return getCount(context,where,args) != 0;
    }

    private static int getCount(Context context, String where, String[] args) {
        String[] projection = new String[]{"count(*)"};
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(ShowEntry.CONTENT_URI, projection, where, args, null);
            cursor.moveToFirst();
            return cursor.getInt(0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
