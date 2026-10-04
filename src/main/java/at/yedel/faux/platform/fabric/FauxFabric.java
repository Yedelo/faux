//? if fabric {
package at.yedel.faux.platform.fabric;



import at.yedel.faux.data.RelationMap;
import at.yedel.faux.utils.Constants;
import at.yedel.faux.utils.FileUtils;
import at.yedel.faux.utils.Properties;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;



public class FauxFabric {
    private static final FauxFabric INSTANCE = new FauxFabric();

    public static FauxFabric getInstance() {
        return INSTANCE;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("FauxFabric");
    private static final String[] RELATION_KEYS = new String[] {"depends", "recommends", "suggests", "breaks", "conflicts"};

    public void initialize() {
        ArrayList<RelationMap> relationMaps = collect();
        write(relationMaps);
    }

    private ArrayList<RelationMap> collect() {
        ArrayList<RelationMap> relationMaps = new ArrayList<>();
        List<File> modFiles = getModFiles();
        for (File modFile: modFiles) {
            // stuff like prism's .index
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
        int dependenciesRavaged = 0;

        File configDir = FileUtils.provideConfigDir();
        File overridesFile = new File(configDir, "fabric_loader_dependencies.json");
        JsonObject object = null;
        if (!overridesFile.exists()) {
            LOGGER.info("Creating new fabric_loader_dependencies.json");
            object = new JsonObject();
        }
        else {
            try (FileReader reader = new FileReader(overridesFile)) {
                object = JsonParser.parseReader(reader).getAsJsonObject();
            }
            catch (IOException e) {
                LOGGER.error("Error reading overrides file!", e);
            }
            LOGGER.info("Found fabric_loader_dependencies.json, editing existing one");
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
        LOGGER.info("{} {} mods and {} dependencies", Constants.CHOICE_OF_WORD, modsRavaged, dependenciesRavaged);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (Properties.of("print-file", false)) {
            LOGGER.info(gson.toJson(object));
        }
        if (Properties.of("write-file", true)) {
            try (BufferedWriter writer = Files.newBufferedWriter(overridesFile.toPath(), StandardCharsets.UTF_8)) {
                gson.toJson(object, writer);
            }
            catch (IOException e) {
                LOGGER.error("Encountered error while writing overrides!", e);
            }
        }
        else {
            LOGGER.warn("Property faux.write-file is false, not writing overrides!");
        }
    }

    public static JsonObject getFmjFromModFile(File modFile) {
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

    public static List<File> getModFiles() {
        List<File> modFiles = new ArrayList<>();
        String customModsFolder = System.getProperty("fabric.modsFolder");
        File modsDir = null;
        if (customModsFolder != null) {
            modsDir = new File(customModsFolder);
        }
        else {
            modsDir = new File(Constants.workDir, "mods");
        }
        if (modsDir.exists()) {
            modFiles.addAll(Arrays.asList(modsDir.listFiles()));
        }
        String customMods = System.getProperty("fabric.addMods");
        if (customMods != null) {
            if (customMods.startsWith("@")) {
                Path additionalModsFile = Path.of(customMods.substring(1));
                try {
                    String additionalMods = Files.readString(additionalModsFile);
                    String[] additionalModLines = additionalMods.split("\n");
                    for (String additionalModLine: additionalModLines) {
                        modFiles.add(new File(additionalModLine));
                    }
                }
                catch (IOException e) {
                    // like bro
                }
            }
            else {
                String[] additionalMods = customMods.split(File.pathSeparator);
                for (String additionalMod: additionalMods) {
                    modFiles.add(new File(additionalMod));
                }
            }
        }
        return modFiles;
    }
}
//?}