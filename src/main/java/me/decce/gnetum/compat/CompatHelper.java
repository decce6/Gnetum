package me.decce.gnetum.compat;

import me.decce.gnetum.compat.optifine.OptiFineCompat;
import net.minecraft.client.Minecraft;

public class CompatHelper {
    public static boolean isVignetteEnabled() {
        return OptiFineCompat.optifineInstalled ? OptiFineCompat.isVignetteEnabled() : Minecraft.getMinecraft().gameSettings.fancyGraphics;
    }
}
