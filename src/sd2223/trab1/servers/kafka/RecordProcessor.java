package sd2223.trab1.servers.kafka;


import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface RecordProcessor {
    void onReceive(ConsumerRecord<String, byte[]> r);
}
