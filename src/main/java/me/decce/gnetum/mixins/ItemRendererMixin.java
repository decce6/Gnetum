package me.decce.gnetum.mixins;

import me.decce.gnetum.FramebufferManager;
import me.decce.gnetum.GnetumConfig;
import me.decce.gnetum.Passes;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Unique
    private int gnetum$previouslyBoundFramebuffer;

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At("HEAD"), cancellable = true)
    public void gnetum$preRenderItemInFirstPerson(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo ci) {
        if (!GnetumConfig.isEnabled() || !GnetumConfig.bufferHand) {
            return;
        }
        if (Passes.current == Passes.FORGE_PRE) {
            gnetum$previouslyBoundFramebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
            FramebufferManager.getInstance().bind();
        }
        else {
            ci.cancel();
        }
    }

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At("RETURN"))
    public void gnetum$postRenderItemInFirstPerson(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo ci) {
        if (!GnetumConfig.isEnabled() || !GnetumConfig.bufferHand) {
            return;
        }
        if (Passes.current == Passes.FORGE_PRE) {
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, gnetum$previouslyBoundFramebuffer);
        }
    }
}
