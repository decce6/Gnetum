package me.decce.gnetum.time;

import org.lwjgl.glfw.GLFW;

public class GlfwTimeSource implements TimeSource {
	@Override
	public double get() {
		return GLFW.glfwGetTime();
	}

	@Override
	public long millis() {
		return (long) (get() * 1_000_000L);
	}

	@Override
	public long nanos() {
		return (long) (get() * 1_000_000_000L);
	}
}
