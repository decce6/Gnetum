package me.decce.gnetum.hud;

//? fabric && <=1.21.10 {
/*import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import me.decce.gnetum.compat.CompatHelper;
import me.decce.gnetum.mixins.GuiAccessor;
import me.decce.gnetum.versioned.HudHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import static me.decce.gnetum.hud.SharedValues.*;

@SuppressWarnings("unused")
public class VanillaHuds {
    // Split HUD rendering into parts for better distribution
    //? >=1.21.1 {
    public static final Hud CAMERA_OVERLAYS = Hud.builder()
            .id("camera_overlays")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderCameraOverlays(guiGraphics(), deltaTracker))
            .build();
    public static final Hud CROSSHAIR = Hud.builder()
            .id("crosshair")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderCrosshair(guiGraphics(), deltaTracker))
            .build();
    public static final Hud HOTBAR = Hud.builder()
            .id("hotbar")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderHotbarAndDecorations(guiGraphics(), deltaTracker))
            .build();
    public static final Hud EXPERIENCE_LEVEL = Hud.builder()
            .id("experience_level")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderExperienceLevel(guiGraphics(), deltaTracker))
            .build();
    public static final Hud EFFECTS = Hud.builder()
            .id("effects")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderEffects(guiGraphics(), deltaTracker))
            .build();
    public static final Hud BOSS_OVERLAY = Hud.builder()
            .id("boss_overlay")
            .blend(true)
            .depth(false)
            .onRender(() -> gui().getBossOverlay().render(guiGraphics))
            .build();
    public static final Hud SLEEP_OVERLAY = Hud.builder()
            .id("sleep_overlay")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderSleepOverlay(guiGraphics(), deltaTracker))
            .build();
    public static final Hud DEMO_OVERLAY = Hud.builder()
            .id("demo_overlay")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderDemoOverlay(guiGraphics(), deltaTracker))
            .build();
    public static final Hud DEBUG_OVERLAY = Hud.builder()
            .id("debug_overlay")
            .blend(true)
            .depth(false)
            .condition(() -> gui().getDebugOverlay().showDebugScreen())
            .onRender(() -> gui().getDebugOverlay().render(guiGraphics))
            .build();
    public static final Hud SCOREBOARD = Hud.builder()
            .id("scoreboard")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderScoreboardSidebar(guiGraphics(), deltaTracker))
            .build();
    public static final Hud OVERLAY_MESSAGE = Hud.builder()
            .id("overlay_message")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderOverlayMessage(guiGraphics(), deltaTracker))
            .build();
    public static final Hud TITLE = Hud.builder()
            .id("title")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderTitle(guiGraphics(), deltaTracker))
            .build();
    public static final Hud CHAT = Hud.builder()
            .id("chat")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderChat(guiGraphics(), deltaTracker))
            .build();
    public static final Hud TAB_LIST = Hud.builder()
            .id("tab_list")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderTabList(guiGraphics(), deltaTracker))
            .build();
    public static final Hud SUBTITLE_OVERLAY = Hud.builder()
            .id("subtitle_overlay")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().gnetum$getSubtitleOverlay().render(guiGraphics))
            .build();
    public static final Hud SAVING_INDICATOR = Hud.builder()
            .id("saving_indicator")
            .blend(true)
            .depth(false)
            .onRender(() -> accessor().invokeRenderSavingIndicator(guiGraphics(), deltaTracker))
            .build();
    public static final Hud UNKNOWN_MODDED_HUDS = Hud.builder()
            .id("unknown_element")
            .blend(true)
            .depth(false)
            .onRender(() -> HudHandler.unknownElements.forEach(Runnable::run))
            .build();
    //? } else {
    /^// 1.20.4-: We copy too much logic from vanilla which may be bad for compatibility
    // TODO: figure out a better implementation
    public static final Hud VIGNETTE = Hud.builder()
            .id("vignette")
            .blend(true)
            .depth(false)
            .condition(CompatHelper::isVignetteEnabled)
            .onRender(() -> accessor().invokeRenderVignette(guiGraphics(), Minecraft.getInstance().getCameraEntity()))
            .build();
    public static final Hud SPYGLASS = Hud.builder()
            .id("spyglass")
            .blend(true)
            .depth(false)
            .condition(() -> Minecraft.getInstance().options.getCameraType().isFirstPerson())
            .onRender(() -> {
                if (Minecraft.getInstance().player.isScoping()) {
                    accessor().setScopeScale(Mth.lerp(0.5f * Minecraft.getInstance().getDeltaFrameTime(), accessor().getScopeScale(), 1.125f));
                    accessor().invokeRenderSpyglassOverlay(guiGraphics(), deltaTracker());
                }
                else {
                    accessor().setScopeScale(0.5f);
                }
            })
            .build();
    public static final Hud HELMET = Hud.builder()
            .id("helmet")
            .blend(true)
            .depth(false)
            .condition(() -> Minecraft.getInstance().options.getCameraType().isFirstPerson() && Minecraft.getInstance().player.getInventory().getArmor(3).is(Blocks.CARVED_PUMPKIN.asItem()))
            .onRender(() -> accessor().invokeRenderTextureOverlay(guiGraphics(), accessor().getPumpkinBlurLocation(), 1.0f))
            .build();
    public static final Hud FROSTBITE = Hud.builder()
            .id("frostbite")
            .blend(true)
            .depth(false)
            .condition(() -> Minecraft.getInstance().player.getTicksFrozen() > 0)
            .onRender(() -> accessor().invokeRenderTextureOverlay(guiGraphics(), accessor().getPowderSnowLocation(), Minecraft.getInstance().player.getPercentFrozen()))
            .build();
    public static final Hud PORTAL = Hud.builder()
            .id("portal")
            .blend(true)
            .depth(false)
            .condition(() -> !Minecraft.getInstance().player.hasEffect(MobEffects.CONFUSION))
            .onRender(() -> {
                float f = Mth.lerp(deltaTracker(), Minecraft.getInstance().player.oSpinningEffectIntensity, Minecraft.getInstance().player.spinningEffectIntensity);
                if (f > 0.0f) {
                    accessor().invokeRenderPortalOverlay(guiGraphics(), f);
                }
            })
            .build();
    public static final Hud HOTBAR = Hud.builder()
            .id("hotbar")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                if (Minecraft.getInstance().gameMode.getPlayerMode() == GameType.SPECTATOR) {
                    gui().getSpectatorGui().renderHotbar(guiGraphics);
                } else if (!Minecraft.getInstance().options.hideGui) {
                    accessor().invokeRenderHotbar(deltaTracker(), guiGraphics);
                }
            })
            .build();
    public static final Hud CROSSHAIR = Hud.builder()
            .id("crosshair")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                accessor().invokeRenderCrosshair(guiGraphics());
            })
            .build();
    public static final Hud BOSSBAR = Hud.builder()
            .id("boss_bar")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                gui().getBossOverlay().render(guiGraphics());
            })
            .build();
    public static final Hud STATUS_BAR = Hud.builder()
            .id("status_bar")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                if (Minecraft.getInstance().gameMode.canHurtPlayer()) {
                    accessor().invokeRenderPlayerHealth(guiGraphics());
                }
                accessor().invokeRenderVehicleHealth(guiGraphics());
            })
            .build();
    public static final Hud EXPERIENCE_BAR = Hud.builder()
            .id("experience_bar")
            .blend(false)
            .depth(false)
            .onRender(() -> {
                int i = accessor().getScreenWidth() / 2 - 91;
                PlayerRideableJumping playerRideableJumping = Minecraft.getInstance().player.jumpableVehicle();
                if (playerRideableJumping != null) {
                    gui().renderJumpMeter(playerRideableJumping, guiGraphics, i);
                } else if (Minecraft.getInstance().gameMode.hasExperience()) {
                    gui().renderExperienceBar(guiGraphics, i);
                }
            })
            .build();
    public static final Hud SELECTED_ITEM_NAME = Hud.builder()
            .id("selected_item_name")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                if (Minecraft.getInstance().gameMode.getPlayerMode() != GameType.SPECTATOR) {
                    gui().renderSelectedItemName(guiGraphics);
                } else if (Minecraft.getInstance().player.isSpectator()) {
                    gui().getSpectatorGui().renderTooltip(guiGraphics);
                }
            })
            .build();
    public static final Hud SLEEP_OVERLAY = Hud.builder()
            .id("sleep_overlay")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                if (Minecraft.getInstance().player.getSleepTimer() > 0) {
                    float j = (float)Minecraft.getInstance().player.getSleepTimer();
                    float k = j / 100.0F;
                    if (k > 1.0F) {
                        k = 1.0F - (j - 100.0F) / 10.0F;
                    }
                    int l = (int)(220.0F * k) << 24 | 1052704;
                    guiGraphics.fill(RenderType.guiOverlay(), 0, 0, accessor().getScreenWidth(), accessor().getScreenHeight(), l);
                }
            })
            .build();
    public static final Hud DEMO_OVERLAY = Hud.builder()
            .id("demo_overlay")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                if (Minecraft.getInstance().isDemo()) {
                    gui().renderDemoOverlay(guiGraphics);
                }
            })
            .build();
    public static final Hud EFFECTS = Hud.builder()
            .id("effects")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                accessor().invokeRenderEffects(guiGraphics());
            })
            .build();
    public static final Hud DEBUG_OVERLAY = Hud.builder()
            .id("debug_overlay")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                if (gui().getDebugOverlay().showDebugScreen()) {
                    gui().getDebugOverlay().render(guiGraphics);
                }
            })
            .build();
    public static final Hud OVERLAY_MESSAGE = Hud.builder()
            .id("overlay_message")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                if (accessor().getOverlayMessageString() != null && accessor().getOverlayMessageTime() > 0) {
                    float j = (float)(accessor().getOverlayMessageTime()) - deltaTracker();
                    int m = (int)(j * 255.0F / 20.0F);
                    if (m > 255) {
                        m = 255;
                    }

                    if (m > 8) {
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate((float)(accessor().getScreenWidth() / 2), (float)(accessor().getScreenHeight() - 68), 0.0F);
                        int l = 16777215;
                        if (accessor().getAnimateOverlayMessageColor()) {
                            l = Mth.hsvToRgb(j / 50.0F, 0.7F, 0.6F) & 16777215;
                        }

                        int n = m << 24 & -16777216;
                        int o = gui().getFont().width(accessor().getOverlayMessageString());
                        accessor().invokeDrawBackdrop(guiGraphics, gui().getFont(), -4, o, 16777215 | n);
                        guiGraphics.drawString(gui().getFont(), accessor().getOverlayMessageString(), -o / 2, -4, l | n);
                        guiGraphics.pose().popPose();
                    }
                }
            })
            .build();
    public static final Hud TITLES = Hud.builder()
            .id("titles")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                if (accessor().getTitle() != null && accessor().getTitleTime() > 0) {
                    float j = (float)accessor().getTitleTime() - deltaTracker();
                    int m = 255;
                    if (accessor().getTitleTime() > accessor().getTitleFadeOutTime() + accessor().getTitleStayTime()) {
                        float p = (float)(accessor().getTitleFadeInTime() + accessor().getTitleStayTime()) + accessor().getTitleFadeOutTime() - j;
                        m = (int)(p * 255.0F / (float)accessor().getTitleFadeInTime());
                    }

                    if (accessor().getTitleTime() <= accessor().getTitleFadeOutTime()) {
                        m = (int)(j * 255.0F / (float)accessor().getTitleFadeOutTime());
                    }

                    m = Mth.clamp(m, 0, 255);
                    if (m > 8) {
                        Font font = gui().getFont();
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate((float)(accessor().getScreenWidth() / 2), (float)(accessor().getScreenHeight() / 2), 0.0F);
                        RenderSystem.enableBlend();
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().scale(4.0F, 4.0F, 4.0F);
                        int l = m << 24 & -16777216;
                        int n = font.width(accessor().getTitle());
                        accessor().invokeDrawBackdrop(guiGraphics, font, -10, n, 16777215 | l);
                        guiGraphics.drawString(font, accessor().getTitle(), -n / 2, -10, 16777215 | l);
                        guiGraphics.pose().popPose();
                        if (accessor().getSubtitle() != null) {
                            guiGraphics.pose().pushPose();
                            guiGraphics.pose().scale(2.0F, 2.0F, 2.0F);
                            int o = font.width(accessor().getSubtitle());
                            accessor().invokeDrawBackdrop(guiGraphics, font, 5, o, 16777215 | l);
                            guiGraphics.drawString(font, accessor().getSubtitle(), -o / 2, 5, 16777215 | l);
                            guiGraphics.pose().popPose();
                        }

                        RenderSystem.disableBlend();
                        guiGraphics.pose().popPose();
                    }
                }
            })
            .build();
    public static final Hud SUBTITLES = Hud.builder()
            .id("subtitles")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                accessor().gnetum$getSubtitleOverlay().render(guiGraphics());
            })
            .build();
    public static final Hud SCOREBOARD = Hud.builder()
            .id("scoreboard")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                Scoreboard scoreboard = Minecraft.getInstance().level.getScoreboard();
                Objective objective = null;
                PlayerTeam playerTeam = scoreboard.getPlayersTeam(Minecraft.getInstance().player.getScoreboardName());
                if (playerTeam != null) {
                    DisplaySlot displaySlot = DisplaySlot.teamColorToSlot(playerTeam.getColor());
                    if (displaySlot != null) {
                        objective = scoreboard.getDisplayObjective(displaySlot);
                    }
                }
                Objective objective2 = objective != null ? objective : scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
                if (objective2 != null) {
                    accessor().invokeDisplayScoreboardSidebar(guiGraphics, objective2);
                }
            })
            .build();
    public static final Hud CHAT = Hud.builder()
            .id("chat")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                Window window = Minecraft.getInstance().getWindow();
                int o = Mth.floor(Minecraft.getInstance().mouseHandler.xpos() * (double)window.getGuiScaledWidth() / (double)window.getScreenWidth());
                int q = Mth.floor(Minecraft.getInstance().mouseHandler.ypos() * (double)window.getGuiScaledHeight() / (double)window.getScreenHeight());
                gui().getChat().render(guiGraphics, accessor().getTickCount(), o, q);
            })
            .build();
    public static final Hud TAB_LIST = Hud.builder()
            .id("tab_list")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                Scoreboard scoreboard = Minecraft.getInstance().level.getScoreboard();
                Objective objective2 = scoreboard.getDisplayObjective(DisplaySlot.LIST);
                if (!Minecraft.getInstance().options.keyPlayerList.isDown() || Minecraft.getInstance().isLocalServer() && Minecraft.getInstance().player.connection.getListedOnlinePlayers().size() <= 1 && objective2 == null) {
                    gui().getTabList().setVisible(false);
                } else {
                    gui().getTabList().setVisible(true);
                    gui().getTabList().render(guiGraphics, accessor().getScreenWidth(), scoreboard, objective2);
                } 
            })
            .build();
    public static final Hud SAVING_INDICATOR = Hud.builder()
            .id("saving_indicator")
            .blend(true)
            .depth(false)
            .onRender(() -> {
                accessor().invokeRenderSavingIndicator(guiGraphics());
            })
            .build();
    ^///? } else {

    //? }

    public static void init() {

    }

    private static GuiAccessor accessor() {
        return (GuiAccessor) Minecraft.getInstance().gui;
    }
}
*///? }