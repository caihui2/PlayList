package activity.netos.com.playlist.Util;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import activity.netos.com.playlist.R;
import activity.netos.com.playlist.View.ActionSlideExpandableListView;
import activity.netos.com.playlist.View.SildeDataAdapter;
import activity.netos.com.playlist.View.SlideExpandableListAdapter;
import activity.notes.com.entity.PlayEntity;

/**
 * Created by yangcaihui on 15/4/10.
 */
public class DataQuery extends AsyncQueryHandler {
    private Context mContext;
    private Handler mHandler;
    public static final int HEADLER_GET_DATA = 100;
    public DataQuery(Context mContext, Handler mHandler) {
        super(mContext.getContentResolver());
        this.mHandler = mHandler;
        this.mContext = mContext;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        System.out.println("cursor" + cursor);
        List<PlayEntity> playEntityList = null;
        if (cursor != null) {
            playEntityList = new LinkedList<PlayEntity>();
            while (cursor.moveToNext()) {
                String playName = disposeName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                String playTime = disposeTime(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)) + "000");
                int  maxDuration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String playDate = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String playDuration = formatDuration(maxDuration);
                PlayEntity entity = new PlayEntity(playName, playTime, playDuration, playDate,maxDuration);
                playEntityList.add(entity);
            }
        }
        Message mes = mHandler.obtainMessage();
        mes.what = HEADLER_GET_DATA;
        mes.obj = playEntityList;
        mHandler.sendMessage(mes);
    }


    private String disposeTime(String playTime) {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormater.format(Long.parseLong(playTime)).toString();
    }

    public String disposeName(String playName) {
        return playName.substring(playName.lastIndexOf("/") + 1, playName.length());
    }

    public String formatDuration(int duration) {
        int time = duration / 1000;
        String mTimerFormat = "";
        String timeStr = "";
        if (time / (60 * 60) >= 1) {
            mTimerFormat = mContext.getResources().getString(
                    R.string.duration_timer_format_with_hour);
            timeStr = String.format(mTimerFormat, time / (60 * 60),
                    (time % 3600) / 60, time % 60);
        } else {
            mTimerFormat = mContext.getResources().getString(
                    R.string.duration_timer_format_without_hour);
            timeStr = String
                    .format(mTimerFormat, (time % 3600) / 60, time % 60);
        }
        return timeStr;
    }

    public void startQuery() {
        Uri queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.TITLE};
        startQuery(0, null, queryUri, projection, null, null, null);
    }

}
