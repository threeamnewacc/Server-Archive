package net.badlion.cosmetics.listeners;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.cosmetics.pets.Pet;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.HorseInventory;

import java.util.HashSet;
import java.util.Set;

public class PetListener implements Listener {

    private Set<Player> renamingPet = new HashSet<>();

    @EventHandler
    public void onPlayerDismountRideableEvent(VehicleExitEvent event) {
        if (event.getExited() instanceof Player) {
            Player player = (Player) event.getExited();
            if (PetManager.isSmellyPet(event.getVehicle())) {
                // Hardcode for void check
                if (player.getLocation().getY() > 0) {
                    // Despawn the pet
                    PetManager.despawnPet(player, CosmeticsManager.getCosmeticsSettings(player.getUniqueId()));

                    String customName = ((LivingEntity) event.getVehicle()).getCustomName();
                    if (customName != null) {
                        player.sendMessage(ChatColor.GREEN + "You have dismounted " + ChatColor.stripColor(customName).replace(player.getName() + "'s ", "") + "!");
                    } else {
                        player.sendMessage(ChatColor.GREEN + "You have dismounted your pet!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(event.getPlayer().getUniqueId());
        if (cosmeticsSettings == null) {
            return;
        }

        Pet pet = cosmeticsSettings.getActivePet();
        if (pet == null) {
            return;
        }

        LivingEntity petEntity = PetManager.getSpawnedPets().get(event.getPlayer().getUniqueId());
        if (petEntity != null && petEntity.getPassenger() != null
                && petEntity.getPassenger() instanceof Player) {
            petEntity.setPassenger(null); // Horse pets
        }

        event.getPlayer().setPassenger(null); // For 'cat in hat' pet

        PetManager.despawnPet(event.getPlayer(), cosmeticsSettings);
    }

    @EventHandler
    public void onSheepEatGrassEvent(EntityChangeBlockEvent event) {
        if (PetManager.isSmellyPet(event.getEntity()) && event.getEntity() instanceof Sheep
                && (event.getTo() == Material.DIRT || event.getTo() == Material.AIR)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPetEnterPortalEvent(EntityPortalEnterEvent event) {
        if (PetManager.isSmellyPet(event.getEntity())) {
            String ownerName = event.getEntity().getMetadata("smelly_pet_owner").get(0).value().toString();
            Player owner = Cosmetics.getInstance().getServer().getPlayerExact(ownerName);
            event.getEntity().teleport(owner.getLocation());
        }
    }

	/*@EventHandler
    public void onPetPickupEvent(PlayerInteractEntityEvent event) {
		if (PetManager.isSmellyPet(event.getRightClicked())) {
			Player player = event.getPlayer();

			event.setCancelled(true);

			// Is the interacter the owner of the pet?
			String ownerName = event.getRightClicked().getMetadata("smelly_pet_owner").get(0).value().toString();
			if (player == Cosmetics.getInstance().getServer().getPlayerExact(ownerName)) {
				// Did they shift right click?
				if (player.isSneaking()) {
					PetManager.despawnPet(player, CosmeticsManager.getCosmeticsSettings(player.getUniqueId()));

					String customName = ((LivingEntity) event.getRightClicked()).getCustomName();
					if (customName != null) {
						player.sendMessage(ChatColor.GREEN + "You have picked up " + ChatColor.stripColor(customName).replace(ownerName + "'s ", "") + "!");
					} else {
						player.sendMessage(ChatColor.GREEN + "You have picked up your pet!");
					}
				}
			}
		}
	}*/

    @EventHandler
    public void onPetDamageEvent(EntityDamageEvent event) {
        if (PetManager.isSmellyPet(event.getEntity())) {
            event.setDamage(0D);
            event.setCancelled(true);

            // Is damager the owner of the pet?
            if (event instanceof EntityDamageByEntityEvent) {
                String ownerName = (String) event.getEntity().getMetadata("smelly_pet_owner").get(0).value();
                if (((EntityDamageByEntityEvent) event).getDamager() == Cosmetics.getInstance().getServer().getPlayerExact(ownerName)) {
                    Player player = ((Player) ((EntityDamageByEntityEvent) event).getDamager());

                    // Did they shift left click?
                    // TODO: Enable later if we want to
					/*if (player.isSneaking()) {
						// Rename pet
						this.renamingPet.add(player);

						player.sendMessage(ChatColor.AQUA + "Please enter a new name for your pet or type 'cancel' to cancel (max 16 characters):");
					}*/
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (this.renamingPet.contains(player)) {
            event.setCancelled(true);

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                this.renamingPet.remove(player);
                player.sendMessage(ChatColor.RED + "You have cancelled the name change.");
                return;
            }

            if (event.getMessage().length() > 16) {
                player.sendMessage(ChatColor.RED + "Name entered was too long, maximum length is 16 characters.");
            } else {
                this.renamingPet.remove(player);
                PetManager.renamePet(player, event.getMessage());

                player.sendMessage(ChatColor.GREEN + "You have renamed your pet to \"" + event.getMessage() + "\"!");
            }
        }
    }

    @EventHandler(priority = EventPriority.LAST, ignoreCancelled = false)
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onHorseInventoryClickEvent(InventoryClickEvent event) {
        if (event.getWhoClicked().isOp()) {
            return;
        }

        if (event.getClickedInventory() instanceof HorseInventory) {
            Horse horse = ((Horse) event.getClickedInventory().getHolder());
            if (PetManager.isSmellyPet(horse)) {
                event.setCancelled(true);
            }
        }
    }

}
