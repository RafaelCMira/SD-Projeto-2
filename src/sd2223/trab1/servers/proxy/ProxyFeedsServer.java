package sd2223.trab1.servers.proxy;

import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.servers.Domain;
import sd2223.trab1.servers.rest.AbstractRestServer;
import sd2223.trab1.servers.rest.RestFeedsPushResource;
import sd2223.trab1.servers.rest.RestFeedsServer;
import utils.Args;

import java.util.logging.Logger;

public class ProxyFeedsServer extends AbstractRestServer {
    public static final int PORT = 4568;

    private static Logger Log = Logger.getLogger(ProxyFeedsServer.class.getName());

    ProxyFeedsServer() {
        super(Log, Feeds.SERVICENAME, PORT);
    }

    @Override
    protected void registerResources(ResourceConfig config) {
        config.register(ProxyFeedsPushResource.class);
    }

    public static void main(String[] args) throws Exception {
        Args.use(args);
        Domain.set(args[0], Long.valueOf(args[1]));
        new ProxyFeedsServer().start();
    }

}
