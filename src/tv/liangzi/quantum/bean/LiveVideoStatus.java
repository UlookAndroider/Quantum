package tv.liangzi.quantum.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by invinjun on 2015/5/30.
 */
public class LiveVideoStatus implements Parcelable{
    private String responseCode;
    private String responseMsg;
    public List<Live> lives;

    protected LiveVideoStatus(Parcel in) {
        responseCode = in.readString();
        responseMsg = in.readString();
        lives = in.createTypedArrayList(Live.CREATOR);
    }

    public static final Creator<LiveVideoStatus> CREATOR = new Creator<LiveVideoStatus>() {
        @Override
        public LiveVideoStatus createFromParcel(Parcel in) {
            return new LiveVideoStatus(in);
        }

        @Override
        public LiveVideoStatus[] newArray(int size) {
            return new LiveVideoStatus[size];
        }
    };

    public List<Live> getLives() {
        return lives;
    }

    public void setLives(List<Live> lives) {
        this.lives = lives;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(responseCode);
        parcel.writeString(responseMsg);
        parcel.writeTypedList(lives);
    }


    public class tags{



    }
}
