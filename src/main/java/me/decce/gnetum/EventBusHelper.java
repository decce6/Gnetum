package me.decce.gnetum;

import net.neoforged.bus.EventBus;
import net.neoforged.bus.ListenerList;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventExceptionHandler;
import net.neoforged.neoforge.common.NeoForge;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;

public class EventBusHelper {
    private static final VarHandle shutdown;
    private static final VarHandle exceptionHandler;
    private static final MethodHandle doPostChecks;
    private static final MethodHandle getListenerList;

    static {
        Class<EventBus> clazz = EventBus.class;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(clazz, lookup);
            shutdown = privateLookup.findVarHandle(clazz, "shutdown", boolean.class);
            exceptionHandler = privateLookup.findVarHandle(clazz, "exceptionHandler", IEventExceptionHandler.class);
            MethodType doPostChecksType = MethodType.methodType(void.class, Event.class);
            doPostChecks = privateLookup.findVirtual(clazz, "doPostChecks", doPostChecksType);
            MethodType getListenerListType = MethodType.methodType(ListenerList.class, Class.class);
            getListenerList = privateLookup.findVirtual(clazz, "getListenerList", getListenerListType);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isShutdown() {
        return (boolean)shutdown.get(NeoForge.EVENT_BUS);
    }

    public static IEventExceptionHandler getExceptionHandler() {
        return (IEventExceptionHandler)exceptionHandler.get(NeoForge.EVENT_BUS);
    }

    public static void doPostChecks(Event event) {
        try {
            doPostChecks.invokeExact((EventBus)NeoForge.EVENT_BUS, event);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static ListenerList getListenerList(Class<?> eventType) {
        try {
            return (ListenerList) getListenerList.invokeExact((EventBus)NeoForge.EVENT_BUS, eventType);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
