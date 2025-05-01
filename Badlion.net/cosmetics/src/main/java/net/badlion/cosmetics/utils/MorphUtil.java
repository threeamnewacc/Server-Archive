package net.badlion.cosmetics.utils;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.tinyprotocol.Reflection;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MorphsUtil class created by Erouax and MasterGberry.
 * <p>
 * Requires Gberry, Cosmetics (for getting player object), TinyProtocolReferences,
 * and a Reflection util class, as well as the Spigot API and Java.
 * <p>
 * Updates:
 * - Added 1.9 support
 * - Gone from cancerous 500 lines of copy-paste cancer, to 150 lines with comments of beauty.
 * - This will probs never be updated (the list) so yeah, just so it makes it look more like a lib ;)
 * <p>
 * Usage: Create a new instance using MorphType and a Player, then use the methods below:
 * sendPlayerSetMorph(), sendPlayerRemoveMorph(), sendServerSetMorph(), sendServerRemoveMorph()
 * <p>
 * You can also get data like the player's UUID, the player himself, and the MorphType.
 *
 * @author - Erouax and MasterGberry
 * @version - 1.0
 */
public class MorphUtil {

    private MorphType morphType;
    private UUID uuid;
    private Object entityLiving;

    /**
     * Constructor for MorphsUtil class
     *
     * @param morphType - The type of morph we are using
     * @param player    - The player in which will be morphed
     */
    public MorphUtil(MorphType morphType, Player player) {
        // Set the MorphType and UUID
        this.morphType = morphType;
        this.uuid = player.getUniqueId();

        // Get the player's location
        Location location = this.getPlayer().getLocation();

        // Get the EntityLiving object
        this.entityLiving = this.getEntity(this.morphType.getEntityName(), player);

        // Check for special cases
        if (this.morphType == MorphType.WITHER_SKELETON) {
            Reflection.MethodInvoker methodSkeleton = Reflection.getMethod(this.entityLiving.getClass(), "setSkeletonType", int.class);
            methodSkeleton.invoke(this.entityLiving, 1);
        }

        // Set the position
        Reflection.MethodInvoker setPosition = Reflection.getMethod(this.entityLiving.getClass(), "setLocation", double.class, double.class, double.class, float.class, float.class);
        setPosition.invoke(this.entityLiving, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        // Set the CustomName
        Reflection.MethodInvoker setCustomName = Reflection.getMethod(this.entityLiving.getClass(), "setCustomName", String.class);
        setCustomName.invoke(this.entityLiving, ChatColor.AQUA + this.getPlayer().getName());

        // Show the CustomName
        Reflection.MethodInvoker setCustomNameVisible = Reflection.getMethod(this.entityLiving.getClass(), "setCustomNameVisible", boolean.class);
        setCustomNameVisible.invoke(this.entityLiving, true);

        // Set the ID so it moves with them
        Reflection.FieldAccessor<Integer> setId = Reflection.getField(TinyProtocolReferences.entityClass, "id", int.class);
        setId.set(this.entityLiving, this.getPlayer().getEntityId());
    }

    /**
     * @return - the UUID of the player in which is being morphed/de-morphing
     */
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Just a quicker way to get the player, calls it once instead of 1337 times
     *
     * @return - Player from UUID
     */
    public Player getPlayer() {
        return Cosmetics.getInstance().getServer().getPlayer(this.uuid);
    }

    /**
     * Easy reflection to get the Entity and store it
     *
     * @param entity - The NMS name of the entity's class
     * @param player - The player in which you are morphing
     * @return - Object of the EntityLiving it's trying to morph into
     */
    private Object getEntity(String entity, Player player) {
        // Get LivingEntity we want
        final Class<?> entityClass = Reflection.getClass("{nms}." + entity);
        Reflection.ConstructorInvoker entityClassConstructor = Reflection.getConstructor(entityClass, TinyProtocolReferences.worldClass);
        Object world = TinyProtocolReferences.getWorldHandle.invoke(player.getWorld());

        // Create LivingEntity
        return entityClassConstructor.invoke(world);
    }

    /**
     * Remove the player's morph for the whole server
     */
    public void sendServerRemoveMorph() {
        // Set the morph to null (not really needed, but precaution)
        this.morphType = null;

        // Send all the players in their world the morph packets
        for (Player pl : this.getPlayer().getWorld().getPlayers()) {
            sendRemovePlayerMorph(pl, false);
        }
    }

    /**
     * Show the player's morph to the whole server
     **/
    public void sendServerSetMorph() {
        // Send all the players in their world the morph packets
        for (Player pl : this.getPlayer().getWorld().getPlayers()) {
            sendPlayerSetMorph(pl, false);
        }
    }

    /**
     * Remove the player's morph for a specific player
     *
     * @param player   - The player to show a regular player again
     * @param override - Whether to override the canSee method
     */
    @SuppressWarnings("PrimitiveArrayArgumentToVariableArgMethod")
    public void sendRemovePlayerMorph(Player player, boolean override) {
        // Check if morphs are disabled
        if (!Cosmetics.getInstance().isMorphsEnabled()) {
            return;
        }

        // Check if they can see them, helps for UHC and other uhcgamemodes
        if (player != this.getPlayer() && (override || player.canSee(this.getPlayer()))) {
            // Make the destroy packet
            int[] entityIdArr = new int[]{this.getPlayer().getEntityId()};
            Object packetEntityDestroy = TinyProtocolReferences.destroyPacketConstructor.invoke(entityIdArr);
            Object entityplayer = TinyProtocolReferences.getPlayerHandle.invoke(this.getPlayer());

            Gberry.protocol.sendPacket(player, packetEntityDestroy);

            // Spawn packets
            Map<Integer, Object> spawnPackets = new HashMap<>();
            Object packetNamedEntitySpawn4 = TinyProtocolReferences.invokeSpawnPacketConstructor(entityplayer, 4);
            Object packetNamedEntitySpawn5 = TinyProtocolReferences.invokeSpawnPacketConstructor(entityplayer, 5);
            Object packetNamedEntitySpawn47 = TinyProtocolReferences.invokeSpawnPacketConstructor(entityplayer, 47);

            spawnPackets.put(4, packetNamedEntitySpawn4);
            spawnPackets.put(5, packetNamedEntitySpawn5);
            spawnPackets.put(47, packetNamedEntitySpawn47);

            // Send le packet
            Gberry.protocol.sendPacket(player, spawnPackets.get(player.getVersion()));
        }
    }

    /**
     * Show the player's morph to a specific player
     *
     * @param player   - The player to show the morph to
     * @param override - Whether to override the canSee method
     */
    @SuppressWarnings("PrimitiveArrayArgumentToVariableArgMethod")
    public void sendPlayerSetMorph(Player player, boolean override) {
        // Check if morphs are disabled
        if (!Cosmetics.getInstance().isMorphsEnabled()) {
            return;
        }

        // Check if they are able to see their morph
        if (player != this.getPlayer() && (override || player.canSee(this.getPlayer()))) {
            // Destroy player packet
            int[] entityIdArr = new int[]{this.getPlayer().getEntityId()};
            Object packetEntityDestroy = TinyProtocolReferences.destroyPacketConstructor.invoke(entityIdArr);

            Gberry.protocol.sendPacket(player, packetEntityDestroy);

            // Spawn player packets
            Map<Integer, Object> spawnPackets = new HashMap<>();
            Object packetNamedEntitySpawn4 = TinyProtocolReferences.invokeEntityLivingSpawnPacketConstructor(this.entityLiving, 4);
            Object packetNamedEntitySpawn5 = TinyProtocolReferences.invokeEntityLivingSpawnPacketConstructor(this.entityLiving, 5);
            Object packetNamedEntitySpawn47 = TinyProtocolReferences.invokeEntityLivingSpawnPacketConstructor(this.entityLiving, 47);

            spawnPackets.put(4, packetNamedEntitySpawn4);
            spawnPackets.put(5, packetNamedEntitySpawn5);
            spawnPackets.put(47, packetNamedEntitySpawn47);

            // Send le packets
            Gberry.protocol.sendPacket(player, spawnPackets.get(player.getVersion()));
        }
    }

    /**
     * Enum for different types of Morphs, makes it easier to access
     * Different Morph Types, so it's easier to do instead of 'EntityPig' every time
     */
    public enum MorphType {
        ZOMBIE("EntityZombie"), HUMAN("EntityHuman"), WITHER_SKELETON("EntitySkeleton"), SKELETON("EntitySkeleton"),
        ZOMBIEPIG("EntityPigZombie"), BLAZE("EntityBlaze"), SQUID("EntitySquid"), SLIME("EntitySlime"),
        MAGMACUBE("EntityMagmaCube"), SILVERFISH("EntitySilverfish"), CAT("EntityOcelot"), WOLF("EntityWolf"),
        GOLEM("EntityIronGolem"), SNOWMAN("EntitySnowman"), ENDERMAN("EntityEnderman"), BAT("EntityBat"),
        MOOSHROOM("EntityMushroomCow"), COW("EntityCow"), CREEPER("EntityCreeper"), SPIDER("EntitySpider"),
        WITCH("EntityWitch"), WITHER_BOSS("EntityWither"), ENDER_DRAGON("EntityEnderDragon"), PIG("EntityPig"),
        SHEEP("EntitySheep"), VILLAGER("EntityVillager"), GHAST("EntityGhast"), GIANT("EntityGiant");

        private String entityName;

        /**
         * Sets up the MorphType, allows us to use getEntityName()
         * Include the Entity's NMS class name, so that it's easier for us to get it in the MorphsUtil constuctor
         *
         * @param entityName - NMS class of the entity
         */
        MorphType(String entityName) {
            this.entityName = entityName;
        }

        /**
         * @return - The String name of the class in which NMS uses
         */
        public String getEntityName() {
            return entityName;
        }
    }

}
