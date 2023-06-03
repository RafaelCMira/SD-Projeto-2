package sd2223.trab1.servers.replication;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.LoggerFactory;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.servers.Domain;
import sd2223.trab1.servers.kafka.TotalOrderExecutor;
import sd2223.trab1.servers.rest.AbstractRestServer;
import utils.Args;

import java.util.logging.Logger;

public class ReplicationFeedsServer extends AbstractRestServer {

    public static final int PORT = 9092;

    private static Logger Log = Logger.getLogger(ReplicationFeedsServer.class.getName());

    public ReplicationFeedsServer() {
        super(Log, Feeds.SERVICENAME, PORT);
    }

    @Override
    protected void registerResources(ResourceConfig config) {
        //config.register(ReplicationFeedsPushResource.class);
        config.register(new ReplicationFeedsPushResource());
    }


    public static void main(String[] args) throws Exception {
        // org.slf4j.Logger kafkaLogger = LoggerFactory.getLogger("org.apache.kafka");
        // ((ch.qos.logback.classic.Logger) kafkaLogger).setLevel(ch.qos.logback.classic.Level.OFF);
        Args.use(args);
        Domain.set(args[0], Long.valueOf(args[1]));
        // 2000000001
        // 4000000001
        // 6000000001
        Log.info("Domain uuid: " + Domain.uuid());
        new TotalOrderExecutor(Domain.get());
        new ReplicationFeedsServer().start();
    }

}
