package me.decce.gnetum;

import net.minecraft.client.DeltaTracker;

import java.util.Arrays;

public class HudDeltaTracker {
    private static float lastRealtimeDeltaTicks;
    private static boolean isReady;
    private static float[] realtimeDeltaTicks;
    private static boolean logOnce;

    public static void update(DeltaTracker.Timer timer) {
        int len = Gnetum.config.numberOfPasses + 1;
        int curr = Gnetum.pass;
        if (realtimeDeltaTicks == null || realtimeDeltaTicks.length != len) {
            reset();
        }
        realtimeDeltaTicks[curr] += timer.getRealtimeDeltaTicks();
    }

    public static void reset() {
        int len = Gnetum.config.numberOfPasses + 1;
        if (realtimeDeltaTicks == null || realtimeDeltaTicks.length != len) {
            realtimeDeltaTicks = new float[len];
        }
        else {
            store();
        }
        Arrays.fill(realtimeDeltaTicks, 0f);
    }

    private static void store() {
        lastRealtimeDeltaTicks = 0f;
        for (int i = 0; i <= Gnetum.config.numberOfPasses; i++) {
            if (i < realtimeDeltaTicks.length) {
                lastRealtimeDeltaTicks += realtimeDeltaTicks[i];
            }
        }
        isReady = true;
    }

    public static void disable() {
        if (!logOnce) {
            logOnce = true;
            Gnetum.LOGGER.warn("Incompatible mod detected, disabling HUD delta tracker.");
            Gnetum.LOGGER.warn("Consider reporting to Gnetum developer.");
        }
        isReady = false;
    }

    public static boolean isReady() {
        return isReady;
    }

    public static float getRealtimeDeltaTicks() {
        return lastRealtimeDeltaTicks;
    }
}
