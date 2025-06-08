package me.decce.gnetum;

import me.decce.gnetum.mixins.MinecraftAccessor;
import net.minecraft.client.Minecraft;

public class HudDeltaTracker {
    private static float[] tickDelta;

    public static void update() {
        int len = Gnetum.config.numberOfPasses + 1;
        int curr = Gnetum.passManager.current;
        if (tickDelta == null || tickDelta.length != len) {
            tickDelta = new float[len];
        }
        tickDelta[curr] += ((MinecraftAccessor)Minecraft.getInstance()).getTimer().tickDelta;
    }

    public static void reset() {
        int len = Gnetum.config.numberOfPasses + 1;
        if (tickDelta == null || tickDelta.length != len) {
            tickDelta = new float[len];
        }
        for (int i = 0; i < len; i++) {
            reset(i);
        }
    }

    public static void reset(int i ) {
        tickDelta[i] = 0F;
    }

    public static void step() {
        int len = Gnetum.config.numberOfPasses + 1;
        if (tickDelta == null || tickDelta.length != len) {
            reset();
        }
        int i = Gnetum.passManager.current;
        reset(i);
    }

    public static float getTickDelta() {
        float ret = 0F;
        for (int i = 0; i <= Gnetum.config.numberOfPasses; i++) {
            if (i < tickDelta.length) {
                ret += tickDelta[i];
            }
        }
        return ret;
    }
}