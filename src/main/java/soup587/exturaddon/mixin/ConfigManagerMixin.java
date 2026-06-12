package soup587.exturaddon.mixin;

import com.google.gson.JsonObject;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import soup587.exturaddon.config.ConfigExtensions;
import org.figuramc.figura.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ConfigManager.class)
public class ConfigManagerMixin {

	@Inject(method = "init", at = @At("HEAD"))
	private static void init(CallbackInfo ci) {
		ConfigExtensions.init();
	}
}
