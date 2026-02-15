package me.decce.gnetum.util;

import me.decce.gnetum.Constants;
import me.decce.gnetum.Gnetum;
import net.minecraft.client.resources.language.I18n;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Beautifier {
	public static String beautify(String string) {
		if (string == null || Constants.UNKNOWN_ELEMENTS.equals(string)) {
			return I18n.get("gnetum.config.unknown_element");
		}
		String key1 = "gnetum.config.element." + string.replace(':', '.');
		if (I18n.exists(key1)) {
			return I18n.get(key1);
		}
		if (!string.contains(":")) {
			return toUpper(string);
		}
		String left = string.substring(0, string.indexOf(':'));
		String right = string.substring(string.indexOf(':') + 1);

		if (Gnetum.platform().isModLoaded(left)) {
			left = "[" + Gnetum.platform().getModName(left) + "]";
		}
		else left = "[" + left + "]";

		right = Character.toUpperCase(right.charAt(0)) + right.substring(1);
		int i;
		while ((i = right.indexOf('_')) != -1) {
			right = right.substring(0, i) + " " + Character.toUpperCase(right.charAt(i + 1)) + right.substring(i + 2);
		}
		return left + " " + right;
	}

	private static String toUpper(String string) {
		return Arrays.stream(string.split("_"))
				.map(s -> s.length() <= 2 || "and".equals(s) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1))
				.collect(Collectors.joining(" "));
	}
}
