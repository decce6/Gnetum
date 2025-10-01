package me.decce.gnetum;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.neoforged.bus.ConsumerEventHandler;
import net.neoforged.bus.SubscribeEventListener;
import net.neoforged.bus.api.EventListener;

public class EventListenerHelper {
    private static final ReferenceSet<EventListener> invalid = new ReferenceOpenHashSet<>(0);
    private static final Reference2ObjectMap<EventListener, String> mapModId = new Reference2ObjectOpenHashMap<>();

    public static String tryGetModId(EventListener listener) {
        if (mapModId.containsKey(listener)) {
            return mapModId.get(listener);
        }
        if (invalid.contains(listener)) {
            return null;
        }
        Class<?> clazz = tryGetClass(listener);
        if (clazz == null) {
            invalid.add(listener);
            return null;
        }
        var module = clazz.getModule();
        if (module.isNamed()) {
            var modid = module.getName();
            mapModId.put(listener, modid);
            return modid;
        }
        else {
            invalid.add(listener);
            return null;
        }
    }

    // Find out which class the given event handler will call
    private static Class<?> tryGetClass(EventListener listener) {
        if (listener instanceof SubscribeEventListener) {
            String name = listener.toString().substring("@SubscribeEvent: ".length());
            if (name.startsWith("class ")) {
                name = name.substring("class ".length());
                int index = name.indexOf(' ');
                if (index == -1) return null;
                name = name.substring(0, index);
                return tryClassForName(name);
            } else if (name.contains("@")) {
                name = name.substring(0, name.indexOf('@'));
                return tryClassForName(name);
            }
        }
        else if (listener instanceof ConsumerEventHandler) {
            String name = listener.toString();
            int index = name.indexOf("$$");
            if (index == -1) return null;
            name = name.substring(0, index);
            return tryClassForName(name);
        }
        return null;
    }

    private static Class<?> tryClassForName(String name) {
        try {
            return Class.forName(name);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
