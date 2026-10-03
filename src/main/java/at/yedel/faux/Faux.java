package at.yedel.faux;



import at.yedel.faux.utils.Constants;
import at.yedel.faux.utils.Logger;
import at.yedel.faux.utils.RelationMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;



public class Faux {
    private static final Faux INSTANCE = new Faux();

    public static Faux getInstance() {
        return INSTANCE;
    }

    private static final String[] RELATION_KEYS = new String[] {"depends", "recommends", "suggests", "breaks", "conflicts"};
    private boolean initialized;

    public void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        long startTime = System.currentTimeMillis();
        Logger.info("Starting Faux");
        Logger.info("Work directory is " + Constants.workDir);
        ArrayList<RelationMap> relationMaps = collect();
        Logger.info(relationMaps);
        Logger.info("Faux initialization took " + (System.currentTimeMillis() - startTime) + "ms");
        if (Boolean.getBoolean("faux.exit-after-run")) {
            Logger.info("Property faux.exit-after-run is true, exiting...");
            System.exit(0);
        }
    }

    private ArrayList<RelationMap> collect() {
        ArrayList<RelationMap> relationMaps = new ArrayList<>();
        File[] modFiles = getModFiles();
        for (File modFile: modFiles) {
            if (modFile.isDirectory()) {
                continue;
            }
            JsonObject fmj = getFmjFromModFile(modFile);
            String id = fmj.get("id").getAsString();
            RelationMap relationMap = new RelationMap(id);
            for (String relationKey: RELATION_KEYS) {
                relationMap.addRelationKey(relationKey);
                JsonObject relationships = fmj.getAsJsonObject(relationKey);
                if (relationships == null) {
                    continue;
                }
                for (String mod: relationships.asMap().keySet()) {
                    relationMap.addRelationMod(relationKey, mod);
                }
            }
            relationMaps.add(relationMap);
        }
        return relationMaps;
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
        File modsDir = new File(Constants.workDir, "mods");
        if (!modsDir.exists()) {
            throw new IllegalStateException("No mods folder found");
        }
        return modsDir.listFiles();
    }
}
