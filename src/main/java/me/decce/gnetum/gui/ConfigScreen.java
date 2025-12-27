package me.decce.gnetum.gui;

import me.decce.gnetum.CacheSetting;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.PerformanceAnalyzer;
import me.decce.gnetum.gui.widgets.IconButton;
import me.decce.gnetum.gui.widgets.ToggleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigScreen extends BaseScreen {
    private Button btnMoreOptions;
    private IconButton btnAnalysis;
    private Button btnModdedPre;
    private Button btnVanilla;
    private Button btnModdedPost;
    private PerformanceAnalyzer.Result analysis;

    public ConfigScreen() {
        this((PerformanceAnalyzer.Result)null);
    }

    public ConfigScreen(Screen parent) {
        super(parent);
    }

    public ConfigScreen(PerformanceAnalyzer.Result analysis) {
        super();
        this.analysis = analysis;
    }

    public ConfigScreen(Screen parent, PerformanceAnalyzer.Result analysis) {
        super(parent);
        this.analysis = analysis;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected void rebuild() {
        super.rebuild();
        var btnEnabled = new ToggleButton(width / 2 - 130, height / 2 - 90, 120, 20, Gnetum.config.enabled, () -> I18n.get("gnetum.config.enabled") + ": %s");
        btnEnabled.setTooltip(Tooltip.create(Component.translatable("gnetum.config.enabled.tooltip")));
        btnEnabled.setTooltipDelay(Duration.ZERO);
        int w1 = analysis == null ? 120 : 120 - 20;
        btnMoreOptions = Button
                .builder(Component.translatable("gnetum.config.moreOptions"), b -> { Minecraft.getInstance().setScreen(new MoreOptionsScreen()); })
                .pos(width / 2 + 10, height / 2 - 90)
                .size(w1, 20)
                .build();
        if (analysis != null) {
            btnAnalysis = new IconButton(width / 2 + 10 + 120 - 20, height / 2 - 90, 20, 20, Component.empty(), b -> { Minecraft.getInstance().setScreen(new AnalysisScreen(analysis)); });
            btnAnalysis.setIcon(analysis.getIcon().icon(), 2, 2, 16, 16);
            btnAnalysis.active = !analysis.isOutdated();

            Component tooltip = Component.translatable(
                    analysis.isOutdated() ? "gnetum.config.analysis.outdated" :
                            (analysis.getMessages().isEmpty() ? "gnetum.config.analysis.good" : "gnetum.config.analysis.suboptimal")
            );
            btnAnalysis.setTooltip(Tooltip.create(tooltip));
            btnAnalysis.setTooltipDelay(Duration.ZERO);

            this.addRenderableWidget(btnAnalysis);
        }

        int w = 260;
        int h = 20;
        int margin = 8;
        int x = width / 2 - 130;
        int y = height / 2 - h / 2 - margin - h;
        btnModdedPre = Button
                .builder(Component.translatable("gnetum.config.moddedPre"),
                        b -> { Minecraft.getInstance().setScreen(new ElementsScreen(Gnetum.config.mapModdedElementsPre, false)); })
                .pos(x, y)
                .size(w, h)
                .build();
        y += margin + h;
        btnVanilla = Button
                .builder(Component.translatable("gnetum.config.vanilla"),
                        b -> Minecraft.getInstance().setScreen(new ElementsScreen(Gnetum.config.mapVanillaElements, true)))
                .pos(x, y)
                .size(w, h)
                .build();
        y += margin + h;
        btnModdedPost = Button
                .builder(Component.translatable("gnetum.config.moddedPost"),
                        b -> Minecraft.getInstance().setScreen(new ElementsScreen(Gnetum.config.mapModdedElementsPost, false)))
                .pos(x, y)
                .size(w, h)
                .build();
        this.addRenderableWidget(btnEnabled);
        this.addRenderableWidget(btnMoreOptions);
        this.addRenderableWidget(btnModdedPre);
        this.addRenderableWidget(btnVanilla);
        this.addRenderableWidget(btnModdedPost);

        super.addDoneButton();
    }

    @Override
    public void tick() {
        super.tick();
        btnMoreOptions.active = Gnetum.config.isEnabled();
        if (btnAnalysis != null) {
            btnAnalysis.active = Gnetum.config.isEnabled() && !analysis.isOutdated();
            btnAnalysis.setTooltipDelay(btnAnalysis.active ? Duration.ZERO : Duration.ofMillis(Long.MAX_VALUE));
            if (Gnetum.config.isEnabled() && analysis.isOutdated()) {
                btnAnalysis.setTooltipDelay(Duration.ZERO); // tells the user that the analysis is outdated
            }
        }
        btnVanilla.active = Gnetum.config.isEnabled();
        btnModdedPre.active = Gnetum.config.isEnabled();
        btnModdedPost.active = Gnetum.config.isEnabled();

    }

    @Override
    public void close() {
        super.close();
    }
}
