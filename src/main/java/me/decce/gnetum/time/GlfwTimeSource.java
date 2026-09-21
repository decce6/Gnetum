package me.decce.gnetum.time;

//? <=26.2 {
import org.lwjgl.glfw.GLFW;

public class GlfwTimeSource implements TimeSource {
	@Override
	public double get() {
		return GLFW.glfwGetTime();
	}

	@Override
	public long nanos() {
		return (long) (get() * 1_000_000_000L);
	}
}
//? }
