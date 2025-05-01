package net.badlion.cosmetics.pets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.UnregistrableListener;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class CreeperPet extends Pet implements Listener, UnregistrableListener {

    public static CreeperPet instance;

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.EXPLOSION_LARGE, 0, 1, 0);

    private int i = 0;

    public CreeperPet() {
        super("charged_creeper", ItemRarity.RARE, ItemStackUtil.createItem(Material.SKULL_ITEM, 1, (byte) 4, ChatColor.GREEN + "Charged Creeper", ChatColor.GRAY + "Don't let it explode!"));

        this.permission = "badlion.pets.creeper";

        // Register listener
        Cosmetics.getInstance().getServer().getPluginManager().registerEvents(this, Cosmetics.getInstance());

        CreeperPet.instance = this;
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        Creeper creeper = (Creeper) this.handlePetInitialization(player, EntityType.CREEPER, cosmeticsSettings);
        creeper.setPowered(true);

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CREEPER_HISS", "ENTITY_CREEPER_PRIMED"), 1F, 1F);
        PetManager.addSpawnedPet(player, creeper);

        return creeper;
    }

    @Override
    public void run(Player player, LivingEntity entity) {
        // Walk to player
        if (player.getLocation().distance(entity.getLocation()) >= 5) {
            entity.teleport(player);
        } else if (player.getLocation().distance(entity.getLocation()) >= 2) {
            this.walkTo(entity, player.getLocation().getX(), player.getLocation().getY(),
                    player.getLocation().getZ(), 1.2D);
        }
        // Strike lightning every 100 ticks (this is called every 2 ticks)
        if (++this.i == 50) {
            for (Player wp : entity.getWorld().getPlayers()) {
                if (wp.getLocation().distance(entity.getLocation()) <= 10) {
                    wp.getWorld().spigot().strikeLightningEffect(wp.getLocation(), true);
                }
            }
            this.particle.sendToLocation(player, player.getLocation(), true, 0.3D, 10);
            this.i = 0;
        }
    }

    @Override
    public void despawn(Player player) {

    }

    // Stop the creeper targeting players
    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Creeper && event.getTarget() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @Override
    public void unregister() {
        EntityTargetEvent.getHandlerList().unregister(this);
    }

}
