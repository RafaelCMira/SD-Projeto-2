package sd2223.trab1.servers.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import sd2223.trab1.servers.Domain;
import sd2223.trab1.servers.java.JavaFeedsPush;
import sd2223.trab1.servers.kafka.sync.SyncPoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.logging.Logger;

public class TotalOrderExecutor extends Thread implements RecordProcessor {
    static final String FROM_BEGINNING = "earliest";
    static final String KAFKA_BROKERS = "kafka:9092";

    final KafkaSubscriber receiver;
    final SyncPoint<KafkaMsg> sync;
    final JavaFeedsPush impl;

    private static final Logger log = Logger.getLogger(TotalOrderExecutor.class.getName());

    public TotalOrderExecutor(String domain) {
        this.receiver = KafkaSubscriber.createSubscriber(KAFKA_BROKERS, List.of(domain), FROM_BEGINNING);
        this.receiver.start(false, this);
        this.sync = SyncPoint.getInstance();
        this.impl = JavaFeedsPush.getInstance();
    }

    @Override
    public void onReceive(ConsumerRecord<String, byte[]> r) {
        var version = r.offset();
        KafkaMsg msg = deserializeByteArray(r.value());
        switch (msg.getOperation()) {
            case KafkaMsg.POST_MESSAGE -> impl.postMessage(msg.getUser(), msg.getPwd(), msg.getMsg());
            case KafkaMsg.REMOVE_FROM_PERSONAL ->
                    impl.removeFromPersonalFeed(msg.getUser(), msg.getMid(), msg.getPwd());
            case KafkaMsg.SUB -> impl.subUser(msg.getUser(), msg.getUserSub(), msg.getPwd());
            case KafkaMsg.UNSUB -> impl.unsubscribeUser(msg.getUser(), msg.getUserSub(), msg.getPwd());
            case KafkaMsg.DELETE_USER_FEED -> impl.deleteUserFeed(Domain.secret(), msg.getUser());
            case KafkaMsg.PUSH_MESSAGE -> impl.push_PushMessage(Domain.secret(), msg.getPushMessage());
            case KafkaMsg.UPDATE_FOLLOWERS ->
                    impl.push_updateFollowers(Domain.secret(), msg.getUser(), msg.getFollower(), msg.isFollowing());
        }
        sync.setResult(version, msg);
    }

    private KafkaMsg deserializeByteArray(byte[] byteArray) {
        KafkaMsg kafkaMsg = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            kafkaMsg = (KafkaMsg) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return kafkaMsg;
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
