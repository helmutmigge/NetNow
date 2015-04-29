package br.hm.netnow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import br.hm.netnow.data.NetNowContract;
import br.hm.netnow.fragment.ScheduleFragment;


public class ScheduleActivity extends ActionBarActivity implements ItemCallBack {
    private String mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Bundle extras = getIntent().getExtras();
        mTitle = extras.getString(NetNowContract.ChannelEntry.COLUMN_CHANNEL_NUMBER) +
                " - " + extras.getString(NetNowContract.ChannelEntry.COLUMN_CHANNEL_NAME);

        if (savedInstanceState == null) {
            int channelId = extras.getInt(NetNowContract.ChannelEntry._ID);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.schedule_activity_content, ScheduleFragment.newInstance(channelId))
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTitle(mTitle);
        getSupportActionBar().setLogo(null);
    }

    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent()
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    @Override
    public void onItemSelected(String type, Bundle bundle) {
        Intent showScheduleDetial = new Intent(this, ScheduleDetailActivity.class);
        showScheduleDetial.putExtras(bundle);
        startActivity(showScheduleDetial);
    }
}

