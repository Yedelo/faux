package at.yedel.faux.platform.neoforge;



import at.yedel.faux.utils.Constants;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;



public class FauxNeoforge {
    private static final FauxNeoforge INSTANCE = new FauxNeoforge();

    public static FauxNeoforge getInstance() {
        return INSTANCE;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("FauxNeoforge");

    public void initialize() {
        Map<String, List<String>> relationMap = collect();
        LOGGER.info(relationMap.toString());
        write(relationMap);
    }

    private Map<String, List<String>> collect() {
        Map<String, List<String>> relationMap = new HashMap<>();
        List<File> modFiles = getModFiles();
        for (File modFile: modFiles) {
            // stuff like prism's .index
            if (modFile.isDirectory()) {
                continue;
            }
            Config nmt = getNmtFromModFile(modFile);
            List<Config> mods = nmt.get("mods");
            Config mod = mods.getFirst();
            String id = mod.get("modId");
            relationMap.put(id, new ArrayList<>());
            Config dependencies = nmt.get("dependencies");
            List<Config> dependenciesForMod = dependencies.get(id);
            for (Config dependencyForMod: dependenciesForMod) {
                String dependencyId = dependencyForMod.get("modId");
                relationMap.get(id).add(dependencyId);
            }
        }
        return relationMap;
    }

    private void write(Map<String, List<String>> relationMap) {
//        int modsRavaged = 0;
//        int dependenciesRavaged = 0;
//
//        File configDir = FileUtils.provideConfigDir();
//        File overridesFile = new File(configDir, "fabric_loader_dependencies.json");
//        JsonObject object = null;
//        if (!overridesFile.exists()) {
//            LOGGER.info("Creating new fabric_loader_dependencies.json");
//            object = new JsonObject();
//        }
//        else {
//            try (FileReader reader = new FileReader(overridesFile)) {
//                object = JsonParser.parseReader(reader).getAsJsonObject();
//            }
//            catch (IOException e) {
//                LOGGER.error("Error reading overrides file!", e);
//            }
//            LOGGER.info("Found fabric_loader_dependencies.json, editing existing one");
//        }
//        if (!object.has("version")) {
//            object.add("version", new JsonPrimitive(1));
//        }
//        if (!object.has("overrides")) {
//            object.add("overrides", new JsonObject());
//        }
//        JsonObject overrides = object.getAsJsonObject("overrides");
//        for (RelationMap relationMap: relationMaps) {
//            String id = relationMap.id;
//            JsonObject objectForMod = new JsonObject();
//            if (object.has(id)) {
//                continue;
//            }
//            for (String relation: relationMap.relations.keySet()) {
//                ArrayList<String> mods = relationMap.relations.get(relation);
//                if (mods.isEmpty()) {
//                    continue;
//                }
//                JsonObject objectForRelation = new JsonObject();
//                for (String mod: mods) {
//                    objectForRelation.add(mod, new JsonPrimitive("IGNORED"));
//                    dependenciesRavaged ++;
//                }
//                objectForMod.add("-" + relation, objectForRelation);
//            }
//            overrides.add(id, objectForMod);
//            modsRavaged ++;
//        }
//        LOGGER.info("{} {} mods and {} dependencies", Constants.CHOICE_OF_WORD, modsRavaged, dependenciesRavaged);
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        if (Properties.of("print-file", false)) {
//            LOGGER.info(gson.toJson(object));
//        }
//        if (Properties.of("write-file", true)) {
//            try (BufferedWriter writer = Files.newBufferedWriter(overridesFile.toPath(), StandardCharsets.UTF_8)) {
//                gson.toJson(object, writer);
//            }
//            catch (IOException e) {
//                LOGGER.error("Encountered error while writing overrides!", e);
//            }
//        }
//        else {
//            LOGGER.warn("Property faux.write-file is false, not writing overrides!");
//        }
    }

    // nmt
    public static Config getNmtFromModFile(File modFile) {
        try (JarFile modJar = new JarFile(modFile)) {
            JarEntry possibleModInfo = modJar.getJarEntry("META-INF/neoforge.mods.toml");
            if (possibleModInfo == null) {
                return null;
            }
            try (
                InputStream modStream = modJar.getInputStream(possibleModInfo);
                InputStreamReader reader = new InputStreamReader(modStream)
            ) {
                return TomlFormat.instance().createParser().parse(reader);
            }
        }
        catch (IOException e) {
            return null;
        }
    }

    public static List<File> getModFiles() {
        List<File> modFiles = new ArrayList<>();
        File modsDir = null;
        modsDir = new File(Constants.workDir, "mods");
        if (modsDir.exists()) {
            modFiles.addAll(Arrays.asList(modsDir.listFiles()));
        }
        return modFiles;
    }
}
