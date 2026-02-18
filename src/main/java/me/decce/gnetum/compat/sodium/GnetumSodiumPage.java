package me.decce.gnetum.compat.sodium;

import com.google.common.collect.ImmutableList;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.GnetumConfig;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpact;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpl;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatter;
import net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl;
import net.caffeinemc.mods.sodium.client.gui.options.control.TickBoxControl;
import net.caffeinemc.mods.sodium.client.gui.options.storage.OptionStorage;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class GnetumSodiumPage extends OptionPage {
    public GnetumSodiumPage() {
        super(Component.literal("Gnetum"), groups());
    }

    public static final OptionStorage<?> STORAGE = new OptionStorage<>() {
        @Override
        public Object getData() {
            return new Object();
        }

        @Override
        public void save() {
            Gnetum.getConfig().save();
        }
    };

    private static ImmutableList<OptionGroup> groups() {
        List<OptionGroup> groups = new ArrayList<>();

        var general = OptionGroup.createBuilder();

        var enabledOption = OptionImpl.createBuilder(boolean.class, STORAGE)
            .setName(Component.translatable("gnetum.config.enabled"))
            .setTooltip(Component.translatable("gnetum.config.enabled.tooltip"))
            .setImpact(OptionImpact.HIGH)
            .setControl(TickBoxControl::new)
            .setBinding((opts, value) -> {
                if (Gnetum.getConfig().enabled.get() != value) {
                    Gnetum.getConfig().enabled.next();
                }
            }, opts -> Gnetum.getConfig().enabled.get())
            .build();
        general.add(enabledOption);
        general.add(OptionImpl.createBuilder(boolean.class, STORAGE)
                .setEnabled(enabledOption::getValue)
                .setName(Component.translatable("gnetum.config.showFps"))
                .setTooltip(Component.translatable("gnetum.config.showFps.tooltip"))
                .setControl(TickBoxControl::new)
                .setBinding((opts, value) -> {
                    if (Gnetum.getConfig().showHudFps.get() != value) {
                        Gnetum.getConfig().showHudFps.next();
                    }
                }, opts -> Gnetum.getConfig().showHudFps.get())
                .build());
        general.add(OptionImpl.createBuilder(boolean.class, STORAGE)
                .setName(Component.translatable("gnetum.config.downscale"))
                .setTooltip(Component.translatable("gnetum.config.downscale.tooltip"))
                .setImpact(OptionImpact.LOW)
                .setControl(TickBoxControl::new)
                .setBinding((opts, value) -> {
                    if (Gnetum.getConfig().downscale.get() != value) {
                        Gnetum.getConfig().downscale.next();
                    }
                }, opts -> Gnetum.getConfig().downscale.get())
                .build());
        general.add(OptionImpl.createBuilder(int.class, STORAGE)
                .setEnabled(enabledOption::getValue)
                .setName(Component.translatable("gnetum.config.numberOfPasses"))
                .setTooltip(Component.translatable("gnetum.config.numberOfPasses.tooltip"))
                .setImpact(OptionImpact.MEDIUM)
                .setControl(option -> new SliderControl(option, 2, 10, 1, ControlValueFormatter.number()))
                .setBinding((opts, value) -> Gnetum.getConfig().numberOfPasses = value, opts -> Gnetum.getConfig().numberOfPasses)
                .build());
        general.add(OptionImpl.createBuilder(int.class, STORAGE)
                .setEnabled(enabledOption::getValue)
                .setName(Component.translatable("gnetum.config.maxFps"))
                .setTooltip(Component.translatable("gnetum.config.maxFps.tooltip"))
                .setImpact(OptionImpact.MEDIUM)
                .setControl(option -> new SliderControl(option, 5, GnetumConfig.UNLIMITED_FPS, 5, i -> i == GnetumConfig.UNLIMITED_FPS ? Component.translatable("options.framerateLimit.max") : Component.translatable("options.framerate", i)))
                .setBinding((opts, value) -> Gnetum.getConfig().setMaxFps(value), opts -> Gnetum.getConfig().getRawMaxFps())
                .build());
        general.add(OptionImpl.createBuilder(int.class, STORAGE)
                .setEnabled(enabledOption::getValue)
                .setName(Component.translatable("gnetum.config.screenMaxFps"))
                .setTooltip(Component.translatable("gnetum.config.screenMaxFps.tooltip"))
                .setImpact(OptionImpact.MEDIUM)
                .setControl(option -> new SliderControl(option, 5, GnetumConfig.SCREEN_UNLIMITED_FPS, 5, i -> i == GnetumConfig.SCREEN_UNLIMITED_FPS ? Component.translatable("options.framerateLimit.max") : Component.translatable("options.framerate", i)))
                .setBinding((opts, value) -> Gnetum.getConfig().screenMaxFps = value, opts -> Gnetum.getConfig().screenMaxFps)
                .build());

        groups.add(general.build());

        return ImmutableList.copyOf(groups);
    }
}
