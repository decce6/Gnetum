package me.decce.gnetum.compat.neoforge;

//? neoforge {
/*import net.neoforged.bus.EventBus;
import net.neoforged.bus.ListenerList;
import net.neoforged.bus.api.IEventExceptionHandler;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;

@SuppressWarnings("UnstableApiUsage")
public class EventBusAccessor {
	public static final MethodHandle GET_LISTENER_LIST;
	public static final VarHandle EXCEPTION_HANDLER;

	static {
		var clazz = EventBus.class;
		try {
			var lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());
			GET_LISTENER_LIST = lookup.findVirtual(clazz, "getListenerList", MethodType.methodType(ListenerList.class, Class.class));
			EXCEPTION_HANDLER = lookup.findVarHandle(clazz, "exceptionHandler", IEventExceptionHandler.class);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static ListenerList getListenerList(EventBus bus, Class<?> eventType) {
		try {
			return (ListenerList) GET_LISTENER_LIST.invokeExact(bus, eventType);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static IEventExceptionHandler getExceptionHandler(EventBus bus) {
		return (IEventExceptionHandler) EXCEPTION_HANDLER.get(bus);
	}
}
*///?}
