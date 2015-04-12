package activity.netos.com.playlist.View;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import activity.netos.com.playlist.R;
import activity.notes.com.entity.PlayEntity;

/**
 * Created by yangcaihui on 15/4/10.
 */
public class SildeDataAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<PlayEntity> playEntityList;
    private ViewHandler viewHandler;

    public SildeDataAdapter(Context mContext, List<PlayEntity> playEntityList) {
        this.mContext = mContext;
        this.playEntityList = playEntityList;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setData(List<PlayEntity> playEntityList){
        this.playEntityList = playEntityList;
    }

    @Override
    public int getCount() {
        return playEntityList.size();
    }


    @Override
    public Object getItem(int position) {
        return playEntityList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewHandler = null;
        if (convertView == null) {
            viewHandler = new ViewHandler();
            convertView = mInflater.inflate(R.layout.expandable_list_item, null);
            viewHandler.initView(convertView);
            convertView.setTag(viewHandler);
        } else {
            viewHandler = (ViewHandler) convertView.getTag();
        }
        PlayEntity playEntity = (PlayEntity) getItem(position);
        viewHandler.initData(playEntity);
        return convertView;
    }

    public class ViewHandler {
        // show item
        TextView createName;
        TextView createTime;
        TextView duration;
        // hidden item
        SeekBar playSeekbar;
        TextView currentTime;
        TextView tvDuration;
        ImageButton pauseBt;

        void initView(View convertView) {
            viewHandler.createName = (TextView) convertView.findViewById(R.id.file_create_time);
            viewHandler.createTime = (TextView) convertView.findViewById(R.id.file_create_date);
            viewHandler.duration = (TextView) convertView.findViewById(R.id.duration);
            viewHandler.playSeekbar = (SeekBar) convertView.findViewById(R.id.play_progress_bar);
            viewHandler.currentTime = (TextView) convertView.findViewById(R.id.current_time);
            viewHandler.tvDuration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHandler.pauseBt = (ImageButton) convertView.findViewById(R.id.pause_bt);
        }

        void initData(PlayEntity playEntity) {
            viewHandler.createName.setText(playEntity.getPlayName());
            viewHandler.createTime.setText(playEntity.getPlayTime());
            viewHandler.duration.setText(playEntity.getPlayDuration());
            viewHandler.tvDuration.setText(playEntity.getPlayDuration());
        }

        public SeekBar getPlaySeekbar() {
            return playSeekbar;
        }

        public TextView getCurrentTime() {
            return currentTime;
        }

        public ImageButton getPauseBt() {
            return pauseBt;
        }
    }
}
