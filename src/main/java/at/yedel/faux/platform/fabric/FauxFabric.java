//? if fabric {
package at.yedel.faux.platform.fabric;



import at.yedel.faux.data.RelationMap;
import at.yedel.faux.utils.Constants;
import at.yedel.faux.utils.FileUtils;
import at.yedel.faux.utils.Logger;
import at.yedel.faux.utils.Properties;
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



public class FauxFabric {
    private static final FauxFabric INSTANCE = new FauxFabric();

    public static FauxFabric getInstance() {
        return INSTANCE;
    }

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
            RelationMap relationMap = getRelationMapFromModFile(modFile);
            if (relationMap != null) {
                relationMaps.add(relationMap);
            }
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
            Logger.info("Creating new fabric_loader_dependencies.json");
            object = new JsonObject();
        }
        else {
            try (FileReader reader = new FileReader(overridesFile)) {
                object = JsonParser.parseReader(reader).getAsJsonObject();
            }
            catch (IOException e) {
                Logger.error("Error reading overrides file!", e);
            }
            Logger.info("Found fabric_loader_dependencies.json, editing existing one");
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
        Logger.info("{} {} mods and {} dependencies", Constants.CHOICE_OF_WORD, modsRavaged, dependenciesRavaged);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (Properties.of("print-file", false)) {
            JsonObject finalObject = object;
            Logger.withoutFormatting(() -> Logger.info(gson.toJson(finalObject)));
        }
        if (Properties.of("write-file", true)) {
            try (BufferedWriter writer = Files.newBufferedWriter(overridesFile.toPath(), StandardCharsets.UTF_8)) {
                gson.toJson(object, writer);
            }
            catch (IOException e) {
                Logger.error("Encountered error while writing overrides!", e);
            }
        }
        else {
            Logger.warn("Property faux.write-file is false, not writing overrides!");
        }
    }

    private static RelationMap getRelationMapFromModFile(File modFile) {
        JarFile modJar = FileUtils.getJarFile(modFile);
        JsonObject fmj = getFmj(modJar);
        if (fmj == null) return null;
        if (fmj.has("jars")) {
            Logger.info("Mod file {} has jars {}", modFile, fmj.get("jars"));
        }
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
        return relationMap;
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

    public static JsonObject getFmj(JarFile modJar) {
        JarEntry possibleModInfo = modJar.getJarEntry("fabric.mod.json");
        if (possibleModInfo == null) {
            return null;
        }
        try (
            InputStream modStream = modJar.getInputStream(possibleModInfo);
            InputStreamReader reader = new InputStreamReader(modStream)
        ) {
            return new JsonParser().parse(reader).getAsJsonObject();
        }
        catch (IOException e) {
            return null;
        }
    }
}
//?}