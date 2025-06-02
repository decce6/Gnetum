package me.decce.gnetum.compat.immediatelyfast;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.fml.ModList;
import net.raphimc.immediatelyfast.feature.batching.BatchingBuffers;

public class ImmediatelyFastCompat {
    public static final boolean INSTALLED = ModList.get().isLoaded("immediatelyfast");

    public static void batchIfInstalled(GuiGraphics guiGraphics, Runnable runnable) {
        if (INSTALLED) {
            try {
                BatchingBuffers.runBatched(guiGraphics, runnable);
            }
            catch (Throwable ignored) {
                runnable.run();
            }
        }
        else {
            runnable.run();
        }
    }
}
