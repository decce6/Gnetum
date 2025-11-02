package me.decce.gnetum;

import com.google.common.collect.ImmutableSet;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.Set;

public class UncachedElements {
    public final Set<String> moddedPre;
    public final Set<String> moddedPost;
    public final Set<String> vanilla;

    public UncachedElements() {
        vanilla = ImmutableSet.of(
                VanillaGuiLayers.CROSSHAIR.toString(),
                VanillaGuiLayers.CAMERA_OVERLAYS.toString(),
                VanillaGuiLayers.DEMO_OVERLAY.toString(),
                "apoli:above_overlay" // Origins mod when origin is Phantom
        );
        moddedPre = ImmutableSet.of(
                "xaerominimap" // in-game waypoint, does not disable minimap
        );
        moddedPost = ImmutableSet.of();
    }

    public boolean has(String vanillaOverlay) {
        return vanilla.contains(vanillaOverlay);
    }

    public boolean has(String moddedOverlay, ElementType type) {
        return switch (type) {
            case PRE -> moddedPre.contains(moddedOverlay);
            case POST -> moddedPost.contains(moddedOverlay);
            case VANILLA -> has(moddedOverlay);
        };
    }
}
