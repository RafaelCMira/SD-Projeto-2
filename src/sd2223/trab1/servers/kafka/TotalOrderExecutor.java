package sd2223.trab1.servers.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import sd2223.trab1.servers.Domain;
import sd2223.trab1.servers.java.JavaFeedsPush;
import sd2223.trab1.servers.kafka.sync.SyncPoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;


public class TotalOrderExecutor extends Thread implements RecordProcessor {
    static final String FROM_BEGINNING = "earliest";

    static final String KAFKA_BROKERS = "kafka:9092";

    static int MAX_NUM_THREADS = 4;

    final KafkaSubscriber receiver;
    final SyncPoint<KafkaMsg> sync;
    final JavaFeedsPush impl;

    public TotalOrderExecutor(String domain) {
        this.receiver = KafkaSubscriber.createSubscriber(KAFKA_BROKERS, List.of(domain), FROM_BEGINNING);
        this.receiver.start(false, this);
        this.sync = SyncPoint.getInstance();
        this.impl = JavaFeedsPush.getInstance();
    }


    @Override
    public void onReceive(ConsumerRecord<String, byte[]> r) {
        var version = r.offset();
        //   KafkaMsg msg = deserializeObject(r.value(), KafkaMsg.class);
        KafkaMsg msg = deserializeByteArray(r.value());
        switch (msg.getOperation()) {
            case KafkaMsg.POST_MESSAGE -> {
                impl.postMessage(msg.getUser(), msg.getPwd(), msg.getMsg());
                sync.setResult(version, msg);
            }
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

    /*
    public void run() {
        for (; ; ) {
            var operation = "op" + System.nanoTime();
            var version = sender.publish(TOPIC, replicaId, operation);
            var result = sync.waitForResult(version);
            System.out.printf("Op: %s, version: %s, result: %s\n", operation, version, result);
            sleep(500);
            //System.err.printf("replicaId: %s, sync state: %s", replicaId, sync);
        }
    }*/

    /*
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < MAX_NUM_THREADS; i++)
            new TotalOrderExecutor("replica(" + i + ")").start();
    }*/
}
