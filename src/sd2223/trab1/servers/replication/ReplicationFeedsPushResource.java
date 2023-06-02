package sd2223.trab1.servers.replication;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import sd2223.trab1.api.PushMessage;
import sd2223.trab1.api.java.FeedsPush;
import sd2223.trab1.api.rest.FeedsServicePush;
import sd2223.trab1.servers.Domain;
import sd2223.trab1.servers.java.JavaFeedsPush;
import sd2223.trab1.servers.kafka.KafkaMsg;

@Singleton
public class ReplicationFeedsPushResource extends ReplicationFeedsResource<FeedsPush> implements FeedsServicePush {

    public ReplicationFeedsPushResource() {
        super(new JavaFeedsPush());
    }

    @Override
    public void push_PushMessage(Long version, PushMessage msg) {
        KafkaMsg op = new KafkaMsg(KafkaMsg.PUSH_MESSAGE, null, null, null, null, -1, -1, msg, null, false);
        if (sync.getVersion() < serverVersion) {
            sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        }
        publisher.publish(topic, Domain.get(), op);
        throw new WebApplicationException(Response.status(HTTP_OK_VOID).header(HEADER_VERSION, serverVersion).build());
    }

    @Override
    public void push_updateFollowers(Long version, String user, String follower, boolean following) {
        KafkaMsg op = new KafkaMsg(KafkaMsg.PUSH_MESSAGE, user, null, null, null, -1, -1, null, follower, following);
        if (sync.getVersion() < serverVersion) {
            sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        }
        publisher.publish(topic, Domain.get(), op);
        throw new WebApplicationException(Response.status(HTTP_OK_VOID).header(HEADER_VERSION, serverVersion).build());
    }
}
