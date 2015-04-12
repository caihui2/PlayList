package activity.netos.com.playlist.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import activity.netos.com.playlist.PlayListActivity;

/**
 * Created by yangcaihui on 15/4/11.
 */
public class PlayService extends Service {
    // play state value
    public static final int DISPLAY_STATE_PALY = 0;
    public static final int DISPLAY_STATE_PAUSE = 1;
    public static final int DISPLAY_STATE_STOP = 2;
    private int displayState = DISPLAY_STATE_STOP;

    private MediaPlayer mediaPlayer;

    // refresh seekBar
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private Boolean isSeeking = false;
    private static final int HANDLE_REFRESH_SEEKBAR = 110;
    //send refresh seekbar position
    public static final String EXTRA_CURRENT_POSITION = "extra_current_position";
    public static final String ACTION_DISPATCH_POSITION = "action_dispatch_position";

    //send display state
    public static final String ACTION_DISPLAY_STATE = "action_display_state";
    public static final String EXTRA_DISPLAY_MODE = "extra_display_mode";

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(msg == null){return;}
            if (msg.what == HANDLE_REFRESH_SEEKBAR && !isSeeking) {
                int reSarPosition = mediaPlayer.getCurrentPosition();
                excuterDispatch(ACTION_DISPATCH_POSITION, EXTRA_CURRENT_POSITION, reSarPosition);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String excuteAction = intent.getStringExtra(PlayListActivity.EXTRA_VALUE);
        switch (excuteAction) {
            case PlayListActivity.ACTION_RUN_PLAY:
                controlPlayState(intent);
                break;
            case PlayListActivity.ACTION_RUN_PAUSE:
                displayPause();
                break;
            case PlayListActivity.ACTION_RUN_CONTINUE:
                displayContinue();
                break;
            case PlayListActivity.ACTION_RUN_STOP:
                displayStop();
                break;
            case PlayListActivity.ACTION_START_SEEKBAR_CHANGE:
                isSeeking = true;
                break;
            case PlayListActivity.ACTION_STOP_SEEKBAR_CHANGE:
                if (isSeeking) {
                    int changePosition = intent.getIntExtra(PlayListActivity.EXTRA_SEKPOSITION, 0);
                    displaySeek(changePosition);
                }

                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void controlPlayState(Intent intent) {
        if (displayState == DISPLAY_STATE_STOP) {
            System.out.println("我运行了");
            String audioPath = intent.getStringExtra(PlayListActivity.EXTRA_PATH);
            displayPlay(audioPath);
        }
    }

    public void excuterDispatch(String action, String name, int value) {
        Intent mIntent = new Intent(action);
        mIntent.putExtra(name, value);
        sendBroadcast(mIntent);
    }

    // Plays the specified path of the audio
    public void displayPlay(String audioPath) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            acyncPrepare(mediaPlayer);
            seekComplete(mediaPlayer);
            displayCompletion(mediaPlayer);
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void acyncPrepare(MediaPlayer player) {
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                startUpdateSeekBar();
                displayState = DISPLAY_STATE_PALY;
                excuterDispatch(ACTION_DISPLAY_STATE, EXTRA_DISPLAY_MODE, displayState);
            }
        });
    }

    private void seekComplete(MediaPlayer player) {
        player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                isSeeking = false;
                handler.sendEmptyMessage(HANDLE_REFRESH_SEEKBAR);
            }
        });
    }

    private void displayCompletion(MediaPlayer player) {
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                displayStop();
            }
        });
    }


    private void displaySeek(int position) {
        if (mediaPlayer != null && position >= 0
                && position < mediaPlayer.getDuration()) {
            mediaPlayer.seekTo(position);
            excuterDispatch(ACTION_DISPATCH_POSITION, EXTRA_CURRENT_POSITION, position);
        }
    }

    private void displayPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            displayState = DISPLAY_STATE_PAUSE;
            excuterDispatch(ACTION_DISPLAY_STATE, EXTRA_DISPLAY_MODE, displayState);
            if (timerTask != null && timer != null) {
                timerTask.cancel();
                timer.purge();
            }
        }
    }

    private void displayContinue() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            startUpdateSeekBar();
            displayState = DISPLAY_STATE_PALY;
            excuterDispatch(ACTION_DISPLAY_STATE, EXTRA_DISPLAY_MODE, displayState);
        }

    }

    private void displayStop() {
        displayState = DISPLAY_STATE_STOP;
        if (mediaPlayer != null && timerTask != null && timer != null) {
            try {
                timerTask.cancel();
                timer.purge();
                mediaPlayer.stop();
                mediaPlayer.release();
                excuterDispatch(ACTION_DISPLAY_STATE, EXTRA_DISPLAY_MODE, displayState);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mediaPlayer = null;
            }

        }
    }

    // 启动刷新界面播放进度条timer
    private void startUpdateSeekBar() {
        if (timerTask != null) {
            timerTask.cancel(); // 取消
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    handler.sendEmptyMessage(HANDLE_REFRESH_SEEKBAR);
                } else {
                    timerTask.cancel();
                }
            }
        };
        timer.schedule(timerTask, 0, 250);
    }


}
