package me.decce.gnetum;

import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

import java.util.HashSet;
import java.util.Set;

public class UncachedVanillaElements {
    public Set<String> set;

    public UncachedVanillaElements() {
        set = new HashSet<>(2);
        set.add(VanillaGuiOverlay.CROSSHAIR.id().toString());
        set.add(VanillaGuiOverlay.VIGNETTE.id().toString());
    }
}
