package activity.netos.com.playlist.data;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;

import java.util.LinkedList;
import java.util.List;

import activity.netos.com.playlist.Util.StringFormat;
import activity.notes.com.entity.SongPlayEntity;

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
        List<SongPlayEntity> songPlayEntityList = null;
        if (cursor != null) {
            songPlayEntityList = new LinkedList<SongPlayEntity>();
            while (cursor.moveToNext()) {
                String playName = cursor.getString(cursor.getColumnIndex(Media.TITLE));
                String playArist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                int  maxDuration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String playDate = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String playDuration = StringFormat.formatDuration(maxDuration, mContext);
                SongPlayEntity entity = new SongPlayEntity(playName, playArist, playDuration, playDate,maxDuration);
                songPlayEntityList.add(entity);
            }
        }
        Message mes = mHandler.obtainMessage();
        mes.what = HEADLER_GET_DATA;
        mes.obj = songPlayEntityList;
        mHandler.sendMessage(mes);
    }


    public void startQuery() {
        Uri queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE};
        startQuery(0, null, queryUri, projection, null, null, null);
    }

}
