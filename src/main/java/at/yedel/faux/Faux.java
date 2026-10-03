package at.yedel.faux;



import at.yedel.faux.utils.Logger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;



public class Faux {
    private static final Faux INSTANCE = new Faux();

    public static Faux getInstance() {
        return INSTANCE;
    }

    private static final String[] RELATIONSHIP_KEYS = new String[] {"depends", "recommends", "suggests", "breaks", "conflicts"};
    private boolean initialized;
    private File workDir = new File(System.getProperty("user.dir"));

    public void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        long startTime = System.currentTimeMillis();
        Logger.info("Starting Faux");
        Logger.info("Work directory is " + workDir);
        Logger.info("Faux initialization took " + (System.currentTimeMillis() - startTime) + "ms");
        File[] modFiles = getModFiles();
        for (File modFile: modFiles) {
            if (modFile.isDirectory()) {
                continue;
            }
            JsonObject fmj = getFmjFromModFile(modFile);
            for (String relationshipKey: RELATIONSHIP_KEYS) {
                JsonObject relationships = fmj.getAsJsonObject(relationshipKey);
                if (relationships == null) {
                    continue;
                }
                for (String mod: relationships.asMap().keySet()) {
                    Logger.info("Mod " + modFile + " " + relationshipKey + " " +  mod);
                }
            }
        }
        if (Boolean.getBoolean("faux.exit-after-run")) {
            Logger.info("Property faux.exit-after-run is true, exiting...");
            System.exit(0);
        }
    }

    private JsonObject getFmjFromModFile(File modFile) {
        try (JarFile modJar = new JarFile(modFile)) {
            JarEntry possibleModInfo = modJar.getJarEntry("fabric.mod.json");
            if (possibleModInfo == null) {
                return null;
            }
            try (
                InputStream modStream = modJar.getInputStream(possibleModInfo);
                InputStreamReader reader = new InputStreamReader(modStream)
            ) {
                JsonObject modObject = new JsonParser().parse(reader).getAsJsonObject();
                return modObject;
            }
        }
        catch (IOException e) {
            return null;
        }
    }

    //@TODO support mods from java argument
    private File[] getModFiles() {
        File modsDir = new File(workDir, "mods");
        if (!modsDir.exists()) {
            throw new IllegalStateException("No mods folder found");
        }
        return modsDir.listFiles();
    }
}
