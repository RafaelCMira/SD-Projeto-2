package sd2223.trab1.servers.replication;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.rest.FeedsService;
import sd2223.trab1.servers.Domain;
import sd2223.trab1.servers.kafka.KafkaOperation;
import sd2223.trab1.servers.kafka.KafkaPublisher;
import sd2223.trab1.servers.kafka.KafkaUtils;
import sd2223.trab1.servers.kafka.sync.SyncPoint;
import sd2223.trab1.servers.rest.RestResource;

import java.util.List;

public class ReplicationFeedsResource<T extends Feeds> extends RestResource implements FeedsService {

    public static final String KAFKA_BROKERS = "kafka:9092";

    private static final int HTTP_OK = 200;
    protected KafkaPublisher publisher;
    protected SyncPoint<String> sync;
    protected long serverVersion;

    protected String topic;

    public ReplicationFeedsResource(T impl) {
        this.impl = impl;
        serverVersion = -1;
        publisher = KafkaPublisher.createPublisher(KAFKA_BROKERS);
        sync = SyncPoint.getInstance();
        topic = Domain.get();
        KafkaUtils.createTopic(Domain.get());
    }

    final protected T impl;

    @Override
    public long postMessage(Long version, String user, String pwd, Message msg) {
        if (version != null && version > serverVersion) {
            sync.waitForVersion(version, Integer.MAX_VALUE);
        }

        if (sync.getVersion() < serverVersion) {
            sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        }

        KafkaOperation op = new KafkaOperation(KafkaOperation.POST_MESSAGE, user, pwd, null, msg, -1, -1);
        serverVersion = publisher.publish(topic, Domain.get(), op);
        sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        throw new WebApplicationException(Response.status(HTTP_OK).header(HEADER_VERSION, serverVersion)
                .encoding(MediaType.APPLICATION_JSON).entity(msg.getId()).build());
    }

    @Override
    public void removeFromPersonalFeed(Long version, String user, long mid, String pwd) {
        if (version != null && version > serverVersion) {
            sync.waitForVersion(version, Integer.MAX_VALUE);
        }

        if (sync.getVersion() < serverVersion) {
            sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        }

        KafkaOperation op = new KafkaOperation(KafkaOperation.REMOVE_FROM_PERSONAL, user, pwd, null, null, mid, -1);
        serverVersion = publisher.publish(topic, Domain.get(), op);
        throw new WebApplicationException(Response.status(204).header(HEADER_VERSION, serverVersion).build());
    }

    @Override
    public Message getMessage(Long version, String user, long mid) {
        if (version != null && version > serverVersion) {
            sync.waitForVersion(version, Integer.MAX_VALUE);
            serverVersion = version;
        }

        if (sync.getVersion() < serverVersion) {
            sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        }

        var result = super.fromJavaResult(impl.getMessage(user, mid));
        throw new WebApplicationException(Response.status(200).header(HEADER_VERSION, serverVersion)
                .encoding(MediaType.APPLICATION_JSON).entity(result).build());
    }

    @Override
    public List<Message> getMessages(Long version, String user, long time) {
        if (version != null && version > serverVersion) {
            sync.waitForVersion(version, Integer.MAX_VALUE);
            serverVersion = version;
        }

        if (sync.getVersion() < serverVersion) {
            sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        }

        var result = fromJavaResult(impl.getMessages(user, time));
        throw new WebApplicationException(Response.status(200).header(HEADER_VERSION, serverVersion)
                .encoding(MediaType.APPLICATION_JSON).entity(result).build());
    }

    @Override
    public void subUser(Long version, String user, String userSub, String pwd) {
        if (version != null && version > serverVersion) {
            sync.waitForVersion(version, Integer.MAX_VALUE);
        }

        if (sync.getVersion() < serverVersion) {
            sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        }

        KafkaOperation op = new KafkaOperation(KafkaOperation.SUB, user, pwd, userSub, null, -1, -1);
        serverVersion = publisher.publish(topic, Domain.get(), op);
        throw new WebApplicationException(Response.status(204).header(HEADER_VERSION, serverVersion).build());
    }

    @Override
    public void unsubscribeUser(Long version, String user, String userSub, String pwd) {
        if (version != null && version > serverVersion) {
            sync.waitForVersion(version, Integer.MAX_VALUE);
        }

        if (sync.getVersion() < serverVersion) {
            sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        }

        KafkaOperation op = new KafkaOperation(KafkaOperation.UNSUB, user, pwd, userSub, null, -1, -1);
        serverVersion = publisher.publish(topic, Domain.get(), op);
        throw new WebApplicationException(Response.status(204).header(HEADER_VERSION, serverVersion).build());
    }

    @Override
    public List<String> listSubs(Long version, String user) {
        if (version != null && version > serverVersion) {
            sync.waitForVersion(version, Integer.MAX_VALUE);
            serverVersion = version;
        }

        if (sync.getVersion() < serverVersion) {
            sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        }

        var result = fromJavaResult(impl.listSubs(user));
        throw new WebApplicationException(Response.status(200).header(HEADER_VERSION, serverVersion)
                .encoding(MediaType.APPLICATION_JSON).entity(result).build());
    }

    @Override
    public void deleteUserFeed(Long version, String user) {
        if (version != null && version > serverVersion) {
            sync.waitForVersion(version, Integer.MAX_VALUE);
        }

        if (sync.getVersion() < serverVersion) {
            sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        }


    }
}
