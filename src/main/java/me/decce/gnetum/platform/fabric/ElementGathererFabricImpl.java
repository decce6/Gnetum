package me.decce.gnetum.platform.fabric;

//? fabric {
import me.decce.gnetum.CachedElement;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.VersionCompatUtil;
import me.decce.gnetum.compat.legacy_fapi.ArrayBackedEventAccessor;
import me.decce.gnetum.compat.xaerominimap.XaeroMinimapCompat;
import me.decce.gnetum.platform.ElementGatherer;

import java.util.Map;

//? >=1.21.10 && <26 {
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
//? }

//? >=1.21.10 {
import me.decce.gnetum.mixins.fabric.HudElementRegistryImplAccessor;
import net.fabricmc.fabric.impl.client.rendering.hud.HudElementRegistryImpl;
//?} else {
/*import me.decce.gnetum.versioned.HudHandler;
*///?}

@SuppressWarnings("UnstableApiUsage")
public class ElementGathererFabricImpl extends ElementGatherer {

	public void gatherImpl(Map<String, CachedElement> map) {

		//? >=1.21.10 {
		var first = HudElementRegistryImplAccessor.getFirst();
		var last = HudElementRegistryImplAccessor.getLast();

		gatherCompatPre(map);

		gather(first, map);
		HudElementRegistryImplAccessor.getVanillaElementIds().stream()
				.filter(root -> !root.equals(first.id()) && !root.equals(last.id()))
				.forEach(root -> {
					var name = VersionCompatUtil.stringValueOf(root);
					map.putIfAbsent(name, new CachedElement(name));
				});
		gather(last, map);
		//?} else {
		/*for (HudHandler.Element element : HudHandler.Element.values()) {
			map.putIfAbsent(element.getName(), element.newElement());
		}
		*///?}

		if (Gnetum.platform().isModLoaded("fabric-api")) {
			gatherLegacy(map);
		}
	}

	private void gatherCompatPre(Map<String, CachedElement> map) {
		//? xaerominimap {
		if (XaeroMinimapCompat.INSTALLED) {
			map.putIfAbsent(XaeroMinimapCompat.ELEMENT, new CachedElement(XaeroMinimapCompat.ELEMENT));
		}
		//? }
	}

	//? >=1.21.10 {
	public void gather(HudElementRegistryImpl.RootLayer root, Map<String, CachedElement> map) {
		root.layers().forEach(layer -> {
			var name = VersionCompatUtil.stringValueOf(layer.id());
			map.putIfAbsent(name, new CachedElement(name));
		});
	}
	//?}

	@SuppressWarnings("deprecation")
	private void gatherLegacy(Map<String, CachedElement> map) {
		//? if <26 {
		var event = HudRenderCallback.EVENT;
		HudRenderCallback[] handlers = (HudRenderCallback[]) ArrayBackedEventAccessor.HANDLERS.get(event);
		for (var callback : handlers) {
			var modid = Gnetum.platform().getModId(callback.getClass());
			map.putIfAbsent(modid, new CachedElement(modid));
		}
		//? }
	}

}
//?}
