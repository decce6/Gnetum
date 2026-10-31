package me.decce.gnetum.compat.legacy_fapi;

//? fabric {
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class ArrayBackedEventAccessor {
	public static VarHandle HANDLERS;
	static {
		try {
			var clazz = Class.forName("net.fabricmc.fabric.impl.base.event.ArrayBackedEvent");
			var lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());
			HANDLERS = lookup.findVarHandle(clazz,"handlers", Object[].class);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
//?}
