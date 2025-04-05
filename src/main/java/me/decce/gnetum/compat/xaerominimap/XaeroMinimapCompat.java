package me.decce.gnetum.compat.xaerominimap;

import me.decce.gnetum.Gnetum;
import net.minecraftforge.fml.common.Loader;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

// Xaero's Minimap renders the ingame waypoints in a very werid way (ASM is used to insert the call into
// GuiIngameForge.renderGameOverlay, which broke due to our mixin), so we manually call their render method as a
// workaround.
// This is only for the ingame waypoint rendering. Minimap rendering is not relevant.
public class XaeroMinimapCompat {
    public static boolean modInstalled;
    private static MethodHandle methodHandle;
    static {
        if (Loader.isModLoaded("xaerominimap")) {
            try {
                Class<?> clazzXaeroMinimapCore = Class.forName("xaero.common.core.XaeroMinimapCore");
                MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
                MethodType methodType = MethodType.methodType(void.class, float.class);
                methodHandle = publicLookup.findStatic(clazzXaeroMinimapCore, "beforeIngameGuiRender", methodType);
                modInstalled = true;
            }
            catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException ignored) {}
        }
    }

    public static void callBeforeIngameGuiRender(float partialTicks) {
        try {
            methodHandle.invokeExact(partialTicks);
        } catch (Throwable e) {
            Gnetum.LOGGER.error("Error invoking Xaero's Minimap method!", e);
        }
    }
}
