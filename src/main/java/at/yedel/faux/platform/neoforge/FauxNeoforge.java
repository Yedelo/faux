//? if neoforge {
/*package at.yedel.faux.platform.neoforge;



import at.yedel.faux.utils.Constants;
import at.yedel.faux.utils.FileUtils;
import at.yedel.faux.utils.Logger;
import at.yedel.faux.utils.Properties;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlWriter;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;



public class FauxNeoforge {
    private static final FauxNeoforge INSTANCE = new FauxNeoforge();

    public static FauxNeoforge getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        Logger.info("Platform: NeoForge");
        Map<String, List<String>> relationMap = collect();
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
            if (nmt == null) {
                Logger.warn("File {} has no neoforge.mods.toml, skipping!", modFile);
                continue;
            }
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
        int modsRavaged = 0;
        int dependenciesRavaged = 0;

        File configDir = FileUtils.provideConfigDir();
        File configFile = new File(configDir, "fml.toml");
        Config config = null;
        if (!configFile.exists()) {
            Logger.info("Creating new fml.toml");
            config = TomlFormat.newConfig();
        }
        else {
            try (FileReader reader = new FileReader(configFile)) {
                config = TomlFormat.instance().createParser().parse(reader);
            }
            catch (IOException e) {
                Logger.error("Error reading config file!", e);
            }
            Logger.info("Found fml.toml, editing existing one");
        }
        if (!config.contains("dependencyOverrides")) {
            config.set("dependencyOverrides", TomlFormat.newConfig());
        }
        Config dependencyOverrides = config.get("dependencyOverrides");
        for (Map.Entry<String, List<String>> entry: relationMap.entrySet()) {
            List<String> ids = entry.getValue();
            dependencyOverrides.set(entry.getKey(), ids.stream().map(id -> "-" + id).toList());
            modsRavaged ++;
            dependenciesRavaged += ids.size();
        }

        Logger.info("{} {} mods and {} dependencies", Constants.CHOICE_OF_WORD, modsRavaged, dependenciesRavaged);
        TomlWriter tomlWriter = new TomlWriter();
        if (Properties.of("print-file", false)) {
            Config finalDependencyOverrides = dependencyOverrides;
            Logger.withoutFormatting(() -> Logger.info(false, tomlWriter.writeToString(finalDependencyOverrides)));
        }
        if (Properties.of("write-file", true)) {
            try (BufferedWriter writer = Files.newBufferedWriter(configFile.toPath(), StandardCharsets.UTF_8)) {
                tomlWriter.write(config, writer);
            }
            catch (IOException e) {
                Logger.error("Encountered error while writing overrides!", e);
            }
        }
        else {
            Logger.warn("Property faux.write-file is false, not writing overrides!");
        }
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
*///?}