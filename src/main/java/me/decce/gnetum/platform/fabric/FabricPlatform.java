package me.decce.gnetum.platform.fabric;

//? fabric {

import me.decce.gnetum.Constants;
import me.decce.gnetum.platform.ElementGatherer;
import me.decce.gnetum.platform.fabric.ElementGathererFabricImpl;
import me.decce.gnetum.platform.Platform;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModOrigin;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FabricPlatform implements Platform {
	private final Map<Path, String> pathToModId = new HashMap<>();

	private static List<Path> determineLocation(String modid) {
		var container = net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(modid).orElse(null);
		if (container == null) {
			return List.of();
		}
		var origin = container.getOrigin();
		if (origin.getKind() == ModOrigin.Kind.PATH) {
			return origin.getPaths();
		}
		else if (origin.getKind() == ModOrigin.Kind.NESTED) {
			return determineLocation(origin.getParentModId());
		}
		return List.of();
	}

	public FabricPlatform() {
		for (var mod : FabricLoader.getInstance().getAllMods()) {
			var modid = mod.getMetadata().getId();
			var paths = determineLocation(modid);
			for (var path : paths) {
				pathToModId.put(path, modid);
			}
		}
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public String getModName(String modId) {
		return FabricLoader.getInstance().getModContainer(modId).map(mod -> mod.getMetadata().getName()).orElse(modId);
	}

	@Override
	public String getModId(Class<?> clazz) {
		var cs = clazz.getProtectionDomain().getCodeSource();
		if (cs == null) {
			return Constants.UNKNOWN_ELEMENTS;
		}
		try {
			return pathToModId.get(Paths.get(cs.getLocation().toURI()));
		} catch (URISyntaxException e) {
			return Constants.UNKNOWN_ELEMENTS;
		}
	}

	@Override
	public ElementGatherer elementGatherer() {
		return new ElementGathererFabricImpl();
	}
}
//?}
