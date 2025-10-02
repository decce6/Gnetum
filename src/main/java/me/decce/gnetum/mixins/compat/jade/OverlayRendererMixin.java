package me.decce.gnetum.mixins.compat.jade;

import me.decce.gnetum.compat.jade.JadeCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import snownee.jade.util.ClientProxy;

@Pseudo
@Mixin(value = ClientProxy.class, remap = false)
public class OverlayRendererMixin {
	@Redirect(method = "getBossBarRect", at = @At(value = "FIELD", target = "Lsnownee/jade/util/ClientProxy;bossbarShown:Z"))
	private static boolean getBossBarRect() {
		return JadeCompat.bossBarShown;
	}
}
