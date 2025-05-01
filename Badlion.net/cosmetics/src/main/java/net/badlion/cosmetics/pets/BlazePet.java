package net.badlion.cosmetics.pets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.gberry.UnregistrableListener;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class BlazePet extends Pet implements Listener, UnregistrableListener {

    public static BlazePet instance;

    public BlazePet() {
        super("blaze", ItemRarity.RARE, ItemStackUtil.createItem(Material.MONSTER_EGG, (short) 61, ChatColor.GREEN + "Blaze", ChatColor.GRAY + "Blaze it."));

        // Register listener
        Cosmetics.getInstance().getServer().getPluginManager().registerEvents(this, Cosmetics.getInstance());

        // Permission
        this.permission = "badlion.pets.blaze";

        BlazePet.instance = this;
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        Blaze blaze = (Blaze) this.handlePetInitialization(player, EntityType.BLAZE, cosmeticsSettings);
        blaze.setFireTicks(0);
        blaze.clearAIGoals();

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "BLAZE_BREATH", "ENTITY_BLAZE_SHOOT"), 1F, 1F);
        PetManager.addSpawnedPet(player, blaze);

        return blaze;
    }

    @Override
    public void despawn(Player player) {

    }

    // Shoot custom fireballs
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
        if (cosmeticsSettings == null) {
            return;
        }

        Pet activePet = cosmeticsSettings.getActivePet();
        if (activePet != null && activePet instanceof BlazePet) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR && event.getItem() != null && event.getItem().getType() == Material.MONSTER_EGG) {
                for (Player pl : player.getWorld().getPlayers()) {
                    if (player.hasLineOfSight(pl)) {
                        ((BlazePet) activePet).launchFireball(PetManager.getSpawnedPets().get(player.getUniqueId()), pl.getLocation());
                    }
                }
            }
        }
    }

    // Stop the blaze shooting
    @EventHandler
    public void onFireballLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Blaze) {
            event.setCancelled(true);
        }
    }

    // Stop the blaze targeting players
    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Blaze && event.getTarget() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @Override
    public void unregister() {
        ProjectileLaunchEvent.getHandlerList().unregister(this);
        EntityTargetEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);
    }

    private void launchFireball(LivingEntity blaze, Location location) {
        Fireball fireball = location.getWorld().spawn(blaze.getEyeLocation().add(blaze.getLocation().getDirection().multiply(1)), Fireball.class);
        fireball.setDirection(location.toVector());
        fireball.setShooter(blaze);
        fireball.setIsIncendiary(true);
        fireball.setBounce(false);
    }

    @Override
    public void run(Player player, LivingEntity entity) {
        if (player.getLocation().distance(entity.getLocation()) >= 2.0D) {
            Vector velocity = player.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
            entity.setVelocity(entity.getVelocity().add(velocity));
            entity.setVelocity(entity.getVelocity().multiply(0.5));
        }
        entity.setFireTicks(0);
    }

}
