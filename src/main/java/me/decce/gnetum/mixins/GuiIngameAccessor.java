package me.decce.gnetum.mixins;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(GuiIngame.class)
public interface GuiIngameAccessor {
    @Accessor
    Random getRand();
    @Invoker
    void callRenderScoreboard(ScoreObjective objective, ScaledResolution scaledRes);
    @Invoker
    void callRenderPumpkinOverlay(ScaledResolution scaledRes);
    @Accessor
    int getUpdateCounter();
}
