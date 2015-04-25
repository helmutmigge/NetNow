package br.hm.netnow.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.hm.netnow.R;
import br.hm.netnow.data.NetNowContract.ChannelEntry;


/**
 * Created by helmutmigge on 22/04/2015.
 */
public class ChannelAdapter extends CursorAdapter {

    public ChannelAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layout = R.layout.list_item_channel;

        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int id_column_channel_number = cursor.getColumnIndex(
                ChannelEntry.COLUMN_CHANNEL_NUMBER);
        int id_column_channel_name = cursor.getColumnIndex(ChannelEntry.COLUMN_CHANNEL_NAME);
        int id_column_channel_image = cursor.getColumnIndex(ChannelEntry.COLUMN_CHANNEL_IMAGE);

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.channelNameView.setText(cursor.getString(id_column_channel_name));
        holder.channelNumerView.setText(cursor.getString(id_column_channel_number));

        // Configure Icon
        holder.channelFrameLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        holder.channelIconView.getLayoutParams().height = holder.channelFrameLayout.getMeasuredHeight();
        holder.channelIconView.getLayoutParams().width = holder.channelFrameLayout.getMeasuredHeight();
        byte[] imageByteArray = cursor.getBlob(id_column_channel_image);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        int colorBackground = bitmap.getPixel(0,0);
        colorBackground = colorBackground == 0?Color.WHITE:colorBackground;
        holder.channelIconView.setImageBitmap(bitmap);
        holder.channelIconView.setBackgroundColor(colorBackground);


    }

    static class ViewHolder {
        public final ImageView channelIconView;
        public final TextView channelNumerView;
        public final TextView channelNameView;
        public final RelativeLayout channelFrameLayout;

        ViewHolder(View view) {
            channelIconView = (ImageView)
                    view.findViewById(R.id.list_item_channel_icon);
            channelNumerView = (TextView)
                    view.findViewById(R.id.list_item_channel_number);
            channelNameView = (TextView)
                    view.findViewById(R.id.list_item_channel_name);
            channelFrameLayout = (RelativeLayout) view.findViewById(R.id.list_item_channel_frame);
        }
    }
}
