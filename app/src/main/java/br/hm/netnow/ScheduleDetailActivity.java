package br.hm.netnow;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import br.hm.netnow.data.NetNowContract;
import br.hm.netnow.fragment.ScheduleDetailFragment;

/**
 * Created by helmutmigge on 28/04/2015.
 */
public class ScheduleDetailActivity extends ActionBarActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_schedule_detail);
        if (savedInstanceState == null) {
            int scheduleId = getIntent().getIntExtra(NetNowContract.ScheduleEntry._ID, -1);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.schedule_detial_activity_content, ScheduleDetailFragment.newInstance(scheduleId))
                    .commit();
        }
    }

    //ToolBar Menu


    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setLogo(null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
