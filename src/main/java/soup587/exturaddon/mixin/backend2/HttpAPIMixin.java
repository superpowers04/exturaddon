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

import java.net.URI;

@Mixin(HttpAPI.class)
public class HttpAPIMixin {
	@Inject(at=@At("HEAD"),method = "getUri", cancellable = true)
	private static void getUri(String url, CallbackInfoReturnable<URI> cir) {
		cir.setReturnValue(URI.create(actuallyGetBackendAddress() + "/" + url));
    }
	@Inject(at=@At("HEAD"),method = "getBackendAddress", cancellable = true)
    private static void getBackendAddress(CallbackInfoReturnable<String> cir) {
		cir.setReturnValue(actuallyGetBackendAddress());
    }
	@Unique
	private static String actuallyGetBackendAddress(){
		if(ConfigExtensions.BLOCK_CLOUD.value) return "http://127.0.0.1:9/api";
        if(ConfigExtensions.VANILLA_CLOUD.value){
            return "https://" + ServerAddress.parseString(Configs.SERVER_IP.defaultValue).getHost() + "/api";
        }
        String backendIP = ConfigExtensions.USE_MC_HOST_RESOLVER.value ? ServerAddress.parseString(Configs.SERVER_IP.value).getHost() : Configs.SERVER_IP.value;
        if(ConfigExtensions.USE_SECURE_CLOUD.value) return "https://" + backendIP + "/api";
        return "http://" + backendIP + "/api";
	}
}
