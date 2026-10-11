package me.decce.gnetum;

import org.joml.Vector4f;
import org.joml.Vector4fc;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
//? >=26.2 {
/*import com.mojang.blaze3d.GpuFormat;
*///? }
//? <1.21.10 {
/*import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.platform.GlConst;
import org.joml.Matrix4f;
*///?}
//? 1.21.4
//import com.mojang.blaze3d.ProjectionType;

public class Framebuffers {
	private final Minecraft mc = Minecraft.getInstance();
	private final Vector4fc clearColor = new Vector4f(0);
	private RenderTarget back;
	private RenderTarget front;
	private RenderTarget backupMainRenderTarget;
	private boolean dropCurrentFrame;
	private int catchUpSerialNumber;
	private int serialNumber;
	private int width;
	private int height;
	//? >=1.21.10 {
	private int guiScale;
	//? } else {
	/*private double guiScale;
	*///? }
	private boolean downscale;
	private int backMinX, backMinY, backMaxX, backMaxY;
	private int frontMinX, frontMinY, frontMaxX, frontMaxY;
	private boolean backDirty = true;
	private boolean frontDirty = true;

	public Framebuffers() {
		//? >=26.3 {
		/*back = new TextureTarget("gnetum_back", 1, 1, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
		front = new TextureTarget("gnetum_front", 1, 1, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
		*///? } >=26.2 {
		/*back = new TextureTarget("gnetum_back", 1, 1, true, GpuFormat.RGBA8_UNORM);
		front = new TextureTarget("gnetum_front", 1, 1, true, GpuFormat.RGBA8_UNORM);
		*///? } else >=1.21.10 {
		back = new TextureTarget("gnetum_back", 1, 1, true);
		front = new TextureTarget("gnetum_front", 1, 1, true);
		//?} else {
		/*//? >=1.21.4 {
		back = new TextureTarget(1, 1, true);
		front = new TextureTarget(1, 1, true);
		//? } else {
		/^back = new TextureTarget(1, 1, true,Minecraft.ON_OSX);
		front = new TextureTarget(1, 1, true,Minecraft.ON_OSX);
		^///? }
		back.setClearColor(0, 0, 0, 0);
		front.setClearColor(0, 0, 0, 0);
		back.setFilterMode(GlConst.GL_NEAREST);
		front.setFilterMode(GlConst.GL_NEAREST);
		*///?}
		resize();
	}

	public void blit(GuiGraphics guiGraphics) {
		//? >=1.21.10 {
		var area = frontBounds(guiGraphics.guiWidth(), guiGraphics.guiHeight());
		if (area != null) {
			FramebufferBlitter.blit(front, guiGraphics, area);
		}
		//?} else {
		/*RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();
		RenderSystem.blendFuncSeparate(GlConst.GL_ONE, GlConst.GL_ONE_MINUS_SRC_ALPHA, GlConst.GL_ONE, GlConst.GL_ONE_MINUS_SRC_ALPHA);
		//? >=1.21.4 {
		front.blitAndBlendToScreen(width, height);
		//? } else {
		/^front.blitToScreen(width, height, false);
		^///? }
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		*///?}

		fixMatrix();
	}

	private void fixMatrix() {
		//? <1.21.10 {
		/*var window = mc.getWindow();
		//? neoforge {
		//var far = ClientHooks.getGuiFarPlane();
		//? } else {
		var far = 21000.0F;
		//? }
		Matrix4f matrix4f = (new Matrix4f()).setOrtho(0.0F, (float)(window.getWidth() / window.getGuiScale()), (float)(window.getHeight() / window.getGuiScale()), 0.0F, 1000.0F, far);
		//? >=1.21.4 {
		RenderSystem.setProjectionMatrix(matrix4f, ProjectionType.ORTHOGRAPHIC);
		//? } else {
		/^RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
		^///? }
		*///?}
	}

	public void resize() {
		var window = mc.getWindow();

		if (window.getWidth() != width ||
				window.getHeight() != height ||
				window.getGuiScale() != guiScale ||
				Gnetum.config.downscale.get() != downscale)
		{
			this.width = window.getWidth();
			this.height = window.getHeight();
			this.guiScale = mc.getWindow().getGuiScale();
			this.downscale = Gnetum.config.downscale.get();
			var fboWidth = downscale ? window.getGuiScaledWidth() : window.getWidth();
			var fboHeight = downscale ? window.getGuiScaledHeight() : window.getHeight();

			//? >=1.21.4 {
			back.resize(fboWidth,  fboHeight);
			front.resize(fboWidth, fboHeight);
			//?} else {
			/*back.resize(fboWidth, fboHeight, Minecraft.ON_OSX);
			front.resize(fboWidth, fboHeight, Minecraft.ON_OSX);
			*///?}
			backDirty = true;
			frontDirty = true;
			markForCatchUp();
		}
	}

	public void swapFramebuffers() {
		if (!this.dropCurrentFrame) {
			var temp = back;
			this.back = this.front;
			this.front = temp;
			var tempDirty = backDirty;
			this.backDirty = this.frontDirty;
			this.frontDirty = tempDirty;
			this.frontMinX = backMinX;
			this.frontMinY = backMinY;
			this.frontMaxX = backMaxX;
			this.frontMaxY = backMaxY;
			this.serialNumber++;
			Gnetum.FPS_COUNTER.tick();
		}
		this.clear();
		this.backMinX = this.backMinY = this.backMaxX = this.backMaxY = 0;
		this.dropCurrentFrame = false;
	}

	public void includeBounds(int x0, int y0, int x1, int y1) {
		if (backMinX >= backMaxX || backMinY >= backMaxY) {
			backMinX = x0;
			backMinY = y0;
			backMaxX = x1;
			backMaxY = y1;
		} else {
			backMinX = Math.min(backMinX, x0);
			backMinY = Math.min(backMinY, y0);
			backMaxX = Math.max(backMaxX, x1);
			backMaxY = Math.max(backMaxY, y1);
		}
		backDirty = true;
	}

	public void includeAll() {
		includeBounds(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public ScreenRectangle frontBounds(int guiWidth, int guiHeight) {
		int x0 = Math.max(frontMinX, 0);
		int y0 = Math.max(frontMinY, 0);
		int x1 = Math.min(frontMaxX, guiWidth);
		int y1 = Math.min(frontMaxY, guiHeight);
		return x0 < x1 && y0 < y1 ? new ScreenRectangle(x0, y0, x1 - x0, y1 - y0) : null;
	}

	public void dropCurrentFrame() {
		this.dropCurrentFrame = true;
	}

	public void bind() {
		backupMainRenderTarget = VersionCompatUtil.getRawMainRenderTarget();
		VersionCompatUtil.setMainRenderTarget(Gnetum.framebuffers().back());
		//? <1.21.10
		//backDirty = true;
		//? <=1.21.4 {
		/*back.bindWrite(true);
		*///?}
	}

	public void unbind() {
		VersionCompatUtil.setMainRenderTarget(backupMainRenderTarget);
		//?	<=1.21.4 {
		/*Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
		*///?}
	}

	public void clear() {
		if (!backDirty) {
			return;
		}
		backDirty = false;
		//? >=26.2 {
		/*RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(back.getColorTexture(), clearColor,
				back.getDepthTexture(), 1.0);
		*///? } >=1.21.10 {
		RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(back.getColorTexture(), 0,
				back.getDepthTexture(), 1.0);
		//?} else >=1.21.4 {
		/*back.clear();
		*///?} else {
		/*back.clear(Minecraft.ON_OSX);
		*///?}
	}

	public void markForCatchUp() {
		this.catchUpSerialNumber = this.serialNumber;
	}

	public boolean needsCatchUp() {
		return this.serialNumber - this.catchUpSerialNumber <= 1;
	}

	public RenderTarget back() {
		return back;
	}
}
