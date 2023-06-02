package sd2223.trab1.servers;

public class Domain {
    static String domain;
    static long uuid;
    static long msgId;

    public static void set(String _domain, long _uuid) {
        domain = _domain;
        uuid = _uuid;
        msgId = 1;
    }

    public static String get() {
        return domain;
    }

    public static long uuid() {
        return uuid;
    }

    public synchronized static long generateMessageId() {
        long result = msgId * 256 + uuid;
        Domain.msgId++;
        return result;
    }

    public static boolean isRemoteUser(String user) {
        var parts = user.split("@");
        return parts.length > 1 && !parts[1].equals(domain);
    }
}
