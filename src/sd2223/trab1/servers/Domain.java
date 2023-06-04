package sd2223.trab1.servers;

public class Domain {

    static String domain;
    static long uuid;
    static String secret;

    public static void set(String _domain, long _uuid) {
        domain = _domain;
        uuid = _uuid;
    }

    public static void set(String _domain, long _uuid, String _secret) {
        domain = _domain;
        uuid = _uuid;
        secret = _secret;
    }

    public static String get() {
        return domain;
    }

    public static long uuid() {
        return uuid;
    }

    public static String secret() {
        return secret;
    }


    public static boolean isRemoteUser(String user) {
        var parts = user.split("@");
        return parts.length > 1 && !parts[1].equals(domain);
    }
}
