package at.yedel.faux.utils;



import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;



public class ModFiles {
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
