package activity.notes.com.entity;

/**
 * Created by yangcaihui on 15/4/10.
 */
public class SongPlayEntity {

    private String playName;
    private String playArtist;
    private String playDuration;
    private String playData;
    private int maxDuration;


    public SongPlayEntity(String playName, String playArtist, String playDuration, String playData, int maxDuration) {
        this.playName = playName;
        this.playArtist = playArtist;
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

    public String getPlayArtist() {
        return playArtist;
    }

    public void setPlayArtist(String playArtist) {
        this.playArtist = playArtist;
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
