package me.decce.gnetum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.decce.gnetum.util.AnyBooleanValue;
import me.decce.gnetum.util.TwoStateBoolean;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;

public class GnetumConfig {
    private static final Path PATH;

	static {
		var directory = Paths.get("config");
		if (!Files.exists(directory)) {
			try {
				Files.createDirectory(directory);
			} catch (IOException ignored) {}
		}
		PATH = directory.resolve("gnetum.json");
	}

    public TwoStateBoolean enabled = new TwoStateBoolean(AnyBooleanValue.ON);
    public TwoStateBoolean showHudFps = new TwoStateBoolean(AnyBooleanValue.ON);
    public int numberOfPasses = 3;
    private int maxFps = 60;
    public int screenMaxFps = 20;

    public LinkedHashMap<String, CachedElement> map = new LinkedHashMap<>();


    public boolean isEnabled() {
        return enabled.get();
    }

    private static GnetumConfig createDefault() {
        var config = new GnetumConfig();
		//TODO
        // config.mapVanillaElements.put(Gnetum.HAND_ELEMENT, new CacheSetting(1, new TriStateBoolean(AnyBooleanValue.AUTO, false)));
        return config;
    }

    private static GnetumConfig load() {
        try {
            if (!Files.exists(PATH)) return createDefault();
            String json = Files.readString(PATH);
            Gson gson = new Gson();
            GnetumConfig config = gson.fromJson(json, GnetumConfig.class);
            config.validate();
            return config;
        } catch (IOException e) {
            Gnetum.LOGGER.error("Failed to read configuration!", e);
        }
        return createDefault();
    }

    public void validate() {
        this.numberOfPasses = clamp(this.numberOfPasses, 2, 10);
        this.maxFps = clamp(this.maxFps, 1, Constants.UNLIMITED_FPS);
        this.screenMaxFps = clamp(this.screenMaxFps, 1, Constants.SCREEN_UNLIMITED_FPS);
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
        /*if (PerformanceAnalyzer.latestAnalysisResult != null) {
            PerformanceAnalyzer.latestAnalysisResult.markOutdated();
        }*/
    }

    public int getMaxFps() {
        if (maxFps <= screenMaxFps) return maxFps;
        if (screenMaxFps == Constants.SCREEN_UNLIMITED_FPS) return maxFps;
        return Minecraft.getInstance().screen == null ? maxFps : screenMaxFps;
    }

    public int getRawMaxFps() {
        return maxFps;
    }

    public void setMaxFps(int maxFps) {
        this.maxFps = maxFps;
    }
}
