package br.hm.netnow.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import br.hm.netnow.R;
import br.hm.netnow.adapter.ScheduleAdapter;
import br.hm.netnow.data.NetNowContract.ScheduleEntry;
import br.hm.netnow.utils.Moment;
import br.hm.netnow.utils.Utility;


public class ScheduleFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_CHANNEL_ID = "channel_id";

    private static final int SCHEDULE_LOADER = 2;

    private int mChannelId;

    public static ScheduleFragment newInstance(int channelId) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHANNEL_ID, channelId);
        fragment.setArguments(args);
        return fragment;
    }

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChannelId = getArguments().getInt(ARG_CHANNEL_ID);
        }
        setHasOptionsMenu(true);
        setListAdapter(new ScheduleAdapter(getActivity(), null, 0));
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setBackgroundResource(R.color.item_schedule_background);
        getListView().setDivider(getActivity().getResources().getDrawable(R.color.item_schedule_line));
        int paddingLeftAndRight = Utility.convertDpToPx(getActivity(),10);
        getListView().setDividerHeight(Utility.convertDpToPx(getActivity(), 2));
        getListView().setPadding(paddingLeftAndRight,0,paddingLeftAndRight,0);
        //setEmptyText(getString(R.string.list_empty_schedule));
    }
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }
*/
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(SCHEDULE_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = ScheduleEntry.COLUMN_SCHEDULE_START_DATE + " ASC";
        int cityId = Utility.getCityIdSetting(getActivity());
        long currentMilliseconds = Utility.getTimeServerSetting(getActivity());
        Moment moment = Moment.instanceMoment(currentMilliseconds, 6, Moment.HOUR);

        final String[] COLUMNS = {
                ScheduleEntry.TABLE_NAME + "." + ScheduleEntry._ID,
                ScheduleEntry.COLUMN_SCHEDULE_START_DATE,
                ScheduleEntry.COLUMN_SCHEDULE_TITLE
        };
        final String SELECT = "(" + ScheduleEntry.COLUMN_SCHEDULE_START_DATE + " BETWEEN ? AND ? OR "
                + ScheduleEntry.COLUMN_SCHEDULE_END_DATE + " BETWEEN ? AND ?) AND "
                + ScheduleEntry.COLUMN_CITY_ID + " = ? AND "
                + ScheduleEntry.COLUMN_CHANNEL_ID + " = ?";
        Uri uri = ScheduleEntry.CONTENT_URI;
        return new CursorLoader(
                getActivity(),
                uri,
                COLUMNS,
                SELECT,
                new String[]{
                        Long.toString(moment.getStartMilliseconds())
                        , Long.toString(moment.getEndMilliseconds())
                        , Long.toString(moment.getStartMilliseconds())
                        , Long.toString(moment.getEndMilliseconds())
                        , Integer.toString(cityId)
                        , Integer.toString(mChannelId)
                },
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        CursorAdapter cursorAdapter = (CursorAdapter) getListAdapter();
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        CursorAdapter cursorAdapter = (CursorAdapter) getListAdapter();
        cursorAdapter.swapCursor(null);
    }
}
