package me.decce.gnetum.compat.sodium;

//? <=1.21.1 {
/*import com.google.common.collect.ImmutableList;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.GnetumConfig;
import me.decce.gnetum.util.AnyBooleanValue;
import me.decce.gnetum.util.Beautifier;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpact;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpl;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatter;
import net.caffeinemc.mods.sodium.client.gui.options.control.CyclingControl;
import net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl;
import net.caffeinemc.mods.sodium.client.gui.options.control.TickBoxControl;
import net.caffeinemc.mods.sodium.client.gui.options.storage.OptionStorage;
import net.minecraft.network.chat.Component;

import javax.sound.sampled.EnumControl;
import java.util.ArrayList;
import java.util.List;

public class LegacySodiumPage extends OptionPage {
	public LegacySodiumPage() {
		super(Component.literal("Gnetum"), groups());
	}

	private static OptionImpl<?, Boolean> enabledOption;

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

		enabledOption = OptionImpl.createBuilder(boolean.class, STORAGE)
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
				.setEnabled(enabledOption::getValue)
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
				.setEnabled(enabledOption::getValue)
				.setName(Component.translatable("gnetum.config.numberOfPasses"))
				.setTooltip(Component.translatable("gnetum.config.numberOfPasses.tooltip"))
				.setImpact(OptionImpact.MEDIUM)
				.setControl(option -> new SliderControl(option, 2, 10, 1, ControlValueFormatter.number()))
				.setBinding((opts, value) -> Gnetum.config.numberOfPasses = value, opts -> Gnetum.config.numberOfPasses)
				.build());
		general.add(OptionImpl.createBuilder(int.class, STORAGE)
				.setEnabled(enabledOption::getValue)
				.setName(Component.translatable("gnetum.config.maxFps"))
				.setTooltip(Component.translatable("gnetum.config.maxFps.tooltip"))
				.setImpact(OptionImpact.MEDIUM)
				.setControl(option -> new SliderControl(option, 5, GnetumConfig.UNLIMITED_FPS, 5, i -> i == GnetumConfig.UNLIMITED_FPS ? Component.translatable("options.framerateLimit.max") : Component.translatable("options.framerate", i)))
				.setBinding((opts, value) -> Gnetum.config.maxFps = value, opts -> Gnetum.config.maxFps)
				.build());

		groups.add(general.build());
		groups.add(elements());

		return ImmutableList.copyOf(groups);
	}

	private static OptionGroup elements() {
		var group = OptionGroup.createBuilder();

		int i = 0;
		for (var element : Gnetum.config.map.entrySet()) {
			var name = Beautifier.beautify(element.getKey());
			var tooltip = Component.literal(name).append("\n\n").append(Component.translatable("gnetum.config.element.tooltip"));
			group.add(OptionImpl.createBuilder(AnyBooleanValue.class, STORAGE)
					.setName(Component.literal(name))
					.setTooltip(tooltip)
					//.setElementNameProvider(AnyBooleanValue::text)
					//.setDefaultValue(AnyBooleanValue.AUTO)
					.setEnabled(enabledOption::getValue)
					.setBinding((opts, v) -> element.getValue().enabled.value = v, (opts) -> element.getValue().enabled.value)
					.setControl(opts -> new CyclingControl<>(opts, AnyBooleanValue.class))
					.setImpact(OptionImpact.VARIES).build());
		}
		return group.build();
	}
}
*///?}
