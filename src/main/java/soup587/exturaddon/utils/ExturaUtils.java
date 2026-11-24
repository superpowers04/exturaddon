package soup587.exturaddon.utils;
import net.minecraft.world.entity.Entity;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.lua.FiguraLuaRuntime;
import org.jetbrains.annotations.Nullable;
import soup587.exturaddon.ducks.AvatarAccessor;

public class ExturaUtils {
	public static final FiguraLuaRuntime GetLuaRuntime(@Nullable Entity entity){
		return GetLuaRuntime(AvatarManager.getAvatar(entity));
	}
	public static final FiguraLuaRuntime GetLuaRuntime(@Nullable Avatar avatar){
		return avatar != null && avatar.luaRuntime != null ? avatar.luaRuntime : null;
	}
	public static final AvatarAccessor getAvatarAccessor(@Nullable Entity entity){
		return entity == null ? null : (AvatarAccessor) AvatarManager.getAvatar(entity);
	}
}
