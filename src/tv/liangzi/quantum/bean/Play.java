package tv.liangzi.quantum.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by invinjun on 2015/7/16.
 */
public class Play implements Parcelable{
    private String hls;
    private String rtmp;

    protected Play(Parcel in) {
        hls = in.readString();
        rtmp = in.readString();
    }

    public static final Creator<Play> CREATOR = new Creator<Play>() {
        @Override
        public Play createFromParcel(Parcel in) {
            return new Play(in);
        }

        @Override
        public Play[] newArray(int size) {
            return new Play[size];
        }
    };

    public String getHls() {
        return hls;
    }

    public void setHls(String hls) {
        this.hls = hls;
    }

    public String getRtmp() {
        return rtmp;
    }

    public void setRtmp(String rtmp) {
        this.rtmp = rtmp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(hls);
        parcel.writeString(rtmp);
    }
}
