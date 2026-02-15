package me.decce.gnetum;

import me.decce.gnetum.gl.Gl;

//? >=1.21.10 {
import com.mojang.blaze3d.opengl.GlStateManager;
//?} else {
 /*import com.mojang.blaze3d.platform.GlStateManager;
*///?}

public class GlImpl implements Gl {
	@Override
	public void enableBlend() {
		GlStateManager._enableBlend();
	}

	@Override
	public void disableBlend() {
		GlStateManager._disableBlend();
	}

	@Override
	public void enableDepth() {
		GlStateManager._enableDepthTest();
	}

	@Override
	public void disableDepth() {
		GlStateManager._disableDepthTest();
	}

	@Override
	public void blendFunc(int src, int dst, int srcAlpha, int dstAlpha) {
		GlStateManager._blendFuncSeparate(src, dst, srcAlpha,  dstAlpha);
	}

	@Override
	public void clearColor(float r, float g, float b, float a) {
		//? <1.21.10 {
		/*GlStateManager._clearColor(r, g, b, a);
		*///?}
	}

	@Override
	public void clearDepth(double depth) {
		//? <1.21.10 {
		/*GlStateManager._clearDepth(depth);
		*///?}
	}

	@Override
	public void activeTexture(int i) {
		GlStateManager._activeTexture(i);
	}
}
