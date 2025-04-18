package me.decce.gnetum;

import com.google.common.collect.Lists;
import me.decce.gnetum.compat.UncachedEventListeners;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, clientSideOnly = true, acceptableRemoteVersions = "*")
public class Gnetum {
    public static boolean rendering;
    public static boolean forceFullRebuild; // forces a full rebuild of the HUD on the first frame after joining world

    public static Logger LOGGER;

    public static UncachedEventListeners uncachedPreEventListeners = new UncachedEventListeners(Lists.newArrayList(
            "customnausea",
            "quark", //for its Better Nausea feature
            "thaumcraft",
            "timeisup" //using incompatible blend functions
    ));

    public static UncachedEventListeners uncachedPostEventListeners = new UncachedEventListeners(Lists.newArrayList(
            "fluxloading",
            "ingameinfo" // InGame Info Reborn has its own framebuffers and refresh rate limit
    ));

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        uncachedPreEventListeners.sort();
        uncachedPreEventListeners.trim();
        uncachedPostEventListeners.sort();
        uncachedPostEventListeners.trim();
    }

    @SubscribeEvent
    public static void onJoinWorld(WorldEvent.Load event) {
        if (event.getWorld().isRemote){
            FramebufferManager.getInstance().reset();
            forceFullRebuild = true;
        }
    }
}
