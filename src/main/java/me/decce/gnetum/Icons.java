package me.decce.gnetum;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;

public class Icons {
    public static final ResourceLocation TICK;
    public static final ResourceLocation EXCLAMATION;
    public static final ResourceLocation QUESTION;

    static {
        TICK = ResourceLocation.fromNamespaceAndPath("gnetum", "icons/tick.png");
        EXCLAMATION = ResourceLocation.fromNamespaceAndPath("gnetum", "icons/exclamation.png");
        QUESTION = ResourceLocation.fromNamespaceAndPath("gnetum", "icons/question.png");
        Minecraft.getInstance().getTextureManager().register(TICK, new SimpleTexture(TICK));
        Minecraft.getInstance().getTextureManager().register(EXCLAMATION, new SimpleTexture(EXCLAMATION));
        Minecraft.getInstance().getTextureManager().register(QUESTION, new SimpleTexture(QUESTION));
    }
}
