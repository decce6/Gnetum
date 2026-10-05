package me.decce.gnetum.platform;

import me.decce.gnetum.CachedElement;
import me.decce.gnetum.Constants;
import me.decce.gnetum.Gnetum;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ElementGatherer {
	private static boolean gathered;

	public void gather() {
		if (gathered) {
			return;
		}
		gathered = true;

		var newMap = new LinkedHashMap<String, CachedElement>(Gnetum.config.map.size());

		newMap.put(Constants.HAND_ELEMENT, new CachedElement(Constants.HAND_ELEMENT));

		gatherImpl(newMap);
		//? >=1.21.10 {
		newMap.put(Constants.UNKNOWN_ELEMENTS, new CachedElement(Constants.UNKNOWN_ELEMENTS));
		newMap.put(Constants.UNKNOWN_LISTENERS, new CachedElement(Constants.UNKNOWN_LISTENERS));
		newMap.put(Constants.DEBUG_OVERLAY, new CachedElement(Constants.DEBUG_OVERLAY));
		//? }

		for (var entry : newMap.entrySet()) {
			if (Gnetum.config.map.containsKey(entry.getKey())) {
				var original = Gnetum.config.map.get(entry.getKey());
				entry.getValue().enabled = original.enabled;
			}
		}

		//? >=1.21.10 {
		newMap.get(Constants.UNKNOWN_LISTENERS).enabled.defaultValue = true;
		newMap.get(Constants.UNKNOWN_ELEMENTS).enabled.defaultValue = false;
		newMap.get(Constants.DEBUG_OVERLAY).enabled.defaultValue = false;
		//? }
		newMap.get(Constants.HAND_ELEMENT).enabled.defaultValue = false;

		if (newMap.containsKey("journeymap")) {
			// Disables caching for the in-game waypoint icons
			// This does not affect the caching for the minimap.
			newMap.get("journeymap").enabled.defaultValue = false;
		}

		Gnetum.config.map = newMap;
	}

	protected abstract void gatherImpl(Map<String, CachedElement> map);
}
