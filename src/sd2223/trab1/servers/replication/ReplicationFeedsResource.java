package sd2223.trab1.servers.replication;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.rest.FeedsService;
import sd2223.trab1.servers.Domain;
import sd2223.trab1.servers.kafka.KafkaMsg;
import sd2223.trab1.servers.kafka.KafkaPublisher;
import sd2223.trab1.servers.kafka.KafkaUtils;
import sd2223.trab1.servers.kafka.sync.SyncPoint;
import sd2223.trab1.servers.rest.RestFeedsPushResource;

import java.util.List;
import java.util.logging.Logger;

@Singleton
public class ReplicationFeedsResource<T extends Feeds> extends RestFeedsPushResource implements FeedsService {
    protected static final int HTTP_OK = 200;
    protected static final int HTTP_OK_VOID = 204;
    public static final String KAFKA_BROKERS = "kafka:9092";
    protected final KafkaPublisher publisher;
    protected final SyncPoint<String> sync;
    protected long serverVersion;
    protected final String topic;

    private static final Logger log = Logger.getLogger(ReplicationFeedsResource.class.getName());

    final protected T impl;

    public ReplicationFeedsResource(T impl) {
        this.impl = impl;
        this.serverVersion = -1;
        this.publisher = KafkaPublisher.createPublisher(KAFKA_BROKERS);
        this.sync = SyncPoint.getInstance();
        this.topic = Domain.get();
        KafkaUtils.createTopic(topic);
    }

    @Override
    public long postMessage(Long version, String user, String pwd, Message msg) {
        writeWaitIfNeeded(version);

        Long mid = super.fromJavaResult(impl.getMsgServerId(user, pwd, msg));
        msg.setId(mid);

        KafkaMsg kafkaMsg = new KafkaMsg(KafkaMsg.POST_MESSAGE, user, pwd, null, msg, -1, -1, null, null, false);
        serverVersion = publisher.publish(topic, Domain.get(), kafkaMsg);
        sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        throw new WebApplicationException(Response.status(HTTP_OK).header(HEADER_VERSION, serverVersion)
                .encoding(MediaType.APPLICATION_JSON).entity(msg.getId()).build());
        // aqui é onde é retornado ao cliente, por isso se nao definirmos aqui o setI devolve sempre -1
    }

    @Override
    public void removeFromPersonalFeed(Long version, String user, long mid, String pwd) {
        writeWaitIfNeeded(version);

        KafkaMsg op = new KafkaMsg(KafkaMsg.REMOVE_FROM_PERSONAL, user, pwd, null, null, mid, -1, null, null, false);
        serverVersion = publisher.publish(topic, Domain.get(), op);
        throw new WebApplicationException(Response.status(HTTP_OK_VOID).header(HEADER_VERSION, serverVersion).build());
    }

    @Override
    public Message getMessage(Long version, String user, long mid) {
        readWaitIfNeeded(version);

        var result = super.fromJavaResult(impl.getMessage(user, mid));
        throw new WebApplicationException(Response.status(HTTP_OK).header(HEADER_VERSION, serverVersion)
                .encoding(MediaType.APPLICATION_JSON).entity(result).build());
    }

    @Override
    public List<Message> getMessages(Long version, String user, long time) {
        readWaitIfNeeded(version);

        var result = super.fromJavaResult(impl.getMessages(user, time));
        throw new WebApplicationException(Response.status(HTTP_OK).header(HEADER_VERSION, serverVersion)
                .encoding(MediaType.APPLICATION_JSON).entity(result).build());
    }

    @Override
    public void subUser(Long version, String user, String userSub, String pwd) {
        writeWaitIfNeeded(version);

        KafkaMsg op = new KafkaMsg(KafkaMsg.SUB, user, pwd, userSub, null, -1, -1, null, null, false);
        serverVersion = publisher.publish(topic, Domain.get(), op);
        throw new WebApplicationException(Response.status(HTTP_OK_VOID).header(HEADER_VERSION, serverVersion).build());
    }

    @Override
    public void unsubscribeUser(Long version, String user, String userSub, String pwd) {
        writeWaitIfNeeded(version);

        KafkaMsg op = new KafkaMsg(KafkaMsg.UNSUB, user, pwd, userSub, null, -1, -1, null, null, false);
        serverVersion = publisher.publish(topic, Domain.get(), op);
        throw new WebApplicationException(Response.status(HTTP_OK_VOID).header(HEADER_VERSION, serverVersion).build());
    }

    @Override
    public List<String> listSubs(Long version, String user) {
        readWaitIfNeeded(version);

        var result = super.fromJavaResult(impl.listSubs(user));
        throw new WebApplicationException(Response.status(HTTP_OK).header(HEADER_VERSION, serverVersion)
                .encoding(MediaType.APPLICATION_JSON).entity(result).build());
    }

    @Override
    public void deleteUserFeed(Long version, String user) {
        writeWaitIfNeeded(version);

        KafkaMsg op = new KafkaMsg(KafkaMsg.DELETE_USER_FEED, user, null, null, null, -1, -1, null, null, false);
        serverVersion = publisher.publish(topic, Domain.get(), op);
        throw new WebApplicationException(Response.status(HTTP_OK_VOID).header(HEADER_VERSION, serverVersion).build());
    }


    protected void writeWaitIfNeeded(Long version) {
        if (version != null && version > serverVersion) {
            sync.waitForVersion(version, Integer.MAX_VALUE);
        }

        if (sync.getVersion() < serverVersion) {
            sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        }
    }

    protected void readWaitIfNeeded(Long version) {
        if (version != null && version > serverVersion) {
            sync.waitForVersion(version, Integer.MAX_VALUE);
            serverVersion = version;
        }

        if (sync.getVersion() < serverVersion) {
            sync.waitForVersion(serverVersion, Integer.MAX_VALUE);
        }
    }

}
