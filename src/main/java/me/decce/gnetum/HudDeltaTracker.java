package me.decce.gnetum;

import net.minecraft.client.DeltaTracker;

public class HudDeltaTracker {
    private static float[] gameTimeDeltaTicks;
    private static float[] gameTimeDeltaPartialTick;
    private static float[] realtimeDeltaTicks;

    public static void update(DeltaTracker.Timer timer) {
        int len = Gnetum.config.numberOfPasses + 1;
        int curr = Gnetum.passManager.current;
        if (gameTimeDeltaTicks == null || gameTimeDeltaTicks.length != len) {
            gameTimeDeltaTicks = new float[len];
            gameTimeDeltaPartialTick = new float[len];
            realtimeDeltaTicks = new float[len];
        }
        gameTimeDeltaTicks[curr] += timer.getGameTimeDeltaTicks();
        gameTimeDeltaPartialTick[curr] += timer.getGameTimeDeltaPartialTick(true);
        realtimeDeltaTicks[curr] += timer.getRealtimeDeltaTicks();
    }

    public static void reset() {
        int len = Gnetum.config.numberOfPasses + 1;
        if (gameTimeDeltaTicks == null || gameTimeDeltaTicks.length != len) {
            gameTimeDeltaTicks = new float[len];
            gameTimeDeltaPartialTick = new float[len];
            realtimeDeltaTicks = new float[len];
        }
        for (int i = 0; i < len; i++) {
            gameTimeDeltaTicks[i] = 0F;
            gameTimeDeltaPartialTick[i] = 0F;
            realtimeDeltaTicks[i] = 0F;
        }
    }

    public static float getGameTimeDeltaTicks() {
        float ret = 0F;
        for (int i = 0; i <= Gnetum.config.numberOfPasses; i++) {
            if (i < gameTimeDeltaTicks.length) {
                ret += gameTimeDeltaTicks[i];
            }
        }
        return ret;
    }

    public static float getGameTimeDeltaPartialTick() {
        float ret = 0F;
        for (int i = 0; i <= Gnetum.config.numberOfPasses; i++) {
            if (i < gameTimeDeltaPartialTick.length) {
                ret += gameTimeDeltaPartialTick[i];
            }
        }
        return ret;
    }

    public static float getRealtimeDeltaTicks() {
        float ret = 0F;
        for (int i = 0; i <= Gnetum.config.numberOfPasses; i++) {
            if (i < realtimeDeltaTicks.length) {
                ret += realtimeDeltaTicks[i];
            }
        }
        return ret;
    }
}
