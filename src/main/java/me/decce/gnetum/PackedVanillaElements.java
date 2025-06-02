package me.decce.gnetum;

import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.HashMap;
import java.util.Map;

public class PackedVanillaElements {
    private static final Map<String, Pack> map;

    // The position of these elements depend on each other and cannot be allowed to be configured individually
    private static final Pack leftElements = Pack.of("gnetum.packedElement.left", VanillaGuiLayers.ARMOR_LEVEL.toString(), VanillaGuiLayers.PLAYER_HEALTH.toString(), "appleskin:health_offset", "appleskin:health_restored");
    private static final Pack rightElements = Pack.of("gnetum.packedElement.right", VanillaGuiLayers.AIR_LEVEL.toString(), VanillaGuiLayers.FOOD_LEVEL.toString(), VanillaGuiLayers.VEHICLE_HEALTH.toString(), "appleskin:exhaustion_level", "appleskin:food_offset", "appleskin:hunger_restored", "appleskin:saturation_level");

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

        public static Pack of(String key, String... overlays) {
            Pack pack = new Pack();
            pack.key = key;
            pack.overlays = overlays;
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
