package me.decce.gnetum.compat.optifine;

import me.decce.gnetum.Gnetum;
import me.decce.gnetum.mixins.early.compat.optifine.ConfigAccessor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;

public class OptiFineCompat {
    public static boolean optifineInstalled = FMLClientHandler.instance().hasOptifine();

    public static boolean isVignetteEnabled() {
        try {
            return ConfigAccessor.invokeIsVignetteEnabled();
        } catch (Throwable e) {
            Gnetum.LOGGER.error("Error invoking OptiFine method when retrieving vignette status.", e);
            optifineInstalled = false;
        }
        return Minecraft.getMinecraft().gameSettings.fancyGraphics;
    }
}
