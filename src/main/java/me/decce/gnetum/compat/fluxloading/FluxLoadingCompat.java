package me.decce.gnetum.compat.fluxloading;

import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;
import me.decce.gnetum.Gnetum;
import net.minecraftforge.fml.common.Loader;

public class FluxLoadingCompat {
    public static boolean modInstalled = Loader.isModLoaded("fluxloading");

    public static boolean isFadingOut() {
        try {
            return WorldLoadingScreenOverhaul.isFadingOut();
        } catch (Throwable throwable) { // The user might be using an old version of FluxLoading so the method does not exist
            modInstalled = false;
            Gnetum.LOGGER.error("Error invoking FluxLoading methods! (Make sure you have the latest version of FluxLoading installed)", throwable);
        }
        return false;
    }
}
