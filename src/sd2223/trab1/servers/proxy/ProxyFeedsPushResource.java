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
    public void push_PushMessage(Long version, PushMessage msg) {
        super.fromJavaResult(impl.push_PushMessage(msg));
    }

    @Override
    public void push_updateFollowers(Long version, String user, String follower, boolean following) {
        super.fromJavaResult(impl.push_updateFollowers(user, follower, following));
    }
}
