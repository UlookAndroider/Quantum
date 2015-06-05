package tv.liangzi.quantum.bean;

import java.util.List;

/**
 * Created by invinjun on 2015/5/30.
 */
public class LiveVideoStatus {
    private String responseCode;
    private String responseMsg;
    public List<Live> lives;

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




    public class tags{



    }
}
