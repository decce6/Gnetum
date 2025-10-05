package me.decce.gnetum;

import me.decce.gnetum.mixins.early.MinecraftAccessor;
import net.minecraft.client.Minecraft;

public class FpsCounter {
    private int fps;
    private int frames;
    private long lastTime;

    public void reset() {
        this.lastTime = ((MinecraftAccessor)Minecraft.getMinecraft()).getDebugUpdateTime(); // makes sure the fps updates at the same time the game fps is updated
        this.frames = 0;
        this.fps = 0;
    }

    public void tick() {
        frames++;
        while (Minecraft.getSystemTime() >= this.lastTime + 1000L) {
            fps = frames;
            frames = 0;
            this.lastTime += 1000L;
        }
    }

    public int getFps() {
        return fps;
    }
}
