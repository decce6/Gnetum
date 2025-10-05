package me.decce.gnetum;

import com.google.common.collect.ImmutableSet;
import me.decce.gnetum.hud.VanillaHuds;

import java.util.Set;

public class UncachedElements {
    public final Set<String> moddedPre;
    public final Set<String> moddedPost;
    public final Set<String> vanilla;

    public UncachedElements() {
        vanilla = ImmutableSet.of(
                VanillaHuds.VIGNETTE.id().toString(),
                VanillaHuds.PUMPKIN.id().toString(),
                VanillaHuds.CROSSHAIRS.id().toString()
        );
        moddedPre = ImmutableSet.of(
                "customnausea",
                "quark", //for its Better Nausea feature
                "thaumcraft",
                "timeisup",
                "scalingguis", // we already suppressed the whole listener - hide it from config screen
                "betterhud" // caching is implemented manually
        );
        moddedPost = ImmutableSet.of(
                "fluxloading",
                "ingameinfo" // InGame Info Reborn has its own framebuffers and refresh rate limit
        );
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
