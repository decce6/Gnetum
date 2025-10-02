package me.decce.gnetum;

import com.mojang.blaze3d.platform.InputConstants;
import me.decce.gnetum.gui.ConfigScreen;
import me.decce.gnetum.util.AnyBooleanValue;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Mod(value = Gnetum.MOD_ID, dist = Dist.CLIENT)
public final class Gnetum {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "gnetum";
    public static final String HAND_ELEMENT = "gnetum:minecraft_hand";

    public static final FpsCounter FPS_COUNTER = new FpsCounter();
    public static GnetumConfig config;
    public static PassManager passManager;
    public static UncachedElements uncachedElements;
    public static String currentElement;
    public static ElementType currentElementType;

    public static long lastSwapNanos;
    public static boolean rendering;
    public static boolean renderingCanceled; // forge allows mods to cancel rendering of the HUD in the pre event

    public static final Lazy<KeyMapping> CONFIG_MAPPING = Lazy.of(() -> new KeyMapping(
            "gnetum.config.keyMapping",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_END,
            "key.categories.misc"));

    public Gnetum(FMLModContainer container, IEventBus modBus, Dist dist) {
        modBus.addListener(Gnetum::registerBindings);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
        NeoForge.EVENT_BUS.addListener(this::onCustomizeF3Text);
        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);

        container.registerExtensionPoint(IConfigScreenFactory.class, new GnetumConfigScreenFactory());
    }

    public static void ensureInitialized() {
        if (Gnetum.passManager == null) {
            Gnetum.passManager = new PassManager();
            Gnetum.uncachedElements = new UncachedElements();
        }
        GnetumConfig.reload(); // We intentionally avoid loading config in the constructor, because at that point GUI layers are not registered yet
    }

    public static CacheSetting getCacheSetting(String vanillaOverlay) {
        if (PackedVanillaElements.isPacked(vanillaOverlay)) {
            vanillaOverlay = PackedVanillaElements.getPacked(vanillaOverlay).getKey();
        }
        if (!config.mapVanillaElements.containsKey(vanillaOverlay)) {
            config.mapVanillaElements.put(vanillaOverlay, new CacheSetting(SuggestedPass.get(vanillaOverlay)));
        }
        return Gnetum.config.mapVanillaElements.get(vanillaOverlay);
    }

    public static CacheSetting getCacheSetting(String moddedOverlay, ElementType type) {
        if (type == ElementType.VANILLA) return getCacheSetting(moddedOverlay);
        var map = type == ElementType.PRE ? config.mapModdedElementsPre : config.mapModdedElementsPost;
        if (!map.containsKey(moddedOverlay)) {
            map.put(moddedOverlay, new CacheSetting(type == ElementType.PRE ? 1 : config.numberOfPasses));
        }
        return map.get(moddedOverlay);
    }

    public static void disableCachingForCurrentElement() {
        if (currentElement == null || currentElementType == null) return;
        CacheSetting cacheSetting = getCacheSetting(currentElement, currentElementType);
        if (cacheSetting.enabled.get() && cacheSetting.enabled.value == AnyBooleanValue.AUTO) {
            LOGGER.info("Disabling caching for element {}. If the cache setting for this element is set to \"ON\" instead of \"AUTO\" you can ignore this message.", currentElement);
            cacheSetting.enabled.defaultValue = false;
            FramebufferManager.getInstance().dropCurrentFrame();
        }
    }

    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(CONFIG_MAPPING.get());
    }

    public void onClientTick(ClientTickEvent.Post event) {
        while (CONFIG_MAPPING.get().consumeClick()) {
            if (!(Minecraft.getInstance().screen instanceof ConfigScreen)) {
                Minecraft.getInstance().setScreen(new ConfigScreen(PerformanceAnalyzer.analyze()));
            }
        }
    }

    public void onCustomizeF3Text(CustomizeGuiOverlayEvent.DebugText event) {
        if (Gnetum.config.isEnabled() && Gnetum.config.showHudFps.get() && Minecraft.getInstance().getDebugOverlay().showDebugScreen()) {
            var left = event.getLeft();
            if (left.size() > 2) {
                event.getLeft().add(2, String.format("HUD: %d fps (nr=%d, cap=%s)", Gnetum.FPS_COUNTER.getFps(), Gnetum.config.numberOfPasses, Gnetum.config.maxFps == GnetumConfig.UNLIMITED_FPS ? "unlimited" : Gnetum.config.maxFps));
            }
        }
    }

    public void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        Gnetum.ensureInitialized();
        Gnetum.FPS_COUNTER.reset();
        FramebufferManager.getInstance().reset();
    }
}
