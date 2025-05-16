package me.decce.gnetum.mixins;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Gui.class)
public interface GuiAccessor {
    @Accessor
    int getScreenWidth();
    @Accessor
    int getScreenHeight();
    @Accessor
    void setScreenWidth(int value);
    @Accessor
    void setScreenHeight(int value);
    @Accessor
    RandomSource getRandom();
    @Accessor
    int getTickCount();
}
