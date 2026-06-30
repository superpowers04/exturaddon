package soup587.exturaddon.mixin.lua;
/* thank you sillyplugin,.,.,,,, <3*/

import soup587.exturaddon.lua.ExturaddonAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.figuramc.figura.lua.docs.FiguraDocsManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(value = FiguraDocsManager.class, remap = false)
public class FiguraDocsManagerMixin {
	@Shadow
	@Final
	private static Map<String, Collection<Class<?>>> GLOBAL_CHILDREN;

	static {
		GLOBAL_CHILDREN.put("exturaddon", List.of(ExturaddonAPI.class));
	}
}
