package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.decce.gnetum.GnetumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @WrapWithCondition(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/shader/Framebuffer;framebufferRender(II)V"))
    private boolean gnetum$fastFramebufferBlits(Framebuffer framebuffer, int width, int height){
        if (!GnetumConfig.useFastFramebufferBlits()) return true;
        OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, framebuffer.framebufferObject);
        GL30.glBlitFramebuffer(
                0, 0, width, height,
                0, 0, width, height,
                GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST
                );
        OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);

        // This line is required to fix sky color when in F1 and hovering over an item
        // A similar issue exists when using Fast Render: https://github.com/sp614x/optifine/issues/7769
        // Many thanks to @boredhuman for opening that issue with a very clear description and analysis.
        GlStateManager.disableLighting();

        return false;
    }
}
