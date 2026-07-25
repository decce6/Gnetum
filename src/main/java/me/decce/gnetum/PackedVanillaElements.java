package me.decce.gnetum;

import net.minecraftforge.client.gui.OverlayRegistry;

import java.util.HashSet;

public class PackedVanillaElements {
    public static final String PACKED_STATUS_BAR = Gnetum.STATUS_BAR;
    public static HashSet<String> set;

    public static void init() {
        set = new HashSet<>();
        var overlays = OverlayRegistry.orderedEntries();
        // Pack all elements on the status bar (see VanillaGuiOverlays)
        boolean pack = false;
        for (var overlay : overlays) {
            if ("Boss Health".equals(overlay.getDisplayName())) {
                pack = true;
                continue; // Start packing from the next element
            }
            if ("Jump Bar".equals(overlay.getDisplayName())) {
                break;
            }
            if (pack) {
                set.add(overlay.getDisplayName());
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
