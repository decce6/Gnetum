package me.decce.gnetum.compat;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class UncachedEventListeners {
    private HashSet<String> uncachedModIds;

    public ArrayList<ArrayList<IEventListener>> priorities;
    public IEventListener[] list;

    public UncachedEventListeners(Collection<String> modids) {
        this.uncachedModIds = new HashSet<>(modids);

        int count = EventPriority.values().length;
        priorities = new ArrayList<ArrayList<IEventListener>>(count);
        for (int x = 0; x < count; x++)
        {
            priorities.add(new ArrayList<IEventListener>());
        }
    }

    public boolean matchModId(String modid) {
        return uncachedModIds.contains(modid);
    }

    public boolean matchEventListener(IEventListener listener) {
        if (listener instanceof EventPriority) {
            return false;
        }
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < list.length; i++) {
            if (listener == list[i]) {
                return true;
            }
        }
        return false;
    }

    public void trim() {
        this.uncachedModIds = null;
        this.priorities = null;
    }

    public void sort() {
        ArrayList<IEventListener> ret = new ArrayList<IEventListener>();
        for (EventPriority value : EventPriority.values())
        {
            List<IEventListener> listeners = getListeners(value);
            if (listeners.size() > 0)
            {
                ret.add(value); //Add the priority to notify the event of it's current phase.
                ret.addAll(listeners);
            }
        }
        list = ret.toArray(new IEventListener[ret.size()]);
    }

    private ArrayList<IEventListener> getListeners(EventPriority priority)
    {
        ArrayList<IEventListener> ret = new ArrayList<IEventListener>(priorities.get(priority.ordinal()));
        return ret;
    }
}
