package me.decce.gnetum.gui;

import me.decce.gnetum.CacheSetting;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.PerformanceAnalyzer;
import me.decce.gnetum.gui.widgets.ToggleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TextAndImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigScreen extends BaseScreen {
    private Button btnMoreOptions;
    private Button btnAnalysis;
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
        btnEnabled.setTooltipDelay(0);
        int w1 = analysis == null ? 120 : 120 - 20;
        btnMoreOptions = Button
                .builder(Component.translatable("gnetum.config.moreOptions"), b -> { Minecraft.getInstance().setScreen(new MoreOptionsScreen()); })
                .pos(width / 2 + 10, height / 2 - 90)
                .size(w1, 20)
                .build();
        if (analysis != null) {
            btnAnalysis = TextAndImageButton
                    .builder(Component.empty(), analysis.getIcon().icon(), b -> { Minecraft.getInstance().setScreen(new AnalysisScreen(analysis)); })
                    .textureSize(16, 16)
                    .usedTextureSize(16, 16)
                    .offset(0, 2)
                    .build();
            btnAnalysis.setPosition(width / 2 + 10 + 120 - 20, height / 2 - 90);
            btnAnalysis.setWidth(20);
            btnAnalysis.setHeight(20);
            btnAnalysis.active = !analysis.isOutdated();

            Component tooltip = Component.translatable(
                    analysis.isOutdated() ? "gnetum.config.analysis.outdated" :
                            (analysis.getMessages().isEmpty() ? "gnetum.config.analysis.good" : "gnetum.config.analysis.suboptimal")
            );
            btnAnalysis.setTooltip(Tooltip.create(tooltip));
            btnAnalysis.setTooltipDelay(0);

            this.addRenderableWidget(btnAnalysis);
        }

        int w = 260;
        int h = 20;
        int margin = 8;
        int x = width / 2 - 130;
        int y = height / 2 - h / 2 - margin - h;
        btnModdedPre = Button
                .builder(Component.translatable("gnetum.config.moddedPre"),
                        b -> { Minecraft.getInstance().setScreen(new ElementsScreen(sorted(Gnetum.config.mapModdedElementsPre), false)); })
                .pos(x, y)
                .size(w, h)
                .build();
        y += margin + h;
        btnVanilla = Button
                .builder(Component.translatable("gnetum.config.vanilla"),
                        b -> Minecraft.getInstance().setScreen(new ElementsScreen(sorted(Gnetum.config.mapVanillaElements), true)))
                .pos(x, y)
                .size(w, h)
                .build();
        y += margin + h;
        btnModdedPost = Button
                .builder(Component.translatable("gnetum.config.moddedPost"),
                        b -> Minecraft.getInstance().setScreen(new ElementsScreen(sorted(Gnetum.config.mapModdedElementsPost), false)))
                .pos(x, y)
                .size(w, h)
                .build();
        this.addRenderableWidget(btnEnabled);
        this.addRenderableWidget(btnMoreOptions);
        this.addRenderableWidget(btnModdedPre);
        this.addRenderableWidget(btnVanilla);
        this.addRenderableWidget(btnModdedPost);
    }

    private Map<String, CacheSetting> sorted(Map<String, CacheSetting> original) {
        return original.entrySet().stream().
                //TODO: should compare display string instead of key
                sorted(Map.Entry.comparingByKey()).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public void tick() {
        super.tick();
        btnMoreOptions.active = Gnetum.config.enabled.get();
        if (btnAnalysis != null) {
            btnAnalysis.active = Gnetum.config.enabled.get() && !analysis.isOutdated();
            btnAnalysis.setTooltipDelay(btnAnalysis.active ? 0 : Integer.MAX_VALUE);
            if (Gnetum.config.enabled.get() && analysis.isOutdated()) {
                btnAnalysis.setTooltipDelay(0); // tells the user that the analysis is outdated
            }
        }
        btnVanilla.active = Gnetum.config.enabled.get();
        btnModdedPre.active = Gnetum.config.enabled.get();
        btnModdedPost.active = Gnetum.config.enabled.get();

    }

    @Override
    public void close() {
        super.close();
    }
}
