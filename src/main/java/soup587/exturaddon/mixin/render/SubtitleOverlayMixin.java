package soup587.exturaddon.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SubtitleOverlay;
import org.figuramc.figura.lua.FiguraLuaRuntime;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import soup587.exturaddon.ducks.RendererAPIAccessor;
import soup587.exturaddon.utils.ExturaUtils;

@Mixin(SubtitleOverlay.class)
public class SubtitleOverlayMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z"))
	public boolean disableSubtitles(boolean original) {
		if (!original) return false;
		FiguraLuaRuntime lua = ExturaUtils.GetLuaRuntime(this.minecraft.getCameraEntity());
		return lua == null || ((RendererAPIAccessor) lua.renderer).shouldRenderSubtitles();
	}

}
