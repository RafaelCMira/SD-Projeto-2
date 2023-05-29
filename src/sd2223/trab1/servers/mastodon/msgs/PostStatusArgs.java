package sd2223.trab1.servers.mastodon.msgs;

public record PostStatusArgs(String status, String visibility) {

	public PostStatusArgs(String msg) {
		this(msg, "private");
	}
}