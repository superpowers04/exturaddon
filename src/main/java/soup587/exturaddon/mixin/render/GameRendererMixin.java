package soup587.exturaddon.mixin.render;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
//? if > 1.20.2
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.GameRenderer;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.lua.FiguraLuaRuntime;
import org.figuramc.figura.permissions.Permissions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import soup587.exturaddon.ducks.AvatarAccessor;
import soup587.exturaddon.ducks.RendererAPIAccessor;
import soup587.exturaddon.utils.ExturaUtils;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow @Final private Minecraft minecraft;

	@IfModAbsent(value = "Figura",minVersion = "0.1.5")
	@Inject(method = "render", at = @At("HEAD"))
	//? if < 1.20.2 {
	/*private void preRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
	*///?} else
	private void preRender(DeltaTracker tickDelta, boolean tick, CallbackInfo ci) {
		Avatar avatar = AvatarManager.getAvatar(this.minecraft.getCameraEntity());
		if (avatar == null) return;
		avatar.customInstructions.get("exturaPreRender").reset(avatar.permissions.get(Permissions.RENDER_INST));

		AvatarManager.executeAll("exturaPreRender", renderedAvatar -> ((AvatarAccessor) renderedAvatar).extura$preRenderEvent(
				tickDelta
			//? if > 1.20.2 {
						.getGameTimeDeltaPartialTick(true)
			//? }
		));
	}

	// mixinextras my bloved - jess

	@Definition(id = "bobView", method = "Lnet/minecraft/client/Options;bobView()Lnet/minecraft/client/OptionInstance;")
	@Definition(id = "get", method = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;")
	@Expression("?.bobView().get()")
	@WrapOperation(method = "renderItemInHand", at = @At("MIXINEXTRAS:EXPRESSION"))
	private Object nopBobItem(OptionInstance instance, Operation<Object> original) {
		FiguraLuaRuntime lua = ExturaUtils.GetLuaRuntime(this.minecraft.getCameraEntity());
		if (lua != null && !((RendererAPIAccessor) lua.renderer).shouldDoItemViewbobbing()) {
			return false;
		}
		return original.call(instance);
	}

	// i have tried multiple methods to cancel the camera bobbing separately, but it's exceedingly resilient for some reason

}
