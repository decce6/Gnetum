package me.decce.gnetum.gui;

import me.decce.gnetum.Gnetum;
import me.decce.gnetum.GnetumConfig;
import me.decce.gnetum.PerformanceAnalyzer;
import me.decce.gnetum.gui.widgets.IntSlider;
import me.decce.gnetum.gui.widgets.ToggleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class MoreOptionsScreen extends BaseScreen {
    private static final int BUTTON_WIDTH = 181;
    private static final int BUTTON_HEIGHT = 20;
    private static final int MARGIN = 5;
    private int count;
    private int y;

    @Override
    public void rebuild() {
        super.rebuild();

        y = height / 2 - BUTTON_HEIGHT / 2 - MARGIN - BUTTON_HEIGHT - MARGIN - BUTTON_HEIGHT - 15;

        ToggleButton btnShowFps = new ToggleButton(0, 0, 0, 0, Gnetum.config.showHudFps, () -> I18n.get("gnetum.config.showFps") + ": %s");
        btnShowFps.setTooltip(Tooltip.create(Component.translatable("gnetum.config.showFps.tooltip")));
        btnShowFps.setTooltipDelay(0);
        ToggleButton btnDownscale = new ToggleButton(0, 0, 0, 0, Gnetum.config.downscale, () -> I18n.get("gnetum.config.downscale") + ": %s");
        btnDownscale.setTooltip(Tooltip.create(Component.translatable("gnetum.config.downscale.tooltip")));
        btnDownscale.setTooltipDelay(0);
        IntSlider sliderNumberOfPasses = new IntSlider(0, 0, 0, 0, () -> I18n.get("gnetum.config.numberOfPasses") + ": %s", 2, 10, Gnetum.config.numberOfPasses, true, i -> Gnetum.config.numberOfPasses = i);
        sliderNumberOfPasses.setTooltip(Tooltip.create(Component.translatable("gnetum.config.numberOfPasses.tooltip")));
        sliderNumberOfPasses.setTooltipDelay(0);
        IntSlider sliderMaxFps = new IntSlider(0, 0, 0, 0, () -> I18n.get("gnetum.config.maxFps") + ": %s fps", 5, GnetumConfig.UNLIMITED_FPS, Gnetum.config.getRawMaxFps(), 5, true, i -> Gnetum.config.setMaxFps(i), i -> i == 125, () -> I18n.get("gnetum.config.maxFps") + ": Unlimited");
        sliderMaxFps.setTooltip(Tooltip.create(Component.translatable("gnetum.config.maxFps.tooltip")));
        sliderMaxFps.setTooltipDelay(0);
        IntSlider sliderScreenMaxFps = new IntSlider(0, 0, 0, 0, () -> I18n.get("gnetum.config.screenMaxFps") + ": %s fps", 5, 60, Gnetum.config.screenMaxFps, 5, true, i -> Gnetum.config.screenMaxFps = i);
        sliderScreenMaxFps.setTooltip(Tooltip.create(Component.translatable("gnetum.config.screenMaxFps.tooltip")));
        sliderScreenMaxFps.setTooltipDelay(0);
        Button btnReset = Button.builder(Component.translatable("gnetum.config.reset"), b -> Minecraft.getInstance().setScreen(new ConfirmationScreen(() -> new ConfigScreen(null, PerformanceAnalyzer.latestAnalysisResult), () -> this, GnetumConfig::reset))).build();
        this.add(btnShowFps);
        this.add(btnDownscale);
        this.add(sliderNumberOfPasses);
        this.add(sliderMaxFps);
        this.add(sliderScreenMaxFps);
        this.add(btnReset);

        super.addDoneButton();
    }


    private void add(AbstractWidget widget) {
        var x = count % 2 == 0 ? width / 2 - 2 - BUTTON_WIDTH : width / 2 + 2;
        widget.setX(x);
        widget.setY(y);
        widget.setWidth(BUTTON_WIDTH);
        widget.setHeight(BUTTON_HEIGHT);
        this.addRenderableWidget(widget);
        if (count % 2 == 1) {
            y += BUTTON_HEIGHT + MARGIN;
        }
        count++;
    }
}
