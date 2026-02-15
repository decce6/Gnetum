package me.decce.gnetum.platform.neoforge;

//? neoforge {
/*import me.decce.gnetum.CachedElement;
import me.decce.gnetum.Constants;
import me.decce.gnetum.Gnetum;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class ElementGathererNeoForgeImpl implements ElementGatherer {
	private static boolean gathered;
	private final Set<String> existing = new HashSet<>();

	@Override
	public void gather() {
		if (gathered) {
			return;
		}
		gathered = true;

		gatherEvent(RenderGuiEvent.Pre.class);

		var mc = Minecraft.getInstance();
		var gui = mc.gui;
		var layerManager = ((GuiAccessor)gui).getLayerManager();
		var layers = ((GuiLayerManagerAccessor) layerManager).getLayers();
		for (var layer : layers) {
			existing.add(VersionCompatUtil.stringValueOf(layer.name()));
			Gnetum.config.map.putIfAbsent(VersionCompatUtil.stringValueOf(layer.name()), new CachedElement());
		}

		gatherEvent(RenderGuiEvent.Post.class);

		existing.add(Constants.UNKNOWN_ELEMENTS);
		Gnetum.config.map.putIfAbsent(Constants.UNKNOWN_ELEMENTS, Gnetum.UNKNOWN_ELEMENT);
		Gnetum.UNKNOWN_ELEMENT = Gnetum.config.map.get(Constants.UNKNOWN_ELEMENTS);

		cleanup();
	}

	private void cleanup() {
		Gnetum.config.map.keySet().removeIf(key -> !existing.contains(key));
	}

	private <T extends Event> void gatherEvent(Class<T> event) {
		var bus = (EventBus) NeoForge.EVENT_BUS;
		var listeners = EventBusAccessor.getListenerList(bus, event).getListeners();
		for (var listener : listeners) {
			var modid = EventListenerHelper.tryGetModId(listener).orElse(Constants.UNKNOWN_ELEMENTS);
			existing.add(modid);
			Gnetum.config.map.putIfAbsent(modid, new CachedElement());
		}
	}
}
*///?}
