package sd2223.trab1.servers.proxy;

import sd2223.trab1.api.PushMessage;
import sd2223.trab1.api.java.FeedsPush;
import sd2223.trab1.api.rest.FeedsServicePush;
import sd2223.trab1.servers.mastodon.Mastodon;

public class ProxyFeedsPushResource extends ProxyFeedsResource<FeedsPush> implements FeedsServicePush {

    public ProxyFeedsPushResource() {
        super(Mastodon.getInstance());
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
