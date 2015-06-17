package tv.liangzi.quantum.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by invinjun on 2015/5/14.
 */
public class PeopleDetails implements Parcelable {
    private String responseCode;
    private String responseMsg;

    private int userId;
    private boolean isFollow;
    private long created;
    private String nickName;
    private String photo;

    private String sign;
    private String wechatNickName;
    private String sinaNickName;
    private int focusNum;
    private int fansNum;
    private double lng;
    private double lat;
    private String addr;
    private String commonUploadToken;
    private String especialUploadToken;
    private String accessToken;
    protected PeopleDetails(){

    }
    protected PeopleDetails(Parcel in) {
        responseCode = in.readString();
        responseMsg = in.readString();
        userId = in.readInt();
        created = in.readLong();
        nickName = in.readString();
        photo = in.readString();
        sign = in.readString();
        wechatNickName = in.readString();
        sinaNickName = in.readString();
        focusNum = in.readInt();
        fansNum = in.readInt();
        lng = in.readDouble();
        lat = in.readDouble();
        addr = in.readString();
        commonUploadToken = in.readString();
        especialUploadToken = in.readString();
        accessToken = in.readString();
    }

    public static final Creator<PeopleDetails> CREATOR = new Creator<PeopleDetails>() {
        @Override
        public PeopleDetails createFromParcel(Parcel in) {
            return new PeopleDetails(in);
        }

        @Override
        public PeopleDetails[] newArray(int size) {
            return new PeopleDetails[size];
        }
    };

    public String getCommonUploadToken() {
        return commonUploadToken;
    }

    public void setCommonUploadToken(String commonUploadToken) {
        this.commonUploadToken = commonUploadToken;
    }

    public String getEspecialUploadToken() {
        return especialUploadToken;
    }

    public void setEspecialUploadToken(String especialUploadToken) {
        this.especialUploadToken = especialUploadToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setIsFollow(boolean isFollow) {
        this.isFollow = isFollow;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getWechatNickName() {
        return wechatNickName;
    }

    public void setWechatNickName(String wechatNickName) {
        this.wechatNickName = wechatNickName;
    }

    public String getSinaNickName() {
        return sinaNickName;
    }

    public void setSinaNickName(String sinaNickName) {
        this.sinaNickName = sinaNickName;
    }

    public int getFocusNum() {
        return focusNum;
    }

    public void setFocusNum(int focusNum) {
        this.focusNum = focusNum;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(responseCode);
        parcel.writeString(responseMsg);
        parcel.writeInt(userId);
        parcel.writeLong(created);
        parcel.writeString(nickName);
        parcel.writeString(photo);
        parcel.writeString(sign);
        parcel.writeString(wechatNickName);
        parcel.writeString(sinaNickName);
        parcel.writeInt(focusNum);
        parcel.writeInt(fansNum);
        parcel.writeDouble(lng);
        parcel.writeDouble(lat);
        parcel.writeString(addr);
        parcel.writeString(commonUploadToken);
        parcel.writeString(especialUploadToken);
        parcel.writeString(accessToken);
    }
}
