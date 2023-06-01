package sd2223.trab1.servers.kafka;

import sd2223.trab1.api.Message;

import java.io.Serializable;

public class KafkaMsg implements Serializable {

    private String user;
    private String pwd;
    private String userSub;
    private String op;
    private Message msg;
    private long mid;
    private long time;

    public KafkaMsg() {

    }

    public KafkaMsg(String user, String pwd, String userSub, String op, Message msg, long mid, long time) {
        this.user = user;
        this.pwd = pwd;
        this.userSub = userSub;
        this.op = op;
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

    public String getOp() {
        return op;
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
}
