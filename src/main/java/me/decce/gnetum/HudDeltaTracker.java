package me.decce.gnetum;

import net.minecraft.client.Minecraft;

import java.util.Arrays;

public class HudDeltaTracker {
    private static float lastTickDelta = Float.NaN;
    private static float[] tickDeltas;

    public static void update(Minecraft minecraft) {
        int len = Gnetum.config.numberOfPasses + 1;
        int curr = Gnetum.passManager.current;
        if (tickDeltas == null || tickDeltas.length != len) {
            reset();
        }
        tickDeltas[curr] += minecraft.getDeltaFrameTime();
    }

    public static void reset() {
        int len = Gnetum.config.numberOfPasses + 1;
        if (tickDeltas == null || tickDeltas.length != len) {
            tickDeltas = new float[len];
        }
        else {
            store();
        }
        Arrays.fill(tickDeltas, 0f);
    }

    private static void store() {
        lastTickDelta = 0f;
        for (int i = 0; i <= Gnetum.config.numberOfPasses; i++) {
            if (i < tickDeltas.length) {
                lastTickDelta += tickDeltas[i];
            }
        }
    }

    public static boolean isReady() {
        return !Float.isNaN(lastTickDelta);
    }

    public static float getTickDelta() {
        return lastTickDelta;
    }
}
