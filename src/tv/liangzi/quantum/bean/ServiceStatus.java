package tv.liangzi.quantum.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by invinjun on 2015/5/11.
 */
public class ServiceStatus implements Serializable{
    private String responseCode;

    private String responseMsg;
    private List<Video> videos;
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
    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
    public List<Video> getVideos() {
        return videos;
    }
    public static class Video implements Serializable {
        private int videoId;
        private  int userId;
        private  String nickName;
        private  String photo;
        private  boolean isLike;
        private String downloadUrl;
        private  String shareUrl;
        private String playUrl;
        private String duration;
        private  String title;
        private  String describe;
        private  String img;
        private  String isManual;//
        private String commentId;

        private List<VideoTag> tags;
        private List<VideoComment> comments;
        private int scope;
        private  int shares;//
        private int downloads;
        private  int likes;
        private  long created;//

        public int getScope() {
            return scope;
        }

        public void setScope(int scope) {
            this.scope = scope;
        }

        public int getShares() {
            return shares;
        }

        public void setShares(int shares) {
            this.shares = shares;
        }

        public int getDownloads() {
            return downloads;
        }

        public void setDownloads(int downloads) {
            this.downloads = downloads;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public long getCreated() {
            return created;
        }

        public void setCreated(long created) {
            this.created = created;
        }
        public int getVideoId() {
            return videoId;
        }

        public void setVideoId(int videoId) {
            this.videoId = videoId;
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

        public boolean isLike() {
            return isLike;
        }

        public void setIsLike(boolean isLike) {
            this.isLike = isLike;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }

        public String getPlayUrl() {
            return playUrl;
        }

        public void setPlayUrl(String playUrl) {
            this.playUrl = playUrl;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
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

        public String getIsManual() {
            return isManual;
        }

        public void setIsManual(String isManual) {
            this.isManual = isManual;
        }

        public String getCommentId() {
            return commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

        public List<VideoTag> getTags() {
            return tags;
        }

        public void setTags(List<VideoTag> tags) {
            this.tags = tags;
        }

        public List<VideoComment> getComments() {
            return comments;
        }

        public void setComments(List<VideoComment> comments) {
            this.comments = comments;
        }

        public static class VideoTag  implements Serializable{
            /**
             *
             */

        }
        public static class VideoComment  implements  Serializable{
            private String commentId;
            private int userId;
            private String nickName;
            private String photo;
            private String content;
            private long created;


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

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public long getCreated() {
                return created;
            }

            public void setCreated(long created) {
                this.created = created;
            }

            public String getCommentId() {
                return commentId;
            }

            public void setCommentId(String commentId) {
                this.commentId = commentId;
            }
        }

    }

}
