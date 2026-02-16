package me.decce.gnetum;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ForgeHooksClient;
import org.joml.Matrix4f;

public class FramebufferManager {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final FramebufferManager instance = new FramebufferManager();
    private boolean dropCurrentFrame;
    private int catchUpSerialNumber;
    private int serialNumber;
    private int width;
    private int height;
    private boolean downscale;
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
            mc.getWindow().getGuiScale() != guiScale ||
            downscale != Gnetum.config.downscale.get()) {
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
        var window = mc.getWindow();
        this.width = window.getWidth();
        this.height = window.getHeight();
        this.guiScale = window.getGuiScale();
        this.downscale = Gnetum.config.downscale.get();
        int fboWidth = downscale ? window.getGuiScaledWidth() : window.getWidth();
        int fboHeight = downscale ? window.getGuiScaledHeight() : window.getHeight();
        if (backFramebuffer != null && backFramebuffer.frameBufferId > 0) backFramebuffer.destroyBuffers();
        backFramebuffer = new TextureTarget(fboWidth, fboHeight, true, false);
        backFramebuffer.setClearColor(0, 0, 0, 0);
        backFramebuffer.setFilterMode(GlConst.GL_NEAREST);
        this.clear(backFramebuffer);
        if (frontFramebuffer != null && frontFramebuffer.frameBufferId > 0) frontFramebuffer.destroyBuffers();
        frontFramebuffer = new TextureTarget(fboWidth, fboHeight, true, false);
        frontFramebuffer.setClearColor(0, 0, 0, 0);
        frontFramebuffer.setFilterMode(GlConst.GL_NEAREST);
        this.clear(frontFramebuffer);
        this.serialNumber = 0;
        this.catchUpSerialNumber = 0;
        Gnetum.passManager.current = 1;
    }

    public void bind() {
        this.bind(true);
    }

    public void bind(boolean setViewport) {
        backFramebuffer.bindWrite(setViewport);
    }

    public void unbind() {
        mc.getMainRenderTarget().bindWrite(true);
    }

    public void blit() {
        mc.getProfiler().push("blit");

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_ONE, GlConst.GL_ONE_MINUS_SRC_ALPHA);

        frontFramebuffer.blitToScreen(width, height, false);

        var window = mc.getWindow();
        Matrix4f matrix4f = (new Matrix4f()).setOrtho(0.0F, (float)((double)window.getWidth() / window.getGuiScale()), (float)((double)window.getHeight() / window.getGuiScale()), 0.0F, 1000.0F, ForgeHooksClient.getGuiFarPlane());
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.defaultBlendFunc();

        mc.getProfiler().pop();
    }

    public void swapFramebuffers() {
        if (!this.dropCurrentFrame) {
            TextureTarget temp = backFramebuffer;
            this.backFramebuffer = this.frontFramebuffer;
            this.frontFramebuffer = temp;
            this.serialNumber++;
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

    public void markForCatchUp() {
        this.catchUpSerialNumber = this.serialNumber;
    }

    public boolean needsCatchUp() {
        return this.serialNumber - this.catchUpSerialNumber <= 1;
    }
}
