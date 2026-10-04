package at.yedel.faux;



import at.yedel.faux.utils.Constants;
import at.yedel.faux.utils.Logger;
import at.yedel.faux.utils.ModFiles;
import at.yedel.faux.utils.Properties;
import at.yedel.faux.data.RelationMap;
import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;



public class Faux {
    private static final Faux INSTANCE = new Faux();

    public static Faux getInstance() {
        return INSTANCE;
    }

    private static final String[] RELATION_KEYS = new String[] {"depends", "recommends", "suggests", "breaks", "conflicts"};
    private static final String CHOICE_OF_WORD = "Muted";
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
        List<File> modFiles = ModFiles.getModFiles();
        for (File modFile: modFiles) {
            if (modFile.isDirectory()) {
                continue;
            }
            JsonObject fmj = ModFiles.getFmjFromModFile(modFile);
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
        JsonObject overrides = object.getAsJsonObject("overrides");
        for (RelationMap relationMap: relationMaps) {
            String id = relationMap.id;
            JsonObject objectForMod = new JsonObject();
            if (object.has(id)) {
                continue;
            }
            for (String relation: relationMap.relations.keySet()) {
                ArrayList<String> mods = relationMap.relations.get(relation);
                if (mods.isEmpty()) {
                    continue;
                }
                JsonObject objectForRelation = new JsonObject();
                for (String mod: mods) {
                    objectForRelation.add(mod, new JsonPrimitive("IGNORED"));
                    dependenciesRavaged ++;
                }
                objectForMod.add("-" + relation, objectForRelation);
            }
            overrides.add(id, objectForMod);
            modsRavaged ++;
        }
        Logger.info(CHOICE_OF_WORD + " " + modsRavaged + " mods, " + dependenciesRavaged + " dependencies.");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (Properties.of("print-json", false)) {
            Logger.info(gson.toJson(object));
        }
        if (Properties.of("write-json", true)) {
            try (BufferedWriter writer = Files.newBufferedWriter(overridesFile.toPath(), StandardCharsets.UTF_8)) {
                gson.toJson(object, writer);
                System.out.println("JSON file written successfully.");
            }
            catch (IOException e) {
                Logger.info("Encountered error while writing overrides!");
                e.printStackTrace();
            }
        }
        else {
            Logger.info("Property faux.write-json is false, not writing overrides!");
        }
    }
}
