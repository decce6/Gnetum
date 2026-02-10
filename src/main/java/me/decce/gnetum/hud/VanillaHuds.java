package me.decce.gnetum.hud;

import me.decce.gnetum.Gnetum;
import me.decce.gnetum.GuiHelper;
import me.decce.gnetum.compat.CompatHelper;
import me.decce.gnetum.compat.thaumcraft.ThaumcraftCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import static me.decce.gnetum.Gnetum.getScaledResolution;
import static me.decce.gnetum.hud.SharedValues.*;

@SuppressWarnings("unused")
public class VanillaHuds {
    public static final Hud DUMMY_FIX_GL_STATE = Hud.builder()
            .dummy()
            .onRender(() -> {
                // Thaumcraft changes the matrix mode to GL_TEXTURE but does not change it back: https://github.com/decce6/Gnetum/issues/59
                // See thaumcraft.client.lib.events.RenderEventHandler#renderShaders
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            })
            .build();
    public static final Hud VIGNETTE = Hud.builder()
            .id("vignette")
            .blend(true)
            .depth(false)
            .condition(() -> GuiIngameForge.renderVignette && CompatHelper.isVignetteEnabled())
            .onRender(() -> renderVignette(mc.player.getBrightness(), getScaledResolution()))
            .build();
    public static final Hud PUMPKIN = Hud.builder()
            .id("pumpkin")
            .blend(true)
            .condition(() -> GuiIngameForge.renderHelmet)
            .onRender(() -> renderPumpkinOverlay(getScaledResolution()))
            .build();
    public static final Hud HELMET = Hud.builder()
            .id("helmet")
            .defaultBlendFunc()
            .depth(true)
            .condition(() -> GuiIngameForge.renderHelmet)
            .onRender(() -> renderHelmetOverlay(getScaledResolution()))
            .build();
    public static final Hud PORTAL = Hud.builder()
            .id("portal")
            .defaultBlendFunc()
            .depth(true)
            .condition(() -> GuiIngameForge.renderPortal && !mc.player.isPotionActive(MobEffects.NAUSEA))
            .onRender(() -> getAccessor().callRenderPortal(getScaledResolution(), partialTicks))
            .build();
    public static final Hud HOTBAR = Hud.builder()
            .id("hotbar")
            .defaultBlendFunc()
            .depth(true)
            .condition(() -> GuiIngameForge.renderHotbar)
            .onRender(() -> renderHotbar(getScaledResolution(), partialTicks))
            .build();
    public static final Hud DUMMY0 = Hud.builder()
            .dummy()
            .onRender(() -> {
                GuiHelper.setZLevel(-90.0F);
                getAccessorVanilla().getRand().setSeed((long)(getAccessorVanilla().getUpdateCounter() * 312871));
            })
            .build();
    public static final Hud CROSSHAIRS = Hud.builder()
            .id("crosshairs")
            .blend(true)
            .depth(true)
            .condition(() -> GuiIngameForge.renderCrosshairs)
            .onRender(() -> getAccessor().callRenderCrosshairs(partialTicks))
            .build();
    public static final Hud BOSS_HEALTH = Hud.builder()
            .id("boss_health")
            .defaultBlendFunc()
            .depth(true)
            .condition(() -> GuiIngameForge.renderBossHealth)
            .onRender(() -> getAccessor().callRenderBossHealth())
            .build();
    public static final Hud PLAYER_LEFT = Hud.builder()
            .id("gnetum", "player_left")
            .defaultBlendFunc()
            .depth(true)
            .condition(() -> mc.playerController.shouldDrawHUD() && mc.getRenderViewEntity() instanceof EntityPlayer)
            .onRender(() -> {
                int width = getScaledResolution().getScaledWidth();
                int height = getScaledResolution().getScaledHeight();
                if (GuiIngameForge.renderHealth) getAccessor().callRenderHealth(width, height);
                if (GuiIngameForge.renderArmor) getAccessor().callRenderArmor(width, height);
            })
            .build();
    public static final Hud PLAYER_RIGHT = Hud.builder()
            .id("gnetum", "player_right")
            .defaultBlendFunc()
            .depth(true)
            .condition(() -> mc.playerController.shouldDrawHUD() && mc.getRenderViewEntity() instanceof EntityPlayer)
            .onRender(() -> {
                int width = getScaledResolution().getScaledWidth();
                int height = getScaledResolution().getScaledHeight();
                if (GuiIngameForge.renderFood) getAccessor().callRenderFood(width, height);
                if (GuiIngameForge.renderHealthMount) getAccessor().callRenderHealthMount(width, height);
                if (GuiIngameForge.renderAir) getAccessor().callRenderAir(width, height);
            })
            .build();
    public static final Hud SLEEP_FADE = Hud.builder()
            .id("sleep_overlay")
            .defaultBlendFunc()
            .depth(false)
            .onRender(() -> getAccessor().callRenderSleepFade(getScaledResolution().getScaledWidth(), getScaledResolution().getScaledHeight()))
            .build();
    public static final Hud JUMP_BAR = Hud.builder()
            .id("jump_meter")
            .blend(false)
            .depth(true)
            .condition(() -> GuiIngameForge.renderJumpBar)
            .onRender(() -> getAccessor().callRenderJumpBar(getScaledResolution().getScaledWidth(), getScaledResolution().getScaledHeight()))
            .build();
    public static final Hud EXPERIENCE = Hud.builder()
            .id("experience")
            .blend(false)
            .depth(true)
            .condition(() -> GuiIngameForge.renderExperiance)
            .onRender(() -> getAccessor().callRenderExperience(getScaledResolution().getScaledWidth(), getScaledResolution().getScaledHeight()))
            .build();
    public static final Hud TOOL_HIGHLIGHT = Hud.builder()
            .id("tool_highlight")
            .defaultBlendFunc()
            .depth(true)
            .onRender(() -> getAccessor().callRenderToolHighlight(getScaledResolution()))
            .build();
    public static final Hud HUD_TEXT = Hud.builder()
            .id("hud_text")
            .defaultBlendFunc()
            .depth(true)
            .onRender(() -> getAccessor().callRenderHUDText(getScaledResolution().getScaledWidth(), getScaledResolution().getScaledHeight()))
            .build();
    public static final Hud DUMMY_FIX_GL_STATE_2 = Hud.builder()
            .dummy()
            .onRender(() -> {
                if (ThaumcraftCompat.modInstalled) {
                    // Thaumcraft already broke GlStateManager with raw GL calls,
                    // We need to call both the state manager methods and the GL methods to correct this.
                    GlStateManager.enableBlend();
                    GL11.glEnable(GL11.GL_BLEND);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderHelper.disableStandardItemLighting();
                    GuiHelper.setZLevel(-90.0F);
                }
            })
            .build();
    public static final Hud FPS_GRAPH = Hud.builder()
            .id("fps_graph")
            .defaultBlendFunc()
            .depth(false)
            .onRender(() -> getAccessor().callRenderFPSGraph())
            .build();
    public static final Hud POTION_ICONS = Hud.builder()
            .id("potion_icons")
            .defaultBlendFunc()
            .depth(true)
            .onRender(() -> getAccessor().callRenderPotionIcons(getScaledResolution()))
            .build();
    public static final Hud RECORD_OVERLAY = Hud.builder()
            .id("record_overlay")
            .defaultBlendFunc()
            .depth(true)
            .onRender(() -> getAccessor().callRenderRecordOverlay(getScaledResolution().getScaledWidth(), getScaledResolution().getScaledHeight(), partialTicks))
            .build();
    public static final Hud SUBTITLES = Hud.builder()
            .id("subtitles")
            .defaultBlendFunc()
            .depth(true)
            .onRender(() -> getAccessor().callRenderSubtitles(getScaledResolution()))
            .build();
    public static final Hud TITLE = Hud.builder()
            .id("title")
            .defaultBlendFunc()
            .depth(true)
            .onRender(() -> getAccessor().callRenderTitle(getScaledResolution().getScaledWidth(), getScaledResolution().getScaledHeight(), partialTicks))
            .build();
    public static final Hud SCOREBOARD = Hud.builder()
            .id("scoreboard")
            .defaultBlendFunc()
            .depth(true)
            .onRender(() -> {
                Scoreboard scoreboard = mc.world.getScoreboard();
                ScoreObjective objective = null;
                ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(mc.player.getName());
                if (scoreplayerteam != null)
                {
                    int slot = scoreplayerteam.getColor().getColorIndex();
                    if (slot >= 0) objective = scoreboard.getObjectiveInDisplaySlot(3 + slot);
                }
                ScoreObjective scoreobjective1 = objective != null ? objective : scoreboard.getObjectiveInDisplaySlot(1);
                if (GuiIngameForge.renderObjective && scoreobjective1 != null)
                {
                    getAccessorVanilla().callRenderScoreboard(scoreobjective1, getScaledResolution());
                }
            })
            .build();
    public static final Hud CHAT_PANEL = Hud.builder()
            .id("chat_panel")
            .alpha(false)
            .defaultBlendFunc()
            .depth(true)
            .onRender(() -> getAccessor().callRenderChat(getScaledResolution().getScaledWidth(), getScaledResolution().getScaledHeight()))
            .build();
    public static final Hud PLAYER_LIST = Hud.builder()
            .id("player_list")
            .alpha(false)
            .defaultBlendFunc()
            .depth(true)
            .onRender(() -> getAccessor().callRenderPlayerList(getScaledResolution().getScaledWidth(), getScaledResolution().getScaledHeight()))
            .build();

    public static void init() {

    }

    //Copied from GuiIngameForge, as accessor doesn't work for some reason
    public static void renderVignette(float lightLevel, ScaledResolution scaledRes) {
        if (getAccessor().callPre(RenderGameOverlayEvent.ElementType.VIGNETTE))
        {
            // Need to put this here, since Vanilla assumes this state after the vignette was rendered.
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            return;
        }
        getAccessorVanilla().callRenderVignette(lightLevel, scaledRes);
        getAccessor().callPost(RenderGameOverlayEvent.ElementType.VIGNETTE);
    }

    //Copied from GuiIngameForge, as accessor doesn't work for some reason
    public static void renderHotbar(ScaledResolution res, float partialTicks)
    {
        if (getAccessor().callPre(RenderGameOverlayEvent.ElementType.HOTBAR)) return;

        if (mc.playerController.isSpectator())
        {
            Minecraft.getMinecraft().ingameGUI.getSpectatorGui().renderTooltip(getScaledResolution(), partialTicks);
        }
        else
        {
            getAccessorVanilla().callRenderHotbar(getScaledResolution(), partialTicks);
        }

        getAccessor().callPost(RenderGameOverlayEvent.ElementType.HOTBAR);
    }

    public static void renderPumpkinOverlay(ScaledResolution res) {
        ItemStack itemstack = mc.player.inventory.armorItemInSlot(3);

        if (mc.gameSettings.thirdPersonView == 0 && itemstack.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN))
        {
            SharedValues.getAccessorVanilla().callRenderPumpkinOverlay(res);
        }
    }

    public static void renderHelmetOverlay(ScaledResolution res) {
        Gnetum.isRenderingHelmet = true;
        getAccessor().callRenderHelmet(res, partialTicks);
        Gnetum.isRenderingHelmet = false;
    }
}
