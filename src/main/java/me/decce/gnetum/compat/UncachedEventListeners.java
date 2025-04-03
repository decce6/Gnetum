package me.decce.gnetum.compat;

import net.minecraftforge.fml.common.eventhandler.IEventListener;

import java.util.ArrayList;

public class UncachedEventListeners {
    private static final String[] uncachedModIds = new String[] {
            "quark", //for its Better Nausea feature
            "customnausea"
    };

    public static ArrayList<IEventListener> list = new ArrayList<>();

    public static boolean matchModId(String modid) {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < uncachedModIds.length; i++) {
            if (modid.equals(uncachedModIds[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchEventListener(IEventListener listener) {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < list.size(); i++) {
            if (listener == list.get(i)) {
                return true;
            }
        }
        return false;
    }
}
