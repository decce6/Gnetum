package me.decce.gnetum.mixins;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SubtitleOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface GuiAccessor {
    //? fabric && <=1.21.10 {
    /*@Invoker
    void invokeRenderCameraOverlays(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    @Invoker
    void invokeRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    @Invoker
    void invokeRenderHotbarAndDecorations(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    @Invoker
    void invokeRenderExperienceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    @Invoker
    void invokeRenderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    @Invoker
    void invokeRenderSleepOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    @Invoker
    void invokeRenderDemoOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    @Invoker
    void invokeRenderScoreboardSidebar(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    @Invoker
    void invokeRenderOverlayMessage(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    @Invoker
    void invokeRenderTitle(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    @Invoker
    void invokeRenderChat(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    @Invoker
    void invokeRenderTabList(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    @Invoker
    void invokeRenderSavingIndicator(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    @Accessor("subtitleOverlay")
    SubtitleOverlay gnetum$getSubtitleOverlay();
    *///? }
}
