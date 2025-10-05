package me.decce.gnetum;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

import java.nio.FloatBuffer;

public class FramebufferManager {
    public static final boolean GL30SUPPORT = GLContext.getCapabilities().OpenGL30;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final FramebufferManager instance = new FramebufferManager();
    private int width;
    private int height;
    private int guiScale;
    private boolean fullscreen;
    private boolean dropCurrentFrame;
    private boolean complete; // whether the frontFramebuffer contains a complete HUD texture
    private final FloatBuffer clearColor;
    private Framebuffer backFramebuffer;
    private Framebuffer frontFramebuffer;

    private FramebufferManager() {
        width = mc.displayWidth;
        height = mc.displayHeight;
        guiScale = mc.gameSettings.guiScale;
        fullscreen = mc.gameSettings.fullScreen;
        clearColor = GLAllocation.createDirectFloatBuffer(4);
        clearColor.put(0).put(0).put(0).put(0);
        clearColor.flip();
        this.reset();
    }

    public static FramebufferManager getInstance() {
        return instance;
    }

    public void reset() {
        if (backFramebuffer != null) {
            backFramebuffer.deleteFramebuffer();
        }
        if (frontFramebuffer != null) {
            frontFramebuffer.deleteFramebuffer();
        }
        backFramebuffer = new Framebuffer(width, height, true);
        backFramebuffer.setFramebufferColor(0, 0, 0, 0);
        backFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
        backFramebuffer.bindFramebuffer(false);
        this.clear();
        this.unbind();
        frontFramebuffer = new Framebuffer(width, height, true);
        frontFramebuffer.setFramebufferColor(0, 0, 0, 0);
        frontFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
        frontFramebuffer.bindFramebuffer(false);
        this.clear();
        this.unbind();
        this.complete = false;
        Gnetum.passManager.current = 1;
    }

    public void ensureSize() {
        if (mc.displayWidth != width ||
                mc.displayHeight != height ||
                mc.gameSettings.guiScale != guiScale ||
                mc.gameSettings.fullScreen != fullscreen) {
            width = mc.displayWidth;
            height = mc.displayHeight;
            guiScale = mc.gameSettings.guiScale;
            fullscreen = mc.gameSettings.fullScreen;
            this.reset();
        }
    }

    private void clear() {
        this.clear(backFramebuffer.framebufferObject);
    }

    private void clear(int fbo) {
        OpenGlHelper.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
        GlStateManager.clearDepth(1.0D);
        if (GL30SUPPORT) {
            // Fixes https://github.com/decce6/Gnetum/issues/8
            GL30.glClearBuffer(GL11.GL_COLOR, 0, clearColor);
            GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        }
        else {
            // macOS does not have GL30 in compatibility context
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        }
    }

    public void bind() {
        this.bind(false);
    }

    public void bind(boolean setViewport) {
        backFramebuffer.bindFramebuffer(setViewport);
    }

    public void unbind() {
        mc.getFramebuffer().bindFramebuffer(false);
    }

    public void blit(double width, double height) {
        mc.profiler.startSection("blit");

        frontFramebuffer.bindFramebufferTexture();

        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepth();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        builder.pos(0, height, 0).tex(0, 0).endVertex();
        builder.pos(width, height, 0).tex(1, 0).endVertex();
        builder.pos(width, 0, 0).tex(1, 1).endVertex();
        builder.pos(0, 0, 0).tex(0, 1).endVertex();
        tessellator.draw();

        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();

        mc.profiler.endSection();
    }

    public void swapFramebuffers() {
        if (!this.dropCurrentFrame) {
            Framebuffer temp = backFramebuffer;
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
        return backFramebuffer.framebufferObject;
    }

    public boolean isComplete() {
        return this.complete;
    }
}
