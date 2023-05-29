package sd2223.trab1.servers.mastodon.msgs;

import sd2223.trab1.api.Message;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public record PostStatusResult(String id, String content, String created_at, MastodonAccount account) {
    public long getId() {
        return Long.valueOf(id);
    }

    long getCreationTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date date = dateFormat.parse(created_at);
            return date.getTime();
        } catch (ParseException e) {

        }

        // usar classe Jsoup, para transformar content (html) em string
        return 0;
    }

    public String getText() {
        return content;
    }

    public Message toMessage() { // Domain.getDomain() usar classe trabalho2
        var m = new Message(getId(), account.username(), "", getText());
        m.setCreationTime(getCreationTime());
        return m;
    }
}