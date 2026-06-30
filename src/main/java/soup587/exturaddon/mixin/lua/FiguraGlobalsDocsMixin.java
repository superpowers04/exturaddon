package soup587.exturaddon.mixin.lua;
/* thank you sillyplugin,.,.,,,, <3*/

import soup587.exturaddon.lua.ExturaddonAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.figuramc.figura.lua.docs.LuaFieldDoc;
import org.figuramc.figura.lua.docs.FiguraGlobalsDocs;

@Mixin(value = FiguraGlobalsDocs.class, remap = false)
public class FiguraGlobalsDocsMixin {

	@Unique
    @LuaFieldDoc("globals.exturaddon")
    public ExturaddonAPI exturaddon;

}
