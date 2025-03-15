package me.decce.gnetum;

import net.minecraft.client.Minecraft;
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
        backFramebuffer.framebufferClear();
    }

    public void bind() {
        if (FramebufferManager.getInstance().shouldClear) {
            this.clear();
            FramebufferManager.getInstance().shouldClear = false;
        }
        backFramebuffer.bindFramebuffer(false);
    }

    public void unbind() {
        mc.getFramebuffer().bindFramebuffer(false);
    }

    public void blit() {
        frontFramebuffer.framebufferRenderExt(width, height, false);
    }

    public void swapFramebuffers() {
        Framebuffer temp = backFramebuffer;
        backFramebuffer = frontFramebuffer;
        frontFramebuffer = temp;
        shouldClear = true;
    }
}
