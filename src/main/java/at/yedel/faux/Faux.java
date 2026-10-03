package at.yedel.faux;



import at.yedel.faux.utils.Constants;
import at.yedel.faux.utils.Logger;
import at.yedel.faux.utils.RelationMap;
import com.google.gson.*;

import java.io.*;
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
        write(relationMaps);
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
                relationMap.relations.put(relationKey, new ArrayList<>());
                JsonObject relationships = fmj.getAsJsonObject(relationKey);
                if (relationships == null) {
                    continue;
                }
                for (String mod: relationships.asMap().keySet()) {
                    relationMap.relations.get(relationKey).add(mod);
                }
            }
            relationMaps.add(relationMap);
        }
        return relationMaps;
    }

    private void write(ArrayList<RelationMap> relationMaps) {
        int modsRavaged = 0;
        int relationsRavaged = 0;
        int dependenciesRavaged = 0;

        File configDir = new File(Constants.workDir, "config");
        if (!configDir.exists()) {
            configDir.mkdir();
        }
        File overridesFile = new File(configDir, "fabric_loader_dependencies.json");
        JsonObject object = null;
        if (!overridesFile.exists()) {
            object = new JsonObject();
        }
        else {
            try (FileReader reader = new FileReader(overridesFile)) {
                object = JsonParser.parseReader(reader).getAsJsonObject();
            }
            catch (IOException e) {
                // like bro
            }
        }
        if (!object.has("version")) {
            object.add("version", new JsonPrimitive(1));
        }
        if (!object.has("overrides")) {
            object.add("overrides", new JsonObject());
        }
        object.add("faux-write-time", new JsonPrimitive(System.currentTimeMillis()));
        JsonObject overrides = object.getAsJsonObject("overrides");
        for (RelationMap relationMap: relationMaps) {
            String id = relationMap.id;
            JsonObject objectForMod = new JsonObject();
            if (object.has(id)) {
                continue;
            }
            for (String relation: relationMap.relations.keySet()) {
                JsonObject objectForRelation = new JsonObject();
                for (String mod: relationMap.relations.get(relation)) {
                    objectForRelation.add(mod, new JsonPrimitive("IGNORED"));
                    dependenciesRavaged ++;
                }
                objectForMod.add("-" + relation, objectForRelation);
                relationsRavaged ++;
            }
            overrides.add(id, objectForMod);
            modsRavaged ++;
        }
        Logger.info(new GsonBuilder().setPrettyPrinting().create().toJson(object));
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
