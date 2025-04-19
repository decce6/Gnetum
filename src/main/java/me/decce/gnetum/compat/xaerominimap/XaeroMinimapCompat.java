package me.decce.gnetum.compat.xaerominimap;

import me.decce.gnetum.Gnetum;
import net.minecraftforge.fml.common.Loader;
import xaero.common.core.XaeroMinimapCore;

// Xaero's Minimap renders the ingame waypoints in a very werid way (ASM is used to insert the call into
// GuiIngameForge.renderGameOverlay, which broke due to our mixin), so we manually call their render method as a
// workaround.
// This is only for the ingame waypoint rendering. Minimap rendering is not relevant.
public class XaeroMinimapCompat {
    public static boolean modInstalled = Loader.isModLoaded("xaerominimap");

    public static void callBeforeIngameGuiRender(float partialTicks) {
        try {
            XaeroMinimapCore.beforeIngameGuiRender(partialTicks);
        } catch (Throwable e) {
            Gnetum.LOGGER.error("Error invoking Xaero's Minimap method!", e);
            modInstalled = false;
        }
    }
}
