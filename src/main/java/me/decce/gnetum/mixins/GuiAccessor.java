package me.decce.gnetum.mixins;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Gui.class)
public interface GuiAccessor {
    @Accessor
    GuiLayerManager getLayerManager();
    @Accessor
    RandomSource getRandom();
    @Accessor
    int getTickCount();
}
