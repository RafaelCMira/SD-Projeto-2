package sd2223.trab1.servers.kafka;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import sd2223.trab1.servers.replication.ReplicationFeedsResource;

public class KafkaPublisher {

    static public KafkaPublisher createPublisher(String brokers) {
        Properties props = new Properties();

        // Localização dos servidores kafka (lista de máquinas + porto)
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);

        // Classe para serializar as chaves dos eventos (string)
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Classe para serializar os valores dos eventos (string)
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        return new KafkaPublisher(new KafkaProducer<String, byte[]>(props));
    }

    private static final Logger Log = Logger.getLogger(ReplicationFeedsResource.class.getName());
    private final KafkaProducer<String, byte[]> producer;

    private KafkaPublisher(KafkaProducer<String, byte[]> producer) {
        this.producer = producer;
    }

    public void close() {
        this.producer.close();
    }

    public long publish(String topic, String key, KafkaMsg value) {
        try {
            Log.info("eNTREI NO PUB1");
            byte[] bytes = serializeObject(value);
            Log.info("eNTREI NO PUB2");
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, bytes);
            Log.info("eNTREI NO PUB3");
            var result = producer.send(record).get();
            Log.info("eNTREI NO PUB4");
            long offset = result.offset();
            Log.info("eNTREI NO PUB5");
            return offset;
        } catch (ExecutionException | InterruptedException x) {
            x.printStackTrace();
        }
        return -1;
    }

    public long publish(String topic, KafkaMsg value) {
        try {
            byte[] bytes = serializeObject(value);
            long offset = producer.send(new ProducerRecord<>(topic, bytes)).get().offset();
            return offset;
        } catch (ExecutionException | InterruptedException x) {
            x.printStackTrace();
        }
        return -1;
    }

    private byte[] serializeObject(KafkaMsg kafkaMsg) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream objStream = new ObjectOutputStream(byteStream)) {
            objStream.writeObject(kafkaMsg);
            return byteStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
