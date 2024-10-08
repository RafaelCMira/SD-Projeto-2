package sd2223.trab1.servers.mastodon;

import sd2223.trab1.api.PushMessage;
import sd2223.trab1.api.User;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.java.FeedsPush;
import sd2223.trab1.api.java.Result;

import sd2223.trab1.servers.mastodon.msgs.PostStatusArgs;
import sd2223.trab1.servers.mastodon.msgs.PostStatusResult;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.reflect.TypeToken;

import utils.JSON;

import java.util.List;

import static sd2223.trab1.api.java.Result.ErrorCode.*;
import static sd2223.trab1.api.java.Result.error;
import static sd2223.trab1.api.java.Result.ok;

public class Mastodon implements FeedsPush {

    static String MASTODON_NOVA_SERVER_URI = "http://10.170.138.52:3000";
    static String MASTODON_SOCIAL_SERVER_URI = "https://mastodon.social";

    static String MASTODON_SERVER_URI = MASTODON_NOVA_SERVER_URI;

    private static final String clientKey = "TCyOlc3rqas5JI-kg5VkFRGKsRLTd8qINBU2z-pGCic";
    private static final String clientSecret = "C8LjpGj9TxDXrF4CH3G5ZBP3o1Vk1qk5fWyAOO-HnMg";
    private static final String accessTokenStr = "-BwWUgxZc6TZ9zARXJD438pey1BpylA-oqUOc6KiPBQ";

    static final String STATUSES_PATH = "/api/v1/statuses";
    static final String TIMELINES_PATH = "/api/v1/timelines/home";
    static final String ACCOUNT_FOLLOWING_PATH = "/api/v1/accounts/%s/following";
    static final String VERIFY_CREDENTIALS_PATH = "/api/v1/accounts/verify_credentials";
    static final String SEARCH_ACCOUNTS_PATH = "/api/v1/accounts/search";
    static final String ACCOUNT_FOLLOW_PATH = "/api/v1/accounts/%s/follow";
    static final String ACCOUNT_UNFOLLOW_PATH = "/api/v1/accounts/%s/unfollow";

    private static final int HTTP_OK = 200;
    private static final int HTTP_NOT_FOUND = 404;


    protected OAuth20Service service;
    protected OAuth2AccessToken accessToken;

    private static Mastodon impl;

    protected Mastodon() {
        try {
            service = new ServiceBuilder(clientKey).apiSecret(clientSecret).build(MastodonApi.instance());
            accessToken = new OAuth2AccessToken(accessTokenStr);
        } catch (Exception x) {
            x.printStackTrace();
            System.exit(0);
        }
    }

    synchronized public static Mastodon getInstance() {
        if (impl == null)
            impl = new Mastodon();
        return impl;
    }

    private String getEndpoint(String path, Object... args) {
        var fmt = MASTODON_SERVER_URI + path;
        return String.format(fmt, args);
    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        try {
            Result<User> result = verifyId(user, pwd);
            if (!result.isOK()) return error(result.error());

            final OAuthRequest request = new OAuthRequest(Verb.POST, getEndpoint(STATUSES_PATH));
            JSON.toMap(new PostStatusArgs(msg.getText())).forEach((k, v) -> {
                request.addBodyParameter(k, v.toString());
            });

            service.signRequest(accessToken, request);

            Response response = service.execute(request);
            if (response.getCode() == HTTP_OK) {
                var res = JSON.decode(response.getBody(), PostStatusResult.class);
                return ok(res.getId());
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return error(INTERNAL_ERROR);
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        try {
            final OAuthRequest request = new OAuthRequest(Verb.GET, getEndpoint(STATUSES_PATH + "/" + mid));
            service.signRequest(accessToken, request);

            Response response = service.execute(request);

            if (response.getCode() == HTTP_OK) {
                var res = JSON.decode(response.getBody(), PostStatusResult.class);
                return ok(res.toMessage());
            }
            if (response.getCode() == HTTP_NOT_FOUND) {
                return error(Result.ErrorCode.NOT_FOUND);
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return error(Result.ErrorCode.INTERNAL_ERROR);
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
        try {
            final OAuthRequest request = new OAuthRequest(Verb.GET, getEndpoint(TIMELINES_PATH));
            service.signRequest(accessToken, request);

            Response response = service.execute(request);
            if (response.getCode() == HTTP_OK) {
                List<PostStatusResult> res = JSON.decode(response.getBody(), new TypeToken<List<PostStatusResult>>() {
                });
                return ok(res.stream().map(PostStatusResult::toMessage).filter(m -> m.getCreationTime() > time).toList());
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return error(Result.ErrorCode.INTERNAL_ERROR);
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
        try {
            Result<User> result = verifyId(user, pwd);
            if (!result.isOK()) return error(result.error());

            final OAuthRequest request = new OAuthRequest(Verb.DELETE, getEndpoint(STATUSES_PATH + "/" + mid));
            service.signRequest(accessToken, request);

            Response response = service.execute(request);

            if (response.getCode() == HTTP_OK) {
                JSON.decode(response.getBody(), PostStatusResult.class);
                return ok();
            }
            if (response.getCode() == HTTP_NOT_FOUND) {
                return error(Result.ErrorCode.NOT_FOUND);
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return error(Result.ErrorCode.INTERNAL_ERROR);
    }


    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {
        try {
            Result<User> result = verifyId(user, pwd);
            if (!result.isOK()) return error(result.error());

            long id = getId(userSub);
            if (id == -1) return error(NOT_FOUND);

            final OAuthRequest request = new OAuthRequest(Verb.POST, getEndpoint(ACCOUNT_FOLLOW_PATH, id));
            service.signRequest(accessToken, request);

            Response response = service.execute(request);
            if (response.getCode() == HTTP_OK) {
                return ok();
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return error(INTERNAL_ERROR);
    }


    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
        try {
            Result<User> result = verifyId(user, pwd);
            if (!result.isOK()) return error(result.error());

            long id = getId(userSub);
            if (id == -1) return error(Result.ErrorCode.NOT_FOUND);

            final OAuthRequest request = new OAuthRequest(Verb.POST, getEndpoint(ACCOUNT_UNFOLLOW_PATH, id));
            service.signRequest(accessToken, request);

            Response response = service.execute(request);
            if (response.getCode() == HTTP_OK) {
                return ok();
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return error(INTERNAL_ERROR);
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        try {
            long id = getId(user);
            if (id == -1) return error(Result.ErrorCode.NOT_FOUND);

            final OAuthRequest request = new OAuthRequest(Verb.GET, getEndpoint(ACCOUNT_FOLLOWING_PATH, id));
            service.signRequest(accessToken, request);

            Response response = service.execute(request);
            if (response.getCode() == HTTP_OK) {
                List<String> res = JSON.decode(response.getBody(), new TypeToken<List<String>>() {
                });
                return ok(res.stream().toList());
            }
        } catch (Exception x) {
            x.printStackTrace();
        }

        return error(NOT_IMPLEMENTED);
    }

    @Override
    public Result<Void> deleteUserFeed(String secret, String user) {
        return error(NOT_IMPLEMENTED);
    }

    @Override
    public Result<Long> getMsgServerId(String user, String pwd, Message msg) {
        return null;
    }

    @Override
    public Result<Void> push_updateFollowers(String secret, String user, String follower, boolean following) {
        return error(NOT_IMPLEMENTED);
    }

    @Override
    public Result<Void> push_PushMessage(String secret, PushMessage msg) {
        return error(NOT_IMPLEMENTED);
    }

    private Result<User> verifyId(String user, String pwd) {
        try {
            final OAuthRequest request = new OAuthRequest(Verb.GET, getEndpoint(VERIFY_CREDENTIALS_PATH));
            request.addQuerystringParameter("user", user);
            request.addQuerystringParameter("pwd", pwd);
            service.signRequest(accessToken, request);

            Response response = service.execute(request);
            if (response.getCode() == HTTP_OK) {
                return ok();
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return error(INTERNAL_ERROR);
    }

    private long getId(String user) {
        try {
            final OAuthRequest request = new OAuthRequest(Verb.GET, getEndpoint(SEARCH_ACCOUNTS_PATH));
            request.addQuerystringParameter("q", user);
            service.signRequest(accessToken, request);

            Response response = service.execute(request);
            if (response.getCode() == HTTP_OK) {
                List<PostStatusResult> res = JSON.decode(response.getBody(), new TypeToken<List<PostStatusResult>>() {
                });
                return res.get(0).getId();
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return -1;
    }
}
