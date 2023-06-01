package sd2223.trab1.servers.replication;

import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.servers.Domain;
import sd2223.trab1.servers.kafka.TotalOrderExecutor;
import sd2223.trab1.servers.rest.AbstractRestServer;
import utils.Args;

import java.util.logging.Logger;

public class ReplicationFeedsServer extends AbstractRestServer {

    public static final int PORT = 4569;

    private static Logger Log = Logger.getLogger(ReplicationFeedsServer.class.getName());

    public ReplicationFeedsServer() {
        super(Log, Feeds.SERVICENAME, PORT);
    }

    @Override
    protected void registerResources(ResourceConfig config) {
        config.register(new ReplicationFeedsPushResource());
    }

    public static void main(String[] args) throws Exception {
        Args.use(args);
        Domain.set(args[0], Long.valueOf(args[1]));
        new TotalOrderExecutor(Domain.get());
        new ReplicationFeedsServer().start();
    }

}
