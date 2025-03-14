package me.decce.gnetum;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

public class FramebufferManager {
    private static final FramebufferManager instance = new FramebufferManager();
    private int width;
    private int height;
    private int guiScale;
    private Framebuffer backFramebuffer;
    private Framebuffer frontFramebuffer;

    public boolean shouldClear;

    private FramebufferManager() {
        width = Minecraft.getMinecraft().displayWidth;
        height = Minecraft.getMinecraft().displayHeight;
        backFramebuffer = new Framebuffer(width, height, true);
        backFramebuffer.setFramebufferColor(0, 0, 0, 0);
        backFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
        backFramebuffer.framebufferClear();
        frontFramebuffer = new Framebuffer(width, height, true);
        frontFramebuffer.setFramebufferColor(0, 0, 0, 0);
        frontFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
        frontFramebuffer.framebufferClear();
        guiScale = Minecraft.getMinecraft().gameSettings.guiScale;
    }

    public static FramebufferManager getInstance() {
        return instance;
    }

    public void ensureSize() {
        if (Minecraft.getMinecraft().displayWidth != width ||
                Minecraft.getMinecraft().displayHeight != height ||
                Minecraft.getMinecraft().gameSettings.guiScale != guiScale) {
            width = Minecraft.getMinecraft().displayWidth;
            height = Minecraft.getMinecraft().displayHeight;
            frontFramebuffer.createBindFramebuffer(width, height);
            frontFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
            backFramebuffer.createBindFramebuffer(width, height);
            backFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
            guiScale = Minecraft.getMinecraft().gameSettings.guiScale;
        }
    }

    public void clear(){
        backFramebuffer.framebufferClear();
    }

    public void bind() {
        backFramebuffer.bindFramebuffer(false);
    }

    public void unbind() {
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
    }

    public void blit() {
        frontFramebuffer.framebufferRenderExt(width, height, false);
    }

    public void swapFramebuffers(){
        Framebuffer temp = backFramebuffer;
        backFramebuffer = frontFramebuffer;
        frontFramebuffer = temp;
        shouldClear = true;
    }
}
