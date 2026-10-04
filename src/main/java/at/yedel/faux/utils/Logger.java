package at.yedel.faux.utils;



import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Logger {
    private static boolean format = true;

    public static void info(Object anything, Object... parameters) {
        System.out.println("[Faux] " + formatString(anything.toString(), parameters));
    }

    public static void warn(Object anything, Object... parameters) {
        System.out.println("[Faux] " + formatString(anything.toString(), parameters));
    }

    public static void error(Object anything, Object... parameters) {
        System.err.println("[Faux] " + formatString(anything.toString(), parameters));
    }

    private static String formatString(String string, Object... parameters) {
        Pattern pattern = Pattern.compile("\\{}");
        Matcher matcher = pattern.matcher(string);
        StringBuilder built = new StringBuilder();

        int parameterNumber = 0;
        while (matcher.find()) {
            String uniqueReplacement = "{" + parameterNumber + "}";
            matcher.appendReplacement(built, Matcher.quoteReplacement(uniqueReplacement));
            parameterNumber ++;
        }
        matcher.appendTail(built);
        if (format) {
            return MessageFormat.format(built.toString(), parameters);
        }
        else {
            return built.toString();
        }
    }

    public static void withoutFormatting(Runnable runnable) {
        format = false;
        runnable.run();
        format = true;
    }
}
