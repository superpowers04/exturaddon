package soup587.exturaddon.mixin.lua;

import org.figuramc.figura.lua.api.entity.EntityAPI;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;

import java.util.function.Predicate;

@Mixin(EntityAPI.class)
class EntityAPIMixin {


	/* Fixes log spam when using sable with Figura's getTargetedEntity*/
	@IfModLoaded(value="sable")
	@Redirect(method = "getTargetedEntity",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"))
	EntityHitResult getHitResult(Entity entity, Vec3 start, Vec3 end, AABB aabb, Predicate<Entity> predicate, double distance){
		try{
			return ProjectileUtil.getEntityHitResult(entity, start, end, aabb, predicate, distance);
		}catch(Exception ignored){}
		return null;
	}
}
