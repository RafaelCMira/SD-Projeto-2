package sd2223.trab1.servers.mastodon.msgs;

import org.jsoup.Jsoup;
import sd2223.trab1.api.Message;
import sd2223.trab1.servers.Domain;

import java.time.Instant;
import java.util.Objects;

public record PostStatusResult(String id, String content, String created_at, MastodonAccount account) {
    public long getId() {
        return Long.valueOf(id);
    }

    long getCreationTime() {
        return Instant.parse(created_at).toEpochMilli();
    }

    public Message toMessage() {
        var m = new Message(getId(), account.username(), Domain.get(), Objects.requireNonNull(Jsoup.parse(content).select("p").first()).text());
        m.setCreationTime(getCreationTime());
        return m;
    }
}