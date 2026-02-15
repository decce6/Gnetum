package me.decce.gnetum.mixins;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor
    long getLastTime();
    // Append gnetum$ prefixes to accessors because method "getMainRenderTarget" exists in target class
    @Accessor("mainRenderTarget")
    @Mutable
    void gnetum$setMainRenderTarget(RenderTarget target);
    @Accessor("mainRenderTarget")
    RenderTarget gnetum$getMainRenderTarget();
}
