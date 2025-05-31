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
    public int numberOfPasses = 4;
    public int maxFps = 30;

    public Map<String, CacheSetting> mapVanillaElements = new HashMap<>();
    public Map<String, CacheSetting> mapModdedElementsPre = new HashMap<>();
    public Map<String, CacheSetting> mapModdedElementsPost = new HashMap<>();

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
            String json = Files.readString(PATH);
            Gson gson = new Gson();
            GnetumConfig config = gson.fromJson(json, GnetumConfig.class);
            if (!config.mapVanillaElements.containsKey(Gnetum.HAND_ELEMENT)) {
                config.mapVanillaElements.put(Gnetum.HAND_ELEMENT, new CacheSetting(1, new TriStateBoolean(AnyBooleanValue.AUTO, false)));
            }
            config.mapVanillaElements.get(Gnetum.HAND_ELEMENT).enabled.defaultValue = false;
            config.validate(true);
            return config;
        } catch (IOException e) {
            Gnetum.LOGGER.error("Failed to read configuration!", e);
        }
        return createDefault();
    }

    public void validate(boolean full) {
        if (full) {
            this.numberOfPasses = clamp(this.numberOfPasses, 2, 10);
            this.mapVanillaElements.forEach((s, c) -> c.pass = clamp(c.pass, 1, this.numberOfPasses));
            this.mapModdedElementsPost.forEach((s, c) -> c.pass = clamp(c.pass, 1, this.numberOfPasses));
            this.maxFps = clamp(this.maxFps, 1, UNLIMITED_FPS);
            this.removeElementsFromUninstalledMods();
            this.removeObsoleteVanillaElements();
        }
        this.mapModdedElementsPre.forEach((s, c) -> {
            c.pass = clamp(c.pass, 1, this.numberOfPasses);
            c.enabled.defaultValue = switch (s) {
                case "xaerominimap" -> false;
                default -> true;
            };
        });
    }

    private void removeElementsFromUninstalledMods() {
        var toRemove = new ArrayList<String>();
        mapModdedElementsPre.forEach((s, c) -> {
            if (!ModList.get().isLoaded(s)) toRemove.add(s);
        });
        mapModdedElementsPost.forEach((s, c) -> {
            if (!ModList.get().isLoaded(s)) toRemove.add(s);
        });
        toRemove.forEach(s -> {
            mapModdedElementsPre.remove(s);
            mapModdedElementsPost.remove(s);
        });
        toRemove.clear();
        mapVanillaElements.forEach((s, c) -> {
            int semicolon = s.indexOf(':');
            if (semicolon == -1) return;
            String modid = s.substring(0, semicolon);
            if (!ModList.get().isLoaded(modid)) toRemove.add(s);
        });
        toRemove.forEach(s -> mapVanillaElements.remove(s));
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

    public void save() {
        this.validate(true);
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
