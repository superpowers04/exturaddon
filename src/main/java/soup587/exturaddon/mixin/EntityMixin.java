package soup587.exturaddon.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;

//? if < 1.21 
import net.minecraft.world.level.gameevent.GameEvent;
//? if > 1.21
import net.minecraft.core.Holder;

import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.permissions.Permissions;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.lua.api.entity.EntityAPI;

import org.jetbrains.annotations.Nullable;
import soup587.exturaddon.ducks.AvatarAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
	/*FIXME WRONG EVENT*/
	//? if <1.21.2 {
	@Inject(at = @At("RETURN"), method = "gameEvent")
	private void gameEvent
	//? if <1.21 
	// (GameEvent event, @Nullable Entity entity, CallbackInfo ci) 
	//? if >1.21
	(Holder event, @Nullable Entity entity, CallbackInfo ci)
		{
		Avatar avatar = AvatarManager.getAvatar((Entity) (Object) this);
		if (avatar == null) return;
		((AvatarAccessor) avatar).extura$interactEvent(event.toString(), entity == null ? null : EntityAPI.wrap(entity));
	}
	//?}
	
}
