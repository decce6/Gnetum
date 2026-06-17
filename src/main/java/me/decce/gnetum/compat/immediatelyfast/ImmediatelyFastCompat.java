package me.decce.gnetum.compat.immediatelyfast;

//? immediatelyfast {
/*import me.decce.gnetum.Gnetum;
import net.minecraft.client.gui.GuiGraphics;
import net.raphimc.immediatelyfast.ImmediatelyFast;
import net.raphimc.immediatelyfast.feature.batching.BatchingBuffers;

public class ImmediatelyFastCompat {
    public static final boolean INSTALLED = Gnetum.platform().isModLoaded("immediatelyfast");
    private static boolean suppressError;

    public static void batchIfInstalled(GuiGraphics guiGraphics, Runnable runnable) {
        if (INSTALLED) {
            try {
                BatchingBuffers.runBatched(guiGraphics, runnable);
            }
            catch (Throwable throwable) {
                if (!suppressError) {
                    suppressError = true;
                    Gnetum.LOGGER.error("Failed to call ImmediatelyFast method! This message will only appear once.", throwable);
                }
                guiGraphics.flush();
                runnable.run();
                guiGraphics.flush();
            }
        }
        else {
            guiGraphics.flush();
            runnable.run();
            guiGraphics.flush();
        }
    }

    public static void flushIfInstalledAndUsingHudBatching(GuiGraphics guiGraphics) {
        if (INSTALLED) {
            if (ImmediatelyFast.runtimeConfig.hud_batching) {
                guiGraphics.flush();
            }
        }
    }
}
*///? }