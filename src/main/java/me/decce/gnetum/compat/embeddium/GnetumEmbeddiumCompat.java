package me.decce.gnetum.compat.embeddium;

import org.embeddedt.embeddium.api.OptionGUIConstructionEvent;

public class GnetumEmbeddiumCompat {
    public static void onSodiumPagesRegister(OptionGUIConstructionEvent e) {
        var pages = e.getPages();

        pages.add(new GnetumEmbeddiumPage());
    }
}
