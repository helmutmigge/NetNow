package br.hm.netnow.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.hm.netnow.R;
import br.hm.netnow.data.NetNowContract.ScheduleEntry;
import br.hm.netnow.utils.Utility;


/**
 * Created by helmutmigge on 22/04/2015.
 */
public class ScheduleAdapter extends CursorAdapter {

    public ScheduleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layout = R.layout.list_item_schedule;

        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int id_column_schedule_title = cursor.getColumnIndex(
                ScheduleEntry.COLUMN_SCHEDULE_TITLE);
        int id_column_schedule_startDate = cursor.getColumnIndex(
                ScheduleEntry.COLUMN_SCHEDULE_START_DATE);

        long startDate = cursor.getLong(id_column_schedule_startDate);
        String startHourMinute = Utility.formatMillisecoundsToHourMinute(startDate);

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.scheduleStartDateView.setText(startHourMinute);
        holder.scheduleTitleView.setText(cursor.getString(id_column_schedule_title));
    }

    static class ViewHolder {

        public final TextView scheduleStartDateView;
        public final TextView scheduleTitleView;


        ViewHolder(View view) {
            scheduleStartDateView = (TextView)
                    view.findViewById(R.id.list_item_schedule_start_date);
            scheduleTitleView = (TextView)
                    view.findViewById(R.id.list_item_schedule_title);
        }
    }
}
