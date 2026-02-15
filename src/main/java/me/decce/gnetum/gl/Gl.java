package me.decce.gnetum.gl;

import static org.lwjgl.opengl.GL11C.*;

public interface Gl {
	void enableBlend();
	void disableBlend();
	void enableDepth();
	void disableDepth();
	void blendFunc(int src, int dst, int srcAlpha, int dstAlpha);
	void clearColor(float r, float g, float b, float a);
	void clearDepth(double depth);
	void activeTexture(int i);
	default void defaultBlendFunc() {
		blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
}
