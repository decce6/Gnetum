package me.decce.gnetum;

import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.HashSet;
import java.util.Set;

public class UncachedVanillaElements {
    public Set<String> set;

    public UncachedVanillaElements() {
        set = new HashSet<>(3);
        set.add(VanillaGuiLayers.CROSSHAIR.toString());
        set.add(VanillaGuiLayers.CAMERA_OVERLAYS.toString());
        set.add(VanillaGuiLayers.DEMO_OVERLAY.toString());
    }
}
