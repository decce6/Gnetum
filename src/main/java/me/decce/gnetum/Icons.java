package me.decce.gnetum;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;

public class Icons {
    public static final ResourceLocation TICK;
    public static final ResourceLocation EXCLAMATION;
    public static final ResourceLocation QUESTION;

    static {
        //noinspection removal // ResourceLocation.fromNamespaceAndPath crashes on older versions of forge
        TICK = new ResourceLocation("gnetum", "icons/tick.png");
        //noinspection removal
        EXCLAMATION = new ResourceLocation("gnetum", "icons/exclamation.png");
        //noinspection removal
        QUESTION = new ResourceLocation("gnetum", "icons/question.png");
        Minecraft.getInstance().getTextureManager().register(TICK, new SimpleTexture(TICK));
        Minecraft.getInstance().getTextureManager().register(EXCLAMATION, new SimpleTexture(EXCLAMATION));
        Minecraft.getInstance().getTextureManager().register(QUESTION, new SimpleTexture(QUESTION));
    }
}
