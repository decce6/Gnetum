package me.decce.gnetum;

import me.decce.gnetum.mixins.GuiAccessor;
import me.decce.gnetum.mixins.GuiLayerManagerAccessor;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.HashSet;

public class PackedVanillaElements {
    public static final String PACKED_STATUS_BAR = "gnetum:status_bar";
    public static HashSet<String> set;

    public static void init() {
        set = new HashSet<>();
        var layerManager = ((GuiAccessor)Minecraft.getInstance().gui).getLayerManager();
        var layers = ((GuiLayerManagerAccessor)layerManager).getLayers();
        // Pack all elements on the status bar (see VanillaGuiOverlays)
        boolean pack = false;
        for (var layer : layers) {
            if (VanillaGuiLayers.EXPERIENCE_BAR.equals(layer.name())) {
                pack = true;
                continue; // Start packing from the next element
            }
            if (VanillaGuiLayers.SELECTED_ITEM_NAME.equals(layer.name())) {
                break;
            }
            if (pack) {
                set.add(layer.name().toString());
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
