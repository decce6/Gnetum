package me.decce.gnetum.compat.optifine;

import me.decce.gnetum.Gnetum;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class OptiFineCompat {
    public static boolean optifineInstalled;

    private static MethodHandle methodHandle;

    static {
        if (FMLClientHandler.instance().hasOptifine()) {
            try {
                Class<?> clazzConfig = Class.forName("Config");
                MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
                MethodType methodType = MethodType.methodType(boolean.class);
                methodHandle = publicLookup.findStatic(clazzConfig, "isVignetteEnabled", methodType);
                optifineInstalled = true;
            }
            catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException ignored) {}
        }
    }

    public static boolean isVignetteEnabled() {
        try {
            return (boolean)methodHandle.invokeExact();
        } catch (Throwable e) {
            Gnetum.LOGGER.error("Error invoking OptiFine method when retrieving vignette status.", e);
        }
        return Minecraft.getMinecraft().gameSettings.fancyGraphics;
    }
}
