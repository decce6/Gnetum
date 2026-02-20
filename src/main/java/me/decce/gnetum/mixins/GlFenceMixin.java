package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.opengl.GlFence;
import me.decce.gnetum.Gnetum;
import org.lwjgl.opengl.GL32C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GlFence.class)
public class GlFenceMixin {
    @ModifyExpressionValue(method = "awaitCompletion", at = @At(value = "CONSTANT", args = "intValue=0"))
    private int gnetum$modifyClientWaitFlags(int original) {
        if (Gnetum.config.isEnabled()) {
            // Only flush commands up to the point where the sync object is placed, instead of where the wait command is
            // issued (for OpenGL 4.5+)
            // See: https://wikis.khronos.org/opengl/Sync_Object#Flushing_and_contexts
            return GL32C.GL_SYNC_FLUSH_COMMANDS_BIT;
        }
        return original;
    }
}
