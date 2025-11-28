package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.decce.gnetum.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.ASMEventHandler;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventBusInvokeDispatcher;
import net.minecraftforge.eventbus.api.IEventListener;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Mixin(ForgeGui.class)
public class ForgeGuiMixin {
    @Unique
    private Minecraft minecraft = Minecraft.getInstance();
    @Shadow @Final
    private static Logger LOGGER;
    @Shadow
    public int leftHeight;
    @Shadow
    public int rightHeight;
    @Shadow
    private Font font;
    @Unique
    private int gnetum$lastLeftHeight = 39;
    @Unique
    private int gnetum$lastRightHeight = 39;
    @Unique
    private int gnetum$currentLeftHeight;
    @Unique
    private int gnetum$currentRightHeight;
    @Unique
    private Matrix4f gnetum$defaultGuiPose = new Matrix4f();

    @Unique
    private GuiAccessor gnetum$getGuiAccessor() {
        return (GuiAccessor)(Gui)(Object)this;
    }

    // We use WrapOperation in favor of Inject(at HEAD) because some mods inject into the tail of ForgeGui.render to
    // render their HUD. If the whole method is canceled their HUD will not render.
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z", ordinal = 0))
    public boolean gnetum$render(IEventBus instance, Event event, Operation<Boolean> original, @Local(argsOnly = true) GuiGraphics guiGraphics, @Local(argsOnly = true) float partialTick)
    {
        if (!Gnetum.config.isEnabled()) {
            return original.call(instance, event);
        }

        guiGraphics.pose().pushPose();

        // Do not use cached HUD when transformation is used (e.g. OkZoomer mod)
        // Because uncached elements are rendered outside of here (in GameRendererMixin), transformation is not applied
        //  to them otherwise, creating inconsistencies
        var pose = guiGraphics.pose().last().pose();
        if (!pose.equals(gnetum$defaultGuiPose, 0.01F)) {
            FramebufferManager.getInstance().markIncomplete();
        }

        if (Gnetum.passManager.current == 1) {
            gnetum$currentLeftHeight = 39;
            gnetum$currentRightHeight = 39;
        }

        font = minecraft.font;

        FramebufferManager.getInstance().ensureSize();

        // If we haven't finished rendering a complete HUD, the original method will be called
        boolean framebufferComplete = FramebufferManager.getInstance().isComplete();

        if (framebufferComplete) {
            minecraft.getProfiler().push("uncached");
            gnetum$postEvent(new RenderGuiEvent.Pre(minecraft.getWindow(), guiGraphics, partialTick), guiGraphics.pose(), modid -> Gnetum.passManager.cachingDisabled(modid, ElementType.PRE));
            gnetum$renderLayers(GuiOverlayManager.getOverlays(), guiGraphics, partialTick, overlay -> Gnetum.passManager.cachingDisabled(overlay));
            guiGraphics.flush();
            minecraft.getProfiler().pop();
        }
        else {
            gnetum$lastLeftHeight = 39;
            gnetum$lastRightHeight = 39;
            gnetum$currentLeftHeight = 39;
            gnetum$currentRightHeight = 39;
        }

        Gnetum.passManager.begin();
        HudDeltaTracker.update(minecraft);
        if (Gnetum.passManager.current > 0) {
            FramebufferManager.getInstance().bind();
            Gnetum.rendering = true;

            Gnetum.renderingCanceled = gnetum$postEvent(new RenderGuiEvent.Pre(minecraft.getWindow(), guiGraphics, partialTick), guiGraphics.pose(), modid -> Gnetum.passManager.shouldRender(modid, ElementType.PRE));

            if (Gnetum.passManager.current != 1) {
                leftHeight = gnetum$currentLeftHeight;
                rightHeight = gnetum$currentRightHeight;
            }
            font = minecraft.font;

            gnetum$getGuiAccessor().getRandom().setSeed(gnetum$getGuiAccessor().getTickCount() * 312871L);

            gnetum$renderLayers(GuiOverlayManager.getOverlays(), guiGraphics, partialTick, rl -> Gnetum.passManager.shouldRender(rl));

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            gnetum$postEvent(new RenderGuiEvent.Post(minecraft.getWindow(), guiGraphics, partialTick), guiGraphics.pose(), modid -> Gnetum.passManager.shouldRender(modid, ElementType.POST));

            guiGraphics.flush();

            gnetum$currentLeftHeight = leftHeight;
            gnetum$currentRightHeight = rightHeight;

            Gnetum.rendering = false;
            Gnetum.currentElement = null;
        }
        Gnetum.passManager.end();

        if (Gnetum.passManager.current != Gnetum.config.numberOfPasses) {
            leftHeight = gnetum$lastLeftHeight;
            rightHeight = gnetum$lastRightHeight;
        }

        Gnetum.passManager.nextPass();

        if (Gnetum.passManager.current == Gnetum.config.numberOfPasses) {
            gnetum$lastLeftHeight = leftHeight;
            gnetum$lastRightHeight = rightHeight;
        }

        FramebufferManager.getInstance().unbind();

        if (framebufferComplete) {
            FramebufferManager.getInstance().blit();

            minecraft.getProfiler().push("uncached");
            gnetum$postEvent(new RenderGuiEvent.Post(minecraft.getWindow(), guiGraphics, partialTick), guiGraphics.pose(), modid -> Gnetum.passManager.cachingDisabled(modid, ElementType.POST));
            minecraft.getProfiler().pop();
        }
        else {
            guiGraphics.pose().popPose();
            return original.call(instance, event);
        }
        guiGraphics.pose().popPose();
        return true;
    }

    @Unique
    private void gnetum$renderLayers(List<NamedGuiOverlay> list, GuiGraphics guiGraphics, float partialTick, Predicate<String> check) {
        ForgeGui forgeGui = (ForgeGui)(Object)this;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < list.size(); i++) {
            var entry = list.get(i);
            try
            {
                String id = entry.id().toString();
                if (check.test(entry.id().toString())) {
                    if (Gnetum.rendering) {
                        Gnetum.currentElement = id;
                        Gnetum.currentElementType = ElementType.VANILLA;
                    }
                    PoseStackHelper.checkIf(Gnetum.rendering, guiGraphics.pose(), () -> {
                        IGuiOverlay overlay = entry.overlay();
                        if (gnetum$pre(entry, guiGraphics)) return;
                        var accessor = gnetum$getGuiAccessor();
                        overlay.render(forgeGui, guiGraphics, partialTick, accessor.getScreenWidth(), accessor.getScreenHeight());
                        gnetum$post(entry, guiGraphics);
                    });
                }
            } catch (Exception e)
            {
                LOGGER.error("Error rendering overlay '{}'", entry.id(), e);
            }
        }
    }

    @Unique
    public boolean gnetum$pre(NamedGuiOverlay overlay, GuiGraphics guiGraphics) {
        return gnetum$postEvent(new RenderGuiOverlayEvent.Pre(this.minecraft.getWindow(), guiGraphics, this.minecraft.getFrameTime(), overlay), guiGraphics.pose());
    }

    @Unique
    public void gnetum$post(NamedGuiOverlay overlay, GuiGraphics guiGraphics) {
        gnetum$postEvent(new RenderGuiOverlayEvent.Post(this.minecraft.getWindow(), guiGraphics, this.minecraft.getFrameTime(), overlay), guiGraphics.pose());
    }

    @Unique
    public boolean gnetum$postEvent(Event event, PoseStack stack, Predicate<String> test) {
        return this.gnetum$postEvent(event, IEventListener::invoke, stack, test);
    }

    @Unique
    public boolean gnetum$postEvent(Event event, PoseStack stack) {
        return gnetum$postEvent(event, IEventListener::invoke, stack, null);
    }

    @Unique
    public boolean gnetum$postEvent(Event event, IEventBusInvokeDispatcher wrapper, PoseStack poseStack, Predicate<String> check) {
        if (EventBusHelper.isShutdown()) {
            return false;
        } else if (EventBusHelper.isCheckTypesOnDispatch() && !EventBusHelper.getBaseType().isInstance(event)) {
            String var10002 = event.getClass().getSimpleName();
            throw new IllegalArgumentException("Cannot post event of type " + var10002 + " to this event. Must match type: " + EventBusHelper.getBaseType().getSimpleName());
        } else {
            IEventListener[] listeners = event.getListenerList().getListeners(EventBusHelper.getBusID());
            int index = 0;
            boolean trackPhases = EventBusHelper.isTrackPhases();
            try {
                for(; index < listeners.length; ++index) {
                    if (trackPhases || !Objects.equals(listeners[index].getClass(), EventPriority.class)) {
                        IEventListener listener = listeners[index];
                        if (listener instanceof ASMEventHandler asm) {
                            String modid = ASMEventHandlerHelper.tryGetModId(asm);
                            if (event instanceof RenderGuiEvent.Pre) {
                                if (modid == null) modid = Gnetum.OTHER_MODS;
                                Gnetum.currentElement = modid;
                                Gnetum.currentElementType = ElementType.PRE;
                                if (check.test(modid)) {
                                    gnetum$invokeWrapperSafe(poseStack, wrapper, listener, event);
                                }
                            } else if (event instanceof RenderGuiEvent.Post) {
                                if (modid == null) modid = Gnetum.OTHER_MODS;
                                Gnetum.currentElement = modid;
                                Gnetum.currentElementType = ElementType.POST;
                                if (check.test(modid)) {
                                    gnetum$invokeWrapperSafe(poseStack, wrapper, listener, event);
                                }
                            }
                            else {
                                wrapper.invoke(listener, event);
                            }
                        }
                        else {
                            // do not cache listeners that are not ASMEventHandler
                            // Listeners that listen to RenderGuiEvent may trigger this path 2 times each frame (one in uncached, one in cached), and we only actually render them once
                            // Listeners that listen to RenderGuiOverlayEvent only trigger this path 1 time
                            if (!Gnetum.rendering || listener instanceof EventPriority || event instanceof RenderGuiOverlayEvent) {
                                wrapper.invoke(listener, event);
                            }
                        }
                    }
                }
            } catch (Throwable throwable) {
                EventBusHelper.getExceptionHandler().handleException(MinecraftForge.EVENT_BUS, event, listeners, index, throwable);
                throw throwable;
            }

            return event.isCancelable() && event.isCanceled();
        }
    }

    @Unique
    private void gnetum$invokeWrapperSafe(PoseStack poseStack, IEventBusInvokeDispatcher wrapper, IEventListener listener, Event event) {
        PoseStackHelper.checkIf(Gnetum.rendering, poseStack, () -> wrapper.invoke(listener, event));
    }
}
