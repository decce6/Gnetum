package me.decce.gnetum.compat;

import net.minecraftforge.fml.common.eventhandler.IEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class UncachedEventListeners {
    private HashSet<String> uncachedModIds;

    public ArrayList<IEventListener> list = new ArrayList<>();

    public UncachedEventListeners(Collection<String> modids) {
        this.uncachedModIds = new HashSet<>(modids);
    }

    public boolean matchModId(String modid) {
        return uncachedModIds.contains(modid);
    }

    public boolean matchEventListener(IEventListener listener) {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < list.size(); i++) {
            if (listener == list.get(i)) {
                return true;
            }
        }
        return false;
    }

    public void trim() {
        this.uncachedModIds = null;
        this.list.trimToSize();
    }
}
