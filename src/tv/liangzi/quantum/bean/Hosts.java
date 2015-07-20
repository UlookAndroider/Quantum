package tv.liangzi.quantum.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by invinjun on 2015/7/16.
 */
public class Hosts implements Parcelable{
    private Publish publish;
    private Play play;

    protected Hosts(Parcel in) {
        publish = in.readParcelable(Publish.class.getClassLoader());
        play = in.readParcelable(Play.class.getClassLoader());
    }

    public static final Creator<Hosts> CREATOR = new Creator<Hosts>() {
        @Override
        public Hosts createFromParcel(Parcel in) {
            return new Hosts(in);
        }

        @Override
        public Hosts[] newArray(int size) {
            return new Hosts[size];
        }
    };

    public Publish getPublish() {
        return publish;
    }

    public void setPublish(Publish publish) {
        this.publish = publish;
    }

    public Play getPlay() {
        return play;
    }

    public void setPlay(Play play) {
        this.play = play;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(publish,i);
        parcel.writeParcelable(play, i);
    }
}
