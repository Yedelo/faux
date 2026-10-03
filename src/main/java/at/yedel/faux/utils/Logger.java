package at.yedel.faux.utils;



public class Logger {
    private static final String userName = System.getProperty("user.name");
    private static final boolean hideNames = Boolean.parseBoolean(System.getProperty("faux.hide-name", "true"));

    public static void info(Object object) {
        info(object.toString());
    }

    public static void info(String string) {
        if (hideNames) {
            string = string.replace(userName, "(REDACTED)");
        }
        System.out.println("[Faux] " + string);
    }
}
