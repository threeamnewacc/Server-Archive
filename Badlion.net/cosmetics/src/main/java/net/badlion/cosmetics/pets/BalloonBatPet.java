package net.badlion.cosmetics.pets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.gberry.UnregistrableListener;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.util.Vector;

public class BalloonBatPet extends Pet implements Listener, UnregistrableListener {

    public static BalloonBatPet instance;

    public BalloonBatPet() {
        super("balloon_bat", ItemRarity.COMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, (short) 65, ChatColor.GREEN + "Balloon Bat", ChatColor.GRAY + "Hold your own bat, like a balloon!"));

        Cosmetics.getInstance().getServer().getPluginManager().registerEvents(this, Cosmetics.getInstance());

        this.permission = "badlion.pets.balloonbat";

        BalloonBatPet.instance = this;
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        Bat bat = (Bat) this.handlePetInitialization(player, EntityType.BAT, cosmeticsSettings);
        bat.setAwake(true);
        bat.setLeashHolder(player);
        bat.clearAIGoals();

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "SHEEP_IDLE", "ENTITY_SHEEP_AMBIENT"), 1F, 1F);
        PetManager.addSpawnedPet(player, bat);

        return bat;
    }

    @Override
    public void despawn(Player player) {

    }

    @Override
    public void run(Player player, LivingEntity entity) {
        if (!((Bat) entity).isAwake()) {
            // Fixes the bat sleeping
            ((Bat) entity).setAwake(true);
        }
        if (player.getLocation().distance(entity.getLocation()) >= 3.5D) {
            Vector velocity = player.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
            entity.setVelocity(entity.getVelocity().add(velocity));
            entity.setVelocity(entity.getVelocity().multiply(0.5));
        }
    }

    // Disable players unleashing the bat
    @EventHandler
    public void onPlayerUnleash(PlayerUnleashEntityEvent event) {
        if (event.getEntity() instanceof Bat) {
            event.setCancelled(true);
        }
    }

    @Override
    public void unregister() {
        PlayerUnleashEntityEvent.getHandlerList().unregister(this);
    }

}
