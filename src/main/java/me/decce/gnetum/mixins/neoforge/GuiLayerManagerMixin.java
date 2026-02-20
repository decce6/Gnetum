package me.decce.gnetum.mixins.neoforge;

//? neoforge {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.decce.gnetum.Constants;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.compat.neoforge.EventBusAccessor;
import me.decce.gnetum.platform.neoforge.EventListenerHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.EventBus;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = GuiLayerManager.class, remap = false)
public class GuiLayerManagerMixin {
	@Shadow
	@Final
	private List<GuiLayerManager.NamedLayer> layers;

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/neoforged/bus/api/IEventBus;post(Lnet/neoforged/bus/api/Event;)Lnet/neoforged/bus/api/Event;"))
	private <T extends Event> T gnetum$render$pre(IEventBus instance, T event, Operation<T> original) {
		if (!Gnetum.config.isEnabled()) {
			return original.call(instance, event);
		}
		var bus = (EventBus) NeoForge.EVENT_BUS;
		var listeners = EventBusAccessor.getListenerList(bus, event.getClass()).getListeners();
		return gnetum$post(event, listeners);
	}

	@Inject(method = "renderInner", at = @At("HEAD"), cancellable = true)
	private void gnetum$renderInner(GuiGraphics guiGraphics, DeltaTracker partialTick, CallbackInfo ci) {
		if (!Gnetum.config.isEnabled()) {
			return;
		}
		ci.cancel();
		for (var layer : this.layers) {
			var element = Gnetum.getElement(layer.name());
			if (element.shouldRender()) {
				element.begin();
				if (!NeoForge.EVENT_BUS.post(new RenderGuiLayerEvent.Pre(guiGraphics, partialTick, layer.name(), layer.layer())).isCanceled()) {
					layer.layer().render(guiGraphics, partialTick);
					NeoForge.EVENT_BUS.post(new RenderGuiLayerEvent.Post(guiGraphics, partialTick, layer.name(), layer.layer()));
				}
				element.end();
			}
		}
	}

	@Unique
	private <T extends Event> T gnetum$post(T event, EventListener[] listeners) {
		int index = 0;
		try {
			for (; index < listeners.length; index++) {
				var listener = listeners[index];
				var modid = EventListenerHelper.tryGetModId(listener).orElse(Constants.UNKNOWN_ELEMENTS);
				var element = Gnetum.getElement(modid);
				if (element.shouldRender()) {
					element.begin();
					listeners[index].invoke(event);
					element.end();
				}
			}
		} catch (Throwable throwable) {
			EventBusAccessor.getExceptionHandler((EventBus) NeoForge.EVENT_BUS).handleException(NeoForge.EVENT_BUS, event, listeners, index, throwable);
			throw throwable;
		}
		return event;
	}
}
*///?}
