package me.decce.gnetum.compat.sodium;

//? >=1.21.10 {
import me.decce.gnetum.Constants;
import me.decce.gnetum.Gnetum;
import me.decce.gnetum.util.AnyBooleanValue;
import me.decce.gnetum.util.Beautifier;
import me.decce.gnetum.util.TwoStateBoolean;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.StorageEventHandler;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SodiumEntrypoint implements ConfigEntryPoint {
	private final StorageEventHandler handler = () -> Gnetum.config.save();

	private final Identifier enabledOption = Identifier.parse("gnetum:enabled");

	@Override
	public void registerConfigLate(ConfigBuilder builder) {
		Gnetum.platform().elementGatherer().gather();

		var optionsBuilder = builder
				.registerOwnModOptions()
				.setVersion(Constants.MOD_VERSION_SHORT)
				.setColorTheme(builder.createColorTheme().setBaseThemeRGB(2991601))
				.setNonTintedIcon(Identifier.parse("gnetum:gnetum.png"));
		optionsBuilder.addPage(builder.createOptionPage()
			.setName(Component.literal("General"))
			.addOptionGroup(builder.createOptionGroup()
				.addOption(builder.createBooleanOption(enabledOption)
					.setName(Component.translatable("gnetum.config.enabled"))
					.setTooltip(Component.translatable("gnetum.config.enabled.tooltip"))
					.setStorageHandler(this.handler)
					.setBinding(consume(Gnetum.config.enabled), supply(Gnetum.config.enabled))
					.setDefaultValue(true)
					.setImpact(OptionImpact.HIGH)
				)
				.addOption(builder.createBooleanOption(Identifier.parse("gnetum:show_fps"))
					.setName(Component.translatable("gnetum.config.showFps"))
					.setTooltip(Component.translatable("gnetum.config.showFps.tooltip"))
					.setStorageHandler(this.handler)
					.setEnabledProvider(cs -> cs.readBooleanOption(enabledOption), enabledOption)
					.setBinding(consume(Gnetum.config.showHudFps), supply(Gnetum.config.showHudFps))
					.setDefaultValue(true)
				)
				.addOption(builder.createBooleanOption(Identifier.parse("gnetum:downscale"))
					.setName(Component.translatable("gnetum.config.downscale"))
					.setTooltip(Component.translatable("gnetum.config.downscale.tooltip"))
					.setStorageHandler(this.handler)
					.setImpact(OptionImpact.LOW)
					.setEnabledProvider(cs -> cs.readBooleanOption(enabledOption), enabledOption)
					.setBinding(consume(Gnetum.config.downscale), supply(Gnetum.config.downscale))
					.setDefaultValue(false)
				)
				.addOption(builder.createIntegerOption(Identifier.parse("gnetum:pass"))
					.setName(Component.translatable("gnetum.config.numberOfPasses"))
					.setTooltip(Component.translatable("gnetum.config.numberOfPasses.tooltip"))
					.setStorageHandler(this.handler)
					.setValueFormatter(v -> Component.literal(v + " passes"))
					.setRange(2, 10, 1)
					.setDefaultValue(3)
					.setEnabledProvider(cs -> cs.readBooleanOption(enabledOption), enabledOption)
					.setBinding((i) -> Gnetum.config.numberOfPasses = i, () -> Gnetum.config.numberOfPasses)
					.setImpact(OptionImpact.MEDIUM)
				)
				.addOption(builder.createIntegerOption(Identifier.parse("gnetum:max_fps"))
					.setName(Component.translatable("gnetum.config.maxFps"))
					.setTooltip(Component.translatable("gnetum.config.maxFps.tooltip"))
					.setStorageHandler(this.handler)
					.setRange(5, Constants.UNLIMITED_FPS, 5)
					.setValueFormatter(v -> v == Constants.UNLIMITED_FPS ? Component.translatable("options.framerateLimit.max") : Component.translatable("options.framerate", v))
					.setDefaultValue(60)
					.setEnabledProvider(cs -> cs.readBooleanOption(enabledOption), enabledOption)
					.setBinding((i) -> Gnetum.config.setMaxFps(i), () -> Gnetum.config.getRawMaxFps())
					.setImpact(OptionImpact.MEDIUM)
				)
				.addOption(builder.createIntegerOption(Identifier.parse("gnetum:screen_max_fps"))
					.setName(Component.translatable("gnetum.config.screenMaxFps"))
					.setTooltip(Component.translatable("gnetum.config.screenMaxFps.tooltip"))
					.setStorageHandler(this.handler)
					.setRange(5, Constants.SCREEN_UNLIMITED_FPS, 5)
					.setValueFormatter(v -> v == Constants.SCREEN_UNLIMITED_FPS ? Component.translatable("options.framerateLimit.max") : Component.translatable("options.framerate", v))
					.setDefaultValue(20)
					.setEnabledProvider(cs -> cs.readBooleanOption(enabledOption), enabledOption)
					.setBinding((i) -> Gnetum.config.screenMaxFps = i, () -> Gnetum.config.screenMaxFps)
					.setImpact(OptionImpact.MEDIUM)
				)
			)
		)
		.addPage(builder.createOptionPage().setName(Component.literal("Elements")).addOptionGroup(addElements(builder, builder.createOptionGroup())));
	}

	private OptionGroupBuilder addElements(ConfigBuilder builder,  OptionGroupBuilder group) {
		int i = 0;
		for (var element : Gnetum.config.map.entrySet()) {
			var name = Beautifier.beautify(element.getKey());
			var tooltip = Component.literal(name).append("\n\n").append(Component.translatable("gnetum.config.element.tooltip"));
			group.addOption(builder
					.createEnumOption(Identifier.parse("gnetum:element" + i++), AnyBooleanValue.class)
					.setName(Component.literal(name))
					.setTooltip(tooltip)
					.setStorageHandler(handler)
					.setElementNameProvider(AnyBooleanValue::text)
					.setDefaultValue(AnyBooleanValue.AUTO)
					.setEnabledProvider(cs -> cs.readBooleanOption(enabledOption), enabledOption)
					.setBinding(v -> element.getValue().enabled.value = v, () -> element.getValue().enabled.value)
					.setImpact(OptionImpact.VARIES));
		}
		return group;
	}

	private Consumer<Boolean> consume(TwoStateBoolean bool) {
		return (bl) -> {
			if (bl != bool.get()) {
				bool.next();
			}
		};
	}

	private Supplier<Boolean> supply(TwoStateBoolean bool) {
		return bool::get;
	}
}
//?}
