package me.decce.gnetum;

import me.decce.gnetum.mixins.GuiAccessor;
import me.decce.gnetum.mixins.GuiLayerManagerAccessor;
import net.minecraft.client.Minecraft;

public class SuggestedPass {
    public static int get(String name) {
        int pass = switch (name) {
            // TODO: stop using string literals
            // note: camera_overlays, crosshair, etc. are uncached but are still included in here to make sure mods that insert layers beside them have a correct default pass
            case "minecraft:camera_overlays", "minecraft:crosshair", "minecraft:hotbar" -> 1;
            case "minecraft:jump_meter", "minecraft:experience_bar", "gnetum:status_bar", "minecraft:selected_item_name", "minecraft:spectator_tooltip", "minecraft:experience_level" -> 2;
            case "minecraft:effects", "minecraft:boss_overlay", "minecraft:sleep_overlay", "minecraft:demo_overlay", "minecraft:debug_overlay", "minecraft:scoreboard_sidebar", "minecraft:overlay_message", "minecraft:title", "minecraft:chat", "minecraft:tab_list", "minecraft:subtitle_overlay", "minecraft:saving_indicator" -> 3;
            default -> {
                if (name.startsWith("minecraft:")) {
                    yield 3;
                }
                var layers = ((GuiLayerManagerAccessor)((GuiAccessor)Minecraft.getInstance().gui).getLayerManager()).getLayers();
                for (int i = 0; i < layers.size(); i++) {
                    if (layers.get(i).name().toString().equals(name)) {
                        if (i == 0) yield 1;
                        yield get(layers.get(i - 1).name().toString());
                    }
                }
                yield 3;
            }
        };
        if (pass > Gnetum.config.numberOfPasses) pass = Gnetum.config.numberOfPasses;
        return pass;
    }
}
