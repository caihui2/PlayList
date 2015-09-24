package activity.netos.com.playlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import activity.netos.com.playlist.data.DataQuery;
import activity.netos.com.playlist.View.ActionSlideExpandableListView;
import activity.netos.com.playlist.View.SildeDataAdapter;
import activity.netos.com.playlist.View.SlideExpandableListAdapter;
import activity.netos.com.playlist.service.PlayService;
import activity.notes.com.entity.SongPlayEntity;


public class PlayListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,  SildeDataAdapter.OnAdapterViewChangeListener{

    private ActionSlideExpandableListView aseListVie;
    private SlideExpandableListAdapter expAdapter;
    private SildeDataAdapter sdAdapter;
    private DataQuery mDataQuery;

    public static final String EXTRA_VALUE = "extra_value";
    public static final String EXTRA_PATH = "extra_path";

    // play operate
    private int displayState = PlayService.DISPLAY_STATE_STOP;
    public static final String ACTION_RUN_PLAY = "action_run_play";
    public static final String ACTION_RUN_PAUSE = "action_run_pause";
    public static final String ACTION_RUN_CONTINUE = "action_run_contnue";
    public static final String ACTION_RUN_STOP = "action_run_stop";


    // drag seekbar operate
    public static final String ACTION_START_SEEKBAR_CHANGE = "action_stare_seekbar_change";
    public static final String ACTION_STOP_SEEKBAR_CHANGE = "action_stop_seekbat_change";
    public static final String EXTRA_SEKPOSITION = "extra_sekPosition";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DataQuery.HEADLER_GET_DATA:
                    List<SongPlayEntity> songPlayEntityList = (List<SongPlayEntity>) msg.obj;
                    sdAdapter = new SildeDataAdapter(PlayListActivity.this, songPlayEntityList);
                    aseListVie.setAdapter(sdAdapter);
                    sdAdapter.setOnAdapterViewChangeListener(PlayListActivity.this);
                    break;
            }
        }
    };

    private BroadcastReceiver mBcr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(PlayService.ACTION_DISPATCH_POSITION)) {
                int position = intent.getIntExtra(PlayService.EXTRA_CURRENT_POSITION, 0);
                sdAdapter.setSekCurPosition(position);
            }
            if (action.equals(PlayService.ACTION_DISPLAY_STATE)) {
                int playState = intent.getIntExtra(PlayService.EXTRA_DISPLAY_MODE, -1);
                sdAdapter.setPlayState(playState);
                displayState = playState;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        initQuery();
        initActionBar();
        initBroadcast();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBcr);
        runStop();
        aseListVie.collapse();

    }

    public void initQuery() {
        mDataQuery = new DataQuery(this,mHandler);
        mDataQuery.startQuery();
    }

    public void initActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setTitle(getString(R.string.sounder_file));
        aseListVie = (ActionSlideExpandableListView) findViewById(R.id.recording_file_list_view);
        aseListVie.setOnItemClickListener(this);
    }

    public void initBroadcast() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(PlayService.ACTION_DISPATCH_POSITION);
        mIntentFilter.addAction(PlayService.ACTION_DISPLAY_STATE);
        registerReceiver(mBcr, mIntentFilter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        controlView(view);
        SongPlayEntity songPlayEntity = (SongPlayEntity) sdAdapter.getItem((int) id);
        if (expAdapter.isAnyItemExpanded()) {
            if (displayState != PlayService.DISPLAY_STATE_STOP) {
                runStop();
            }
            runPlay(songPlayEntity);
        }
        if (!expAdapter.isAnyItemExpanded()) {
            runStop();
        }
    }

    public void runPlay(SongPlayEntity songPlayEntity) {
        excuteControlAction(ACTION_RUN_PLAY, EXTRA_PATH, songPlayEntity.getPlayData());
    }

    public void runPause() {
        excuteControlAction(ACTION_RUN_PAUSE);
    }

    public void runContinue() {
        excuteControlAction(ACTION_RUN_CONTINUE);
    }

    public void runStop() {
        excuteControlAction(ACTION_RUN_STOP);
    }

    // Control to display hidden
    public void controlView(View view) {
        expAdapter = (SlideExpandableListAdapter) aseListVie.getAdapter();
        expAdapter.getExpandToggleButton(view).performClick();
    }

    private void excuteControlAction(String action) {
        Intent intent = new Intent(this, PlayService.class);
        intent.putExtra(EXTRA_VALUE, action);
        startService(intent);
    }

    private void excuteControlAction(String action, String name, String value) {
        Intent intent = new Intent(this, PlayService.class);
        intent.putExtra(EXTRA_VALUE, action);
        intent.putExtra(name, value);
        startService(intent);
    }

    private void excuteControlAction(String action, String name, int value) {
        Intent intent = new Intent(this, PlayService.class);
        intent.putExtra(EXTRA_VALUE, action);
        intent.putExtra(name, value);
        startService(intent);
    }


    @Override
    public void onStartTrackingTouch() {
        excuteControlAction(ACTION_START_SEEKBAR_CHANGE);
    }

    @Override
    public void onStopTrackingTouch(int position) {
        excuteControlAction(ACTION_STOP_SEEKBAR_CHANGE, EXTRA_SEKPOSITION, position);
    }

    @Override
    public void onClicks(View v) {
        if (displayState == PlayService.DISPLAY_STATE_PALY) {
            runPause();
        }
        if (displayState == PlayService.DISPLAY_STATE_PAUSE) {
            runContinue();
        }
    }

}
