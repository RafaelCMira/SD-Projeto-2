package sd2223.trab1.clients.rest;


import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sd2223.trab1.api.PushMessage;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.java.FeedsPush;

import static sd2223.trab1.api.rest.FeedsService.SECRET;

public class RestFeedsPushClient extends RestFeedsClient implements FeedsPush {

    public RestFeedsPushClient(String serverURI) {
        super(serverURI);
    }

    @Override
    public Result<Void> push_PushMessage(String secret, PushMessage msg) {
        return super.reTry(() -> clt_pushMessage(secret, msg));
    }

    @Override
    public Result<Void> push_updateFollowers(String secret, String user, String follower, boolean following) {
        return super.reTry(() -> clt_updateFollowers(secret, user, follower, following));
    }

    private Result<Void> clt_pushMessage(String secret, PushMessage pm) {
        Response r = target.queryParam(SECRET, secret).request()
                .post(Entity.entity(pm, MediaType.APPLICATION_JSON));

        return super.toJavaResult(r, Void.class);
    }

    private Result<Void> clt_updateFollowers(String secret, String user, String follower, boolean following) {
        Response r = target.path("followers").path(user).path(follower)
                .queryParam(SECRET, secret)
                .request()
                .put(Entity.entity(following, MediaType.APPLICATION_JSON));

        return super.toJavaResult(r, Void.class);
    }
}
