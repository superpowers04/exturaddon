package soup587.exturaddon.lua;


import soup587.exturaddon.Exturaddon;
import com.mojang.brigadier.StringReader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
//? if > 1.20.2 {
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
//?}
//? if < 1.21.2 {
import net.minecraft.client.player.Input;
//? }
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.figuramc.figura.FiguraMod;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.avatar.local.LocalAvatarFetcher;
import org.figuramc.figura.avatar.local.LocalAvatarLoader;
import org.figuramc.figura.backend2.NetworkStuff;
import org.figuramc.figura.backend2.HttpAPI;
import org.figuramc.figura.gui.widgets.lists.AvatarList;
import org.figuramc.figura.lua.LuaNotNil;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.api.HostAPI;
import org.figuramc.figura.lua.api.entity.EntityAPI;
import org.figuramc.figura.lua.api.entity.PlayerAPI;
import org.figuramc.figura.lua.api.world.BlockStateAPI;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaFieldDoc;
import org.figuramc.figura.lua.docs.LuaTypeDoc;
import org.figuramc.figura.lua.docs.LuaMethodOverload;
import org.figuramc.figura.math.vector.FiguraVec2;
import org.figuramc.figura.math.vector.FiguraVec3;
import org.figuramc.figura.mixin.input.KeyMappingAccessor;
import org.figuramc.figura.utils.LuaUtils;
import org.figuramc.figura.utils.PlatformUtils;
import org.figuramc.figura.config.*;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import soup587.exturaddon.ExturaPermissions;
import soup587.exturaddon.lua.KeyMappingAPI;
import soup587.exturaddon.lua.NotImplementedLuaError;
import soup587.exturaddon.overrides.ExturaInput;

import soup587.exturaddon.config.ConfigExtensions;
import soup587.exturaddon.overrides.NoInput;
import soup587.exturaddon.mixin.backend2.HttpAPIMixin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@LuaWhitelist
@LuaTypeDoc(name = "ExturaddonAPI", value = "exturaddon")
/* TODO: Really need to divide this up into several exturaddon APIs*/
public class ExturaddonAPI {

	private Avatar owner;
	private boolean isHost;
	private Minecraft minecraft;
	//? if < 1.21.2 {
	private static Input defaultInput;
	//? }
	private static final boolean HAS_CURIOS = PlatformUtils.isModLoaded("curios");



	public ExturaddonAPI(Avatar owner) {
		//? if < 1.21.2 {
			defaultInput = null;
		//? }
		this.minecraft = Minecraft.getInstance();
		this.isHost = (this.owner = owner).isHost;
	}
	@LuaWhitelist
	@LuaMethodDoc("extura.allow_extura_cheats")
	

	public Boolean allowExturaCheats() {
		if(!this.isHost) return false;
		LocalPlayer player = this.minecraft.player;
		if(player == null) return false;
		if((//? if < 1.21.2 {
			player.hasPermissions(2)  ||
			//? }
				this.minecraft.isLocalServer()
				//? if < 1.20.2 {
				/* || (player.getScoreboard().hasObjective("extura_can_cheat"))
				*///?} else if < 1.21.10 {
				 || (player.getScoreboard().getObjective("extura_can_cheat") != null)
				//?}
				/* TODO Implement 1.21.10+ handling of permissions*/
		)) return true;

		//? if > 1.20.2 {
		//// Stolen from sillyplugin at https://github.com/Figura-Solstice/SillyPlugin/blob/master/src/main/java/dev/celestial/silly/lua/SillyAPI.java#L169
		////  with permission
		ClientPacketListener con = minecraft.getConnection();
		if (con != null){
			ServerData servDt = con.getServerData();
			Component motd = servDt != null ? servDt.motd : Component.empty();
			String motdStr = motd.getString();
			if(motdStr.contains("§s§i§l§l§y§p§l§u§g§i§n") // SillyPlugin support
					// some servers optimize the MOTD by removing
					// formatting codes that do nothing. (COUGH COUGH
					// PAPER).
					|| motdStr.contains("§s§i§y§p§u§g§i")
					// Extura
					|| motdStr.contains("§e§x§t§u§r§a")
			) return true;
		}
		//? }

		return false;
	}
	public Boolean canExturaCheat() {
		if(!this.isHost) return false;
		LocalPlayer player = this.minecraft.player;
		if(player == null) return false;
		if(allowExturaCheats()) return true;
		if(!owner.noPermissions.contains(ExturaPermissions.EXTURA_CHEATING)){
			owner.noPermissions.add(ExturaPermissions.EXTURA_CHEATING);
		}
		return false;
	}


	@LuaWhitelist
	@LuaMethodDoc("extura.upload_avatar")
	public boolean uploadAvatar() {
		if(!this.isHost) return false;
		Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
		if(avatar == null) throw new LuaError("Cannot upload a null avatar!");
		try {
			LocalAvatarLoader.loadAvatar(null, null);
		} catch (Exception ignored) {}
		NetworkStuff.uploadAvatar(avatar);
		AvatarList.selectedEntry = null;
		return true;
	}
	/*@LuaWhitelist
	@LuaMethodDoc("extura.upload_avatar_to")
	public boolean uploadAvatarTo(boolean backend,boolean fsb) {
		if(!this.isHost) return false;
		Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
		if(avatar == null) throw new LuaError("Cannot upload a null avatar!");
		try {
			LocalAvatarLoader.loadAvatar(null, null);
		} catch (Exception ignored) {}
		NetworkStuff.uploadAvatar(avatar,(!backend && !fsb) ? Destination.FSB_OR_BACKEND : Destination.fromBool(backend,fsb));
		AvatarList.selectedEntry = null;
		return true;
	}
	 */
	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(argumentTypes = String.class, argumentNames = "Avatar Owner"),
					@LuaMethodOverload(argumentTypes = EntityAPI.class, argumentNames = "Avatar Owner")
			},
			value = "extura.reload_avatar")
	public void reloadAvatar(Object playerUUID) {
		if(!this.isHost) return;
		final UUID uuid;
		if(playerUUID instanceof EntityAPI){
			uuid = ((EntityAPI) playerUUID).getEntity().getUUID();
		}else if(playerUUID instanceof String){
			uuid = UUID.fromString((String) playerUUID);
		}else if(playerUUID != null){
			throw new LuaError("Expected String, EntityAPI or Nil");
		}else{
			uuid = FiguraMod.getLocalPlayerUUID();
		}
		AvatarManager.reloadAvatar(uuid);
	}

	@LuaWhitelist
	@LuaMethodDoc("extura.load_local_avatar") // Did not steal this from GoofyPlugin, no proof
	public void loadLocalAvatar(String path) {
		if(!this.isHost) return;
		if(path == null || path.isEmpty()){
			AvatarManager.clearAvatars(FiguraMod.getLocalPlayerUUID());
			AvatarList.selectedEntry = null;
			return;
		}
		Path _path = LocalAvatarFetcher.getLocalAvatarDirectory().resolve(path);
		AvatarManager.loadLocalAvatar(_path);
		AvatarList.selectedEntry = _path;
	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = Boolean.class,
							argumentNames = "vec"
					),
			},
			value = "extura.set_velocity"
	)
	public void setVelocity(Object x, Double y, Double z) {
		if(!canExturaCheat()) return;
		this.minecraft.player.setDeltaMovement(LuaUtils.parseVec3("player_setVelocity", x, y, z).asVec3());

	}
	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = Boolean.class,
							argumentNames = "vec"
					),
			},
			value = "extura.travel"
	)
	public void travel(Object x, Double y, Double z) {
		if(!canExturaCheat()) return;
		this.minecraft.player.travel(LuaUtils.parseVec3("player_travel", x, y, z).asVec3());

	}
	@LuaWhitelist
	@LuaMethodDoc("extura.set_pose")
	public void setPose(String pose) {
		if(!canExturaCheat()) return;
		try{
			Pose _pose = Pose.valueOf(pose);
			this.minecraft.player.setPose(_pose);
		}catch(IllegalArgumentException ignored){
			throw new LuaError("Invalid pose " + pose);
		}
	}
	@LuaWhitelist
	@LuaMethodDoc("extura.set_physics")
	public void setPhysics(Boolean physics) {
		if(!canExturaCheat()) return;
		this.minecraft.player.noPhysics = !physics;
	}
	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = Boolean.class,
							argumentNames = "pos"
					),
			},
			value = "extura.set_pos"
	)
	public void setPos(Object x, Double y, Double z) {
		if (!canExturaCheat() || x == null) return;
		LocalPlayer player = this.minecraft.player;
		player.setPos(LuaUtils.parseVec3("player_setPos", x, y, z).asVec3());
	}
	@LuaWhitelist
	@LuaMethodDoc("extura.start_riding")
	public void startRiding(EntityAPI entity,boolean bool) {
		if (!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		if(entity == null) {
			player.removeVehicle();
			return;
		}
//?		if > 1.21.10 {
//			throw new NotImplementedLuaError();
//?} else {
			Entity t = entity.getEntity();
			if(t == player) throw new LuaError("You cannot ride yourself!");
			player.startRiding(t,bool);
//?		}
	}

	@LuaWhitelist
	@LuaMethodDoc("extura.drop_item")
	public void dropItem(boolean dropAll) {
		if(!this.isHost) return;
		LocalPlayer player = this.minecraft.player;
		player.drop(dropAll == true);
	}
	@LuaWhitelist
	@LuaMethodDoc("extura.close_container")
	public void closeContainer() {
		LocalPlayer player = this.minecraft.player;
		player.closeContainer();
	}
	@LuaWhitelist
	@LuaMethodDoc("extura.start_using_item")
	public void startUsingItem(boolean offHand) {
		if (!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		player.startUsingItem(offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
	}
	@LuaWhitelist
	@LuaMethodDoc("extura.stop_using_item")
	public void stopUsingItem() {
		if (!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		player.stopUsingItem();
	}
	@LuaWhitelist
	@LuaMethodDoc("extura.send_open_inventory")
	public void sendOpenInventory() {
		if (!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		player.sendOpenInventory();
	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = {Boolean.class},
							argumentNames = {"playerMovement"}
					)
			},
			value = "extura.set_player_movement"
	)
	public void setPlayerMovement(Boolean playerMovement) {
//?		if < 1.21.2 {
		LocalPlayer player;
		if (!canExturaCheat() || (player = this.minecraft.player) == null) return;
		player.input = (playerMovement ? new ExturaInput(this.minecraft.options) : new NoInput());
//?}else {
		/*throw new NotImplementedLuaError();
*///?}
	}
	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = {String.class,Boolean.class},
							argumentNames = {"input","state"}
					),
					@LuaMethodOverload(
							argumentTypes = {String.class},
							argumentNames = {"input","state"}
					),
			},
			value = "extura.override_player_movement"
	)
	public void overridePlayerMovement(@LuaNotNil String input,Boolean sta) {
		if(!canExturaCheat()) return;
		LocalPlayer player;
		if (!this.isHost || (player = this.minecraft.player) == null) return;
//?		if < 1.21.2 {
		if(!(player.input instanceof ExturaInput)){
			player.input = new ExturaInput(this.minecraft.options);
		}
		int state = sta == null ? 0 : sta ? 2 : 1;
		ExturaInput inputObj =(ExturaInput) player.input;
		switch(input.toLowerCase()){
			case "up": inputObj.upOverride = state; break;
			case "down": inputObj.downOverride = state; break;
			case "left": inputObj.leftOverride = state; break;
			case "right": inputObj.rightOverride = state; break;
			case "jump": inputObj.jumpOverride = state; break;
			case "shift": inputObj.shiftOverride = state; break;
			default: throw new LuaError("Invalid input");
		}
//?}else {
		/*throw new NotImplementedLuaError();
*///?}
	}
	@LuaWhitelist
	@LuaMethodDoc("extura.get_player_movement")
	public Boolean getPlayerMovement() {
		LocalPlayer player;
		if (!this.isHost || (player = this.minecraft.player) == null) return true;
//?		if < 1.21.2 {
		return (player.input instanceof NoInput);
//?}else {
		/*throw new NotImplementedLuaError();
*///?}
	}

	@LuaWhitelist
	@LuaMethodDoc("extura.get_last_death_pos")
	public FiguraVec3 getLastDeathPos() {
		if(!isHost) return null;
		LocalPlayer player = this.minecraft.player;
		if (player != null) {
			Optional<GlobalPos> deathLocation = player.getLastDeathLocation();
			if(deathLocation.isPresent()) return FiguraVec3.fromBlockPos(deathLocation.get().pos());
		}
		return null;
	}


	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = FiguraVec2.class,
							argumentNames = "vec"
					),
					@LuaMethodOverload(
							argumentTypes = {Double.class, Double.class},
							argumentNames = {"x", "y"}
					)
			},
			value = "extura.set_rot"
	)
	public void setRot(Object x, Double y) {
		if(!canExturaCheat()) return;
		FiguraVec2 vec = LuaUtils.parseVec2("player_setRot", x, y);
		LocalPlayer player = this.minecraft.player;
		player.setXRot((float) vec.x);
		player.setYRot((float) vec.y);

	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = {Double.class},
							argumentNames = {"angle"}
					)
			},
			value = "extura.set_body_rot"
	)
	public void setBodyRot(Double angle) {
		if(!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		player.setYBodyRot(angle.floatValue());

	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = {Double.class},
							argumentNames = {"angle"}
					)
			},
			value = "extura.set_body_offset_rot"
	)
	public void setBodyOffsetRot(Double angle) {
		if(!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		player.setYBodyRot( angle.floatValue() + player.getYRot() );
	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = {Boolean.class},
							argumentNames = {"hasForce"}
					)
			},
			value = "extura.set_gravity"
	)
	public void setGravity(Boolean hasForce) {
		if(!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		if (player == null) return;
		player.setNoGravity(!hasForce);

	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = {Boolean.class},
							argumentNames = {"hasForce"}
					)
			},
			value = "extura.set_drag"
	)
	public void setDrag(Boolean hasForce) {
		if(!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		if (player == null) return;
		player.setDiscardFriction(hasForce != true);
	}


	@LuaWhitelist
	@LuaMethodDoc("extura.get_key_mappings")
	public Map<String, KeyMappingAPI<?>> getKeyMappings() {
		if (!this.isHost) return new HashMap<>();
		HashMap<String, KeyMappingAPI<?>> mappingslist = new HashMap<>();

		Map<String, KeyMapping> mappings = KeyMappingAccessor.getAll();

		mappings.forEach((k,v) -> {
			mappingslist.put(k,KeyMappingAPI.wrap(v));
		});
		return mappingslist;
	}


	// borrowed this from vivecraft - jess

	@LuaWhitelist
	@LuaMethodDoc("extura.set_bind_pressed")
	public void setBindPressed(@LuaNotNil String id, boolean state) {
		if (!isHost) return;
		KeyMapping key = KeyMappingAccessor.getAll().get(id);
		if (key == null)
			throw new LuaError("Failed to find key: \"" + id + "\"");
		key.setDown(state);
		key.clickCount += 1;
	}

/*	@LuaWhitelist
	@LuaMethodDoc("extura.get_nameplate")
	public String getNameplate(EntityAPI entity, String type) {
		entity.checkEntity();
		Avatar avi = AvatarManager.getAvatar(entity.entity);
		if(avi == null || avi.luaRuntime == null) return null;
		switch(type.toUpperCase()){
			case "ENTITY": return avi.luaRuntime.nameplate.ENTITY.getText();
			case "LIST": return avi.luaRuntime.nameplate.LIST.getText();
			case "CHAT": return avi.luaRuntime.nameplate.CHAT.getText();
			default: return null;
		}
	}*/
/*	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					// @LuaMethodOverload(argumentTypes = String.class, argumentNames = "Avatar Owner"),
					@LuaMethodOverload(argumentTypes = EntityAPI.class, argumentNames = "Avatar Owner")
			},
			value = "extura.get_nameplate")
	public LuaTable getNameplate(EntityAPI entity_api) {
		if(!this.isHost) return;
		Entity entity;
		if(playerUUID instanceof EntityAPI){
			entity = entity_api.getEntity();
		// }else if(playerUUID instanceof String){
		// 	uuid = UUID.fromString((String) playerUUID);
		}else{
			throw new LuaError("bad argument #1 to 'getNameplate' (entity expected)");
		}
		Avatar avi = AvatarManager.getAvatar(entity);
		if(avi == null || avi.luaRuntime == null) return null;

		LuaTable table = new LuaTable();
		table.rawset(LuaString.valueOf(), avi.luaRuntime.nameplate.ENTITY.getText());
		return table;
	}*/

	@LuaWhitelist
	@LuaMethodDoc("extura.is_backend_connected")
	public static boolean isBackendConnected() {
		return NetworkStuff.isConnected();
	}
	// @LuaWhitelist
	// @LuaMethodDoc("extura.get_mod_name")
	// public static String getModName(@LuaNotNil String id) {
	// 	return PlatformUtils.isModLoaded(id) ? PlatformUtils.getModName(id) : "";
	// }
	@LuaWhitelist
	@LuaMethodDoc("extura.get_backend_address")
	public static String getBackendAddress() {
		return NetworkStuff.isConnected() ? actuallyGetBackendAddress() : "" ;
	}

	public static String actuallyGetBackendAddress(){
		if(ConfigExtensions.BLOCK_CLOUD.value) return "http://127.0.0.1:9/api";
        if(ConfigExtensions.VANILLA_CLOUD.value){
            return "https://" + ServerAddress.parseString(Configs.SERVER_IP.defaultValue).getHost() + "/api";
        }
        String backendIP = ConfigExtensions.USE_MC_HOST_RESOLVER.value ? ServerAddress.parseString(Configs.SERVER_IP.value).getHost() : Configs.SERVER_IP.value;
        if(ConfigExtensions.USE_SECURE_CLOUD.value) return "https://" + backendIP + "/api";
        return "http://" + backendIP + "/api";
	}



	@LuaWhitelist
	@LuaFieldDoc("extura.has_parcool")
	private static boolean HAS_PARCOOL = PlatformUtils.isModLoaded("parcool");

	private static Class<?> PARCOOL_ParkourAbility, PARCOOL_Action = null;
	private static Method   PARCOOL_ParkourAbility_GetAbility, PARCOOL_ParkourAbility_GetActions, PARCOOL_Action_isDoing = null;
	private static EReadOnlyLuaTable   parcool_actions = new EReadOnlyLuaTable();
	
	private static void loadParcool(){
		if (PARCOOL_ParkourAbility != null) return;
		if (!HAS_PARCOOL) throw new LuaError("Attempt to grab parcool when it's not installed!");
		try{
			PARCOOL_ParkourAbility = Class.forName("com.alrex.parcool.common.attachment.common.Parkourability");
			PARCOOL_Action = Class.forName("com.alrex.parcool.common.action.Action");
			PARCOOL_ParkourAbility_GetAbility = PARCOOL_ParkourAbility.getMethod("get", Class.forName("net.minecraft.world.entity.player.Player"));
			PARCOOL_ParkourAbility_GetActions = PARCOOL_ParkourAbility.getMethod("getList");
			PARCOOL_Action_isDoing = PARCOOL_Action.getMethod("isDoing");
		}catch(Exception e){
			throw new LuaError("Unable to load parcool:"+e.toString());
		}
	}
	@LuaWhitelist
	@LuaMethodDoc("extura.parcool_get_actions")
	public LuaTable parcoolGetActions(PlayerAPI target_player_api) {
		if(!HAS_PARCOOL) return parcool_actions;
		loadParcool();
		Player player = target_player_api.getEntity();
		if (player == null) throw new LuaError("invalid player");
		Object pa = null;
		try{
			pa = PARCOOL_ParkourAbility_GetAbility .invoke(PARCOOL_ParkourAbility, player);
		}catch(Exception e){
			throw new LuaError("Unable to grab player abilities:"+e.toString());
		}
		if (pa == null) throw new LuaError("invalid object returned by parcool!");
		try{
			for (Object action : (List<Object>) PARCOOL_ParkourAbility_GetActions.invoke(pa) ) {
				parcool_actions.javaset(LuaString.valueOf(action.getClass().getSimpleName()), ((boolean) PARCOOL_Action_isDoing.invoke(action)) ? LuaValue.TRUE : LuaValue.FALSE);
			}
		}catch(Exception e){
			throw new LuaError("Unable to grab actions:"+e.toString());
		}

		return parcool_actions;
	}




	@LuaWhitelist
	public Object __index(String arg) {
		return switch (arg.toLowerCase()) {
			case "version" -> Exturaddon.MOD_VERSION;
			case "hasParcool" -> HAS_PARCOOL;
			default -> null;
		};
	}

	@LuaWhitelist
	public void __newindex(@LuaNotNil String key) {
		throw new LuaError("Cannot assign value on key \"" + key + "\"");
	}

	@Override
	public String toString() {
		return "ExturaAPI";
	}
}
