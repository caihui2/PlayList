package activity.netos.com.playlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;

import java.util.List;

import activity.netos.com.playlist.Util.DataQuery;
import activity.netos.com.playlist.View.ActionSlideExpandableListView;
import activity.netos.com.playlist.View.SildeDataAdapter;
import activity.netos.com.playlist.View.SlideExpandableListAdapter;
import activity.netos.com.playlist.service.PlayService;
import activity.notes.com.entity.PlayEntity;


public class PlayListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ActionSlideExpandableListView aseListVie;
    private SlideExpandableListAdapter expAdapter;
    private SildeDataAdapter sdAdapter;
    private SildeDataAdapter.ViewHandler viewHandler;
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
                    List<PlayEntity> playEntityList = (List<PlayEntity>) msg.obj;
                    sdAdapter = new SildeDataAdapter(PlayListActivity.this, playEntityList);
                    aseListVie.setAdapter(sdAdapter);
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
                viewHandler.getPlaySeekbar().setProgress(position);
                viewHandler.getCurrentTime().setText(durationToString(position));
            }
            if (action.equals(PlayService.ACTION_DISPLAY_STATE)) {
                int playState = intent.getIntExtra(PlayService.EXTRA_DISPLAY_MODE, -1);
                updateUI(playState);
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
    }

    public void initQuery() {
        mDataQuery = DataQuery.getDataQuery(this, mHandler);
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
        viewHandler = (SildeDataAdapter.ViewHandler) view.getTag();
        PlayEntity playEntity = (PlayEntity) sdAdapter.getItem((int) id);
        if (expAdapter.isAnyItemExpanded()) {
            if (displayState != PlayService.DISPLAY_STATE_STOP) {
                runStop();
            }
            runPlay(playEntity);
        }
        if (!expAdapter.isAnyItemExpanded()) {
            runStop();
        }
    }

    @Override
    public void onClick(View v) {
        if (displayState == PlayService.DISPLAY_STATE_PALY) {
            runPause();
        }
        if (displayState == PlayService.DISPLAY_STATE_PAUSE) {
            runContinue();
        }
    }

    public void runPlay(PlayEntity playEntity) {
        excuteControlAction(ACTION_RUN_PLAY, EXTRA_PATH, playEntity.getPlayData());
        viewHandler.getPlaySeekbar().setMax(playEntity.getMaxDuration());
        seekBarChange(viewHandler.getPlaySeekbar());
        viewHandler.getPauseBt().setOnClickListener(this);
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

    public void seekBarChange(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int changePositon = seekBar.getProgress();
                System.out.println(changePositon);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                excuteControlAction(ACTION_START_SEEKBAR_CHANGE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int changePosition = seekBar.getProgress();
                excuteControlAction(ACTION_STOP_SEEKBAR_CHANGE, EXTRA_SEKPOSITION, changePosition);
            }
        });
    }


    public static String durationToString(int duration) {
        String reVal = "";
        int i = duration / 1000;
        int hour = (int) i / (60 * 60);
        int min = (int) ((i / 60) % 60);
        int sec = i % 60;
        if (hour > 9) {
            reVal = ":";
        } else {
            reVal = "0" + hour + ":";
        }
        if (min > 9) {
            reVal += min + ":";
        } else {
            reVal += "0" + min + ":";
        }
        if (sec > 9) {
            reVal += sec;
        } else {
            reVal += "0" + sec;
        }
        return reVal;
    }

    private void updateUI(int mState) {
        Drawable tempdrawable = null;
        switch (mState) {
            case PlayService.DISPLAY_STATE_STOP:
                viewHandler.getPlaySeekbar().setProgress(0);
                viewHandler.getCurrentTime().setText(durationToString(0));
                viewHandler.getPlaySeekbar().setEnabled(true);
                viewHandler.getPauseBt().setEnabled(true);
                viewHandler.getPauseBt().setBackgroundResource(R.drawable.play_selector);
                if (tempdrawable == null) {
                    tempdrawable = this.getResources().getDrawable(
                            R.drawable.play01);
                }
                viewHandler.getPauseBt().setImageDrawable(tempdrawable);
                break;
            case PlayService.DISPLAY_STATE_PALY:
                viewHandler.getPauseBt().setEnabled(true);
                viewHandler.getPauseBt().setBackgroundResource(R.drawable.pause_selector);
                if (tempdrawable == null) {
                    tempdrawable = this.getResources().getDrawable(
                            R.drawable.pause02);
                }
                viewHandler.getPauseBt().setImageDrawable(tempdrawable);
                viewHandler.getPlaySeekbar().setEnabled(true);
                break;
            case PlayService.DISPLAY_STATE_PAUSE:
                viewHandler.getPauseBt().setEnabled(true);
                viewHandler.getPlaySeekbar().setBackgroundResource(R.drawable.play_selector);
                if (tempdrawable == null) {
                    tempdrawable = this.getResources().getDrawable(R.drawable.play02);
                }
                viewHandler.getPauseBt().setImageDrawable(tempdrawable);
                viewHandler.getPlaySeekbar().setEnabled(true);
                break;
        }
    }
}
