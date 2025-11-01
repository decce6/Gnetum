package me.decce.gnetum;

import me.decce.gnetum.gui.BaseScreen;
import me.decce.gnetum.gui.ConfigScreen;
import me.decce.gnetum.hud.VanillaHuds;
import me.decce.gnetum.util.AnyBooleanValue;
import me.decce.gnetum.util.time.GlfwTimeSource;
import me.decce.gnetum.util.time.JavaTimeSource;
import me.decce.gnetum.util.time.TimeSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
@Mod(modid = Tags.MOD_ID,
        name = Tags.MOD_NAME,
        version = Tags.VERSION,
        clientSideOnly = true,
        acceptableRemoteVersions = "*",
        guiFactory = "me.decce.gnetum.GnetumConfigGuiFactory")
public class Gnetum {
    private static MutableScaledResolution scaledResolution;
    private static TimeSource timeSource;
    public static final FpsCounter FPS_COUNTER = new FpsCounter();
    public static GnetumConfig config;
    public static final String HAND_ELEMENT = "gnetum:minecraft_hand";
    public static final String OTHER_MODS = "gnetum_unknown";
    public static PassManager passManager;
    public static UncachedElements uncachedElements;
    public static String currentElement;
    public static ElementType currentElementType;
    public static boolean rendering;
    public static boolean renderingCanceled;
    public static boolean isRenderingHelmet;
    public static long lastSwapNanos;

    public static final KeyBinding KEYBIND = new KeyBinding("gnetum.config.keyMapping", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_END, "key.categories.misc");

    public static Logger LOGGER;

    public static MutableScaledResolution getScaledResolution() {
        if (scaledResolution == null) {
            scaledResolution = new MutableScaledResolution();
        }
        return scaledResolution;
    }

    public static TimeSource getTimeSource() {
        if (timeSource == null) {
            if (GlfwTimeSource.isAvailable()) {
                timeSource = new GlfwTimeSource();
            }
            else {
                timeSource = new JavaTimeSource();
            }
        }
        return timeSource;
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
            config.validate();
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

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        uncachedElements = new UncachedElements();
        config = GnetumConfig.load(event.getModConfigurationDirectory().toPath());
        passManager = new PassManager();
        VanillaHuds.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientRegistry.registerKeyBinding(KEYBIND);
    }

    @SubscribeEvent
    public static void onJoinWorld(WorldEvent.Load event) {
        if (config.isEnabled() && event.getWorld().isRemote){
            Gnetum.FPS_COUNTER.reset();
            FramebufferManager.getInstance().reset();
        }
    }

    @SubscribeEvent
    public static void onKeyPressed(InputEvent.KeyInputEvent event) {
        if (KEYBIND.isPressed() && !(Minecraft.getMinecraft().currentScreen instanceof BaseScreen)) {
            Minecraft.getMinecraft().displayGuiScreen(new ConfigScreen(PerformanceAnalyzer.analyze()));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH) // HIGH to run after VintageFix's listener
    public static void onRenderF3(RenderGameOverlayEvent.Text event) {
        if (!Gnetum.config.isEnabled() || !Gnetum.config.showHudFps.get()) {
            return;
        }
        if(Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            String str = String.format("HUD: %d fps (nr=%d, cap=%s)", Gnetum.FPS_COUNTER.getFps(), Gnetum.config.numberOfPasses, Gnetum.config.maxFps == GnetumConfig.UNLIMITED_FPS ? "unlimited" : Gnetum.config.maxFps);
            if (event.getLeft().size() > 2) {
                event.getLeft().add(2, str);
            }
            else {
                event.getLeft().add(str);
            }
        }
    }
}
