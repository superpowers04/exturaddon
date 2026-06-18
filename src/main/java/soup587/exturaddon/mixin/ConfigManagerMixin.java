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
import soup587.exturaddon.Exturaddon;


@Mixin(ConfigManager.class)
public class ConfigManagerMixin {

	@Inject(method = "init", at = @At("HEAD"))
	private static void init(CallbackInfo ci) {
		ConfigExtensions.init();
	}

	@Inject(method = "loadConfig", at = @At("TAIL"))
	private static void loadConfig(CallbackInfo ci) {
		Exturaddon.cachedPath = null;
	}
	@Inject(method = "saveConfig", at = @At("TAIL"))
	private static void saveConfig(CallbackInfo ci) {
		Exturaddon.cachedPath = null;
	}
	@Inject(method = "applyConfig", at = @At("TAIL"))
	private static void applyConfig(CallbackInfo ci) {
		Exturaddon.cachedPath = null;
	}
}
