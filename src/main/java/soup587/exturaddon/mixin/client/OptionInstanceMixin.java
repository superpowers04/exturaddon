package soup587.exturaddon.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.world.entity.Entity;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soup587.exturaddon.ducks.RendererAPIAccessor;

@Mixin(OptionInstance.class)
public class OptionInstanceMixin {

	// i have no idea if this is a "oh yeah flowery told me" or a "what the fuck are you doing this is terrible" - jess
	@Inject(at = @At("HEAD"), method = "get", cancellable = true)
	private void nopBob(CallbackInfoReturnable<Object> cir) {
		Minecraft instance = Minecraft.getInstance();
		if (instance.options == null || (Object) this != instance.options.bobView()) return;
		Avatar avatar = AvatarManager.getAvatar(instance.getCameraEntity());
		if (avatar != null && avatar.luaRuntime != null && !((RendererAPIAccessor) avatar.luaRuntime.renderer).shouldDoViewbobbing()) {
			cir.setReturnValue(false);
		}

	}
}
