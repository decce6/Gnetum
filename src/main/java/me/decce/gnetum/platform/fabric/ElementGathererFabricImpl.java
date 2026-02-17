package me.decce.gnetum.platform.fabric;

//? fabric {
import me.decce.gnetum.CachedElement;
import me.decce.gnetum.Constants;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.VersionCompatUtil;
import me.decce.gnetum.compat.legacy_fapi.ArrayBackedEventAccessor;
import me.decce.gnetum.platform.ElementGatherer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

//? >=1.21.10 {
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.impl.client.rendering.hud.HudElementRegistryImpl;

import java.util.Map;
//?} else {
/*import me.decce.gnetum.versioned.HudHandler;
*///?}

public class ElementGathererFabricImpl extends ElementGatherer {

	public void gatherImpl(Map<String, CachedElement> map) {

		//? >=1.21.10 {
		var first = HudElementRegistryImpl.getRoot(VanillaHudElements.MISC_OVERLAYS);
		var last = HudElementRegistryImpl.getRoot(VanillaHudElements.SUBTITLES);

		gather(first, map);
		HudElementRegistryImpl.ROOT_ELEMENTS.values().stream()
				.filter(root -> root != first && root != last)
				.forEach(root -> {
					map.putIfAbsent(VersionCompatUtil.stringValueOf(root.id()), new CachedElement());
				});
		gather(last, map);
		//?} else {
		/*map.putIfAbsent(HudHandler.VANILLA_LAYERS, new CachedElement());
		*///?}

		if (Gnetum.platform().isModLoaded("fabric-api")) {
			gatherLegacy(map);
		}
	}

	//? >=1.21.10 {
	public void gather(HudElementRegistryImpl.RootLayer root, Map<String, CachedElement> map) {
		root.layers().forEach(layer -> {
			map.putIfAbsent(VersionCompatUtil.stringValueOf(layer.id()), new CachedElement());
		});
	}
	//?}

	@SuppressWarnings("deprecation")
	private void gatherLegacy(Map<String, CachedElement> map) {
		var event = HudRenderCallback.EVENT;
		HudRenderCallback[] handlers = (HudRenderCallback[]) ArrayBackedEventAccessor.HANDLERS.get(event);
		for (var callback : handlers) {
			var modid = Gnetum.platform().getModId(callback.getClass());
			map.putIfAbsent(modid, new CachedElement());
		}
	}

}
//?}
