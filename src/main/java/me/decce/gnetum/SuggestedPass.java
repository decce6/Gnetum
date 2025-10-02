package me.decce.gnetum;

import net.minecraftforge.client.gui.overlay.GuiOverlayManager;

public class SuggestedPass {
    public static int get(String name) {
        int pass = switch (name) {
            case "minecraft:spyglass", "minecraft:helmet", "minecraft:frostbite", "minecraft:portal", "minecraft:hotbar" -> 1;
            case "minecraft:boss_event_progress", "gnetum.packedElement.left", "gnetum.packedElement.right", "minecraft:jump_bar", "minecraft:experience_bar" -> 2;
            default -> {
                if (name.startsWith("minecraft:")) {
                    if (PackedVanillaElements.isPacked(name)) {
                        yield get(PackedVanillaElements.getPacked(name).getKey());
                    }
                    yield 3;
                }
                var overlays = GuiOverlayManager.getOverlays();
                for (int i = 0; i < overlays.size(); i++) {
                    if (overlays.get(i).id().toString().equals(name)) {
                        if (i == 0) yield 1;
                        yield get(overlays.get(i - 1).id().toString());
                    }
                }
                yield 3;
            }
        };
        if (pass > Gnetum.config.numberOfPasses) pass = Gnetum.config.numberOfPasses;
        return pass;
    }
}
