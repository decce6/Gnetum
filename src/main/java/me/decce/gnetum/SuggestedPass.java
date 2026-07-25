package me.decce.gnetum;

public class SuggestedPass {
    public static int get(String name) {
        int pass = switch (name) {
            case "Spyglass", "Helmet", "Frostbite", "Portal", "Hotbar" -> 1;
            case "Boss Health", Gnetum.STATUS_BAR, "Jump Bar", "Experience Bar" -> 2;
            default -> {
//                if (name.startsWith("minecraft:")) {
//                    yield 3;
//                }
//                var overlays = OverlayRegistry.orderedEntries();
//                for (int i = 0; i < overlays.size(); i++) {
//                    if (overlays.get(i).getDisplayName().equals(name)) {
//                        if (i == 0) yield 1;
//                        yield get(overlays.get(i - 1).getDisplayName());
//                    }
//                }
                yield 3;
            }
        };
        if (pass > Gnetum.config.numberOfPasses) pass = Gnetum.config.numberOfPasses;
        return pass;
    }
}
