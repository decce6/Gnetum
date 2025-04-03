package me.decce.gnetum;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

public class FramebufferManager {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final FramebufferManager instance = new FramebufferManager();
    private int width;
    private int height;
    private int guiScale;
    private Framebuffer backFramebuffer;
    private Framebuffer frontFramebuffer;

    public boolean shouldClear;

    private FramebufferManager() {
        width = mc.displayWidth;
        height = mc.displayHeight;
        backFramebuffer = new Framebuffer(width, height, true);
        backFramebuffer.setFramebufferColor(0, 0, 0, 0);
        backFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
        backFramebuffer.framebufferClear();
        frontFramebuffer = new Framebuffer(width, height, true);
        frontFramebuffer.setFramebufferColor(0, 0, 0, 0);
        frontFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
        frontFramebuffer.framebufferClear();
        guiScale = mc.gameSettings.guiScale;
    }

    public static FramebufferManager getInstance() {
        return instance;
    }

    public void ensureSize() {
        if (mc.displayWidth != width ||
                mc.displayHeight != height ||
                mc.gameSettings.guiScale != guiScale) {
            width = mc.displayWidth;
            height = mc.displayHeight;
            guiScale = mc.gameSettings.guiScale;
            frontFramebuffer.createBindFramebuffer(width, height);
            frontFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
            backFramebuffer.createBindFramebuffer(width, height);
            backFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
        }
    }

    public void clear() {
        GlStateManager.clearColor(0, 0, 0, 0);
        GlStateManager.clearDepth(1.0D);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void bind() {
        backFramebuffer.bindFramebuffer(false);
        if (FramebufferManager.getInstance().shouldClear) {
            this.clear();
            FramebufferManager.getInstance().shouldClear = false;
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
        shouldClear = true;
    }
}
