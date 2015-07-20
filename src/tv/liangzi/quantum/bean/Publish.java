package tv.liangzi.quantum.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by invinjun on 2015/7/16.
 */
public class Publish implements Parcelable{
    private String rtmp;

    protected Publish(Parcel in) {
        rtmp = in.readString();
    }

    public static final Creator<Publish> CREATOR = new Creator<Publish>() {
        @Override
        public Publish createFromParcel(Parcel in) {
            return new Publish(in);
        }

        @Override
        public Publish[] newArray(int size) {
            return new Publish[size];
        }
    };

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
        parcel.writeString(rtmp);
    }
}
