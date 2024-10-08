package sd2223.trab1.servers.rest;

import jakarta.inject.Singleton;
import sd2223.trab1.api.PushMessage;
import sd2223.trab1.api.java.FeedsPush;
import sd2223.trab1.api.rest.FeedsServicePush;
import sd2223.trab1.servers.java.JavaFeedsPush;

@Singleton
public class RestFeedsPushResource extends RestFeedsResource<FeedsPush> implements FeedsServicePush {

    public RestFeedsPushResource() {
        super(new JavaFeedsPush());
    }

    @Override
    public void push_PushMessage(String secret, Long version, PushMessage msg) {
        super.fromJavaResult(impl.push_PushMessage(secret, msg));
    }

    @Override
    public void push_updateFollowers(String secret, Long version, String user, String follower, boolean following) {
        super.fromJavaResult(impl.push_updateFollowers(secret, user, follower, following));
    }
}
