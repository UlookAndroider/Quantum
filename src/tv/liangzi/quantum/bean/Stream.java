package tv.liangzi.quantum.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by invinjun on 2015/7/16.
 */
public class Stream implements Parcelable {
    private String id;
    private String hub;
    private String title;
    private String publishKey;
    private String publishSecurity;
    private Hosts hosts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHub() {
        return hub;
    }

    public void setHub(String hub) {
        this.hub = hub;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishKey() {
        return publishKey;
    }

    public void setPublishKey(String publishKey) {
        this.publishKey = publishKey;
    }

    public String getPublishSecurity() {
        return publishSecurity;
    }

    public void setPublishSecurity(String publishSecurity) {
        this.publishSecurity = publishSecurity;
    }

    public Hosts getHosts() {
        return hosts;
    }

    public void setHosts(Hosts hosts) {
        this.hosts = hosts;
    }

    protected Stream(Parcel in) {
        id = in.readString();
        hub = in.readString();
        title = in.readString();
        publishKey = in.readString();
        publishSecurity = in.readString();
        hosts = in.readParcelable(Hosts.class.getClassLoader());
    }

    public static final Creator<Stream> CREATOR = new Creator<Stream>() {
        @Override
        public Stream createFromParcel(Parcel in) {
            return new Stream(in);
        }

        @Override
        public Stream[] newArray(int size) {
            return new Stream[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(hub);
        parcel.writeString(title);
        parcel.writeString(publishKey);
        parcel.writeString(publishSecurity);
        parcel.writeParcelable(hosts, i);
    }
}
