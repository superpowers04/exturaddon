package soup587.exturaddon.mixin.font;


import com.google.gson.JsonObject;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.figuramc.figura.font.EmojiMetadata;
import org.figuramc.figura.font.EmojiUnicodeLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded(value = "figura", minVersion = "0.1.0", maxVersion = "0.1.5")
@IfModAbsent(value = "extura")
@Mixin(EmojiUnicodeLookup.class)

public class EmojiUnicodeLookupMixin {
	@Inject(at=@At("HEAD"), method = "putMetadata(ILorg/figuramc/figura/font/EmojiMetadata;)V")
	void putMetadata(int codepoint, EmojiMetadata metadata, CallbackInfo ci){}
}
