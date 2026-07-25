package me.decce.gnetum.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import me.decce.gnetum.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.ASMEventHandler;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBusInvokeDispatcher;
import net.minecraftforge.eventbus.api.IEventListener;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Mixin(ForgeIngameGui.class)
public class ForgeIngameGuiMixin {
    @Unique
    private final Minecraft minecraft = Minecraft.getInstance();
    @Shadow @Final
    private static Logger LOGGER;
    @Shadow
    public int left_height;
    @Shadow
    public int right_height;
    @Shadow
    private Font font;
    @Shadow
    private RenderGameOverlayEvent eventParent;
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
    private boolean gnetum$wasChatScreenOpen;
    @Unique
    private int gnetum$lastVanillaOverlayIndex = -1;

    private int gnetum$getLastVanillaOverlayIndex() {
        if (gnetum$lastVanillaOverlayIndex == -1) {
            var layers = OverlayRegistry.orderedEntries();
            for (int i = 0; i < layers.size(); i++) {
                if ("Player List".equals(layers.get(i).getDisplayName())) {
                    gnetum$lastVanillaOverlayIndex = i;
                    break;
                }
            }
        }
        return gnetum$lastVanillaOverlayIndex;
    }

    @Unique
    private GuiAccessor gnetum$getGuiAccessor() {
        return (GuiAccessor)(Gui)(Object)this;
    }

    // We use WrapOperation in favor of Inject(at HEAD) because some mods inject into the tail of ForgeGui.render to
    // render their HUD. If the whole method is canceled their HUD will not render.
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/gui/ForgeIngameGui;pre(Lnet/minecraftforge/client/event/RenderGameOverlayEvent$ElementType;Lcom/mojang/blaze3d/vertex/PoseStack;)Z", ordinal = 0))
    public boolean gnetum$render(ForgeIngameGui instance, RenderGameOverlayEvent.ElementType type, PoseStack poseStack, Operation<Boolean> original, @Local(argsOnly = true) float partialTick)
    {
        if (!Gnetum.config.isEnabled()) {
            return original.call(instance, type, poseStack);
        }

        var chatScreenOpen = Minecraft.getInstance().screen instanceof ChatScreen;
        if (gnetum$wasChatScreenOpen != chatScreenOpen) {
            gnetum$wasChatScreenOpen = chatScreenOpen;
            FramebufferManager.getInstance().markForCatchUp();
        }

        PoseStackHelper.beginHudRendering(poseStack);

        // Do not use cached HUD when transformation is used (e.g. OkZoomer mod)
        // Because uncached elements are rendered outside of here (in GameRendererMixin), transformation is not applied
        //  to them otherwise, creating inconsistencies
        //TODO
//        var pose = poseStack.last().pose();
//        if (!pose.equals(gnetum$defaultGuiPose)) {
//            FramebufferManager.getInstance().markForCatchUp();
//        }

        if (Gnetum.passManager.current == 1) {
            gnetum$currentLeftHeight = 39;
            gnetum$currentRightHeight = 39;
        }

        font = minecraft.font;

        FramebufferManager.getInstance().ensureSize();

        // If we haven't finished rendering a complete HUD, the original method will be called
        boolean needsCatchUp = FramebufferManager.getInstance().needsCatchUp();

        if (!needsCatchUp) {
            minecraft.getProfiler().push("uncached");
            gnetum$postEvent(new RenderGameOverlayEvent.Pre(poseStack, eventParent, type), poseStack, modid -> Gnetum.passManager.cachingDisabled(modid, ElementType.PRE));
            gnetum$renderLayers(OverlayRegistry.orderedEntries(), poseStack, type, partialTick, overlay -> Gnetum.passManager.cachingDisabled(overlay), 0, gnetum$getLastVanillaOverlayIndex());
            if (Gnetum.passManager.current > 0) {
                // guiGraphics.flush();
            }
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

            Gnetum.renderingCanceled = gnetum$postEvent(new RenderGameOverlayEvent.Pre(poseStack, eventParent, type), poseStack, modid -> Gnetum.passManager.shouldRender(modid, ElementType.PRE));

            if (Gnetum.passManager.current != 1) {
                left_height = gnetum$currentLeftHeight;
                right_height = gnetum$currentRightHeight;
            }
            font = minecraft.font;

            gnetum$getGuiAccessor().getRandom().setSeed(gnetum$getGuiAccessor().getTickCount() * 312871L);

            gnetum$renderLayers(OverlayRegistry.orderedEntries(), poseStack, type, partialTick, rl -> Gnetum.passManager.shouldRender(rl));

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            gnetum$postEvent(new RenderGameOverlayEvent.Post(poseStack, eventParent, type), poseStack, modid -> Gnetum.passManager.shouldRender(modid, ElementType.POST));

            // guiGraphics.flush();

            gnetum$currentLeftHeight = left_height;
            gnetum$currentRightHeight = right_height;

            Gnetum.rendering = false;
            Gnetum.currentElement = null;
        }
        Gnetum.passManager.end();

        if (Gnetum.passManager.current != Gnetum.config.numberOfPasses) {
            left_height = gnetum$lastLeftHeight;
            right_height = gnetum$lastRightHeight;
        }

        Gnetum.passManager.nextPass();

        if (Gnetum.passManager.current == Gnetum.config.numberOfPasses) {
            gnetum$lastLeftHeight = left_height;
            gnetum$lastRightHeight = right_height;
        }

        FramebufferManager.getInstance().unbind();

        if (!needsCatchUp) {
            FramebufferManager.getInstance().blit();

            minecraft.getProfiler().push("uncached");
            gnetum$postEvent(new RenderGameOverlayEvent.Post(poseStack, eventParent, type), poseStack, modid -> Gnetum.passManager.cachingDisabled(modid, ElementType.POST));
            gnetum$renderLayers(OverlayRegistry.orderedEntries(), poseStack, type, partialTick, overlay -> Gnetum.passManager.cachingDisabled(overlay), gnetum$getLastVanillaOverlayIndex() + 1, -1);
            minecraft.getProfiler().pop();
        }
        else {
            PoseStackHelper.endHudRendering(poseStack);
            return original.call(instance, type, poseStack);
        }
        PoseStackHelper.endHudRendering(poseStack);
        return true;
    }

    @Unique
    private void gnetum$renderLayers(List<OverlayRegistry.OverlayEntry> list, PoseStack poseStack, RenderGameOverlayEvent.ElementType type, float partialTick, Predicate<String> check) {
        gnetum$renderLayers(list, poseStack, type, partialTick, check,0, -1);
    }

    @Unique
    private void gnetum$renderLayers(List<OverlayRegistry.OverlayEntry> list, PoseStack poseStack, RenderGameOverlayEvent.ElementType type, float partialTick, Predicate<String> check, int startIndex, int endIndex) {
        ForgeIngameGui forgeGui = (ForgeIngameGui)(Object)this;
        for (int i = startIndex; i < list.size(); i++) {
            if (endIndex != -1 && i > endIndex) {
                break;
            }
            var entry = list.get(i);
            try
            {
                String id = entry.getDisplayName();
                if (check.test(entry.getDisplayName())) {
                    if (Gnetum.rendering) {
                        Gnetum.currentElement = id;
                        Gnetum.currentElementType = ElementType.VANILLA;
                    }
                    PoseStackHelper.checked(poseStack, () -> {
                        IIngameOverlay overlay = entry.getOverlay();
                        if (gnetum$pre(entry, poseStack)) return;
                        var accessor = gnetum$getGuiAccessor();
                        overlay.render(forgeGui, poseStack, partialTick, accessor.getScreenWidth(), accessor.getScreenHeight());
                        gnetum$post(entry, poseStack);
                    });
                }
            } catch (Exception e)
            {
                LOGGER.error("Error rendering overlay '{}'", entry.getDisplayName(), e);
            }
        }
    }

    @Unique
    public boolean gnetum$pre(OverlayRegistry.OverlayEntry overlay, PoseStack poseStack) {
        return gnetum$postEvent(new RenderGameOverlayEvent.PreLayer(poseStack, eventParent, overlay.getOverlay()), poseStack);
    }

    @Unique
    public void gnetum$post(OverlayRegistry.OverlayEntry overlay, PoseStack poseStack) {
        gnetum$postEvent(new RenderGameOverlayEvent.PostLayer(poseStack, eventParent, overlay.getOverlay()), poseStack);
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
                            if (event instanceof RenderGameOverlayEvent.Pre) {
                                if (modid == null) modid = Gnetum.OTHER_MODS;
                                Gnetum.currentElement = modid;
                                Gnetum.currentElementType = ElementType.PRE;
                                if (check == null || check.test(modid)) {
                                    gnetum$invokeWrapperSafe(poseStack, wrapper, listener, event);
                                }
                            } else if (event instanceof RenderGameOverlayEvent.Post) {
                                if (modid == null) modid = Gnetum.OTHER_MODS;
                                Gnetum.currentElement = modid;
                                Gnetum.currentElementType = ElementType.POST;
                                if (check == null || check.test(modid)) {
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
                            if (!Gnetum.rendering || listener instanceof EventPriority || event instanceof RenderGameOverlayEvent) {
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
        PoseStackHelper.checked(poseStack, () -> wrapper.invoke(listener, event));
    }
}
