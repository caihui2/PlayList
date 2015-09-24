package activity.netos.com.playlist.Util;

import android.content.Context;

import java.text.SimpleDateFormat;

import activity.netos.com.playlist.R;

/**
 * Created by yangcaihui on 15/9/24.
 */
public class StringFormat {

    public static String disposeTime(String playTime) {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormater.format(Long.parseLong(playTime)).toString();
    }

    public static String formatDuration(int duration,Context mContext) {
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
}
