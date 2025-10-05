package me.decce.gnetum.mixins.early;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import me.decce.gnetum.FramebufferManager;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.gl.FramebufferTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenGlHelper.class)
public class OpenGlHelperMixin {
    @Inject(method = "glBindFramebuffer", at = @At("TAIL"))
    private static void glBindFramebuffer(int target, int framebufferIn, CallbackInfo ci) {
        if (target != GL30.GL_FRAMEBUFFER) {
            return;
        }
        FramebufferTracker.setCurrentlyBoundFbo(framebufferIn);
    }
}
