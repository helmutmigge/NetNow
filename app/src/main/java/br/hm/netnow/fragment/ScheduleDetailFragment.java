package br.hm.netnow.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.hm.netnow.R;
import br.hm.netnow.data.NetNowContract;
import br.hm.netnow.data.ScheduleDetailHelper;
import br.hm.netnow.utils.Utility;


public class ScheduleDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CompoundButton.OnCheckedChangeListener {

    private static final String ARG_SCHEDULE_ID = "scheduleId";

    private static final int SCHEDULE_DETAIL = 4;

    private int mScheduleId;

    private long mScheduleStartDate;




    private boolean mScheduleRemember;

    private Switch mRememberSwitch;

    private synchronized boolean getRemenber(){
        return mScheduleRemember;
    }

    private void setRemember(boolean remember) {
        mScheduleRemember = remember;
        synchronized (this) {
            if (mRememberSwitch != null) {
                mRememberSwitch.setChecked(remember);
            }
        }
    }

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_schedule_detail, menu);
        MenuItem switchItem = menu.findItem(R.id.action_alarm);
        mRememberSwitch = (Switch) MenuItemCompat.getActionView(switchItem);
        mRememberSwitch.setText(getString(R.string.switch_remember));
        mRememberSwitch.setChecked(getRemenber());
        mRememberSwitch.setOnCheckedChangeListener(this);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.moveToNext()) {
            View rootView = getView();
            mScheduleStartDate = ScheduleDetailHelper.getScheduleStartDate(cursor);

            boolean remember = ScheduleDetailHelper.getScheduleRemember(cursor);

            setRemember(remember);
            TextView scheduleDetailTitle = (TextView) rootView.findViewById(R.id.schedule_detail_title);
            String scheduleTitle = ScheduleDetailHelper.getShowTitle(cursor);
            scheduleDetailTitle.setText(scheduleTitle);

            TextView scheduleDetailOriginalTitle = (TextView) rootView.findViewById(R.id.schedule_detail_original_title);
            String originalTitle = ScheduleDetailHelper.getShowOriginalTitle(cursor);
            if (originalTitle != null && !originalTitle.isEmpty()) {
                scheduleDetailOriginalTitle.setText(originalTitle);
            } else {
                scheduleDetailOriginalTitle.setVisibility(View.GONE);
            }

            configureContentRantingView(rootView, cursor);

            TextView genreView = (TextView) rootView.findViewById(R.id.schedule_detail_genre);
            String genre = ScheduleDetailHelper.getShowGenre(cursor);
            String subgenus = ScheduleDetailHelper.getShowSubgenus(cursor);
            genreView.setText(genre + " / " + subgenus);

            RatingBar ratingView = (RatingBar) rootView.findViewById(R.id.schedule_detail_rating);
            int ranting = ScheduleDetailHelper.getShowRating(cursor);
            if (ranting == 0) {
                ratingView.setVisibility(View.GONE);
            } else {
                ratingView.setRating(ranting);
            }

            TextView durationMinutesView = (TextView) rootView.findViewById(R.id.schedule_detail_duration);
            int durationMinutes = ScheduleDetailHelper.getShowDurationMinutes(cursor);
            durationMinutesView.setText(durationMinutes + " " + getResources().getString(R.string.duration_minutes));


            configureStartDateView(rootView, cursor);

            TextView descriptionView = (TextView) rootView.findViewById(R.id.schedule_detail_decription);
            String description = ScheduleDetailHelper.getShowDescription(cursor);
            descriptionView.setText(description);

            String director = ScheduleDetailHelper.getShowDirector(cursor);
            if (director != null && !director.isEmpty()) {
                TextView directorView = (TextView) rootView.findViewById(R.id.schedule_detail_director);
                directorView.setText(director);
            } else {
                RelativeLayout directorLayout = (RelativeLayout) rootView.findViewById(R.id.schedule_detail_container_director);
                directorLayout.setVisibility(View.GONE);
            }

            String cast = ScheduleDetailHelper.getShowCast(cursor);
            if (cast != null && !cast.isEmpty()) {
                TextView castView = (TextView) rootView.findViewById(R.id.schedule_detail_cast);
                castView.setText(cast);
            } else {
                RelativeLayout directorLayout = (RelativeLayout) rootView.findViewById(R.id.schedule_detail_container_cast);
                directorLayout.setVisibility(View.GONE);
            }
        }
    }

    protected void configureStartDateView(View rootView, Cursor cursor) {
        SimpleDateFormat format = new SimpleDateFormat(getString(R.string.schedule_detal_start_date_format));
        Date startDate = new Date(mScheduleStartDate);
        String dateFormatStr = format.format(startDate);
        String channelName = ScheduleDetailHelper.getChannelName(cursor);
        TextView startDateView = (TextView) rootView.findViewById(R.id.schedule_detail_start_date);
        String scheduleMessage = dateFormatStr + " " + channelName;
        startDateView.setText(scheduleMessage);
    }

    protected void configureContentRantingView(View rootView, Cursor cursor) {
        TextView contentRantingView = (TextView) rootView.findViewById(R.id.schedule_detail_content_ranting);
        ViewGroup containerContentRating = (ViewGroup) rootView.findViewById(R.id.schedule_detail_container_content_rating);
        int contentRanting = ScheduleDetailHelper.getShowContentRating(cursor);
        if (contentRanting <= 5) {
            containerContentRating.setBackgroundResource(R.color.range_content_rating_5);
            contentRantingView.setText("L");
        } else if (contentRanting <= 12) {
            containerContentRating.setBackgroundResource(R.color.range_content_rating_12);
            contentRantingView.setText(Integer.toString(contentRanting));
        } else if (contentRanting <= 16) {
            containerContentRating.setBackgroundResource(R.color.range_content_rating_16);
            contentRantingView.setText(Integer.toString(contentRanting));
        } else {
            containerContentRating.setBackgroundResource(R.color.range_content_rating_18);
            contentRantingView.setText(Integer.toString(contentRanting));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean remember) {
        int rows = ScheduleDetailHelper.updateRemember(getActivity(), mScheduleId, remember);
        if (rows != 0) {
            if (remember) {
                long scheduleRememberDate = Utility.getScheduleRememberDate(mScheduleStartDate);
                Utility.registryAlarmNotifyRemember(getActivity(), mScheduleId, scheduleRememberDate);
            } else {
                Utility.unregistryAlarmNotifyRemember(getActivity(), mScheduleId);
            }
        }

    }
}

