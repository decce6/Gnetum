package me.decce.gnetum;

import com.mojang.blaze3d.platform.InputConstants;
import me.decce.gnetum.gui.ConfigScreen;
import me.decce.gnetum.util.AnyBooleanValue;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Mod(value = Gnetum.MOD_ID)
public final class Gnetum {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "gnetum";

    public static final FpsCounter FPS_COUNTER = new FpsCounter();
    public static GnetumConfig config;
    public static PassManager passManager;
    public static UncachedVanillaElements uncachedVanillaElements;
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

    public Gnetum() {
        Gnetum.passManager = new PassManager();
        Gnetum.uncachedVanillaElements = new UncachedVanillaElements();
        GnetumConfig.reload();

        //noinspection removal // we want the mod to be loadable on an older version of forge
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Gnetum::registerBindings);
        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerJoin);

        //noinspection removal
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, ()-> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> new ConfigScreen()));
    }

    public static CacheSetting getCacheSetting(String vanillaOverlay) {
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
            config.validate(false); // sets the correct default value
        }
        return map.get(moddedOverlay);
    }

    public static void disableCachingForCurrentElement() {
        if (currentElement == null || currentElementType == null) return;
        CacheSetting cacheSetting = getCacheSetting(currentElement, currentElementType);
        if (cacheSetting.enabled.get() && cacheSetting.enabled.value == AnyBooleanValue.AUTO) {
            LOGGER.info("Disabling caching for element {}.", currentElement);
            cacheSetting.enabled.defaultValue = false;
            FramebufferManager.getInstance().dropCurrentFrame();
        }
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(CONFIG_MAPPING.get());
    }

    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (CONFIG_MAPPING.get().consumeClick()) {
                if (!(Minecraft.getInstance().screen instanceof ConfigScreen)) {
                    Minecraft.getInstance().setScreen(new ConfigScreen(PerformanceAnalyzer.analyze()));
                }
            }
        }
    }

    public void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        Gnetum.FPS_COUNTER.reset();
    }
}
