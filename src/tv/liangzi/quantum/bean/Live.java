package tv.liangzi.quantum.bean;

import android.os.Parcel;
import android.os.Parcelable;

public  class Live implements Parcelable{

	public Live(){

	}

	private String responseCode;
	private String responseMsg;
	private int liveId;
	private int userId;
	private String nickName;
	private String photo;
	private String title;
	private String describe;
	private String img;
	private String rtmpPublishUrl;
	private String hlsPublishUrl;
	private String rtmpPlayUrl;
	private String hlsPlayUrl;
	private String groupid;
	private String shareUrl;
	private int scope;
	private int subscibeId;
	private int subscibes;
	private int shares;
	private int likes;
	private int audiences;
	private int state;
	private long reserved;
	private long begin;
	private String chatroomId;
	private Stream stream;


	protected Live(Parcel in) {
		responseCode = in.readString();
		responseMsg = in.readString();
		liveId = in.readInt();
		userId = in.readInt();
		nickName = in.readString();
		photo = in.readString();
		title = in.readString();
		describe = in.readString();
		img = in.readString();
		rtmpPublishUrl = in.readString();
		hlsPublishUrl = in.readString();
		rtmpPlayUrl = in.readString();
		hlsPlayUrl = in.readString();
		groupid = in.readString();
		shareUrl = in.readString();
		scope = in.readInt();
		subscibeId = in.readInt();
		subscibes = in.readInt();
		shares = in.readInt();
		likes = in.readInt();
		audiences = in.readInt();
		state = in.readInt();
		reserved = in.readLong();
		begin = in.readLong();
		chatroomId = in.readString();
		stream = in.readParcelable(Stream.class.getClassLoader());
	}

	public static final Creator<Live> CREATOR = new Creator<Live>() {
		@Override
		public Live createFromParcel(Parcel in) {
			return new Live(in);
		}

		@Override
		public Live[] newArray(int size) {
			return new Live[size];
		}
	};

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

	public int getLiveId() {
		return liveId;
	}

	public void setLiveId(int liveId) {
		this.liveId = liveId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getRtmpPublishUrl() {
		return rtmpPublishUrl;
	}

	public void setRtmpPublishUrl(String rtmpPublishUrl) {
		this.rtmpPublishUrl = rtmpPublishUrl;
	}

	public String getHlsPublishUrl() {
		return hlsPublishUrl;
	}

	public void setHlsPublishUrl(String hlsPublishUrl) {
		this.hlsPublishUrl = hlsPublishUrl;
	}

	public String getRtmpPlayUrl() {
		return rtmpPlayUrl;
	}

	public void setRtmpPlayUrl(String rtmpPlayUrl) {
		this.rtmpPlayUrl = rtmpPlayUrl;
	}

	public String getHlsPlayUrl() {
		return hlsPlayUrl;
	}

	public void setHlsPlayUrl(String hlsPlayUrl) {
		this.hlsPlayUrl = hlsPlayUrl;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public int getSubscibeId() {
		return subscibeId;
	}

	public void setSubscibeId(int subscibeId) {
		this.subscibeId = subscibeId;
	}

	public int getSubscibes() {
		return subscibes;
	}

	public void setSubscibes(int subscibes) {
		this.subscibes = subscibes;
	}

	public int getShares() {
		return shares;
	}

	public void setShares(int shares) {
		this.shares = shares;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getAudiences() {
		return audiences;
	}

	public void setAudiences(int audiences) {
		this.audiences = audiences;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getReserved() {
		return reserved;
	}

	public void setReserved(long reserved) {
		this.reserved = reserved;
	}

	public long getBegin() {
		return begin;
	}

	public void setBegin(long begin) {
		this.begin = begin;
	}

	public String getChatroomId() {
		return chatroomId;
	}

	public void setChatroomId(String chatroomId) {
		this.chatroomId = chatroomId;
	}

	public Stream getStream() {
		return stream;
	}

	public void setStream(Stream stream) {
		this.stream = stream;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(responseCode);
		parcel.writeString(responseMsg);
		parcel.writeInt(liveId);
		parcel.writeInt(userId);
		parcel.writeString(nickName);
		parcel.writeString(photo);
		parcel.writeString(title);
		parcel.writeString(describe);
		parcel.writeString(img);
		parcel.writeString(rtmpPublishUrl);
		parcel.writeString(hlsPublishUrl);
		parcel.writeString(rtmpPlayUrl);
		parcel.writeString(hlsPlayUrl);
		parcel.writeString(groupid);
		parcel.writeString(shareUrl);
		parcel.writeInt(scope);
		parcel.writeInt(subscibeId);
		parcel.writeInt(subscibes);
		parcel.writeInt(shares);
		parcel.writeInt(likes);
		parcel.writeInt(audiences);
		parcel.writeInt(state);
		parcel.writeLong(reserved);
		parcel.writeLong(begin);
		parcel.writeString(chatroomId);
		parcel.writeParcelable(stream, i);
	}
}
