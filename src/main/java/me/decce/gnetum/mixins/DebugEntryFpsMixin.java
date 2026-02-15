package me.decce.gnetum.mixins;

import me.decce.gnetum.Gnetum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//? >=1.21.10 {
import net.minecraft.client.gui.components.debug.DebugEntryFps;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugEntryFps.class)
public class DebugEntryFpsMixin {
	@Inject(method = "display", at = @At("RETURN"))
	private void gnetum$displayHudFps(DebugScreenDisplayer displayer, Level level, LevelChunk levelChunk, LevelChunk levelChunk2, CallbackInfo ci) {
		if (level != null && Gnetum.config.isEnabled() && Gnetum.config.showHudFps.get()) {
			displayer.addPriorityLine(Gnetum.getFpsString());
		}
	}
}
//?} else {
/*import net.minecraft.client.gui.components.DebugScreenOverlay;
import java.util.List;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value = DebugScreenOverlay.class, priority = 5000)
public class DebugEntryFpsMixin {
	@Inject(method = "getGameInformation", at = @At("RETURN"))
	private void gnetum$displayHudFps(CallbackInfoReturnable<List<String>> cir) {
		cir.getReturnValue().add(2, Gnetum.getFpsString());
	}
}
*///?}

