package me.decce.gnetum.hud;

//? fabric && <=1.21.10 {
/*import me.decce.gnetum.mixins.GuiAccessor;
import me.decce.gnetum.versioned.HudHandler;
import net.minecraft.client.Minecraft;

import static me.decce.gnetum.hud.SharedValues.*;

@SuppressWarnings("unused")
public class VanillaHuds {
    //TODO condition (F1)
    public static final Hud CAMERA_OVERLAYS = Hud.builder()
            .id("camera_overlays")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderCameraOverlays(guiGraphics, deltaTracker))
            .build();
    public static final Hud CROSSHAIR = Hud.builder()
            .id("crosshair")
            .blend(true)
            .depth(true)
            .onRender(() -> accessor().invokeRenderCrosshair(guiGraphics, deltaTracker))
            .build();
    public static final Hud HOTBAR = Hud.builder()
            .id("hotbar")
            .blend(true)
            .depth(true)
            .onRender(() -> accessor().invokeRenderHotbarAndDecorations(guiGraphics, deltaTracker))
            .build();
    public static final Hud EXPERIENCE_LEVEL = Hud.builder()
            .id("experience_level")
            .blend(true)
            .depth(true)
            .onRender(() -> accessor().invokeRenderExperienceLevel(guiGraphics, deltaTracker))
            .build();
    public static final Hud EFFECTS = Hud.builder()
            .id("effects")
            .blend(true)
            .depth(true)
            .onRender(() -> accessor().invokeRenderEffects(guiGraphics, deltaTracker))
            .build();
    public static final Hud BOSS_OVERLAY = Hud.builder()
            .id("boss_overlay")
            .blend(true)
            .depth(true)
            .onRender(() -> gui().getBossOverlay().render(guiGraphics))
            .build();
    public static final Hud SLEEP_OVERLAY = Hud.builder()
            .id("sleep_overlay")
            .blend(true)
            .depth(true)
            .onRender(() -> accessor().invokeRenderSleepOverlay(guiGraphics, deltaTracker))
            .build();
    public static final Hud DEMO_OVERLAY = Hud.builder()
            .id("demo_overlay")
            .blend(true)
            .depth(true)
            .onRender(() -> accessor().invokeRenderDemoOverlay(guiGraphics, deltaTracker))
            .build();
    public static final Hud DEBUG_OVERLAY = Hud.builder()
            .id("debug_overlay")
            .blend(true)
            .depth(true)
            .condition(() -> gui().getDebugOverlay().showDebugScreen())
            .onRender(() -> gui().getDebugOverlay().render(guiGraphics))
            .build();
    public static final Hud SCOREBOARD = Hud.builder()
            .id("scoreboard")
            .blend(true)
            .depth(true)
            .onRender(() -> accessor().invokeRenderScoreboardSidebar(guiGraphics, deltaTracker))
            .build();
    public static final Hud OVERLAY_MESSAGE = Hud.builder()
            .id("overlay_message")
            .blend(true)
            .depth(true)
            .onRender(() -> accessor().invokeRenderOverlayMessage(guiGraphics, deltaTracker))
            .build();
    public static final Hud TITLE = Hud.builder()
            .id("title")
            .blend(true)
            .depth(true)
            .onRender(() -> accessor().invokeRenderTitle(guiGraphics, deltaTracker))
            .build();
    public static final Hud CHAT = Hud.builder()
            .id("chat")
            .blend(true)
            .depth(true)
            .onRender(() -> accessor().invokeRenderChat(guiGraphics, deltaTracker))
            .build();
    public static final Hud TAB_LIST = Hud.builder()
            .id("tab_list")
            .blend(true)
            .depth(true)
            .onRender(() -> accessor().invokeRenderTabList(guiGraphics, deltaTracker))
            .build();
    public static final Hud SUBTITLE_OVERLAY = Hud.builder()
            .id("subtitle_overlay")
            .blend(true)
            .depth(true)
            .onRender(() -> accessor().gnetum$getSubtitleOverlay().render(guiGraphics))
            .build();
    public static final Hud SAVING_INDICATOR = Hud.builder()
            .id("saving_indicator")
            .blend(true)
            .depth(true)
            .onRender(() -> accessor().invokeRenderSavingIndicator(guiGraphics, deltaTracker))
            .build();
    public static final Hud UNKNOWN_MODDED_HUDS = Hud.builder()
            .id("unknown_element")
            .blend(true)
            .depth(true)
            .onRender(() -> HudHandler.unknownElements.forEach(Runnable::run))
            .build();

    public static void init() {

    }

    private static GuiAccessor accessor() {
        return (GuiAccessor) Minecraft.getInstance().gui;
    }
}
*///? }