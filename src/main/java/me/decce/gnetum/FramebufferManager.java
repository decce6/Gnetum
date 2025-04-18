package me.decce.gnetum;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL44;

import java.nio.FloatBuffer;

public class FramebufferManager {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final FramebufferManager instance = new FramebufferManager();
    private int width;
    private int height;
    private int guiScale;
    private boolean fullscreen;
    private final FloatBuffer clearColor;
    private Framebuffer backFramebuffer;
    private Framebuffer frontFramebuffer;

    private boolean shouldClearDepthBuffer;

    private FramebufferManager() {
        width = mc.displayWidth;
        height = mc.displayHeight;
        guiScale = mc.gameSettings.guiScale;
        fullscreen = mc.gameSettings.fullScreen;
        clearColor = GLAllocation.createDirectFloatBuffer(4);
        clearColor.put(0).put(0).put(0).put(0);
        this.reset();
    }

    public static FramebufferManager getInstance() {
        return instance;
    }

    public void reset() {
        if (backFramebuffer != null && backFramebuffer.framebufferObject > 0) {
            backFramebuffer.deleteFramebuffer();
        }
        if (frontFramebuffer != null && frontFramebuffer.framebufferObject > 0) {
            frontFramebuffer.deleteFramebuffer();
        }
        backFramebuffer = new Framebuffer(width, height, true);
        backFramebuffer.setFramebufferColor(0, 0, 0, 0);
        backFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
        this.clear(false, backFramebuffer);
        frontFramebuffer = new Framebuffer(width, height, true);
        frontFramebuffer.setFramebufferColor(0, 0, 0, 0);
        frontFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
        this.clear(false, frontFramebuffer);
        Passes.current = 0;
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
        this.clear(true, backFramebuffer);
    }

    private void clear(boolean allowDelay, Framebuffer framebuffer) {
        if (GnetumConfig.useFastFramebufferClear() && allowDelay) {
            GL44.glClearTexImage(framebuffer.framebufferTexture, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (FloatBuffer) null);
            shouldClearDepthBuffer = true; // delays clearing depth buffer to avoid bind/unbind here
        }
        else {
            OpenGlHelper.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer.framebufferObject);
            GL30.glClearBuffer(GL11.GL_COLOR, 0, clearColor);
            GlStateManager.clearDepth(1.0D);
            GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
            OpenGlHelper.glBindFramebuffer(GL30.GL_FRAMEBUFFER, Minecraft.getMinecraft().getFramebuffer().framebufferObject);
        }
    }

    public void bind() {
        backFramebuffer.bindFramebuffer(false);
        if (shouldClearDepthBuffer) {
            GlStateManager.clearDepth(1.0D);
            GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
            shouldClearDepthBuffer = false;
        }
    }

    public void unbind() {
        mc.getFramebuffer().bindFramebuffer(false);
    }

    public void blit(double width, double height) {
        frontFramebuffer.bindFramebufferTexture();

        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

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

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    }

    public void swapFramebuffers() {
        Framebuffer temp = backFramebuffer;
        backFramebuffer = frontFramebuffer;
        frontFramebuffer = temp;
        this.clear();
    }
}
