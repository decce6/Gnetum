package me.decce.gnetum.mixins.fabric;

import net.fabricmc.fabric.impl.client.rendering.hud.HudElementRegistryImpl;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Mixin(HudElementRegistryImpl.class)
public interface HudElementRegistryImplAccessor {
    @Accessor("VANILLA_ELEMENT_IDS")
    static List<Identifier> getVanillaElementIds() {
        throw new AssertionError();
    }
    @Accessor("FIRST")
    static HudElementRegistryImpl.RootLayer getFirst() {
        throw new AssertionError();
    }
    @Accessor("LAST")
    static HudElementRegistryImpl.RootLayer getLast() {
        throw new AssertionError();
    }
}
