package soup587.exturaddon.mixin;

import org.figuramc.figura.config.ConfigManager;
import org.figuramc.figura.config.Configs;
import org.figuramc.figura.utils.IOUtils;
import org.figuramc.figura.FiguraMod;
import soup587.exturaddon.Exturaddon;

import java.lang.reflect.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.nio.file.Path;

@Mixin(FiguraMod.class)
public class FiguraModMixin {

	@Inject(at=@At("HEAD"),method = "getFiguraDirectory", cancellable = true)
    private static Path getFiguraDirectory(CallbackInfoReturnable<String> cir) {
		if(Exturaddon.cachedPath != null) return Exturaddon.cachedPath;
		String config = Configs.MAIN_DIR.value;
		if(!config.isBlank()) return Exturaddon.cachedPath = IOUtils.createDirIfNeeded(Path.of(config.toString()));
		Path p = FiguraMod.GAME_DIR.resolve(FiguraMod.MOD_ID);
		// int indexOfInstances = p.toAbsolutePath().toString().lastIndexOf("instances");
		
		// if(indexOfInstances != -1){
		// 	Path p2 = Path.of(p.toAbsolutePath().toString().substring(0,indexOfInstances)).resolve(MOD_ID);
		// 	if(Files.exists(p2)){
		// 		return cachedPath = p2;
		// 	}
		// }
		return Exturaddon.cachedPath = IOUtils.createDirIfNeeded(p);
	}
	// @Inject(at=@At("HEAD"), method="getWrapper", cancellable = true)
	// public void getWrapper(@NotNull Method method, CallbackInfoReturnable<VarArgFunction> cir){
    //     if(method.getParameterTypes().length == 0) {
    //         if(Modifier.isStatic(method.getModifiers())){
    //             cir.setReturnValue(new LuaTypeFunctions.StaticFunctionWithoutArgs((LuaTypeManager) (Object) this, method));
	// 			return;
    //         }
	// 		cir.setReturnValue( new LuaTypeFunctions.InstanceFunctionWithoutArgs((LuaTypeManager) (Object) this, method));
    //         return;
    //     }
    //     cir.setReturnValue(new LuaTypeFunctions.FunctionWithArgs((LuaTypeManager) (Object) this, method));
	// }

}
