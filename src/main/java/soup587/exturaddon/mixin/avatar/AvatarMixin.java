package soup587.exturaddon.mixin.avatar;

import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.entity.EntityType;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.lua.FiguraLuaRuntime;
import org.figuramc.figura.lua.api.entity.EntityAPI;
import org.figuramc.figura.model.rendering.EntityRenderMode;
import org.figuramc.figura.permissions.PermissionPack;
import org.figuramc.figura.permissions.Permissions;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.Varargs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import soup587.exturaddon.ducks.AvatarAccessor;

import java.util.Map;
import java.util.UUID;

@Mixin(Avatar.class)
public abstract class AvatarMixin implements AvatarAccessor {

	@Shadow public boolean loaded;

	@Shadow public FiguraLuaRuntime luaRuntime;

	@Shadow @Final public PermissionPack.PlayerPermissionPack permissions;

	@Shadow public EntityRenderMode renderMode;

	@Shadow
	@Final
	public Map<String, Avatar.Instructions> customInstructions;

	@Invoker("run")
	abstract Varargs exturaddon$invokeRun(Object toRun, Avatar.Instructions limit, Object... args);

	public Avatar.Instructions preRender, init, tick;

	@Inject(method = "<init>(Ljava/util/UUID;)V", at = @At("TAIL"))
	@IfModAbsent(value = "extura")
	private void constructor(UUID owner, CallbackInfo ci) {
		this.preRender = new Avatar.Instructions(permissions.get(Permissions.RENDER_INST));
		customInstructions.putIfAbsent("preRender", this.preRender);
	}


	@Unique
	private boolean isCancelled(Varargs args) {
		if (args == null)
			return false;
		int l = args.narg();
		int i = 0;
		while (++i <= l) {
			if (args.arg(i).isboolean() && args.arg(i).checkboolean())
				return true;
		}
		return false;
	}


	@Unique
	@IfModAbsent(value = "extura")
	public void extura$preRenderEvent(float delta) {
		if (loaded && luaRuntime != null && luaRuntime.getUser() != null)
			exturaddon$invokeRun("PRE_RENDER", preRender, delta, renderMode.name());
	}
	@Unique
	@IfModAbsent(value = "extura")
	public boolean extura$uploadEvent() { // String type, boolean backend, boolean fsb
		return isCancelled(exturaddon$invokeRun("UPLOAD", this.init));
	}

	@Unique
	@IfModAbsent(value = "extura")
	public void extura$handleEntityEventEvent(byte id) {
		if (loaded && luaRuntime != null && luaRuntime.getUser() != null)
			exturaddon$invokeRun("HANDLE_ENTITY_EVENT", this.tick, id);
	}
	@Unique
	@IfModAbsent(value = "extura")
	public void extura$interactEvent(String event, @Nullable EntityAPI entity) { 
		if (loaded && luaRuntime != null && luaRuntime.getUser() != null)
			exturaddon$invokeRun("INTERACT", this.tick, event, entity);
	}
}
