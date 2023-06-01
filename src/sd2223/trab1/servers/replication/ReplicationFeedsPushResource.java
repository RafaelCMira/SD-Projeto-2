package sd2223.trab1.servers.replication;

import sd2223.trab1.api.PushMessage;
import sd2223.trab1.api.java.FeedsPush;
import sd2223.trab1.api.rest.FeedsServicePush;
import sd2223.trab1.servers.Domain;
import sd2223.trab1.servers.kafka.KafkaPublisher;
import sd2223.trab1.servers.kafka.KafkaUtils;
import sd2223.trab1.servers.kafka.sync.SyncPoint;

public class ReplicationFeedsPushResource extends ReplicationFeedsResource<FeedsPush> implements FeedsServicePush {

    KafkaPublisher kafkaPublisher;
    SyncPoint<String> sync;

    private long serverVersion;

    public ReplicationFeedsPushResource(FeedsPush impl) {
        super(impl);
        serverVersion = 1;
        KafkaUtils.createTopic(Domain.get());
        kafkaPublisher = KafkaPublisher.createPublisher("kafka:9092");
        sync = SyncPoint.getInstance();
    }

    @Override
    public void push_PushMessage(PushMessage msg) {

    }

    @Override
    public void push_updateFollowers(String user, String follower, boolean following) {

    }
}
