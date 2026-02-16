package me.decce.gnetum;

import me.decce.gnetum.mixins.MinecraftAccessor;
import net.minecraft.client.Minecraft;
//? >=1.21.10 {
import net.minecraft.util.Util;
//?} else {
/*import net.minecraft.Util;
*///?}

public class FpsCounter {
    private int fps;
    private int frames;
    private long lastTimeMillis;
    private long lastTimeNanos;

    public void reset() {
        this.lastTimeMillis = ((MinecraftAccessor)Minecraft.getInstance()).getLastTime(); // makes sure the fps updates at the same time the game fps is updated
		this.lastTimeNanos = Gnetum.time().nanos();
        this.frames = 0;
        this.fps = 0;
    }

    public void tick() {
        frames++;
        while (Util.getMillis() >= this.lastTimeMillis + 1000L) {
            fps = frames;
            frames = 0;
            this.lastTimeMillis += 1000L;
        }
		this.lastTimeNanos = Gnetum.time().nanos();
    }

    public int getFps() {
        return fps;
    }

	public boolean belowMax() {
		return Gnetum.config.maxFps == Constants.UNLIMITED_FPS ||
				Gnetum.time().nanos() >= this.lastTimeNanos + 1_000_000_000L / Gnetum.config.maxFps;
	}
}
