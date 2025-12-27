package me.decce.gnetum.gui;

import com.google.common.collect.Maps;
import me.decce.gnetum.CacheSetting;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.gui.widgets.IntSlider;
import me.decce.gnetum.gui.widgets.ToggleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ElementsScreen extends BaseScreen {
    private final Map<String, CacheSetting> rawMap;
    private Map<String, CacheSetting> map;
    private boolean vanilla;
    private int page;
    private int pageCount;
    private String searchText = "";
    private EditBox searchBox;

    private Button btnPrevPage;
    private Button btnNextPage;

    public ElementsScreen(Map<String, CacheSetting> map, boolean vanilla) {
        super();
        this.vanilla = vanilla;
        this.rawMap = map;
        this.updateFilter();
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

        if (this.searchBox == null) {
            this.searchBox = new EditBox(Minecraft.getInstance().font, 0, 0, 0, 0, Component.literal(searchText));
            this.searchBox.setHint(Component.translatable("gnetum.config.searchHint"));
            this.searchBox.setResponder(s -> {
                if (!this.searchText.equals(s)) {
                    this.searchText = s;
                    this.updateFilter();
                }
            });
        }
        this.searchBox.setX(xlb + 2);
        this.searchBox.setY(y - h - margin);
        this.searchBox.setWidth(2 * wb + 2 * ws);
        this.searchBox.setHeight(h);
        this.addRenderableWidget(searchBox);

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
        super.addDoneButton();

        this.setFocused(searchBox);
    }

    @Override
    protected void renderTitle(GuiGraphics graphics) {

    }

    private void updateFilter() {
        var sorted = this.rawMap.entrySet().stream()
                .sorted((o1, o2) -> beautifyString(o1.getKey()).compareTo(beautifyString(o2.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        this.map = Maps.filterEntries(sorted, entry -> !entry.getValue().hidden && shouldDisplay(entry.getKey()));
        this.pageCount = this.map.size() % 10 == 0 ? this.map.size() / 10 : this.map.size() / 10 + 1;
        this.setPage(0);
    }

    private boolean shouldDisplay(String entry) {
        return StringUtils.containsIgnoreCase(entry, this.searchText) || StringUtils.containsIgnoreCase(beautifyString(entry), this.searchText);
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
            String key1 = "gnetum.config.element." + string.replace(':', '.');
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
            if (Gnetum.OTHER_MODS.equals(string)) return I18n.get("gnetum.config.element.gnetum_unknown");
            var file = ModList.get().getModFileById(string);
            return file == null ? string : file.getMods().get(0).getDisplayName();
        }
    }
}
