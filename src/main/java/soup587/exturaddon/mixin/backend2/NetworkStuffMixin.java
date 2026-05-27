package soup587.exturaddon.mixin.backend2;


import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.backend2.NetworkStuff;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import soup587.exturaddon.ducks.AvatarAccessor;

@Mixin(NetworkStuff.class)
public class NetworkStuffMixin {

	@Inject(at = @At("HEAD"), method="uploadAvatar", cancellable = true)
	private static void uploadAvatar(Avatar avatar, CallbackInfo ci){
		if(((AvatarAccessor)avatar).extura$uploadEvent()){
			ci.cancel();
		}
	}

}
