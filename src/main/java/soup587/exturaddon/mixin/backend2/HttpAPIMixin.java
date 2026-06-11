package soup587.exturaddon.mixin.backend2;

import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.figuramc.figura.backend2.HttpAPI;
import org.figuramc.figura.config.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soup587.exturaddon.config.ConfigExtensions;
import soup587.exturaddon.lua.ExturaddonAPI;

import java.net.URI;

@Mixin(HttpAPI.class)
public class HttpAPIMixin {
	@Inject(at=@At("HEAD"),method = "getUri", cancellable = true)
	private static void getUri(String url, CallbackInfoReturnable<URI> cir) {
		cir.setReturnValue(URI.create(ExturaddonAPI.actuallyGetBackendAddress() + "/" + url));
    }
	@Inject(at=@At("HEAD"),method = "getBackendAddress", cancellable = true)
    private static void getBackendAddress(CallbackInfoReturnable<String> cir) {
		cir.setReturnValue(ExturaddonAPI.actuallyGetBackendAddress());
    }

}
