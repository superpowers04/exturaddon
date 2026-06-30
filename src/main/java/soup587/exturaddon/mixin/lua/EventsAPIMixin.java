package soup587.exturaddon.mixin.lua;

import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.api.event.EventsAPI;
import org.figuramc.figura.lua.api.event.LuaEvent;
import org.figuramc.figura.lua.docs.LuaFieldDoc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import soup587.exturaddon.ducks.EventsAPIAccessor;

import java.util.Map;

@Mixin(EventsAPI.class)
public class EventsAPIMixin implements EventsAPIAccessor {

	@Shadow @Final private Map<String, LuaEvent> events;

	@LuaWhitelist
	@LuaFieldDoc("events.pre_render")
	public LuaEvent PRE_RENDER = new LuaEvent();

	@LuaWhitelist
	@LuaFieldDoc("events.upload")
	public LuaEvent UPLOAD = new LuaEvent();

	@LuaWhitelist
	@LuaFieldDoc("events.handle_entity_event")
	public LuaEvent HANDLE_ENTITY_EVENT = new LuaEvent();

	@LuaWhitelist
	@LuaFieldDoc("events.interact")
	public LuaEvent INTERACT = new LuaEvent();


	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	void addEvents(CallbackInfo ci) {
		events.put("PRE_RENDER", PRE_RENDER);
		events.put("UPLOAD", UPLOAD);
		events.put("HANDLE_ENTITY_EVENT", HANDLE_ENTITY_EVENT);
		events.put("INTERACT", INTERACT);
	}

	public LuaEvent extura$getPreRenderEvent() {
		return PRE_RENDER;
	}
	public LuaEvent extura$getUploadEvent() {
		return UPLOAD;
	}
	public LuaEvent extura$getHandleEntityEvent() {
		return HANDLE_ENTITY_EVENT;
	}
	public LuaEvent extura$getInteractEvent() {
		return INTERACT;
	}

}
