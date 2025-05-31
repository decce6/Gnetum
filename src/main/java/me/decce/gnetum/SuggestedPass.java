package me.decce.gnetum;

public class SuggestedPass {
    public static int get(String name) {
        int pass = switch (name) {
            case "minecraft:spyglass", "minecraft:helmet", "minecraft:frostbite", "minecraft:portal" -> 1;
            case "minecraft:hotbar", "minecraft:boss_event_progress" -> 2;
            case "minecraft:player_health", "minecraft:armor_level", "minecraft:food_level", "minecraft:air_level", "minecraft:mount_health", "minecraft:jump_bar", "minecraft:experience_bar"  -> 3;
            default -> 4;
        };
        if (pass > Gnetum.config.numberOfPasses) pass = Gnetum.config.numberOfPasses;
        return pass;
    }
}
