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


public class ScheduleActivity extends ActionBarActivity {

    public static final String ARG_CHANNEL_ID = "channel_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            int channelId = extras.getInt(NetNowContract.ChannelEntry._ID);
            String title = extras.getString(NetNowContract.ChannelEntry.COLUMN_CHANNEL_NUMBER) +
                    " - " + extras.getString(NetNowContract.ChannelEntry.COLUMN_CHANNEL_NAME);
            setTitle(title);
            getSupportActionBar().setLogo(null);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.schedule_activity_content, ScheduleFragment.newInstance(channelId))
                    .commit();
        }
    }

    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent()
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}

