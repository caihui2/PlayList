package activity.notes.com.entity;

/**
 * Created by yangcaihui on 15/4/10.
 */
public class PlayEntity {

    private String playName;
    private String playTime;
    private String playDuration;
    private String playData;
    private int maxDuration;


    public PlayEntity(String playName, String playTime, String playDuration, String playData,int maxDuration) {
        this.playName = playName;
        this.playTime = playTime;
        this.playDuration = playDuration;
        this.playData = playData;
        this.maxDuration = maxDuration;
    }


    public String getPlayName() {
        return playName;
    }

    public void setPlayName(String playName) {
        this.playName = playName;
    }

    public String getPlayDuration() {
        return playDuration;
    }

    public void setPlayDuration(String playDuration) {
        this.playDuration = playDuration;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

    public String getPlayData() {
        return playData;
    }

    public void setPlayData(String playData) {
        this.playData = playData;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }
}
