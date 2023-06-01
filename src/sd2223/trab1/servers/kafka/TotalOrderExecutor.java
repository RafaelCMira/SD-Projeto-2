package sd2223.trab1.servers.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import sd2223.trab1.servers.java.JavaFeedsPush;
import sd2223.trab1.servers.kafka.sync.SyncPoint;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;


public class TotalOrderExecutor extends Thread implements RecordProcessor {
    static final String FROM_BEGINNING = "earliest";
    static final String TOPIC = "single_partition_topic";
    static final String KAFKA_BROKERS = "kafka:9092";

    static int MAX_NUM_THREADS = 4;

    final String replicaId;

    final KafkaPublisher sender;
    final KafkaSubscriber receiver;
    final SyncPoint<String> sync;

    final JavaFeedsPush javaFeedsPush;

    public TotalOrderExecutor(String replicaId) {
        this.replicaId = replicaId;
        this.sender = KafkaPublisher.createPublisher(KAFKA_BROKERS);
        this.receiver = KafkaSubscriber.createSubscriber(KAFKA_BROKERS, List.of(TOPIC), FROM_BEGINNING);
        this.receiver.start(false, this);
        this.sync = new SyncPoint<>();
        this.javaFeedsPush = new JavaFeedsPush();
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

    @Override
    public void onReceive(ConsumerRecord<String, byte[]> r) {
        var version = r.offset();

        // voltar a serializar o r
        // DeSerialize. r.value

        var obj = deserializeObject(r.value(), KafkaMsg.class);

        // System.out.printf("%s : processing: (%d, %s)\n", replicaId, version, r.value());

        var result = "result of " + r.value();

        // switch chama operacao correta

        sync.setResult(version, result);
    }

    public static <T extends Serializable> T deserializeObject(byte[] serializedObject, Class<T> objectType) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedObject);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            return objectType.cast(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < MAX_NUM_THREADS; i++)
            new TotalOrderExecutor("replica(" + i + ")").start();
    }
}
