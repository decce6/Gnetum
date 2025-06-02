package me.decce.gnetum.mixins;

import net.neoforged.neoforge.client.gui.GuiLayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = GuiLayerManager.class, remap = false)
public interface GuiLayerManagerAccessor {
    @Accessor
    List<GuiLayerManager.NamedLayer> getLayers();
}
