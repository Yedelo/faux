package at.yedel.faux.utils;



public class Properties {
    public static boolean of(String propertyName, boolean defaultValue) {
        return Boolean.parseBoolean(System.getProperty("faux." + propertyName, String.valueOf(defaultValue)));
    }
}
