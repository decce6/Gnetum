package me.decce.gnetum;

import me.decce.gnetum.mixins.GuiAccessor;
import me.decce.gnetum.mixins.GuiLayerManagerAccessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.ConsumerEventHandler;
import net.neoforged.bus.SubscribeEventListener;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventListener;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;
import java.util.function.Predicate;

public class GuiHelper {
    public static GuiAccessor getGuiAccessor() {
        return (GuiAccessor)(Gui)(Object)Minecraft.getInstance().gui;
    }

    public static GuiLayerManagerAccessor getGuiLayerManagerAccessor() {
        return (GuiLayerManagerAccessor) getGuiAccessor().getLayerManager();
    }

    public static void renderLayers(List<GuiLayerManager.NamedLayer> list, GuiGraphics guiGraphics, DeltaTracker partialTick, Predicate<String> check) {
        guiGraphics.pose().pushPose();

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < list.size(); i++) {
            var layer = list.get(i);
            String id = layer.name().toString();
            if (check.test(id)) {
                if (Gnetum.rendering) {
                    Gnetum.currentElement = id;
                    Gnetum.currentElementType = ElementType.VANILLA;
                }
                if (!pre(layer, guiGraphics, partialTick)) {
                    layer.layer().render(guiGraphics, partialTick);
                    post(layer, guiGraphics, partialTick);
                }
                guiGraphics.pose().translate(0.0F, 0.0F, GuiLayerManager.Z_SEPARATION);
            }
        }

        guiGraphics.pose().popPose();
    }

    public static boolean pre(GuiLayerManager.NamedLayer layer, GuiGraphics guiGraphics, DeltaTracker partialTick) {
        return ((RenderGuiLayerEvent.Pre) postEvent(new RenderGuiLayerEvent.Pre(guiGraphics, partialTick, layer.name(), layer.layer()))).isCanceled();
    }

    public static void post(GuiLayerManager.NamedLayer layer, GuiGraphics guiGraphics, DeltaTracker partialTick) {
        postEvent(new RenderGuiLayerEvent.Post(guiGraphics, partialTick, layer.name(), layer.layer()));
    }

    public static <T extends Event> T postEvent(Event event) {
        return (T) postEvent(event, null);
    }

    public static <T extends Event> T postEvent(T event, Predicate<String> check) {
        if (EventBusHelper.isShutdown()) {
            return event;
        } else {
            EventBusHelper.doPostChecks(event);
            var listeners = EventBusHelper.getListenerList(event.getClass()).getListeners();

            int index = 0;

            try {
                for (; index < listeners.length; index++) {
                    EventListener listener = listeners[index];
                    String modid = null;
                    if (listener instanceof SubscribeEventListener sub) {
                        modid = EventListenerHelper.tryGetModId(sub);
                    }
                    else if (listener instanceof ConsumerEventHandler con) {
                        modid = EventListenerHelper.tryGetModId(con);
                    }
                    if (modid != null) {
                        if (event instanceof RenderGuiEvent.Pre) {
                            Gnetum.currentElement = modid;
                            Gnetum.currentElementType = ElementType.PRE;
                            if (check == null || check.test(modid)) {
                                listener.invoke(event);
                            }
                        } else if (event instanceof RenderGuiEvent.Post) {
                            Gnetum.currentElement = modid;
                            Gnetum.currentElementType = ElementType.POST;
                            if (check == null || check.test(modid)) {
                                listener.invoke(event);
                            }
                        }
                        else {
                            listener.invoke(event);
                        }
                    }
                    else {
                        // do not cache listeners that are not SubscribeEventListener
                        // Listeners that listen to RenderGuiEvent may trigger this path 2 times each frame (one in uncached, one in cached), and we only actually render them once
                        // Listeners that listen to RenderGuiLayerEvent only trigger this path 1 time
                        if (!Gnetum.rendering /*|| listener instanceof EventPriority*/|| event instanceof RenderGuiLayerEvent) {
                            listener.invoke(event);
                        }
                    }
                }
                return event;
            } catch (Throwable throwable) {
                EventBusHelper.getExceptionHandler().handleException(NeoForge.EVENT_BUS, event, listeners, index, throwable);
                throw throwable;
            }
        }
    }
}
