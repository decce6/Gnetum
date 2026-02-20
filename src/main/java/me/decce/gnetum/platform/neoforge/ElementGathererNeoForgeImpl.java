package me.decce.gnetum.platform.neoforge;

//? neoforge {

/*import me.decce.gnetum.CachedElement;
import me.decce.gnetum.Constants;
import me.decce.gnetum.VersionCompatUtil;
import me.decce.gnetum.compat.neoforge.EventBusAccessor;
import me.decce.gnetum.mixins.neoforge.GuiAccessor;
import me.decce.gnetum.mixins.neoforge.GuiLayerManagerAccessor;
import me.decce.gnetum.platform.ElementGatherer;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.EventBus;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class ElementGathererNeoForgeImpl extends ElementGatherer {

	@Override
	public void gatherImpl(Map<String, CachedElement> map) {
		gatherEvent(RenderGuiEvent.Pre.class, map);

		var mc = Minecraft.getInstance();
		var gui = mc.gui;
		var layerManager = ((GuiAccessor)gui).getLayerManager();
		var layers = ((GuiLayerManagerAccessor) layerManager).getLayers();
		for (var layer : layers) {
			var name = VersionCompatUtil.stringValueOf(layer.name());
			map.putIfAbsent(name, new CachedElement(name));
		}

		gatherEvent(RenderGuiEvent.Post.class, map);
	}

	private <T extends Event> void gatherEvent(Class<T> event, Map<String, CachedElement> map) {
		var bus = (EventBus) NeoForge.EVENT_BUS;
		var listeners = EventBusAccessor.getListenerList(bus, event).getListeners();
		for (var listener : listeners) {
			var modid = EventListenerHelper.tryGetModId(listener).orElse(Constants.UNKNOWN_ELEMENTS);
			map.putIfAbsent(modid, new CachedElement(modid));
		}
	}
}
*///?}
