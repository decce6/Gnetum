package me.decce.gnetum.compat.embeddium;

import me.decce.gnetum.Gnetum;
import org.embeddedt.embeddium.api.OptionGUIConstructionEvent;

public class GnetumEmbeddiumCompat {
    public static void onSodiumPagesRegister(OptionGUIConstructionEvent e) {
        Gnetum.ensureInitialized();

        var pages = e.getPages();

        pages.add(new GnetumEmbeddiumPage());
    }
}
