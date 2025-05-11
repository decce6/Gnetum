package me.decce.gnetum.mixins.early.compat.optifine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(targets = "Config")
public interface ConfigAccessor {
    @Invoker("isVignetteEnabled")
    static boolean invokeIsVignetteEnabled() {
        throw new AssertionError();
    }
}
