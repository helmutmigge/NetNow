package br.hm.netnow.fragment;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.hm.netnow.R;
import br.hm.netnow.data.NetNowContract;
import br.hm.netnow.data.ScheduleDetailHelper;


public class ScheduleDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_SCHEDULE_ID = "scheduleId";

    private static final int SCHEDULE_DETAIL = 4;

    private int mScheduleId;


    public static ScheduleDetailFragment newInstance(int scheduleId) {
        ScheduleDetailFragment fragment = new ScheduleDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCHEDULE_ID, scheduleId);
        fragment.setArguments(args);
        return fragment;
    }

    public ScheduleDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScheduleId = getArguments().getInt(ARG_SCHEDULE_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(SCHEDULE_DETAIL, null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLoaderManager().destroyLoader(SCHEDULE_DETAIL);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = NetNowContract.ScheduleDetailView.buildScheduleDetailUri(mScheduleId);
        return new CursorLoader(
                getActivity(),
                uri,
                ScheduleDetailHelper.COLUMNS,
                null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.moveToNext()) {
            View rootView = getView();
            configureChannelImageView(rootView, cursor);
            TextView channelNumber = (TextView) rootView.findViewById(R.id.list_item_channel_number);
            channelNumber.setText(ScheduleDetailHelper.getChannelNumber(cursor));

            TextView channelName = (TextView) rootView.findViewById(R.id.list_item_channel_name);
            channelName.setText(ScheduleDetailHelper.getChannelName(cursor));

            TextView scheduleDetailTitle = (TextView) rootView.findViewById(R.id.schedule_detail_title);
            scheduleDetailTitle.setText(ScheduleDetailHelper.getShowTitle(cursor));

            TextView scheduleDetailOriginalTitle = (TextView) rootView.findViewById(R.id.schedule_detail_original_title);
            scheduleDetailOriginalTitle.setText(ScheduleDetailHelper.getShowOriginalTitle(cursor));
        }
    }

    private void configureChannelImageView(final View rootView, Cursor cursor) {
        final ImageView channelImageView = (ImageView) rootView.findViewById(R.id.list_item_channel_icon);
        final RelativeLayout channelFrameLayout = (RelativeLayout) rootView.findViewById(R.id.list_item_channel_frame);
        // Configure Icon
        channelFrameLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        channelImageView.getLayoutParams().height = channelFrameLayout.getMeasuredHeight();
        channelImageView.getLayoutParams().width = channelFrameLayout.getMeasuredHeight();
        byte[] imageByteArray = ScheduleDetailHelper.getChannelImage(cursor);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        int colorBackground = bitmap.getPixel(0, 0);
        colorBackground = colorBackground == 0 ? Color.WHITE : colorBackground;
        channelImageView.setImageBitmap(bitmap);
        channelImageView.setBackgroundColor(colorBackground);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
