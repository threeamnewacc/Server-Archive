package net.badlion.combattag;

import net.badlion.combattag.events.CombatTagDestroyEvent;
import net.badlion.combattag.events.CombatTagDropInventoryEvent;
import net.badlion.combattag.listeners.CombatTagListener;
import net.badlion.gberry.utils.EntityUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class LoggerNPC {

    public enum REMOVE_REASON { DEATH, REJOIN, TIMEOUT }

    private UUID uuid;
    private Player player;

    private Zombie entity;

	/**
	 * Creates a combat logger for an offline player.
	 *
	 * @param uuid - Player UUID
	 * @param name - Player disguised name
	 * @param location - Location to spawn combat logger
	 */
	public LoggerNPC(UUID uuid, String name, Location location) {
		this.uuid = uuid;

		// Create the NPC
		Zombie entity = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
		EntityUtil.clearAI(entity);
		entity.setCustomName(name);
		entity.setCustomNameVisible(true);
		entity.setHealth(20D);
		entity.setCanPickupItems(true);
		entity.setBaby(false);
		entity.setVillager(false);
		entity.setMetadata("CombatLoggerNPC", new FixedMetadataValue(CombatTagPlugin.getInstance(), this.uuid));
		entity.getEquipment().clear();
		entity.getEquipment().setArmorContents(new ItemStack[4]);
		entity.getEquipment().setHelmetDropChance(0F);
		entity.getEquipment().setChestplateDropChance(0F);
		entity.getEquipment().setLeggingsDropChance(0F);
		entity.getEquipment().setBootsDropChance(0F);
		entity.getEquipment().setItemInHand(null);
		entity.getEquipment().setItemInHandDropChance(0F);
		entity.setCanPickupItems(false);
		entity.setMetadata("CombatLoggerInventory", new FixedMetadataValue(CombatTagPlugin.getInstance(), new ItemStack[36]));
		entity.setMetadata("CombatLoggerArmorInventory", new FixedMetadataValue(CombatTagPlugin.getInstance(), new ItemStack[4]));
		this.entity = entity;

		// Add to our map
		CombatTagPlugin.getInstance().addCombatTagLogger(this.uuid, this);

		Integer num = CombatTagListener.chunksWithTagsInThem.get(location.getChunk());
		if (num == null) {
			CombatTagListener.chunksWithTagsInThem.put(location.getChunk(), 1);
		} else {
			CombatTagListener.chunksWithTagsInThem.put(location.getChunk(), num + 1);
		}
	}

	/**
	 * Creates a combat logger for an online player.
	 *
	 * @param player - Player
	 */
	public LoggerNPC(Player player) {
		this(player, "");
	}

	/**
	 * Creates a combat logger for an online player.
	 *
	 * @param player - Player
	 * @param namePrefix - Prefix for the combat logger's custom name
	 */
    public LoggerNPC(Player player, String namePrefix) {
        this.uuid = player.getUniqueId();

        // Create the NPC
        Zombie entity = (Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
        EntityUtil.clearAI(entity);
        entity.setCustomName(namePrefix + player.getDisguisedName());
        entity.setCustomNameVisible(true);
        entity.setHealth(player.getHealth());
        entity.setCanPickupItems(true);
        entity.setBaby(false);
        entity.setVillager(false);
        entity.setMetadata("CombatLoggerNPC", new FixedMetadataValue(CombatTagPlugin.getInstance(), this.uuid));
        entity.getEquipment().clear();
        entity.getEquipment().setArmorContents(new ItemStack[4]);
        entity.getEquipment().setArmorContents(player.getInventory().getArmorContents());
        entity.getEquipment().setHelmetDropChance(0F);
        entity.getEquipment().setChestplateDropChance(0F);
        entity.getEquipment().setLeggingsDropChance(0F);
        entity.getEquipment().setBootsDropChance(0F);
        entity.getEquipment().setItemInHand(player.getItemInHand());
        entity.getEquipment().setItemInHandDropChance(0F);
        entity.setCanPickupItems(false);
        entity.setMetadata("CombatLoggerInventory", new FixedMetadataValue(CombatTagPlugin.getInstance(), player.getInventory().getContents()));
        entity.setMetadata("CombatLoggerArmorInventory", new FixedMetadataValue(CombatTagPlugin.getInstance(), player.getInventory().getArmorContents()));
        this.entity = entity;
        this.player = player;

        // Add to our map
        CombatTagPlugin.getInstance().addCombatTagLogger(this.uuid, this);

        Integer num = CombatTagListener.chunksWithTagsInThem.get(player.getLocation().getChunk());
        if (num == null) {
            CombatTagListener.chunksWithTagsInThem.put(player.getLocation().getChunk(), 1);
        } else {
            CombatTagListener.chunksWithTagsInThem.put(player.getLocation().getChunk(), num + 1);
        }
    }

    public void remove(REMOVE_REASON reason) {
        CombatTagDestroyEvent combatTagJoinEvent = new CombatTagDestroyEvent(this, reason);
        CombatTagPlugin.getInstance().getServer().getPluginManager().callEvent(combatTagJoinEvent);

        Integer num = CombatTagListener.chunksWithTagsInThem.get(this.entity.getLocation().getChunk());
        if (num != null && num == 1) {
            CombatTagListener.chunksWithTagsInThem.remove(this.entity.getLocation().getChunk());
        }

        if (reason == REMOVE_REASON.DEATH) {
            CombatTagDropInventoryEvent event = new CombatTagDropInventoryEvent(this, this.getArmor(), this.getInventory());
            CombatTagPlugin.getInstance().getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                // Drop their items
	            for (ItemStack item : event.getArmor()) {
		            if (item != null && item.getType() != Material.AIR) {
			            Item droppedItem = this.entity.getWorld().dropItemNaturally(this.entity.getLocation(), item);
			            droppedItem.setAge(event.getDroppedItemAge());
		            }
	            }

                for (ItemStack item : event.getInventory()) {
                    if (item != null && item.getType() != Material.AIR) {
                        Item droppedItem = this.entity.getWorld().dropItemNaturally(this.entity.getLocation(), item);
	                    droppedItem.setAge(event.getDroppedItemAge());
                    }
                }
            }
        }

        if (!this.entity.isDead()) {
            this.entity.remove();
        }

        CombatTagPlugin.getInstance().removeCombatTagged(this.uuid);
    }

	public ItemStack[] getArmor() {
		return (ItemStack[]) this.getEntity().getMetadata("CombatLoggerArmorInventory").get(0).value();
	}

	public void setArmor(ItemStack[] contents) {
		this.entity.setMetadata("CombatLoggerArmorInventory", new FixedMetadataValue(CombatTagPlugin.getInstance(), contents));
	}

	public ItemStack[] getInventory() {
		return (ItemStack[]) this.getEntity().getMetadata("CombatLoggerInventory").get(0).value();
	}

	public void setInventory(ItemStack[] contents) {
		this.entity.setMetadata("CombatLoggerInventory", new FixedMetadataValue(CombatTagPlugin.getInstance(), contents));
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public Zombie getEntity() {
		return this.entity;
	}

	public Player getPlayer() {
		if (this.player == null) {
			throw new RuntimeException("Player is null for " + this.uuid + "'s combat logger");
		}

		return this.player;
	}

}

