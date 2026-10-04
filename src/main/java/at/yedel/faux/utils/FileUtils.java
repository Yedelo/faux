package at.yedel.faux.utils;



import java.io.File;



public class FileUtils {
    public static File provideConfigDir() {
        File configDir = new File(Constants.workDir, "config");
        if (!configDir.exists()) {
            configDir.mkdir();
        }
        return configDir;
    }
}
