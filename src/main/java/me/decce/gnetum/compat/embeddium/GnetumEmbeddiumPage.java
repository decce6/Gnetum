package me.decce.gnetum.compat.embeddium;

import com.google.common.collect.ImmutableList;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.GnetumConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.embeddedt.embeddium.api.options.OptionIdentifier;
import org.embeddedt.embeddium.api.options.control.ControlValueFormatter;
import org.embeddedt.embeddium.api.options.control.SliderControl;
import org.embeddedt.embeddium.api.options.control.TickBoxControl;
import org.embeddedt.embeddium.api.options.structure.OptionGroup;
import org.embeddedt.embeddium.api.options.structure.OptionImpact;
import org.embeddedt.embeddium.api.options.structure.OptionImpl;
import org.embeddedt.embeddium.api.options.structure.OptionPage;
import org.embeddedt.embeddium.api.options.structure.OptionStorage;

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

        var general = OptionGroup.createBuilder().setId(ResourceLocation.fromNamespaceAndPath("gnetum", "general"));

        var enabledOption = OptionImpl.createBuilder(boolean.class, STORAGE)
                .setId(ResourceLocation.fromNamespaceAndPath("gnetum", "enabled"))
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
                .setId(ResourceLocation.fromNamespaceAndPath("gnetum", "show_fps"))
                .setName(Component.translatable("gnetum.config.showFps"))
                .setTooltip(Component.translatable("gnetum.config.showFps.tooltip"))
                .setEnabledPredicate(enabledOption::getValue)
                .setControl(TickBoxControl::new)
                .setBinding((opts, value) -> {
                    if (Gnetum.config.showHudFps.get() != value) {
                        Gnetum.config.showHudFps.next();
                    }
                }, opts -> Gnetum.config.showHudFps.get())
                .build());
        general.add(OptionImpl.createBuilder(int.class, STORAGE)
                .setId(ResourceLocation.fromNamespaceAndPath("gnetum", "number_of_passes"))
                .setName(Component.translatable("gnetum.config.numberOfPasses"))
                .setTooltip(Component.translatable("gnetum.config.numberOfPasses.tooltip"))
                .setEnabledPredicate(enabledOption::getValue)
                .setImpact(OptionImpact.MEDIUM)
                .setControl(option -> new SliderControl(option, 2, 10, 1, ControlValueFormatter.number()))
                .setBinding((opts, value) -> Gnetum.config.numberOfPasses = value, opts -> Gnetum.config.numberOfPasses)
                .build());
        general.add(OptionImpl.createBuilder(int.class, STORAGE)
                .setId(ResourceLocation.fromNamespaceAndPath("gnetum", "max_fps"))
                .setName(Component.translatable("gnetum.config.maxFps"))
                .setTooltip(Component.translatable("gnetum.config.maxFps.tooltip"))
                .setEnabledPredicate(enabledOption::getValue)
                .setImpact(OptionImpact.MEDIUM)
                .setControl(option -> new SliderControl(option, 5, GnetumConfig.UNLIMITED_FPS, 5, i -> i == GnetumConfig.UNLIMITED_FPS ? Component.translatable("options.framerateLimit.max") : Component.translatable("options.framerate", i)))
                .setBinding((opts, value) -> Gnetum.config.maxFps = value, opts -> Gnetum.config.maxFps)
                .build());

        groups.add(general.build());

        return ImmutableList.copyOf(groups);
    }
}
