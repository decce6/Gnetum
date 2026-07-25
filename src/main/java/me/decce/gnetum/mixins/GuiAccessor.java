package me.decce.gnetum.mixins;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Random;

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
    Random getRandom();
    @Accessor
    int getTickCount();
}
