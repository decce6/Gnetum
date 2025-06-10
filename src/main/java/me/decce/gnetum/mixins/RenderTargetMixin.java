package me.decce.gnetum.mixins;

import com.mojang.blaze3d.pipeline.RenderTarget;
import me.decce.gnetum.FramebufferManager;
import me.decce.gnetum.Gnetum;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderTarget.class)
public class RenderTargetMixin {
    @Inject(method = "_bindWrite", at = @At("HEAD"), cancellable = true)
    public void gnetum$bindWrite(boolean setViewport, CallbackInfo ci) {
        if (Gnetum.rendering && (((RenderTarget)(Object)this) == Minecraft.getInstance().getMainRenderTarget())) {
            ci.cancel();
            FramebufferManager.getInstance().bind(setViewport);
        }
    }
}
