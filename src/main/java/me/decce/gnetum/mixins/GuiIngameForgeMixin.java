package me.decce.gnetum.mixins;

import me.decce.gnetum.FramebufferManager;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.GnetumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraftforge.client.GuiIngameForge.*;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ALL;

@Mixin(value = GuiIngameForge.class)
public class GuiIngameForgeMixin {
    @Unique
    private final Minecraft gnetum$mc = Minecraft.getMinecraft();
    @Shadow
    private ScaledResolution res;
    @Shadow
    private FontRenderer fontrenderer;
    @Shadow
    private RenderGameOverlayEvent eventParent;

    @Shadow
    protected void renderCrosshairs(float partialTicks) {
    }

    @Shadow
    protected void renderHelmet(ScaledResolution res, float partialTicks) {
    }

    @Shadow
    protected void renderVignette(float lightLevel, ScaledResolution scaledRes) {
    }

    @Shadow
    protected void renderPortal(ScaledResolution res, float partialTicks) {
    }

    @Shadow
    protected void renderHotbar(ScaledResolution res, float partialTicks) {
    }

    @Shadow
    protected void renderSleepFade(int width, int height) {
    }

    @Shadow
    protected void renderSubtitles(ScaledResolution resolution) {
    }

    @Shadow
    protected void renderBossHealth() {
    }

    @Shadow
    protected void renderArmor(int width, int height) {
    }

    @Shadow
    protected void renderHUDText(int width, int height) {
    }

    @Shadow
    protected void renderExperience(int width, int height) {
    }

    @Shadow
    public void renderHealth(int width, int height) {
    }


    @Shadow
    protected void renderToolHighlight(ScaledResolution res) {
    }

    @Shadow
    protected void renderJumpBar(int width, int height) {
    }

    @Shadow
    protected void renderFPSGraph() {
    }

    @Shadow
    protected void renderRecordOverlay(int width, int height, float partialTicks) {
    }

    @Shadow
    protected void renderPotionIcons(ScaledResolution resolution) {
    }

    @Shadow
    protected void renderChat(int width, int height) {
    }

    @Shadow
    protected void renderTitle(int width, int height, float partialTicks) {
    }

    @Shadow
    public void renderFood(int width, int height) {
    }

    @Shadow
    protected void renderHealthMount(int width, int height) {
    }

    @Shadow
    protected void renderAir(int width, int height) {
    }


    @Shadow
    protected void renderPlayerList(int width, int height) {
    }

    @Shadow
    private boolean pre(RenderGameOverlayEvent.ElementType type) {
        throw new AssertionError();
    }

    @Shadow
    private void post(RenderGameOverlayEvent.ElementType type) {
    }


    @Unique
    private void gnetum$renderScoreboard(ScoreObjective objective, ScaledResolution scaledRes) {
        gnetum$getGuiIngameAccessor().callRenderScoreboard(objective, scaledRes);
    }

    @Unique
    private GuiIngameAccessor gnetum$getGuiIngameAccessor() {
        GuiIngameForge gui = (GuiIngameForge) (Object) this;
        return (GuiIngameAccessor) (GuiIngame) gui;
    }

    @Unique
    private GuiAccessor gnetum$getGuiAccessor() {
        GuiIngameForge gui = (GuiIngameForge) (Object) this;
        return (GuiAccessor) (Gui) gui;
    }


    @Inject(method = "renderGameOverlay", at = @At("HEAD"), cancellable = true)
    public void renderGameOverlay(float partialTicks, CallbackInfo ci) {
        if (!GnetumConfig.isEnabled()) return;
        ci.cancel();
        res = new ScaledResolution(gnetum$mc);
        eventParent = new RenderGameOverlayEvent(partialTicks, res);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        renderHealthMount = gnetum$mc.player.getRidingEntity() instanceof EntityLivingBase;
        renderFood = gnetum$mc.player.getRidingEntity() == null;
        renderJumpBar = gnetum$mc.player.isRidingHorse();

        right_height = 39;
        left_height = 39;

        GlStateManager.enableBlend();
        GlStateManager.enableDepth();

        if (renderVignette && Minecraft.isFancyGraphicsEnabled()) {
            renderVignette(gnetum$mc.player.getBrightness(), res);
        }
        if (renderCrosshairs) renderCrosshairs(partialTicks);

        FramebufferManager.getInstance().ensureSize();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        FramebufferManager.getInstance().blit();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        FramebufferManager.getInstance().bind();

        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableFog();

        GlStateManager.color(1, 1, 1, 1);

        Gnetum.rendering = true;
        gnetum$mc.entityRenderer.setupOverlayRendering();

        switch (Gnetum.pass) {
            //Having separate methods makes it easier to see which pass took the longest, during profiling
            case Gnetum.Passes.MISC:
                gnetum$renderPass0(width, height, partialTicks);
                break;
            case Gnetum.Passes.HUD_TEXT: //F3, The One Probe, etc.
                gnetum$renderPass1(width, height);
                break;
            case Gnetum.Passes.HOTBAR: //Items in the hotbar can be quite slow to render, so let's give them a pass
                gnetum$renderPass2(partialTicks);
                break;
            case Gnetum.Passes.FORGE_PRE:
                gnetum$renderPass3();
                break;
        }

        Gnetum.rendering = false;

        FramebufferManager.getInstance().unbind();

        Gnetum.nextPass();

        GlStateManager.enableDepth();
        GlStateManager.enableBlend();

    }

    @Unique
    private void gnetum$renderPass0(int width, int height, float partialTicks) {
        gnetum$mc.profiler.startSection("pass0");

        // we should render the hand here, so it's possible to see the actual time spent on this pass via a profiler
        // the config for hand buffering should be kept disabled by default before this gets resolved

        fontrenderer = gnetum$mc.fontRenderer;
        GlStateManager.enableBlend();

        GlStateManager.enableDepth();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        if (renderHelmet) renderHelmet(res, partialTicks);

        if (renderPortal && !gnetum$mc.player.isPotionActive(MobEffects.NAUSEA)) {
            renderPortal(res, partialTicks);
        }

        //if (renderHotbar) renderHotbar(res, partialTicks);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        gnetum$getGuiAccessor().setZLevel(-90.0F);
        gnetum$getGuiIngameAccessor().getRand().setSeed((long) (gnetum$getGuiIngameAccessor().getUpdateCounter() * 312871));

        if (renderBossHealth) renderBossHealth();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.gnetum$mc.playerController.shouldDrawHUD() && this.gnetum$mc.getRenderViewEntity() instanceof EntityPlayer) {
            if (renderHealth) renderHealth(width, height);
            if (renderArmor) renderArmor(width, height);
            if (renderFood) renderFood(width, height);
            if (renderHealthMount) renderHealthMount(width, height);
            if (renderAir) renderAir(width, height);
        }

        renderSleepFade(width, height);

        if (renderJumpBar) {
            renderJumpBar(width, height);
        } else if (renderExperiance) {
            renderExperience(width, height);
        }

        renderToolHighlight(res);
        //renderHUDText(width, height);
        renderFPSGraph();
        renderPotionIcons(res);
        renderRecordOverlay(width, height, partialTicks);
        renderSubtitles(res);
        renderTitle(width, height, partialTicks);


        Scoreboard scoreboard = this.gnetum$mc.world.getScoreboard();
        ScoreObjective objective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(gnetum$mc.player.getName());
        if (scoreplayerteam != null) {
            int slot = scoreplayerteam.getColor().getColorIndex();
            if (slot >= 0) objective = scoreboard.getObjectiveInDisplaySlot(3 + slot);
        }
        ScoreObjective scoreobjective1 = objective != null ? objective : scoreboard.getObjectiveInDisplaySlot(1);
        if (renderObjective && scoreobjective1 != null) {
            this.gnetum$renderScoreboard(scoreobjective1, res);
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.disableAlpha();

        renderChat(width, height);

        renderPlayerList(width, height);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();

        post(ALL);

        gnetum$mc.profiler.endSection();
    }

    @Unique
    private void gnetum$renderPass1(int width, int height) {
        gnetum$mc.profiler.startSection("pass1");

        renderHUDText(width, height);

        gnetum$mc.profiler.endSection();
    }

    @Unique
    private void gnetum$renderPass2(float partialTicks) {
        gnetum$mc.profiler.startSection("pass2");

        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        if (renderHotbar) renderHotbar(res, partialTicks);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        gnetum$mc.profiler.endSection();
    }

    @Unique
    private void gnetum$renderPass3() {
        gnetum$mc.profiler.startSection("pass3");

        pre(ALL); //TODO: cancellation ignored

        gnetum$mc.profiler.endSection();
    }
}
