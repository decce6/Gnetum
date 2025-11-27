package me.decce.gnetum.compat.embeddium;

import com.google.common.collect.ImmutableList;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.GnetumConfig;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.OptionStorage;
import net.minecraft.network.chat.Component;
import org.embeddedt.embeddium.client.gui.options.OptionIdentifier;

import java.util.ArrayList;
import java.util.List;

public class GnetumEmbeddiumPage extends OptionPage {
    public GnetumEmbeddiumPage() {
        super(OptionIdentifier.create("gnetum", "basic"), Component.literal("Gnetum"), groups());
    }

    public static final OptionStorage<?> STORAGE = new OptionStorage<>() {
        @Override
        public Object getData() {
            return new Object();
        }

        @Override
        public void save() {
            Gnetum.config.save();
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
                    if (Gnetum.config.enabled.get() != value) {
                        Gnetum.config.enabled.next();
                    }
                }, opts -> Gnetum.config.enabled.get())
                .build();
        general.add(enabledOption);
        general.add(OptionImpl.createBuilder(boolean.class, STORAGE)
                .setName(Component.translatable("gnetum.config.showFps"))
                .setTooltip(Component.translatable("gnetum.config.showFps.tooltip"))
                .setControl(TickBoxControl::new)
                .setBinding((opts, value) -> {
                    if (Gnetum.config.showHudFps.get() != value) {
                        Gnetum.config.showHudFps.next();
                    }
                }, opts -> Gnetum.config.showHudFps.get())
                .build());
        general.add(OptionImpl.createBuilder(int.class, STORAGE)
                .setName(Component.translatable("gnetum.config.numberOfPasses"))
                .setTooltip(Component.translatable("gnetum.config.numberOfPasses.tooltip"))
                .setImpact(OptionImpact.MEDIUM)
                .setControl(option -> new SliderControl(option, 2, 10, 1, ControlValueFormatter.number()))
                .setBinding((opts, value) -> Gnetum.config.numberOfPasses = value, opts -> Gnetum.config.numberOfPasses)
                .build());
        general.add(OptionImpl.createBuilder(int.class, STORAGE)
                .setName(Component.translatable("gnetum.config.maxFps"))
                .setTooltip(Component.translatable("gnetum.config.maxFps.tooltip"))
                .setImpact(OptionImpact.MEDIUM)
                .setControl(option -> new SliderControl(option, 5, GnetumConfig.UNLIMITED_FPS, 5, i -> i == GnetumConfig.UNLIMITED_FPS ? Component.translatable("options.framerateLimit.max") : Component.translatable("options.framerate", i)))
                .setBinding((opts, value) -> Gnetum.config.maxFps = value, opts -> Gnetum.config.maxFps)
                .build());

        groups.add(general.build());

        return ImmutableList.copyOf(groups);
    }
}
