package net.badlion.cosmetics.managers;

import net.badlion.common.libraries.StringCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.pets.*;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PetManager implements Listener {

    public static Map<String, Pet> pets = new LinkedHashMap<>();
    private static Map<UUID, LivingEntity> spawnedPets = new HashMap<>();

    static {
        // pet_internalname --> pet_balloon_bat
        PetManager.pets.put("balloon_bat", new BalloonBatPet());
        PetManager.pets.put("cat_in_a_hat", new CatInAHatPet());
        PetManager.pets.put("lion", new LionPet());
        PetManager.pets.put("lucky_sheep", new LuckySheepPet());
        PetManager.pets.put("nyan_sheep", new NyanSheepPet());
        PetManager.pets.put("slime", new SlimePet());
        PetManager.pets.put("pig", new PigPet());
        PetManager.pets.put("skeleton_horse", new SkeletonHorsePet());
        PetManager.pets.put("undead_horse", new UndeadHorsePet());
        PetManager.pets.put("horse", new HorsePet());
        PetManager.pets.put("ocelot", new OcelotPet());
        PetManager.pets.put("blaze", new BlazePet());
        PetManager.pets.put("snowman", new SnowmanPet());
        PetManager.pets.put("charged_creeper", new CreeperPet());
        PetManager.pets.put("baby_cow", new CowPet());
        PetManager.pets.put("baby_chicken", new ChickenPet());
    }

    public static boolean isSmellyPet(String petName) {
        return PetManager.pets.containsKey(petName);
    }

    public static boolean isSmellyPet(Entity entity) {
        return entity.hasMetadata("smelly_pet_owner");
    }

    public static void addPet(final CommandSender sender, final Player player, final String smellyPetName) {
        PetManager.addPet(sender, player, smellyPetName, true);
    }

    public static void addPet(final CommandSender sender, final Player player, final String smellyPetName, final boolean verbose) {
        // Is pet name valid?
        if (!PetManager.isSmellyPet(smellyPetName)) {
            sender.sendMessage(ChatColor.RED + "Pet " + smellyPetName + " not found.");
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
        if (cosmeticsSettings == null) {
            cosmeticsSettings = CosmeticsManager.createCosmeticsSettings(player.getUniqueId());
        }

        // Does player already have pet?
        if (cosmeticsSettings.hasPet(smellyPetName)) {
            sender.sendMessage(ChatColor.RED + player.getName() + " already has the pet " + smellyPetName);
            return;
        }

        // Add pet to cache if player is online
        cosmeticsSettings.addPet(smellyPetName);

        // Send message to command sender
        if (verbose) {
            sender.sendMessage(ChatColor.GREEN + "Gave pet " + smellyPetName + " to " + player.getName());
        }
    }

    public static void spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings, Pet pet) {
        if ((pet.isAllowedForAllPermissions() && player.hasPermission("badlion.allcosmetics")) || cosmeticsSettings.hasPet(pet.getName())) {
            // Do they already have a pet spawned?

            if (cosmeticsSettings.getActivePet() != null) {
                // Despawn their current pet
                PetManager.despawnPet(player, null);
            }

            cosmeticsSettings.setActivePet(pet, true);
            LivingEntity entity = pet.spawnPet(player, cosmeticsSettings);

            if (!(entity instanceof Sheep) && !entity.getCustomName().contains("_jeb"))
                player.sendMessage(ChatColor.GREEN + "Spawned " + entity.getCustomName().substring(2).replace(player.getName() + "'s ", ""));
            else player.sendMessage(ChatColor.GREEN + "Spawned Nyan Sheep");
        } else {
            player.sendMessage(ChatColor.RED + "You don't own this pet.");
        }
    }

    public static void renamePet(Player player, String customName) {
        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
        LivingEntity entity = PetManager.spawnedPets.get(player.getUniqueId());
        entity.setCustomName(customName);

        String smellyPetName = entity.getMetadata("smelly_pet_name").get(0).value().toString();
        cosmeticsSettings.renamePet(smellyPetName, customName);
    }

    public static void despawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        PetManager.despawnPet(player, cosmeticsSettings, false);
    }

    public static void despawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings, boolean shuttingDown) {
        LivingEntity entity;
        if (shuttingDown) {
            entity = PetManager.spawnedPets.get(player.getUniqueId());
        } else {
            entity = PetManager.spawnedPets.remove(player.getUniqueId());
        }

        if (entity != null) {
            PetManager.getPet(entity.getMetadata("smelly_pet_name").get(0).value().toString()).despawn(player);

            // Cancel task if exists
            if (entity.hasMetadata("smelly_pet_task")) {
                ((BukkitTask) entity.getMetadata("smelly_pet_task").get(0).value()).cancel();
            }

            entity.remove();

            // We do this check because in onDisable() and when player quits we call this,
            // but we still want this pet to be set as their active pet, we just want to despawn the pet
            // since the player is logging out (also don't send message)
            if (cosmeticsSettings != null) {
                cosmeticsSettings.setActivePet(null, true);

                //player.sendMessage(ChatColor.YELLOW + "You have despawned " + entity.getCustomName());
            }
        }
    }

    public static Pet getPet(String smellyPetName) {
        return PetManager.pets.get(smellyPetName);
    }

    public static void addSpawnedPet(Player player, LivingEntity entity) {
        PetManager.spawnedPets.put(player.getUniqueId(), entity);
    }

    public static Map<UUID, LivingEntity> getSpawnedPets() {
        return spawnedPets;
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        // Remove a pet if they have one out
        PetManager.despawnPet(event.getPlayer(), null);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!Cosmetics.getInstance().isPetsEnabled()) {
            return;
        }

        // Despawn Pet tool
        if (event.getItem() == null) return;
        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(event.getPlayer().getUniqueId());
        if (event.getItem().getType() == Material.BLAZE_POWDER) {
            event.getPlayer().getInventory().setItem(3, null);
            PetManager.despawnPet(event.getPlayer(), cosmeticsSettings);
        } else if (event.getItem().getType() == Material.ANVIL) {
            Player player = event.getPlayer();
            player.sendMessage(ChatColor.YELLOW + "You have unequipped " + StringCommon.niceUpperCase(cosmeticsSettings.getActiveMorph().getName()));
            cosmeticsSettings.getActiveMorph().removeMorph(player);
            cosmeticsSettings.setActiveMorph(null, true);
        }
    }

    public void activateListeners() {
        Cosmetics.getInstance().getServer().getPluginManager().registerEvents(BalloonBatPet.instance, Cosmetics.getInstance());
        Cosmetics.getInstance().getServer().getPluginManager().registerEvents(BlazePet.instance, Cosmetics.getInstance());
        Cosmetics.getInstance().getServer().getPluginManager().registerEvents(CreeperPet.instance, Cosmetics.getInstance());
        Cosmetics.getInstance().getServer().getPluginManager().registerEvents(SnowmanPet.instance, Cosmetics.getInstance());
    }

    public void deactivateListeners() {
        BukkitUtil.unregisterListener(BalloonBatPet.instance);
        BukkitUtil.unregisterListener(BlazePet.instance);
        BukkitUtil.unregisterListener(CreeperPet.instance);
        BukkitUtil.unregisterListener(SnowmanPet.instance);
    }

}
