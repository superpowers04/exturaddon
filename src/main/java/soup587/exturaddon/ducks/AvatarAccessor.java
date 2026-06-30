package soup587.exturaddon.ducks;

import org.figuramc.figura.lua.api.entity.EntityAPI;
import org.jetbrains.annotations.Nullable;

public interface AvatarAccessor {
	void extura$interactEvent(String event, @Nullable EntityAPI entity);
	void extura$handleEntityEventEvent(byte id);
	void extura$preRenderEvent(float d);
	boolean extura$uploadEvent();
}
