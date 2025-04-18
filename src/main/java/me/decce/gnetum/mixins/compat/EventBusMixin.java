package me.decce.gnetum.mixins.compat;

import com.llamalad7.mixinextras.sugar.Local;
import me.decce.gnetum.Gnetum;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Method;

@Mixin(value = EventBus.class, remap = false)
public class EventBusMixin {
    @Inject(method = "register(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/reflect/Method;Lnet/minecraftforge/fml/common/ModContainer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/eventhandler/ListenerList;register(ILnet/minecraftforge/fml/common/eventhandler/EventPriority;Lnet/minecraftforge/fml/common/eventhandler/IEventListener;)V"))
    private void register(Class<?> eventType, Object target, Method method, ModContainer owner, CallbackInfo ci, @Local IEventListener listener)
    {
        if (eventType.isAssignableFrom(RenderGameOverlayEvent.Pre.class)) {
            if (Gnetum.uncachedPreEventListeners.matchModId(owner.getModId())) {
                Gnetum.uncachedPreEventListeners.list.add(listener);
            }
        }
        else if (eventType.isAssignableFrom(RenderGameOverlayEvent.Post.class)) {
            if (Gnetum.uncachedPostEventListeners.matchModId(owner.getModId())) {
                Gnetum.uncachedPostEventListeners.list.add(listener);
            }
        }
    }
}
