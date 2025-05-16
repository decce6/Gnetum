package me.decce.gnetum;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.IEventExceptionHandler;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class EventBusHelper {
    private static final VarHandle shutdown;
    private static final VarHandle trackPhases;
    private static final VarHandle checkTypesOnDispatch;
    private static final VarHandle baseType;
    private static final VarHandle busID;
    private static final VarHandle exceptionHandler;

    static {
        Class<EventBus> clazz = EventBus.class;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(clazz, lookup);
            shutdown = privateLookup.findVarHandle(clazz, "shutdown", boolean.class);
            trackPhases = privateLookup.findVarHandle(clazz, "trackPhases", boolean.class);
            checkTypesOnDispatch = privateLookup.findVarHandle(clazz, "checkTypesOnDispatch", boolean.class);
            baseType = privateLookup.findVarHandle(clazz, "baseType", Class.class);
            busID = privateLookup.findVarHandle(clazz, "busID", int.class);
            exceptionHandler = privateLookup.findVarHandle(clazz, "exceptionHandler", IEventExceptionHandler.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isShutdown() {
        return (boolean)shutdown.get(MinecraftForge.EVENT_BUS);
    }

    public static boolean isTrackPhases() {
        return (boolean)trackPhases.get(MinecraftForge.EVENT_BUS);
    }

    public static boolean isCheckTypesOnDispatch() {
        return (boolean)checkTypesOnDispatch.get(MinecraftForge.EVENT_BUS);
    }

    public static Class<?> getBaseType() {
        return (Class<?>)baseType.get(MinecraftForge.EVENT_BUS);
    }

    public static int getBusID() {
        return (int)busID.get(MinecraftForge.EVENT_BUS);
    }

    public static IEventExceptionHandler getExceptionHandler() {
        return (IEventExceptionHandler)exceptionHandler.get(MinecraftForge.EVENT_BUS);
    }
}
