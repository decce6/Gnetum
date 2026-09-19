package me.decce.gnetum.compat.iris;

import me.decce.gnetum.Gnetum;
import net.irisshaders.iris.vertices.ImmediateState;

public class IrisCompat {
    public static final boolean INSTALLED = Gnetum.platform().isModLoaded("iris");
    private static final boolean IS_RENDERING_LEVEL_AVAILABLE = INSTALLED && isRenderingLevelAvailable();

    private static boolean isRenderingLevelAvailable() {
        try {
            ImmediateState.isRenderingLevel = ImmediateState.isRenderingLevel;
            return true;
        }
        catch (Throwable throwable) {
            Gnetum.LOGGER.warn("Could not access Iris ImmediateState.isRenderingLevel; the HUD may render incorrectly while a shaderpack is in use", throwable);
            return false;
        }
    }

    public static void runOutsideLevel(Runnable runnable) {
        if (!IS_RENDERING_LEVEL_AVAILABLE) {
            runnable.run();
            return;
        }
        boolean previous = ImmediateState.isRenderingLevel;
        ImmediateState.isRenderingLevel = false;
        try {
            runnable.run();
        }
        finally {
            ImmediateState.isRenderingLevel = previous;
        }
    }
}
