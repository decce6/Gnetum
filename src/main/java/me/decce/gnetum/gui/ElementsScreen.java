package me.decce.gnetum.gui;

import com.google.common.collect.Maps;
import me.decce.gnetum.CacheSetting;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.gui.widgets.IntSlider;
import me.decce.gnetum.gui.widgets.ToggleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;

import java.util.Locale;
import java.util.Map;

public class ElementsScreen extends BaseScreen {
    private Map<String, CacheSetting> map;
    private boolean vanilla;
    private int page;
    private int pageCount;

    private Button btnPrevPage;
    private Button btnNextPage;

    public ElementsScreen(Map<String, CacheSetting> map, boolean vanilla) {
        super();
        this.map = Maps.filterValues(map, c -> !c.hidden);
        this.vanilla = vanilla;
        this.pageCount = map.size() % 10 == 0 ? map.size() / 10 : map.size() / 10 + 1;
    }

    @Override
    public void init() {
        this.setPage(0);
    }

    @Override
    protected void rebuild() {
        super.rebuild();

        int max = Math.min(map.size(), page * 10 + 10);

        int wb = 130;
        int ws = 50;
        int extrawb = width - wb * 2 - ws * 2 - 60;
        extrawb = Math.max(0, Math.min(60, extrawb));
        wb += extrawb;
        int h = 20;
        int margin = 5;
        int xlb = width / 2 - 2 - ws - wb;
        int xls = width / 2 - 2 - ws;
        int xrb = width / 2 + 2;
        int xrs = width / 2 + 2 + wb;
        int y = height / 2 - h / 2 - margin - h - margin - h - 15;
        int i = 0;
        boolean left = true;
        for (var entry : map.entrySet()) {
            if (i >= 10 * page && i < max) {
                var btn = new ToggleButton(left ? xlb : xrb, y, wb, h, (map.get(entry.getKey())).enabled, () -> beautifyString(entry.getKey()) + ": %s");
                btn.setTooltip(() -> {
                    String line1 = btn.getMessage().getString() + "\n(" + entry.getKey() + ")";
                    String line2 = I18n.get("gnetum.config.tooltip." + entry.getValue().enabled.value.name().toLowerCase(Locale.ROOT), entry.getValue().enabled.effectiveText());
                    return line1 + "\n\n" + line2;
                }
                );
                this.addRenderableWidget(btn);
                var slider = new IntSlider(left ? xls : xrs, y, ws, h, () -> I18n.get("gnetum.config.pass") + ": %s", 1, Gnetum.config.numberOfPasses, (entry.getValue()).pass, true, pass -> ((CacheSetting)entry.getValue()).pass = pass);
                this.addRenderableWidget(slider);
                if (!left) {
                    y += h + margin;
                }
                left = !left;
            }
            i++;
        }

        y = height / 2 + h / + margin + h + margin + h + margin;
        int lineHeight = Minecraft.getInstance().font.lineHeight;
        int currentPage = map.isEmpty() ? 0 : this.page + 1;
        String pageString = currentPage + " / " + pageCount;
        int stringWidth = Minecraft.getInstance().font.width(pageString);
        var page = new StringWidget(width / 2 - stringWidth / 2, y + lineHeight / 2, stringWidth, lineHeight, Component.literal(pageString), Minecraft.getInstance().font);
        btnPrevPage = Button
                .builder(Component.literal("<"), (btn) -> setPage(this.page - 1))
                .pos(xlb, y)
                .size(20, 20)
                .build();
        btnNextPage = Button
                .builder(Component.literal(">"), (btn) -> setPage(this.page + 1))
                .pos(xrs + ws - 20, y)
                .size(20, 20)
                .build();
        btnPrevPage.active = this.page > 0;
        btnNextPage.active = this.page < pageCount - 1;
        this.addRenderableWidget(page);
        this.addRenderableWidget(btnPrevPage);
        this.addRenderableWidget(btnNextPage);
    }

    private void setPage(int newPage) {
        this.page = newPage;
        this.rebuild();
    }

    @Override
    public void close() {
        super.close();
    }

    private String beautifyString(String string) {
        if (this.vanilla) {
            String key1 = string;
            if (!string.startsWith("gnetum.packedElement")) {
                key1 = "gnetum.config.element." + string.replace(':', '.');
            }
            if (I18n.exists(key1)) {
                return I18n.get(key1);
            }
            String left = string.substring(0, string.indexOf(':'));
            String right = string.substring(string.indexOf(':') + 1);

            var file = ModList.get().getModFileById(left);
            if (file != null) {
                left = "[" + file.getMods().get(0).getDisplayName() + "]";
            }
            else left = "[" + left + "]";

            right = Character.toUpperCase(right.charAt(0)) + right.substring(1);
            int i;
            while ((i = right.indexOf('_')) != -1) {
                right = right.substring(0, i) + " " + Character.toUpperCase(right.charAt(i + 1)) + right.substring(i + 2);
            }
            return left + " " + right;
        }
        else {
            var file = ModList.get().getModFileById(string);
            return file == null ? string : file.getMods().get(0).getDisplayName();
        }
    }
}
