package me.decce.gnetum.gui;

import me.decce.gnetum.Gnetum;
import me.decce.gnetum.GnetumConfig;
import me.decce.gnetum.PerformanceAnalyzer;
import me.decce.gnetum.gui.widgets.IntSlider;
import me.decce.gnetum.gui.widgets.ToggleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.time.Duration;

public class MoreOptionsScreen extends BaseScreen {
    @Override
    public void rebuild() {
        super.rebuild();

        int w = 181;
        int h = 20;
        int margin = 5;
        int xl = width / 2 - 2 - w;
        int xr = width / 2 + 2;
        int y = height / 2 - h / 2 - margin - h - margin - h - 15;

        ToggleButton btnShowFps = new ToggleButton(xl, y, w, h, Gnetum.config.showHudFps, () -> I18n.get("gnetum.config.showFps") + ": %s");
        btnShowFps.setTooltip(Tooltip.create(Component.translatable("gnetum.config.showFps.tooltip")));
        btnShowFps.setTooltipDelay(Duration.ZERO);
        IntSlider sliderNumberOfPasses = new IntSlider(xr, y, w, h, () -> I18n.get("gnetum.config.numberOfPasses") + ": %s", 2, 10, Gnetum.config.numberOfPasses, true, i -> Gnetum.config.numberOfPasses = i);
        sliderNumberOfPasses.setTooltip(Tooltip.create(Component.translatable("gnetum.config.numberOfPasses.tooltip")));
        sliderNumberOfPasses.setTooltipDelay(Duration.ZERO);
        y += h + margin;
        IntSlider sliderMaxFps = new IntSlider(xl, y, w, h, () -> I18n.get("gnetum.config.maxFps") + ": %s fps", 5, GnetumConfig.UNLIMITED_FPS, Gnetum.config.maxFps, 5, true, i -> Gnetum.config.maxFps = i, i -> i == 125, () -> I18n.get("gnetum.config.maxFps") + ": Unlimited");
        sliderMaxFps.setTooltip(Tooltip.create(Component.translatable("gnetum.config.maxFps.tooltip")));
        sliderMaxFps.setTooltipDelay(Duration.ZERO);
        Button btnReset = Button.builder(Component.translatable("gnetum.config.reset"), b -> Minecraft.getInstance().setScreen(new ConfirmationScreen(() -> new ConfigScreen(null, PerformanceAnalyzer.latestAnalysisResult), () -> this, GnetumConfig::reset))).pos(xr, y).size(w, h).build();
        this.addRenderableWidget(btnShowFps);
        this.addRenderableWidget(sliderNumberOfPasses);
        this.addRenderableWidget(sliderMaxFps);
        this.addRenderableWidget(btnReset);

        super.addDoneButton();
    }
}
