package me.decce.gnetum;

import me.decce.gnetum.compat.fluxloading.FluxLoadingCompat;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GLContext;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
@Config(modid = Tags.MOD_ID, name = Tags.MOD_NAME)
public class GnetumConfig {
    private static Boolean gl44;

    @Config.LangKey("gnetum.config.enabled")
    public static boolean enabled = true;

    @Config.LangKey("gnetum.config.fastBlit")
    public static boolean fastFramebufferBlits = true;

    @Config.LangKey("gnetum.config.fastClear")
    public static boolean fastFramebufferClear = true;

    @Config.LangKey("gnetum.config.hand")
    public static boolean bufferHand = false;

    public static boolean isEnabled() {
        if (FluxLoadingCompat.modInstalled && FluxLoadingCompat.isFadingOut()) {
            return false; // Temporarily disables Gnetum when FluxLoading is fading in the HUD element (only during world load - does not impact performance during gameplay)
        }
        return enabled && OpenGlHelper.isFramebufferEnabled();
    }

    private static boolean isGl44Supported() {
        if (gl44 == null) {
            gl44 = GLContext.getCapabilities().OpenGL44;
        }
        return gl44;
    }

    public static boolean useFastFramebufferBlits() {
        return fastFramebufferBlits && OpenGlHelper.isFramebufferEnabled();
    }

    public static boolean useFastFramebufferClear() {
        return fastFramebufferClear && isGl44Supported();
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Tags.MOD_ID)) {
            ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
        }
    }
}
