package me.decce.gnetum.mixins.compat.jade;

import me.decce.gnetum.compat.jade.JadeCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "snownee.jade.util.ClientProxy", remap = false)
public class ClientProxyMixin {
	@Redirect(method = "getBossBarRect", at = @At(value = "FIELD", target = "Lsnownee/jade/util/ClientProxy;bossbarShown:Z"))
	private static boolean getBossBarRect() {
		return JadeCompat.bossBarShown;
	}
}
