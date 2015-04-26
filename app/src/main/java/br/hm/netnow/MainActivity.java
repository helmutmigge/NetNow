package br.hm.netnow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import br.hm.netnow.data.NetNowContract;
import br.hm.netnow.fragment.ScheduleFragment;
import br.hm.netnow.utils.Utility;


public class MainActivity extends ActionBarActivity implements ChannelCallBack {

    boolean isLand = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");

        isLand = Utility.isLand(this);
        if (isLand) {

            if (savedInstanceState == null) {
                Bundle bundle = new Bundle();
                bundle.putInt(NetNowContract.ChannelEntry._ID, -1);
                onItemSelected(bundle);
            }

        }
    }

    @Override
    public void onItemSelected(Bundle bundle) {

        if (isLand) {
            int channelId = bundle.getInt(NetNowContract.ChannelEntry._ID);
            // Exiba o fragment de detalhe
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_container, ScheduleFragment.newInstance(channelId))
                    .commit();
        } else {
            Intent showSchedule = new Intent(this, ScheduleActivity.class);
            showSchedule.putExtras(bundle);

            startActivity(showSchedule);
        }
    }
}
