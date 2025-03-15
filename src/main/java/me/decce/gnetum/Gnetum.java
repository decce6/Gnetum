package me.decce.gnetum;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class Gnetum {
    //TODO: customization
    public static class Passes {
        public final static int MISC = 0;
        public final static int HUD_TEXT = 1;
        public final static int HOTBAR = 2;
        public final static int FORGE_PRE = 3;
    }

    public static int pass = 0;

    public static void nextPass()
    {
        if (++pass == 4)
        {
            pass = 0;

            FramebufferManager.getInstance().swapFramebuffers();
        }
    }
    public static boolean rendering;

    public static Logger LOGGER;

    /**
     * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
     *     Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
     * </a>
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
    }

}
