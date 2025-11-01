package me.decce.gnetum;

public class HudDeltaTracker {
    private static float[] partialTicks;

    public static void update(float partialTick) {
        int len = Gnetum.config.numberOfPasses + 1;
        int curr = Gnetum.passManager.current;
        if (partialTicks == null || partialTicks.length != len) {
            partialTicks = new float[len];
        }
        partialTicks[curr] += partialTick;
    }

    public static void reset() {
        int len = Gnetum.config.numberOfPasses + 1;
        if (partialTicks == null || partialTicks.length != len) {
            partialTicks = new float[len];
        }
        for (int i = 0; i < len; i++) {
            partialTicks[i] = 0F;
        }
    }

    public static float getPartialTick() {
        float ret = 0F;
        for (int i = 0; i <= Gnetum.config.numberOfPasses; i++) {
            if (i < partialTicks.length) {
                ret += partialTicks[i];
            }
        }
        return Math.min(ret, 1F);
    }
}