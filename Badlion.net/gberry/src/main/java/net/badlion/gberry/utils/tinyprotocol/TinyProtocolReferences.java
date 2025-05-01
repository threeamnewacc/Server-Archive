package net.badlion.gberry.utils.tinyprotocol;

// These are not versioned, but they require CraftBukkit

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Represents a very tiny alternative to ProtocolLib in 1.7.2.
 * <p>
 * It now supports intercepting packets during login and status ping (such as OUT_SERVER_PING)!
 * @author Kristian
 */
public abstract class TinyProtocolReferences {

	// NBT
	public static final Class<?> nbtTagCompoundClass = Reflection.getClass("{nms}.NBTTagCompound");
	public static final Reflection.MethodInvoker nbtTagCompoundSetString = Reflection.getMethod(nbtTagCompoundClass, "setString", String.class, String.class);
	public static final Reflection.MethodInvoker getNbtTagCompoundGetString = Reflection.getMethod(nbtTagCompoundClass, "getString", String.class);
	public static final Reflection.MethodInvoker getNbtTagCompoundGetInt = Reflection.getMethod(nbtTagCompoundClass, "getInt", String.class);

	// NMS Handle for Entities
	public static final Class<?> entityClass = Reflection.getClass("{nms}.Entity");
	public static final Class<?> entityLivingClass = Reflection.getClass("{nms}.EntityLiving");
	public static final Class<?> entityHumanClass = Reflection.getClass("{nms}.EntityHuman");
	public static final Class<?> entityPlayerClass = Reflection.getClass("{nms}.EntityPlayer");
	public static final Class<?> entityEnderDragonClass = Reflection.getClass("{nms}.EntityEnderDragon");
	public static final Reflection.MethodInvoker getEntityHandle = Reflection.getMethod("{obc}.entity.CraftEntity", "getHandle");

	// Entity
	public static final Reflection.FieldAccessor<Integer> entityCount = Reflection.getField(entityClass, "entityCount", int.class);
	public static Class<?> pathfinderGoalSelector = Reflection.getClass("{nms}.PathfinderGoalSelector");
	public static final Reflection.FieldAccessor<List> pathfinderGoalSelectorTaskEntries = Reflection.getField(pathfinderGoalSelector, "b", List.class);
	public static final Reflection.FieldAccessor<List> pathfinderGoalSelectorExecutingTaskEntries = Reflection.getField(pathfinderGoalSelector, "c", List.class);
	public static final Reflection.FieldAccessor<?> entityGoalSelector = Reflection.getField("{nms}.EntityInsentient", "goalSelector", pathfinderGoalSelector);
	public static final Reflection.FieldAccessor<?> entityTargetSelector = Reflection.getField("{nms}.EntityInsentient", "targetSelector", pathfinderGoalSelector);
	public static final Class<?> pathfinderGoal = Reflection.getClass("{nms}.PathfinderGoal");
	public static final Class<?> pathfinderGoalFloat = Reflection.getClass("{nms}.PathfinderGoalFloat");
	public static final Reflection.FieldAccessor<?> pathfinderGoalSelectorItemAction = Reflection.getField("{nms}.PathfinderGoalSelectorItem", "a", pathfinderGoal);

	// World Specifics
	public static final Class<?> worldClass = Reflection.getClass("{nms}.World");
	public static final Class<?> enumGamemode = Reflection.getClass("{nms}.EnumGamemode");
	public static final Reflection.MethodInvoker getWorldHandle = Reflection.getMethod("{obc}.CraftWorld", "getHandle");

	// Constructors for above
	public static final Reflection.ConstructorInvoker entityEnderDragonConstructor = Reflection.getConstructor(entityEnderDragonClass, worldClass);

	// ItemStack
	public static final Class<?> itemStackClass = Reflection.getClass("{nms}.ItemStack");
	public static final Class<?> itemStackArrayClass = Reflection.getClass("[L{nms}.ItemStack;");
	public static final Class<?> craftItemStackClass = Reflection.getClass("{obc}.inventory.CraftItemStack");
	public static final Reflection.MethodInvoker asCraftMirror = Reflection.getMethod(craftItemStackClass, "asCraftMirror", itemStackClass);
	public static final Reflection.MethodInvoker getItemStackNMSCopy = Reflection.getMethod("{obc}.inventory.CraftItemStack", "asNMSCopy", ItemStack.class);
	public static final Reflection.MethodInvoker getItemStackEnchantments = Reflection.getMethod(itemStackClass, "getEnchantments");
	public static final Reflection.FieldAccessor itemStackTag = Reflection.getField(itemStackClass, Object.class, 1);
	public static final Reflection.MethodInvoker itemStackTagRemove = Reflection.getMethod("{nms}.NBTTagCompound", "remove", String.class);
	public static final Reflection.MethodInvoker itemStackGetName = Reflection.getMethod(itemStackClass, "getName");
	public static final Class<?> playerInventoryClass = Reflection.getClass("{nms}.PlayerInventory");
	public static final Reflection.FieldAccessor<?> playerInventoryAccessor = Reflection.getField(entityHumanClass, playerInventoryClass, 0);
	public static final Reflection.FieldAccessor<?> playerItemsAccessor = Reflection.getField(playerInventoryClass, itemStackArrayClass, 0);
	public static final Reflection.FieldAccessor<?> playerArmorAccessor = Reflection.getField(playerInventoryClass, itemStackArrayClass, 1);

	public enum InventorySection {
		MAIN,
		ARMOR,
		EXTRA
	}

	public enum ItemModifier {
		ATTACK_DAMAGE("generic.attackDamage"),
		ATTACK_SPEED("generic.attackSpeed");

		private String nmsName;

		ItemModifier(String nmsName) {
			this.nmsName = nmsName;
		}

		public String getNmsName() {
			return nmsName;
		}
	}

	// Blocks
	public static final Class<?> blockClass = Reflection.getClass("{nms}.Block");
	private static final Reflection.MethodInvoker getType = Reflection.getTypedMethod(worldClass, "getType", blockClass, int.class, int.class, int.class);

	// Player specifics
	public static final Reflection.MethodInvoker getPlayerHandle = Reflection.getMethod("{obc}.entity.CraftPlayer", "getHandle");
	public static final Reflection.MethodInvoker getPlayerProfile = Reflection.getMethod("{obc}.entity.CraftPlayer", "getProfile");
	public static final Reflection.FieldAccessor<Double> craftPlayerHealth = Reflection.getField("{obc}.entity.CraftPlayer", "health", double.class);
	public static final Reflection.MethodInvoker playerSleepInBed = Reflection.getMethod("{nms}.EntityPlayer", "a", int.class, int.class, int.class);
	//public static final Reflection.FieldAccessor<Object> getManager = Reflection.getField("{nms}.PlayerConnection", "networkManager", Object.class);
	//public static final Reflection.FieldAccessor<Channel> getChannel = Reflection.getField("{nms}.NetworkManager", Channel.class, 0);

	public static Object getNMSBlock(Block block) {
		return TinyProtocolReferences.getNMSBlock(block.getWorld(), block.getX(), block.getY(), block.getZ());
	}

	public static Object getNMSBlock(World world, double x, double y, double z) {
		return TinyProtocolReferences.getNMSBlock(world, (int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
	}

	public static Object getNMSBlock(World world, int x, int y, int z) {
		Object nmsWorld = TinyProtocolReferences.getWorldHandle.invoke(world);

		return TinyProtocolReferences.getType.invoke(nmsWorld, x, y, z);
	}

	// DataWatcher
	// TODO: Cry about the DataWatcher cancer that is 1.9
	public static final Class<?> dataWatcher = Reflection.getClass("{nms}.DataWatcher");
	public static final Reflection.ConstructorInvoker dataWatcherConstructor = Reflection.getConstructor(dataWatcher, entityClass);
	public static final Reflection.MethodInvoker dataWatcherA = Reflection.getMethod(dataWatcher, "a", int.class, Object.class);

	// Depends on which version (1.7/1.8 vs 1.9)
	public static final Class<Object> gameProfileClass = Reflection.getUntypedClasses("net.minecraft.util.com.mojang.authlib.GameProfile", "com.mojang.authlib.GameProfile");
	public static final Reflection.ConstructorInvoker gameProfileConstructor = Reflection.getConstructor(gameProfileClass, UUID.class, String.class);
	public static final Reflection.FieldAccessor<UUID> gameProfileUUID = Reflection.getField(gameProfileClass, UUID.class, 0);
	public static final Reflection.FieldAccessor<String> gameProfileName = Reflection.getField(gameProfileClass, String.class, 0);
	public static final Reflection.FieldAccessor<Object> gameProfilePropertyMap = Reflection.getField(gameProfileClass, "properties", Object.class);

	// Spawn packets
	public static final Class<?> spawnPacket = Reflection.getClass("{nms}.PacketPlayOutNamedEntitySpawn");
	public static final Class<?> spawnLivingEntityPacket = Reflection.getClass("{nms}.PacketPlayOutSpawnEntityLiving");
	private static final Reflection.ConstructorInvoker spawnPacketConstructor = Reflection.getConstructor(spawnPacket, entityHumanClass);
	private static final Reflection.ConstructorInvoker spawnLivingEntityConstructor = Reflection.getConstructor(spawnLivingEntityPacket, entityLivingClass);
	public static final Reflection.FieldAccessor<Object> spawnPacketGameProfile = Reflection.getField(spawnPacket, "b", Object.class);
	public static final Reflection.FieldAccessor<Integer> spawnPacketEntityID = Reflection.getField(spawnPacket, "a", int.class);

	public static Object invokeSpawnPacketConstructor(Object entityhuman, int version) {
		return TinyProtocolReferences.spawnPacketConstructor.invoke(entityhuman);
	}

	public static Object invokeEntityLivingSpawnPacketConstructor(Object entityliving, int version) {
		return TinyProtocolReferences.spawnLivingEntityConstructor.invoke(entityliving);
	}

	// Use bed packet
	// TODO: 1.9 not finished
	public static final Class<?> useBedPacket = Reflection.getClass("{nms}.PacketPlayOutBed");
	private static final Reflection.ConstructorInvoker useBedPacketConstructor = Reflection.getConstructor(useBedPacket, entityHumanClass, int.class, int.class, int.class);
	private static final Reflection.ConstructorInvoker useBedPacketConstructorEmpty = Reflection.getConstructor(useBedPacket);
	public static final Reflection.FieldAccessor<Integer> useBedPacketEntityId = Reflection.getField(useBedPacket, int.class, 0);
	public static final Reflection.FieldAccessor<Integer> useBedPacketEntityX = Reflection.getField(useBedPacket, int.class, 1);
	public static final Reflection.FieldAccessor<Integer> useBedPacketEntityY = Reflection.getField(useBedPacket, int.class, 2);
	public static final Reflection.FieldAccessor<Integer> useBedPacketEntityZ = Reflection.getField(useBedPacket, int.class, 3);

	public static Object invokeBedPacketConstructor(Object entityhuman, int x, int y, int z) {
		return TinyProtocolReferences.useBedPacketConstructor.invoke(entityhuman, x, y, z);
	}

	public static Object invokeBedPacketConstructorEmpty() {
		return TinyProtocolReferences.useBedPacketConstructorEmpty.invoke();
	}

	// Block action packet
	public static final Class<?> blockActionPacket = Reflection.getClass("{nms}.PacketPlayOutBlockAction");
	private static final Reflection.ConstructorInvoker blockActionPacketConstructor = Reflection.getConstructor(blockActionPacket, int.class, int.class, int.class, blockClass, int.class, int.class);

	public static Object invokeBlockActionPacketConstructor(int x, int y, int z, Object block, int l1, int l2) {
		return TinyProtocolReferences.blockActionPacketConstructor.invoke(x, y, z, block, l1, l2);
	}

	// Destroy packets
	public static final Class<?> destroyPacket = Reflection.getClass("{nms}.PacketPlayOutEntityDestroy");
	public static final Reflection.ConstructorInvoker destroyPacketConstructor = Reflection.getConstructor(destroyPacket, int[].class);

	// Tab List
	public static final Class<?> tabPacketClass = Reflection.getClass("{nms}.PacketPlayOutPlayerInfo");
	public static final Reflection.ConstructorInvoker tabPacketConstructor = Reflection.getConstructor(tabPacketClass);
	public static final Reflection.FieldAccessor<String> tabPacketName = Reflection.getField(tabPacketClass, String.class, 0);
	public static final Reflection.FieldAccessor<Object> tabPacketGameProfile = Reflection.getField(tabPacketClass, "player", Object.class);
	public static final Reflection.FieldAccessor<Integer> tabPacketAction = Reflection.getField(tabPacketClass, int.class, 5);
	public static final Reflection.FieldAccessor<Integer> tabPacketGamemode = Reflection.getField(tabPacketClass, int.class, 6);
	public static final Reflection.FieldAccessor<Boolean> tabPacketDisguisePlugin = Reflection.getField(tabPacketClass, boolean.class, 0);
	// public PlayerInfoData(GameProfile gameprofile, int i, WorldSettings.EnumGamemode worldsettings_enumgamemode, IChatBaseComponent ichatbasecomponent) {

	// Scoreboard teams
	public static final Class<?> scoreboardTeamPacket = Reflection.getClass("{nms}.PacketPlayOutScoreboardTeam");
	public static final Reflection.FieldAccessor<String> teamScoreboardPacketRegisteredName = Reflection.getField(scoreboardTeamPacket, String.class, 0);
	public static final Reflection.FieldAccessor<String> teamScoreboardPacketDisplayName = Reflection.getField(scoreboardTeamPacket, String.class, 1);
	public static final Reflection.FieldAccessor<String> teamScoreboardPacketPrefix = Reflection.getField(scoreboardTeamPacket, String.class, 2);
	public static final Reflection.FieldAccessor<String> teamScoreboardPacketSuffix = Reflection.getField(scoreboardTeamPacket, String.class, 3);
	public static final Reflection.FieldAccessor<Collection> teamScoreboardPacketList = Reflection.getField(scoreboardTeamPacket, Collection.class, 0);
	public static final Reflection.FieldAccessor<Integer> teamScoreboardPacketAction = Reflection.getField(scoreboardTeamPacket, int.class, 0);
	public static final Reflection.FieldAccessor<Integer> teamScoreboardPacketFlag = Reflection.getField(scoreboardTeamPacket, int.class, 1);

	// Scoreboard Score
	public static final Class<?> scoreboardScorePacket = Reflection.getClass("{nms}.PacketPlayOutScoreboardScore");
	public static final Reflection.FieldAccessor<String> scoreScoreboardPacketUsername = Reflection.getField(scoreboardScorePacket, String.class, 0);
	public static final Reflection.FieldAccessor<String> scoreScoreboardPacketObjectiveName = Reflection.getField(scoreboardScorePacket, String.class, 1);
	public static final Reflection.FieldAccessor<Integer> scoreScoreboardPacketScore = Reflection.getField(scoreboardScorePacket, int.class, 0);
	public static final Reflection.FieldAccessor<?> scoreScoreboardPacketAction = Reflection.getField(scoreboardScorePacket, int.class, 1);

	// Scoreboard Objective
	public static final Class<?> scoreboardObjectivePacket = Reflection.getClass("{nms}.PacketPlayOutScoreboardObjective");
	public static final Reflection.FieldAccessor<String> objectiveScoreboardPacketName = Reflection.getField(scoreboardObjectivePacket, String.class, 0);
	public static final Reflection.FieldAccessor<String> objectiveScoreboardPacketTitle = Reflection.getField(scoreboardObjectivePacket, String.class, 1);
	public static final Reflection.FieldAccessor<Integer> objectiveScoreboardPacketAction = Reflection.getField(scoreboardObjectivePacket, int.class, 0);

	// Scoreboard Display Objective
	public static final Class<?> scoreboardDisplayObjectivePacket = Reflection.getClass("{nms}.PacketPlayOutScoreboardDisplayObjective");
	public static final Reflection.FieldAccessor<Integer> displayObjectiveScoreboardPacketPosition = Reflection.getField(scoreboardDisplayObjectivePacket, int.class, 0);
	public static final Reflection.FieldAccessor<String> displayObjectiveScoreboardPacketName = Reflection.getField(scoreboardDisplayObjectivePacket, String.class, 0);


	// Teleport packets
	// TODO: Fix with Bar API for 1.9
	public static final Class<?> entityTeleportPacket = Reflection.getClass("{nms}.PacketPlayOutEntityTeleport");
	public static final Reflection.ConstructorInvoker entityTeleportPacketConstructor = Reflection.getConstructor(entityTeleportPacket, int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class, entityClass);

	public static Object invokeEntityTeleportPacketConstructor(int entityId, double x, double y, double z, float yaw, float pitch, boolean inGround, Object entity) {
		int b = (int) Math.floor(x * 32.0D);
		int c = (int) Math.floor(y * 32.0D);
		int d = (int) Math.floor(z * 32.0D);
		byte e = (byte) ((int) (yaw * 256.0F / 360.0F));
		byte f = (byte) ((int) (pitch * 256.0F / 360.0F));

		return TinyProtocolReferences.entityTeleportPacketConstructor.invoke(entityId, b, c, d, e, f, inGround, entity);
	}

	// EntityMetadata Packet
	// TODO: Fix with Bar API for 1.9
	public static final Class<?> entityMetadataPacket = Reflection.getClass("{nms}.PacketPlayOutEntityMetadata");
	public static final Reflection.ConstructorInvoker entityMetadataPacketConstructor = null;

	// Entity equipment packet
	public static final Class<?> packetEntityEquipmentClass = Reflection.getClass("{nms}.PacketPlayOutEntityEquipment");
	public static final Reflection.FieldAccessor<Integer> packetEntityEquipmentEntityID = Reflection.getField(packetEntityEquipmentClass, int.class, 0);
	private static final Reflection.FieldAccessor<Integer> packetEntityEquipmentSlot = Reflection.getField(packetEntityEquipmentClass, int.class, 1);
	public static final Reflection.FieldAccessor<Object> packetEntityEquipmentItem = Reflection.getField(packetEntityEquipmentClass, Object.class, 0);

	public static int getPacketEntityEquipmentSlot(Object packet) {
		return TinyProtocolReferences.packetEntityEquipmentSlot.get(packet);
	}

	public static void setPacketEntityEquipmentSlot(Object packet, int slot) {
		TinyProtocolReferences.packetEntityEquipmentSlot.set(packet, slot);
	}

	// Block change packet
	public static final Class<?> packetBlockChangeClass = Reflection.getClass("{nms}.PacketPlayOutBlockChange");
	private static final Reflection.FieldAccessor<Integer> packetBlockChangeXCoord = Reflection.getField(packetBlockChangeClass, int.class, 0);
	private static final Reflection.FieldAccessor<Integer> packetBlockChangeYCoord = Reflection.getField(packetBlockChangeClass, int.class, 1);
	private static final Reflection.FieldAccessor<Integer> packetBlockChangeZCoord = Reflection.getField(packetBlockChangeClass, int.class, 2);

	public static int getPacketBlockChangeCoord(Object packet, char field) {
		if (field != 'x' && field != 'y' && field != 'z') throw new RuntimeException("STUPID SMELLY! SEND THE RIGHT FIELD");

		if (field == 'x') {
			return TinyProtocolReferences.packetBlockChangeXCoord.get(packet);
		} else if (field == 'y') {
			return TinyProtocolReferences.packetBlockChangeYCoord.get(packet);
		} else {
			return TinyProtocolReferences.packetBlockChangeZCoord.get(packet);
		}
	}

	// Chunks for sign interception
	public static final Class<?> packetMapChunk = Reflection.getClass("{nms}.PacketPlayOutMapChunk");

	// Update sign packet
	public static final Class<?> packetUpdateSignClass = Reflection.getClass("{nms}.PacketPlayOutUpdateSign");
	private static final Class<?> craftSignClass = Reflection.getClass("{obc}.block.CraftSign");
	// Note return type in 1.9 is IChatBaseComponent[] and in 1.7 it is String[]
	public static final Reflection.MethodInvoker sanitizeLines = Reflection.getMethod(craftSignClass, "sanitizeLines", String[].class);
	private static final Reflection.FieldAccessor<Integer> packetUpdateSignXCoord = Reflection.getField(packetUpdateSignClass, int.class, 0);
	private static final Reflection.FieldAccessor<Integer> packetUpdateSignYCoord = Reflection.getField(packetUpdateSignClass, int.class, 1);
	private static final Reflection.FieldAccessor<Integer> packetUpdateSignZCoord = Reflection.getField(packetUpdateSignClass, int.class, 2);
	private static final Reflection.FieldAccessor<String[]> packetUpdateSignLines = Reflection.getField(packetUpdateSignClass, String[].class, 0);

	// Always assume this is being called for packet updates for 1.7
	public static boolean isSignUpdate(Object packet) {
		return true;
	}

	public static int getPacketUpdateSignCoord(Object packet, char field) {
		if (field != 'x' && field != 'y' && field != 'z') throw new RuntimeException("STUPID SMELLY! SEND THE RIGHT FIELD");

		if (field == 'x') {
			return TinyProtocolReferences.packetUpdateSignXCoord.get(packet);
		} else if (field == 'y') {
			return TinyProtocolReferences.packetUpdateSignYCoord.get(packet);
		} else {
			return TinyProtocolReferences.packetUpdateSignZCoord.get(packet);
		}
	}

	public static void setPacketUpdateSignLines(Object packet, String[] lines) {
		TinyProtocolReferences.packetUpdateSignLines.set(packet, lines);
	}

	public static final Class<?> packetEntityStatusClass = Reflection.getClass("{nms}.PacketPlayOutEntityStatus");
	public static final Reflection.ConstructorInvoker packetEntityStatusConstructor = Reflection.getConstructor(packetEntityStatusClass);
	public static final Reflection.FieldAccessor<Integer> packetEntityStatusEntityID = Reflection.getField(packetEntityStatusClass, "a", int.class);
	public static final Reflection.FieldAccessor<Byte> packetEntityStatusStatusID = Reflection.getField(packetEntityStatusClass, "b", byte.class);


	// Property
	public static final Class<Object> propertyClass = Reflection.getUntypedClasses("net.minecraft.util.com.mojang.authlib.properties.Property", "com.mojang.authlib.properties.Property");
	public static final Reflection.ConstructorInvoker propertyConstructor = Reflection.getConstructor(propertyClass, String.class, String.class, String.class);
	public static final Reflection.FieldAccessor<String> propertyName = Reflection.getField(propertyClass, String.class, 0);
	public static final Reflection.FieldAccessor<String> propertyValue = Reflection.getField(propertyClass, String.class, 1);
	public static final Reflection.FieldAccessor<String> propertySignature = Reflection.getField(propertyClass, String.class, 2);

	// PropertyMap
	public static final Class<Object> propertyMapClass = Reflection.getUntypedClasses("net.minecraft.util.com.mojang.authlib.properties.PropertyMap", "com.mojang.authlib.properties.PropertyMap");
	public static final Reflection.MethodInvoker propertyMapGet = Reflection.getMethod(propertyMapClass, "get", Object.class);
	public static final Reflection.MethodInvoker propertyMapPut = Reflection.getMethod(propertyMapClass, "put", Object.class, Object.class);
	public static final Reflection.MethodInvoker propertyMapRemoveAll = Reflection.getMethod(propertyMapClass, "removeAll", Object.class);

	// FUCK BUKKIT
	public static final Reflection.FieldAccessor<File> pluginFile = Reflection.getField(JavaPlugin.class, File.class, 0);

}