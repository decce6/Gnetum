package me.decce.gnetum;

import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

import java.util.HashSet;

public class PackedVanillaElements {
    public static final String PACKED_STATUS_BAR = "gnetum.statusBar";
    public static HashSet<String> set;

    public static void init() {
        set = new HashSet<>();
        var overlays = GuiOverlayManager.getOverlays();
        // Pack all elements on the status bar (see VanillaGuiOverlays)
        boolean pack = false;
        for (var overlay : overlays) {
            if (overlay.id().equals(VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id())) {
                pack = true;
                continue; // Start packing from the next element
            }
            if (overlay.id().equals(VanillaGuiOverlay.JUMP_BAR.id())) {
                break;
            }
            if (pack) {
                set.add(overlay.id().toString());
            }
        }
    }

    public static String consider(String element) {
        if (set == null) {
            init();
        }
        return set.contains(element) ? PACKED_STATUS_BAR : element;
    }

    public static void reset() {
        set = null;
    }
}
