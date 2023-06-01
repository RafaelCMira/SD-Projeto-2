package sd2223.trab1.servers.replication;

import sd2223.trab1.api.Message;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.rest.FeedsService;
import sd2223.trab1.servers.rest.RestResource;

import java.util.List;

public class ReplicationFeedsResource<T extends Feeds> extends RestResource implements FeedsService {

    public ReplicationFeedsResource(T impl) {
        this.impl = impl;
    }

    final protected T impl;

    @Override
    public long postMessage(Long version, String user, String pwd, Message msg) {
        return 0;
    }

    @Override
    public void removeFromPersonalFeed(Long version, String user, long mid, String pwd) {

    }

    @Override
    public Message getMessage(Long version, String user, long mid) {
        return null;
    }

    @Override
    public List<Message> getMessages(Long version, String user, long time) {
        return null;
    }

    @Override
    public void subUser(Long version, String user, String userSub, String pwd) {

    }

    @Override
    public void unsubscribeUser(Long version, String user, String userSub, String pwd) {

    }

    @Override
    public List<String> listSubs(Long version, String user) {
        return null;
    }

    @Override
    public void deleteUserFeed(String user) {

    }
}
