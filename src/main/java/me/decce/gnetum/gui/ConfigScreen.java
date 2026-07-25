package me.decce.gnetum.gui;

import me.decce.gnetum.Gnetum;
import me.decce.gnetum.PerformanceAnalyzer;
import me.decce.gnetum.gui.widgets.ToggleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;

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
        // TODO fix analysis screen
        // this.analysis = analysis;
    }

    public ConfigScreen(Screen parent, PerformanceAnalyzer.Result analysis) {
        super(parent);
        // this.analysis = analysis;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected void rebuild() {
        super.rebuild();
        var btnEnabled = new ToggleButton(width / 2 - 130, height / 2 - 90, 120, 20, Gnetum.config.enabled, () -> I18n.get("gnetum.config.enabled") + ": %s");
        btnEnabled.setTooltip(() -> I18n.get("gnetum.config.enabled.tooltip"));
        int w1 = analysis == null ? 120 : 120 - 20;
        btnMoreOptions = new Button(width / 2 + 10, height / 2 - 90, w1, 20, new TranslatableComponent("gnetum.config.moreOptions"), b -> { Minecraft.getInstance().setScreen(new MoreOptionsScreen()); });
        if (analysis != null) {
            btnAnalysis = new ImageButton(16, 16, 16, 16, 0, 2, analysis.getIcon().icon(), b -> { Minecraft.getInstance().setScreen(new AnalysisScreen(analysis)); });
            btnAnalysis.x = width / 2 + 10 + 120 - 20;
            btnAnalysis.y = height / 2 - 90;
            btnAnalysis.setWidth(20);
            btnAnalysis.setHeight(20);
            btnAnalysis.active = !analysis.isOutdated();

            this.addRenderableWidget(btnAnalysis);
        }

        int w = 260;
        int h = 20;
        int margin = 8;
        int x = width / 2 - 130;
        int y = height / 2 - h / 2 - margin - h;
        btnModdedPre = new Button(x, y, w, h, new TranslatableComponent("gnetum.config.moddedPre"), b -> { Minecraft.getInstance().setScreen(new ElementsScreen(Gnetum.config.mapModdedElementsPre, false)); });
        y += margin + h;
        btnVanilla = new Button(x, y, w, h, new TranslatableComponent("gnetum.config.vanilla"), b -> Minecraft.getInstance().setScreen(new ElementsScreen(Gnetum.config.mapVanillaElements, true)));
        y += margin + h;
        btnModdedPost = new Button(x, y, w, h, new TranslatableComponent("gnetum.config.moddedPost"), b -> Minecraft.getInstance().setScreen(new ElementsScreen(Gnetum.config.mapModdedElementsPost, false)));
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
        btnMoreOptions.active = Gnetum.config.enabled.get();
        if (btnAnalysis != null) {
            btnAnalysis.active = Gnetum.config.enabled.get() && !analysis.isOutdated();
//            btnAnalysis.setTooltipDelay(btnAnalysis.active ? 0 : Integer.MAX_VALUE);
//            if (Gnetum.config.enabled.get() && analysis.isOutdated()) {
//                btnAnalysis.setTooltipDelay(0); // tells the user that the analysis is outdated
//            }
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
