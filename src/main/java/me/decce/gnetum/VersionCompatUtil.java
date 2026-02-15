package me.decce.gnetum;

import net.minecraft.resources.Identifier;

public class VersionCompatUtil {
	public static void profilerPush(String str) {
		//? if >1.21.1 {
		net.minecraft.util.profiling.Profiler.get().push(str);
		//?} else {
		/*net.minecraft.client.Minecraft.getInstance().getProfiler().push(str);
		 *///?}
	}

	public static void profilerPop() {
		//? if >1.21.1 {
		net.minecraft.util.profiling.Profiler.get().pop();
		//?} else {
		/*net.minecraft.client.Minecraft.getInstance().getProfiler().pop();
		 *///?}
	}

	public static void profilerPopPush(String str) {
		profilerPop();
		profilerPush(str);
	}

	public static String stringValueOf(Identifier identifier) {
		//TODO optimize string alloc & hash (cache concat result)
		return identifier.getNamespace().equals("minecraft")
				? identifier.getPath()
				: identifier.toString();
	}
}
