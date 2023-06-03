package sd2223.trab1.servers.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import sd2223.trab1.api.Message;
import sd2223.trab1.servers.Domain;
import sd2223.trab1.servers.java.JavaFeedsCommon;
import sd2223.trab1.servers.java.JavaFeedsPush;
import sd2223.trab1.servers.kafka.sync.SyncPoint;
import sd2223.trab1.servers.replication.ReplicationFeedsResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;


public class TotalOrderExecutor extends Thread implements RecordProcessor {
    static final String FROM_BEGINNING = "earliest";
    static final String KAFKA_BROKERS = "kafka:9092";

    final KafkaSubscriber receiver;
    final SyncPoint<String> sync;
    final JavaFeedsPush impl;

    private static final Logger Log = Logger.getLogger(TotalOrderExecutor.class.getName());

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
            case KafkaMsg.POST_MESSAGE -> execPostMessage(version, msg);
            case KafkaMsg.REMOVE_FROM_PERSONAL -> execRemoveFromPersonal(version, msg);
            case KafkaMsg.SUB -> execSubUser(version, msg);
            case KafkaMsg.UNSUB -> execUnsubUser(version, msg);
            case KafkaMsg.DELETE_USER_FEED -> execDeleteUserFeed(version, msg);
            case KafkaMsg.PUSH_MESSAGE -> execPushMessage(version, msg);
            case KafkaMsg.UPDATE_FOLLOWERS -> execUpdateFollowers(version, msg);
        }
    }

    public static <T extends Serializable> T deserializeObject(byte[] serializedObject, Class<T> objectType) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedObject);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            Object object = objectInputStream.readObject();
            return objectType.cast(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    private void execPostMessage(long version, KafkaMsg msg) {
        System.out.println("ENTREI NO POST_MESSAGE");
        impl.postMessage(msg.getUser(), msg.getPwd(), msg.getMsg());
        sync.setResult(version, String.valueOf(msg));
    }

    private void execRemoveFromPersonal(long version, KafkaMsg msg) {
        impl.removeFromPersonalFeed(msg.getUser(), msg.getMid(), msg.getPwd());
        sync.setResult(version, String.valueOf(msg));
    }

    private void execSubUser(long version, KafkaMsg msg) {
        impl.subUser(msg.getUser(), msg.getUserSub(), msg.getPwd());
        sync.setResult(version, String.valueOf(msg));
    }

    private void execUnsubUser(long version, KafkaMsg msg) {
        impl.unsubscribeUser(msg.getUser(), msg.getUserSub(), msg.getPwd());
        sync.setResult(version, String.valueOf(msg));
    }

    private void execDeleteUserFeed(long version, KafkaMsg msg) {
        impl.deleteUserFeed(msg.getUser());
        sync.setResult(version, String.valueOf(msg));
    }

    private void execPushMessage(long version, KafkaMsg msg) {
        impl.push_PushMessage(msg.getPushMessage());
        sync.setResult(version, String.valueOf(msg));
    }

    private void execUpdateFollowers(long version, KafkaMsg msg) {
        impl.push_updateFollowers(msg.getUser(), msg.getFollower(), msg.isFollowing());
        sync.setResult(version, String.valueOf(msg));
    }

}
