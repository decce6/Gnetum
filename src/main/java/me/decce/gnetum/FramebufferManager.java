package me.decce.gnetum;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;

public class FramebufferManager {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final FramebufferManager instance = new FramebufferManager();
    private boolean dropCurrentFrame;
    private boolean complete; // whether the frontFramebuffer contains a complete HUD texture
    private int width;
    private int height;
    private double guiScale;
    private TextureTarget backFramebuffer;
    private TextureTarget frontFramebuffer;

    private FramebufferManager() {
        this.reset();
    }

    public static FramebufferManager getInstance() {
        return instance;
    }

    public void ensureSize() {
        if (mc.getWindow().getWidth() != width ||
            mc.getWindow().getHeight() != height ||
            mc.getWindow().getGuiScale() != guiScale) {
            this.reset();
        }
    }

    // TODO: optimize this by avoiding unnecessary bind
    public void clear() {
        this.clear(backFramebuffer);
    }

    public void clear(TextureTarget framebuffer) {
        mc.getProfiler().push("clear");
        framebuffer.clear(Minecraft.ON_OSX);
        mc.getProfiler().pop();
    }

    public void reset() {
        this.width = mc.getWindow().getWidth();
        this.height = mc.getWindow().getHeight();
        this.guiScale = mc.getWindow().getGuiScale();
        if (backFramebuffer != null) backFramebuffer.destroyBuffers();
        backFramebuffer = new TextureTarget(width, height, true, false);
        backFramebuffer.setClearColor(0, 0, 0, 0);
        backFramebuffer.setFilterMode(GlConst.GL_NEAREST);
        this.clear(backFramebuffer);
        if (frontFramebuffer != null) frontFramebuffer.destroyBuffers();
        frontFramebuffer = new TextureTarget(width, height, true, false);
        frontFramebuffer.setClearColor(0, 0, 0, 0);
        frontFramebuffer.setFilterMode(GlConst.GL_NEAREST);
        this.clear(frontFramebuffer);
        this.complete = false;
        Gnetum.passManager.current = 1;
    }

    public void bind() {
        backFramebuffer.bindWrite(true);
    }

    public void unbind() {
        mc.getMainRenderTarget().bindWrite(true);
    }

    public void blit() {
        mc.getProfiler().push("blit");

        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.blendFunc(GlConst.GL_ONE, GlConst.GL_ONE_MINUS_SRC_ALPHA);

        frontFramebuffer.blitToScreen(width, height, false);

        RenderSystem.defaultBlendFunc();

        mc.getProfiler().pop();
    }

    public void swapFramebuffers() {
        if (!this.dropCurrentFrame) {
            TextureTarget temp = backFramebuffer;
            this.backFramebuffer = this.frontFramebuffer;
            this.frontFramebuffer = temp;
            this.complete = true;
            Gnetum.FPS_COUNTER.tick();
        }
        this.clear();
        this.dropCurrentFrame = false;
    }

    public void dropCurrentFrame() {
        this.dropCurrentFrame = true;
    }

    public int id() {
        return backFramebuffer.frameBufferId;
    }

    public boolean isComplete() {
        return this.complete;
    }
}
