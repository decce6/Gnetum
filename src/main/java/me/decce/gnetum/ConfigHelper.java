package me.decce.gnetum;

import me.decce.gnetum.compat.optifine.OptiFineCompat;
import net.minecraft.client.Minecraft;

public class ConfigHelper {
    public static boolean isVignetteEnabled() {
        return OptiFineCompat.optifineInstalled ? OptiFineCompat.isVignetteEnabled() : Minecraft.getMinecraft().gameSettings.fancyGraphics;
    }
}
