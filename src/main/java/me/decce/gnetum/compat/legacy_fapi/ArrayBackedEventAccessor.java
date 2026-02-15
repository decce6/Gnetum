package me.decce.gnetum.compat.legacy_fapi;

//? fabric {
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class ArrayBackedEventAccessor {
	public static Class<?> ARRAY_BACKED_EVENT ;
	public static VarHandle HANDLERS;
	static {
		try {
			ARRAY_BACKED_EVENT = Class.forName("net.fabricmc.fabric.impl.base.event.ArrayBackedEvent");
			var lookup = MethodHandles.privateLookupIn(ARRAY_BACKED_EVENT, MethodHandles.lookup());
			HANDLERS = lookup.findVarHandle(ARRAY_BACKED_EVENT,"handlers", Object[].class);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
//?}
