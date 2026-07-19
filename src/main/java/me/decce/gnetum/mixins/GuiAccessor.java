package me.decce.gnetum.mixins;

//$ import_delta_tracker
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

//? <=1.20.4
//import net.minecraft.world.entity.Entity;

@Mixin(Gui.class)
public interface GuiAccessor {
    //? fabric && <=1.21.10 {
    /*//? <=1.20.4 {
    /^@Accessor
    float getScopeScale();
    @Accessor
    void setScopeScale(float f);
    @Accessor("PUMPKIN_BLUR_LOCATION")
    Identifier getPumpkinBlurLocation();
    @Accessor("POWDER_SNOW_OUTLINE_LOCATION")
    Identifier getPowderSnowLocation();
    @Accessor
    int getTitleTime();
    @Accessor
    int getOverlayMessageTime();
    @Accessor
    boolean getAnimateOverlayMessageColor();
    @Accessor
    Component getOverlayMessageString();
    @Accessor
    Component getTitle();
    @Accessor
    Component getSubtitle();
    @Accessor
    int getTickCount();
    @Accessor
    int getTitleFadeInTime();
    @Accessor
    int getTitleStayTime();
    @Accessor
    int getTitleFadeOutTime();
    @Accessor
    int getScreenWidth();
    @Accessor
    int getScreenHeight();
    @Invoker
    void invokeDisplayScoreboardSidebar(GuiGraphics guiGraphics, Objective objective);
    @Invoker
    void invokeRenderVignette(GuiGraphics guiGraphics, Entity entity);
    @Invoker
    void invokeRenderSpyglassOverlay(GuiGraphics guiGraphics, float f);
    @Invoker
    void invokeRenderTextureOverlay(GuiGraphics guiGraphics, Identifier resourceLocation, float f);
    @Invoker
    void invokeRenderPortalOverlay(GuiGraphics guiGraphics, float f);
    @Invoker
    void invokeRenderHotbar(float f, GuiGraphics guiGraphics);
    @Invoker
    void invokeRenderCrosshair(GuiGraphics guiGraphics);
    @Invoker
    void invokeRenderPlayerHealth(GuiGraphics guiGraphics);
    @Invoker
    void invokeRenderVehicleHealth(GuiGraphics guiGraphics);
    @Invoker
    void invokeRenderEffects(GuiGraphics guiGraphics);
    @Invoker
    void invokeDrawBackdrop(GuiGraphics guiGraphics, Font font, int i, int j, int k);
    @Invoker
    void invokeRenderSavingIndicator(GuiGraphics guiGraphics);
    ^///? } else {
    @Invoker
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
    //? }
    *///? }
    //? <=1.21.4 {
    /*@Accessor("subtitleOverlay")
    SubtitleOverlay gnetum$getSubtitleOverlay();
    *///? }
}
