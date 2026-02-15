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
//?} else {
/*import me.decce.gnetum.versioned.HudHandler;
*///?}

public class ElementGathererFabricImpl implements ElementGatherer {
	private static boolean gathered;

	public void gather() {
		if (gathered) {
			return;
		}
		gathered = true;

		//? >=1.21.10 {
		var first = HudElementRegistryImpl.getRoot(VanillaHudElements.MISC_OVERLAYS);
		var last = HudElementRegistryImpl.getRoot(VanillaHudElements.SUBTITLES);

		gather(first);
		HudElementRegistryImpl.ROOT_ELEMENTS.values().stream()
				.filter(root -> root != first && root != last)
				.forEach(root -> {
					Gnetum.config.map.putIfAbsent(VersionCompatUtil.stringValueOf(root.id()), new CachedElement());
				});
		gather(last);
		//?} else {
		/*Gnetum.config.map.putIfAbsent(HudHandler.VANILLA_LAYERS, new CachedElement());
		*///?}

		if (Gnetum.platform().isModLoaded("fabric-api")) {
			gatherLegacy();
		}

		Gnetum.config.map.putIfAbsent(Constants.UNKNOWN_ELEMENTS, Gnetum.UNKNOWN_ELEMENT);
		Gnetum.UNKNOWN_ELEMENT = Gnetum.config.map.get(Constants.UNKNOWN_ELEMENTS);
	}

	//? >=1.21.10 {
	public void gather(HudElementRegistryImpl.RootLayer root) {
		root.layers().forEach(layer -> {
			Gnetum.config.map.putIfAbsent(VersionCompatUtil.stringValueOf(layer.id()), new CachedElement());
		});
	}
	//?}

	@SuppressWarnings("deprecation")
	private void gatherLegacy() {
		var event = HudRenderCallback.EVENT;
		HudRenderCallback[] handlers = (HudRenderCallback[]) ArrayBackedEventAccessor.HANDLERS.get(event);
		for (var callback : handlers) {
			var modid = Gnetum.platform().getModId(callback.getClass());
			Gnetum.config.map.putIfAbsent(modid, new CachedElement());
		}
	}

}
//?}
