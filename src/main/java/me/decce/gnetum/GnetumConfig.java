package me.decce.gnetum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.decce.gnetum.util.AnyBooleanValue;
import me.decce.gnetum.util.TriStateBoolean;
import me.decce.gnetum.util.TwoStateBoolean;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GnetumConfig {
    public static final int UNLIMITED_FPS = 125; // when maxFps is set to this value it means unlimited
    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve("gnetum.json");

    public TwoStateBoolean enabled = new TwoStateBoolean(AnyBooleanValue.ON);
    public TwoStateBoolean showHudFps = new TwoStateBoolean(AnyBooleanValue.ON);
    public int numberOfPasses = 3;
    public int maxFps = 60;

    public HashMap<String, CacheSetting> mapVanillaElements = new HashMap<>();
    public HashMap<String, CacheSetting> mapModdedElementsPre = new HashMap<>();
    public HashMap<String, CacheSetting> mapModdedElementsPost = new HashMap<>();

    public boolean isEnabled() {
        return enabled.get();
    }

    private static GnetumConfig createDefault() {
        var config = new GnetumConfig();
        config.mapVanillaElements.put(Gnetum.HAND_ELEMENT, new CacheSetting(1, new TriStateBoolean(AnyBooleanValue.AUTO, false)));
        return config;
    }

    private static GnetumConfig load() {
        try {
            if (!Files.exists(PATH)) return createDefault();
            String json = Files.readString(PATH);
            Gson gson = new Gson();
            GnetumConfig config = gson.fromJson(json, GnetumConfig.class);
            if (!config.mapVanillaElements.containsKey(Gnetum.HAND_ELEMENT)) {
                config.mapVanillaElements.put(Gnetum.HAND_ELEMENT, new CacheSetting(1, new TriStateBoolean(AnyBooleanValue.AUTO, false)));
            }
            config.mapVanillaElements.get(Gnetum.HAND_ELEMENT).enabled.defaultValue = false;
            config.validate();
            return config;
        } catch (IOException e) {
            Gnetum.LOGGER.error("Failed to read configuration!", e);
        }
        return createDefault();
    }

    public void validate() {
        this.numberOfPasses = clamp(this.numberOfPasses, 2, 10);
        this.mapVanillaElements.forEach((s, c) -> c.pass = clamp(c.pass, 1, this.numberOfPasses));
        this.mapModdedElementsPre.forEach((s, c) -> c.pass = clamp(c.pass, 1, this.numberOfPasses));
        this.mapModdedElementsPost.forEach((s, c) -> c.pass = clamp(c.pass, 1, this.numberOfPasses));
        this.maxFps = clamp(this.maxFps, 1, UNLIMITED_FPS);
        this.hideElementsOrphanOrUncached();
        this.removeObsoleteVanillaElements();
    }

    private void hideElementsOrphanOrUncached() {
        mapModdedElementsPre.entrySet().stream()
                .filter(entry -> !ModList.get().isLoaded(entry.getKey()) || Gnetum.uncachedElements.has(entry.getKey(), ElementType.PRE))
                .forEach(entry -> entry.getValue().hidden = true);
        mapModdedElementsPost.entrySet().stream()
                .filter(entry -> !ModList.get().isLoaded(entry.getKey()) || Gnetum.uncachedElements.has(entry.getKey(), ElementType.POST))
                .forEach(entry -> entry.getValue().hidden = true);
        mapVanillaElements.forEach((s, c) -> {
            int semicolon = s.indexOf(':');
            if (semicolon == -1) return;
            String modid = s.substring(0, semicolon);
            if (!ModList.get().isLoaded(modid)) c.hidden = true;
        });
    }

    private void removeObsoleteVanillaElements() {
        PackedVanillaElements.getMap().forEach((key, value)
                -> Arrays.stream(value.getOverlays()).forEach(key1 -> mapVanillaElements.remove(key1)));
    }

    private static int clamp(int value, int min, int max) { // min & max: inclusive
        return Math.max(Math.min(value, max), min);
    }

    public static void reload() {
        Gnetum.config = load();
    }

    public static void reset() {
        Gnetum.config = createDefault();
        Gnetum.config.save();
    }

    public void save() {
        this.validate();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);
        try {
            Files.writeString(PATH, json, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            Gnetum.LOGGER.error("Failed to save config file!", e);
        }
        if (PerformanceAnalyzer.latestAnalysisResult != null) {
            PerformanceAnalyzer.latestAnalysisResult.markOutdated();
        }
    }
}
