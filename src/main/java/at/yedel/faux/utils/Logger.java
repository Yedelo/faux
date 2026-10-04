package at.yedel.faux.utils;



public class Logger {
    private static final String userName = System.getProperty("user.name");

    public static void info(Object object) {
        info(object.toString());
    }

    public static void info(String string) {
        if (Properties.of("hide-name", true)) {
            string = string.replace(userName, "(REDACTED)");
        }
        System.out.println("[Faux] " + string);
    }
}
