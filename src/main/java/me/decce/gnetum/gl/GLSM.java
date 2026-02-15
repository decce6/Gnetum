package me.decce.gnetum.gl;

public class GLSM {
	private static Gl gl;
	public static Gl get() {
		return gl;
	}
	public static void set(Gl gl) {
		GLSM.gl = gl;
	}
}
