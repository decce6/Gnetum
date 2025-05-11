package me.decce.gnetum;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
@Config(modid = Tags.MOD_ID, name = Tags.MOD_NAME)
public class GnetumConfig {
    @Config.LangKey("gnetum.config.enabled")
    public static boolean enabled = true;

    @Config.LangKey("gnetum.config.fastBlit")
    public static boolean fastFramebufferBlits = true;

    @Config.LangKey("gnetum.config.fastClear")
    public static boolean fastFramebufferClear = true; //TODO: remove this when we implement our own config screen

    @Config.LangKey("gnetum.config.hand")
    public static boolean bufferHand = false;

    public static boolean isEnabled() {
        return enabled && OpenGlHelper.isFramebufferEnabled();
    }

    public static boolean useFastFramebufferBlits() {
        return fastFramebufferBlits && OpenGlHelper.isFramebufferEnabled();
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Tags.MOD_ID)) {
            ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
        }
    }
}
