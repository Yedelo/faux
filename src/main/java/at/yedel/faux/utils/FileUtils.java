package at.yedel.faux.utils;



import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;



public class FileUtils {
    public static File provideConfigDir() {
        File configDir = new File(Constants.workDir, "config");
        if (!configDir.exists()) {
            configDir.mkdir();
        }
        return configDir;
    }

    public static JarFile getJarFile(File modFile) {
        try {
            return new JarFile(modFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
