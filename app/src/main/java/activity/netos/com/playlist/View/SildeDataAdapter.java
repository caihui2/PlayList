package activity.netos.com.playlist.View;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import activity.netos.com.playlist.R;
import activity.netos.com.playlist.Util.StringFormat;
import activity.netos.com.playlist.service.PlayService;
import activity.notes.com.entity.SongPlayEntity;

/**
 * Created by yangcaihui on 15/4/10.
 */
public class SildeDataAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<SongPlayEntity> songPlayEntityList;
    private ViewHandler viewHandler;
    private OnAdapterViewChangeListener adapterViewChangeListener;
    private int playState = -1;
    private int  sekCurPosition = 0;


    public interface  OnAdapterViewChangeListener{
        public void onStartTrackingTouch();
        public void onStopTrackingTouch(int position);
        public void onClicks(View v);
    }

    public SildeDataAdapter(Context mContext, List<SongPlayEntity> songPlayEntityList) {
        this.mContext = mContext;
        this.songPlayEntityList = songPlayEntityList;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setSekCurPosition(int sekCurPosition){
        this.sekCurPosition = sekCurPosition;
        notifyDataSetChanged();
    }

    public void setPlayState(int playState){
        this.playState = playState;
        notifyDataSetChanged();
    }

    public void setOnAdapterViewChangeListener(OnAdapterViewChangeListener adapterViewChangeListener){
        this.adapterViewChangeListener = adapterViewChangeListener;
    }


    @Override
    public int getCount() {
        return songPlayEntityList.size();
    }


    @Override
    public Object getItem(int position) {
        return songPlayEntityList.get(position);
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
        SongPlayEntity songPlayEntity = (SongPlayEntity) getItem(position);
        viewHandler.initData(songPlayEntity);
        return convertView;
    }

    public class ViewHandler {
        // show item
        TextView createName;
        TextView createTime;
        // hidden item
        SeekBar playSeekbar;
        TextView currentTime;
        TextView tvDuration;
        ImageButton pauseBt;

        void initView(View convertView) {
            viewHandler.createName = (TextView) convertView.findViewById(R.id.file_create_time);
            viewHandler.createTime = (TextView) convertView.findViewById(R.id.file_create_date);
            viewHandler.playSeekbar = (SeekBar) convertView.findViewById(R.id.play_progress_bar);
            viewHandler.currentTime = (TextView) convertView.findViewById(R.id.current_time);
            viewHandler.tvDuration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHandler.pauseBt = (ImageButton) convertView.findViewById(R.id.pause_bt);
        }

        void initData(SongPlayEntity songPlayEntity) {
            viewHandler.createName.setText(songPlayEntity.getPlayName());
            viewHandler.createTime.setText(songPlayEntity.getPlayArtist());
            viewHandler.tvDuration.setText(songPlayEntity.getPlayDuration());
            // set SeekBar value
            viewHandler.playSeekbar.setMax(songPlayEntity.getMaxDuration());
            viewHandler.playSeekbar.setProgress(sekCurPosition);
            viewHandler.currentTime.setText(StringFormat.durationToString(sekCurPosition));
            updateUI(playState);
            seekBarChange(viewHandler.playSeekbar);
            buttonClick(viewHandler.pauseBt);
        }



        private void updateUI(int mState) {
            switch (mState) {
                case PlayService.DISPLAY_STATE_STOP:
                    viewHandler.playSeekbar.setProgress(0);
                    viewHandler.currentTime.setText(StringFormat.durationToString(0));
                    viewHandler.playSeekbar.setEnabled(true);
                    viewHandler.pauseBt.setEnabled(true);
                    viewHandler.pauseBt.setBackgroundResource(R.drawable.play_selector);
                    viewHandler.pauseBt.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play01));
                    break;
                case PlayService.DISPLAY_STATE_PALY:
                    viewHandler.pauseBt.setEnabled(true);
                    viewHandler.pauseBt.setBackgroundResource(R.drawable.pause_selector);
                    viewHandler.pauseBt.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pause02));
                    viewHandler.playSeekbar.setEnabled(true);
                    break;
                case PlayService.DISPLAY_STATE_PAUSE:
                    viewHandler.pauseBt.setEnabled(true);
                    viewHandler.pauseBt.setBackgroundResource(R.drawable.play_selector);
                    viewHandler.pauseBt.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play02));
                    viewHandler.playSeekbar.setEnabled(true);
                    break;
            }
        }

        public void seekBarChange(SeekBar seekBar) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    adapterViewChangeListener.onStartTrackingTouch();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int changePosition = seekBar.getProgress();
                    adapterViewChangeListener.onStopTrackingTouch(changePosition);
                }
            });
        }

        public void buttonClick(ImageButton imageButton){
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterViewChangeListener.onClicks(v);
                }
            });
        }

    }


}
