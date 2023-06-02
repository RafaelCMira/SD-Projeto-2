package sd2223.trab1.servers.kafka;

import sd2223.trab1.api.Message;
import sd2223.trab1.api.PushMessage;

import java.io.Serializable;


public class KafkaMsg implements Serializable {

    public static final String POST_MESSAGE = "POST_MESSAGE";
    public static final String REMOVE_FROM_PERSONAL = "REMOVE_FROM_PERSONAL";
    public static final String SUB = "SUB";
    public static final String UNSUB = "UNSUB";
    public static final String DELETE_USER_FEED = "DELETE_USER_FEED";
    public static final String PUSH_MESSAGE = "PUSH_MESSAGE";
    public static final String UPDATE_FOLLOWERS = "UPDATE_FOLLOWERS";

    private String operation;
    private String user;
    private String pwd;
    private String userSub;
    private Message msg;
    private long mid;
    private long time;
    private PushMessage pushMessage;
    private String follower;
    private boolean following;

    public KafkaMsg() {
        this.operation = null;
        this.user = null;
        this.pwd = null;
        this.userSub = null;
        this.msg = null;
        this.mid = -1;
        this.time = -1;
        this.pushMessage = null;
        this.follower = null;
        this.following = false;
    }

    public KafkaMsg(String operation, String user, String pwd, String userSub, Message msg, long mid, long time, PushMessage pushMessage, String follower, boolean following) {
        this.operation = operation;
        this.user = user;
        this.pwd = pwd;
        this.userSub = userSub;
        this.msg = msg;
        this.mid = mid;
        this.time = time;
        this.pushMessage = pushMessage;
        this.follower = follower;
        this.following = following;
    }

    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    public String getUserSub() {
        return userSub;
    }

    public String getOperation() {
        return operation;
    }

    public Message getMsg() {
        return msg;
    }

    public long getMid() {
        return mid;
    }

    public long getTime() {
        return time;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setUserSub(String userSub) {
        this.userSub = userSub;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public PushMessage getPushMessage() {
        return pushMessage;
    }

    public void setPushMessage(PushMessage pushMessage) {
        this.pushMessage = pushMessage;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }
}
