package soup587.exturaddon.ducks;

import org.figuramc.figura.lua.api.event.LuaEvent;

public interface EventsAPIAccessor {
	LuaEvent extura$getPreRenderEvent();
	LuaEvent extura$getHandleEntityEvent();
	LuaEvent extura$getUploadEvent();
}
