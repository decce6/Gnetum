package me.decce.gnetum;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import me.decce.gnetum.mixins.MinecraftAccessor;
import net.minecraft.client.Minecraft;
//? <1.21.10 {
/*import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.platform.GlConst;
import org.joml.Matrix4f;
*///?}

public class Framebuffers {
	private final Minecraft mc = Minecraft.getInstance();
	private RenderTarget back;
	private RenderTarget front;
	private RenderTarget backupMainRenderTarget;
	private boolean dropCurrentFrame;
	private int catchUpSerialNumber;
	private int serialNumber;
	private int width;
	private int height;
	private int guiScale;
	private boolean downscale;

	public Framebuffers() {
		//? >=1.21.10 {
		back = new TextureTarget("gnetum_back", 1, 1, true);
		front = new TextureTarget("gnetum_front", 1, 1, true);
		//?} else {
		/*back = new TextureTarget(1, 1, true,Minecraft.ON_OSX);
		front = new TextureTarget(1, 1, true,Minecraft.ON_OSX);
		*///?}
		resize();
	}

	public void blit() {
		//? >=1.21.10 {
		front.blitAndBlendToTexture(mc.getMainRenderTarget().getColorTextureView());
		//?} else {
		/*RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();
		RenderSystem.blendFuncSeparate(GlConst.GL_ONE, GlConst.GL_ONE_MINUS_SRC_ALPHA, GlConst.GL_ONE, GlConst.GL_ONE_MINUS_SRC_ALPHA);
		front.blitToScreen(width,  height, false);
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
		*///?}

		fixMatrix();
	}

	private void fixMatrix() {
		//? <1.21.10 {
		/*var window = mc.getWindow();
		//? neoforge {
		//var far = ClientHooks.getGuiFarPlane();
		//? } else {
		var far = 3000.0F;
		//? }
		Matrix4f matrix4f = (new Matrix4f()).setOrtho(0.0F, (float)((double)window.getWidth() / window.getGuiScale()), (float)((double)window.getHeight() / window.getGuiScale()), 0.0F, 1000.0F, far);
		RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
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

			//? >=1.21.10 {
			back.resize(fboWidth,  fboHeight);
			front.resize(fboWidth, fboHeight);
			//?} else {
			/*back.resize(fboWidth, fboHeight, Minecraft.ON_OSX);
			front.resize(fboWidth, fboHeight, Minecraft.ON_OSX);
			*///?}
			markForCatchUp();
		}
	}

	public void swapFramebuffers() {
		if (!this.dropCurrentFrame) {
			var temp = back;
			this.back = this.front;
			this.front = temp;
			this.serialNumber++;
			Gnetum.FPS_COUNTER.tick();
		}
		this.clear();
		this.dropCurrentFrame = false;
	}

	public void dropCurrentFrame() {
		this.dropCurrentFrame = true;
	}

	public void bind() {
		//? >=1.21.10 {
		var accessor = (MinecraftAccessor) mc;
		backupMainRenderTarget = accessor.gnetum$getMainRenderTarget();
		accessor.gnetum$setMainRenderTarget(Gnetum.framebuffers().back());
		//?} else {
		/*back.bindWrite(true);
		*///?}
	}

	public void unbind() {
		//? >=1.21.10 {
		((MinecraftAccessor) mc).gnetum$setMainRenderTarget(backupMainRenderTarget);
		//?} else {
		/*Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
		*///?}
	}

	public void clear() {
		//? >=1.21.10 {
		RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(back.getColorTexture(), 0,
				back.getDepthTexture(), 1.0);
		//?} else {
		/*back.clear(false);
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
