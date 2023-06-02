package sd2223.trab1.servers.kafka;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaPublisher {

    static public KafkaPublisher createPublisher(String brokers) {
        Properties props = new Properties();

        // Localização dos servidores kafka (lista de máquinas + porto)
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);

        // Classe para serializar as chaves dos eventos (string)
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Classe para serializar os valores dos eventos (string)
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaPublisher(new KafkaProducer<String, byte[]>(props));
    }

    private final KafkaProducer<String, byte[]> producer;

    private KafkaPublisher(KafkaProducer<String, byte[]> producer) {
        this.producer = producer;
    }

    public void close() {
        this.producer.close();
    }

    public long publish(String topic, String key, KafkaMsg value) {
        try {
            byte[] bytes = serializeObject(value);
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, bytes);
            var result = producer.send(record).get();
            long offset = result.offset();
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
