package sd2223.trab1.clients.soap;

import javax.xml.namespace.QName;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import sd2223.trab1.api.PushMessage;
import sd2223.trab1.api.java.FeedsPush;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.soap.push.FeedsService;

public class SoapFeedsPushClient extends SoapFeedsClient implements FeedsPush {

    public SoapFeedsPushClient(String serverURI) {
        super(serverURI);
    }

    private FeedsService stub;

    synchronized protected FeedsService stub() {
        if (stub == null) {
            QName QNAME = new QName(FeedsService.NAMESPACE, FeedsService.NAME);
            Service service = Service.create(super.toURL(super.uri + WSDL), QNAME);
            this.stub = service.getPort(sd2223.trab1.api.soap.push.FeedsService.class);
            super.setTimeouts((BindingProvider) stub);
        }
        Thread.dumpStack();
        return stub;
    }

    @Override
    public Result<Void> push_PushMessage(String secret, PushMessage msg) {
        return super.reTry(() -> super.toJavaResult(() -> stub().push_PushMessage(secret, msg)));
    }

    @Override
    public Result<Void> push_updateFollowers(String secret, String user, String follower, boolean following) {
        return super.reTry(() -> super.toJavaResult(() -> stub().push_updateFollowers(secret, user, follower, following)));
    }
}
