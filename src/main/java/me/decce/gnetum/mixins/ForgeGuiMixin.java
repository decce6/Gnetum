package me.decce.gnetum.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import me.decce.gnetum.ASMEventHandlerHelper;
import me.decce.gnetum.EventBusHelper;
import me.decce.gnetum.FramebufferManager;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.ElementType;
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
import net.minecraftforge.eventbus.api.IEventBusInvokeDispatcher;
import net.minecraftforge.eventbus.api.IEventListener;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Mixin(value = ForgeGui.class, priority = 1500)
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
    private GuiAccessor gnetum$getGuiAccessor() {
        return (GuiAccessor)(Gui)(Object)this;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void gnetum$render(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci)
    {
        if (!Gnetum.config.isEnabled()) {
            return;
        }

        ci.cancel();

        gnetum$getGuiAccessor().setScreenWidth(this.minecraft.getWindow().getGuiScaledWidth());
        gnetum$getGuiAccessor().setScreenHeight(this.minecraft.getWindow().getGuiScaledHeight());

        rightHeight = 39;
        leftHeight = 39;

        font = minecraft.font;

        Minecraft.getInstance().getProfiler().push("uncached");
        gnetum$postEvent(new RenderGuiEvent.Pre(minecraft.getWindow(), guiGraphics, partialTick), modid -> Gnetum.passManager.cachingDisabled(modid, ElementType.PRE));
        gnetum$renderLayers(GuiOverlayManager.getOverlays(), guiGraphics, partialTick, overlay -> Gnetum.passManager.cachingDisabled(overlay));
        Minecraft.getInstance().getProfiler().pop();

        Gnetum.passManager.begin();
        if (Gnetum.passManager.current > 0) {
            FramebufferManager.getInstance().ensureSize();
            FramebufferManager.getInstance().bind();
            Gnetum.rendering = true;

            Gnetum.renderingCanceled = gnetum$postEvent(new RenderGuiEvent.Pre(minecraft.getWindow(), guiGraphics, partialTick), modid -> Gnetum.passManager.shouldRender(modid, ElementType.PRE));

            font = minecraft.font;

            gnetum$getGuiAccessor().getRandom().setSeed(gnetum$getGuiAccessor().getTickCount() * 312871L);

            gnetum$renderLayers(GuiOverlayManager.getOverlays(), guiGraphics, partialTick, rl -> Gnetum.passManager.shouldRender(rl));

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            gnetum$postEvent(new RenderGuiEvent.Post(minecraft.getWindow(), guiGraphics, partialTick), modid -> Gnetum.passManager.shouldRender(modid, ElementType.POST));

            Gnetum.rendering = false;
            Gnetum.currentElement = null;
        }
        Gnetum.passManager.end();
        Gnetum.passManager.nextPass();
        FramebufferManager.getInstance().unbind();

        FramebufferManager.getInstance().blit();

        Minecraft.getInstance().getProfiler().push("uncached");
        gnetum$postEvent(new RenderGuiEvent.Post(minecraft.getWindow(), guiGraphics, partialTick), modid -> Gnetum.passManager.cachingDisabled(modid, ElementType.POST));
        Minecraft.getInstance().getProfiler().pop();
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
                    IGuiOverlay overlay = entry.overlay();
                    if (gnetum$pre(entry, guiGraphics)) continue;
                    overlay.render(forgeGui, guiGraphics, partialTick, gnetum$getGuiAccessor().getScreenWidth(), gnetum$getGuiAccessor().getScreenHeight());
                    gnetum$post(entry, guiGraphics);
                }
            } catch (Exception e)
            {
                LOGGER.error("Error rendering overlay '{}'", entry.id(), e);
            }
        }
    }

    @Unique
    public boolean gnetum$pre(NamedGuiOverlay overlay, GuiGraphics guiGraphics) {
        return gnetum$postEvent(new RenderGuiOverlayEvent.Pre(this.minecraft.getWindow(), guiGraphics, this.minecraft.getFrameTime(), overlay));
    }

    @Unique
    public void gnetum$post(NamedGuiOverlay overlay, GuiGraphics guiGraphics) {
        gnetum$postEvent(new RenderGuiOverlayEvent.Post(this.minecraft.getWindow(), guiGraphics, this.minecraft.getFrameTime(), overlay));
    }

    @Unique
    public boolean gnetum$postEvent(Event event, Predicate<String> test) {
        return this.gnetum$postEvent(event, IEventListener::invoke, test);
    }

    @Unique
    public boolean gnetum$postEvent(Event event) {
        return gnetum$postEvent(event, IEventListener::invoke, null);
    }

    @Unique
    public boolean gnetum$postEvent(Event event, IEventBusInvokeDispatcher wrapper, Predicate<String> check) {
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
                                Gnetum.currentElement = modid;
                                Gnetum.currentElementType = ElementType.PRE;
                                if (modid == null || check.test(modid)) {
                                    wrapper.invoke(listener, event);
                                }
                            } else if (event instanceof RenderGuiEvent.Post) {
                                Gnetum.currentElement = modid;
                                Gnetum.currentElementType = ElementType.POST;
                                if (modid == null || check.test(modid)) {
                                    wrapper.invoke(listener, event);
                                }
                            }
                            else {
                                wrapper.invoke(listener, event);
                            }
                        }
                        else {
                            if (!Gnetum.rendering || listener instanceof EventPriority) { // do not cache listeners that are not ASMEventHandler
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
}
