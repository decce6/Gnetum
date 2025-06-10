package me.decce.gnetum.mixins;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import me.decce.gnetum.FramebufferManager;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.gl.FramebufferTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Unique
    private boolean gnetum$renderingCachedChat;
    @Unique
    private int gnetum$previouslyBoundFbo;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void gnetum$preRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!Gnetum.config.isEnabled() || Minecraft.getInstance().options.hideGui || Gnetum.passManager.cachingDisabled(VanillaGuiLayers.CHAT.toString()) ||
                (Minecraft.getInstance().screen != null && !(Minecraft.getInstance().screen instanceof ChatScreen))) {
            return;
        }
        if (Gnetum.passManager.shouldRender(VanillaGuiLayers.CHAT.toString())) {
            gnetum$renderingCachedChat = true;
            gnetum$previouslyBoundFbo = FramebufferTracker.getCurrentlyBoundFbo();
            Gnetum.passManager.begin();
            FramebufferManager.getInstance().bind();
        }
        else {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void gnetum$postRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (gnetum$renderingCachedChat) {
            guiGraphics.bufferSource().endLastBatch();
            gnetum$renderingCachedChat = false;
            GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, gnetum$previouslyBoundFbo);
            Gnetum.passManager.end();
        }
    }
}
