package tv.liangzi.quantum.bean;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * Created by invinjun on 2015/5/14.
 */
public class PeopleDetails implements Serializable {
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


}
