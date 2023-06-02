package sd2223.trab1.servers.kafka;

import sd2223.trab1.api.Message;

import java.io.Serializable;


public class KafkaMsg implements Serializable {

    public static final String POST_MESSAGE = "POST_MESSAGE";
    public static final String REMOVE_FROM_PERSONAL = "REMOVE_FROM_PERSONAL";
    public static final String SUB = "SUB";
    public static final String UNSUB = "UNSUB";
    public static final String DELETE_USER_FEED = "DELETE_USER_FEED";

    private String operation;
    private String user;
    private String pwd;
    private String userSub;
    private Message msg;
    private long mid;
    private long time;
    
    public KafkaMsg() {
        this.operation = null;
        this.user = null;
        this.pwd = null;
        this.userSub = null;
        this.msg = null;
        this.mid = -1;
        this.time = -1;
    }

    public KafkaMsg(String operation, String user, String pwd, String userSub, Message msg, long mid, long time) {
        this.operation = operation;
        this.user = user;
        this.pwd = pwd;
        this.userSub = userSub;
        this.msg = msg;
        this.mid = mid;
        this.time = time;
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
}
