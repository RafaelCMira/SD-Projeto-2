package sd2223.trab1.servers.replication;

import jakarta.inject.Singleton;
import sd2223.trab1.api.PushMessage;
import sd2223.trab1.api.java.FeedsPush;
import sd2223.trab1.api.rest.FeedsServicePush;
import sd2223.trab1.servers.java.JavaFeedsPush;

@Singleton
public class ReplicationFeedsPushResource extends ReplicationFeedsResource<FeedsPush> implements FeedsServicePush {

    public ReplicationFeedsPushResource() {
        super(JavaFeedsPush.getInstance());
    }

    @Override
    public void push_PushMessage(PushMessage msg) {

    }

    @Override
    public void push_updateFollowers(String user, String follower, boolean following) {

    }
}
