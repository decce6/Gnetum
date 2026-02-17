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

		newMap.put(Constants.HAND_ELEMENT, Gnetum.HAND_ELEMENT);

		gatherImpl(newMap);

		newMap.put(Constants.UNKNOWN_ELEMENTS, Gnetum.UNKNOWN_ELEMENT);

		for (var entry : Gnetum.config.map.entrySet()) {
			if (newMap.containsKey(entry.getKey())) {
				newMap.put(entry.getKey(), entry.getValue());
			}
		}

		Gnetum.config.map = newMap;
	}

	protected abstract void gatherImpl(Map<String, CachedElement> map);
}
