package me.decce.gnetum;

import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

import java.util.HashMap;
import java.util.Map;

public class PackedVanillaElements {
    private static final Map<String, Pack> map;

    // The position of these elements depend on each other and cannot be allowed to be configured individually
    private static final Pack leftElements = Pack.of("gnetum.packedElement.left", VanillaGuiOverlay.ARMOR_LEVEL, VanillaGuiOverlay.PLAYER_HEALTH);
    private static final Pack rightElements = Pack.of("gnetum.packedElement.right", VanillaGuiOverlay.AIR_LEVEL, VanillaGuiOverlay.FOOD_LEVEL, VanillaGuiOverlay.MOUNT_HEALTH);

    static {
        map = new HashMap<>(leftElements.getOverlays().length + rightElements.getOverlays().length);
        for (String overlay : leftElements.getOverlays()) {
            map.put(overlay, leftElements);
        }
        for (String overlay : rightElements.getOverlays()) {
            map.put(overlay, rightElements);
        }
    }

    public static boolean isPacked(String element) {
        return map.containsKey(element);
    }

    public static Pack getPacked(String element) {
        return map.get(element);
    }

    public static Map<String, Pack> getMap() {
        return map;
    }

    public static class Pack {
        private String[] overlays;
        private String key;

        public static Pack of(String key, VanillaGuiOverlay... overlays) {
            Pack pack = new Pack();
            pack.key = key;
            pack.overlays = new String[overlays.length];
            for (int i = 0; i < overlays.length; i++) {
                pack.overlays[i] = overlays[i].id().toString();
            }
            return pack;
        }

        public String[] getOverlays() {
            return overlays;
        }

        public String getKey() {
            return key;
        }
    }
}
