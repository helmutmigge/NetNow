package br.hm.netnow.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import br.hm.netnow.ItemCallBack;
import br.hm.netnow.R;
import br.hm.netnow.adapter.ChannelAdapter;
import br.hm.netnow.data.NetNowContract.ChannelEntry;
import br.hm.netnow.sync.NetnowSyncAdapter;
import br.hm.netnow.utils.Utility;


public class ChannelFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int CHANNEL_LOADER = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new ChannelAdapter(getActivity(), null, 0));
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDivider(null);
        getListView().setBackgroundResource(R.color.frame_background);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CHANNEL_LOADER, null, this);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        int id_column_channel_id = cursor.getColumnIndex(
                ChannelEntry._ID);
        int id_column_channel_number = cursor.getColumnIndex(
                ChannelEntry.COLUMN_CHANNEL_NUMBER);
        int id_column_channel_name = cursor.getColumnIndex(ChannelEntry.COLUMN_CHANNEL_NAME);


        int channelId = cursor.getInt(id_column_channel_id);
        String channelName = cursor.getString(id_column_channel_name);
        String channelNumber = cursor.getString(id_column_channel_number);


        if (getActivity() instanceof ItemCallBack) {
            ItemCallBack callBack = (ItemCallBack) getActivity();
            Bundle bundle = new Bundle();
            bundle.putInt(ChannelEntry._ID, channelId);
            bundle.putString(ChannelEntry.COLUMN_CHANNEL_NUMBER, channelNumber);
            bundle.putString(ChannelEntry.COLUMN_CHANNEL_NAME, channelName);
            callBack.onItemSelected(ChannelEntry.TABLE_NAME, bundle);
        }
        Toast.makeText(getActivity(), channelNumber + " - " + channelName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = ChannelEntry.COLUMN_CHANNEL_NUMBER + " ASC";
        int cityId = Utility.getCityIdSetting(getActivity());
        final String[] COLUMNS = {
                ChannelEntry.TABLE_NAME + "." + ChannelEntry._ID,
                ChannelEntry.COLUMN_CHANNEL_NAME,
                ChannelEntry.COLUMN_CHANNEL_IMAGE,
                ChannelEntry.COLUMN_CHANNEL_NUMBER
        };
        final String SELECT = ChannelEntry.COLUMN_CITY_ID + " = ?";
        Uri uri = ChannelEntry.CONTENT_URI;
        return new CursorLoader(
                getActivity(),
                uri,
                COLUMNS,
                SELECT,
                new String[]{Integer.toString(cityId)},
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLoaderManager().destroyLoader(CHANNEL_LOADER);
    }

    //ToolBar Menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_channel, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:

                NetnowSyncAdapter.syncImmediately(getActivity());
                return false;
        }

        return super.onOptionsItemSelected(item);
    }
}
