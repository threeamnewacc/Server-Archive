package net.badlion.cosmetics.pets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.customparticles.SnowmanEffect;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.gberry.UnregistrableListener;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class SnowmanPet extends Pet implements Listener, UnregistrableListener {

    public static SnowmanPet instance;

    private int i = 0;

    public SnowmanPet() {
        super("snowman", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.SNOW_BALL, ChatColor.GREEN + "Snowman", ChatColor.GRAY + "Own a personal snowman!"));

        this.permission = "badlion.pets.snowman";

        // Register listener
        Cosmetics.getInstance().getServer().getPluginManager().registerEvents(this, Cosmetics.getInstance());

        SnowmanPet.instance = this;
    }

    @Override
    public LivingEntity spawnPet(Player player, CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        Snowman snowman = (Snowman) this.handlePetInitialization(player, EntityType.SNOWMAN, cosmeticsSettings);

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "DIG_SNOW", "BLOCK_SNOW_BREAK"), 1F, 1F);
        PetManager.addSpawnedPet(player, snowman);

        return snowman;
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
        // Feet particles
        SnowmanEffect.spawnParticle((Snowman) entity, player);
        // Shoot a snowball
        if (i == 20) {
            Snowball snowball = entity.getWorld().spawn(entity.getEyeLocation(), Snowball.class);
            snowball.setShooter(entity);
            snowball.setMetadata("petsnowball", new FixedMetadataValue(Cosmetics.getInstance(), "petsnowball"));
            snowball.setVelocity(entity.getLocation().getDirection().multiply(1.5));
            player.playSound(entity.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "SHOOT_ARROW", "ENTITY_ARROW_SHOOT"), 1.0f, 1.0f);
            i = 0;
        }
        i++;
    }

    @Override
    public void despawn(Player player) {

    }

    // Stop snowmen shooting snowballs
    @EventHandler
    public void onSnowmanShoot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Snowball && event.getEntity().getServer() instanceof Snowman && !event.getEntity().hasMetadata("petsnowball")) {
            event.setCancelled(true);
        }
    }

    // Stop the snowman targeting things
    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Snowman) {
            event.setCancelled(true);
        }
    }

    // Stop snowmen creating snow
    @EventHandler
    public void onEntityFormBlock(final EntityBlockFormEvent event) {
        if (event.getNewState().getType() == Material.SNOW) {
            event.setCancelled(true);
            //Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
            //	@Override
            //	public void run() {
            //		event.getBlock().setType(Material.AIR);
            //	}
            //}, 1L);
        }
    }

    @Override
    public void unregister() {
        EntityTargetEvent.getHandlerList().unregister(this);
        ProjectileLaunchEvent.getHandlerList().unregister(this);
        EntityBlockFormEvent.getHandlerList().unregister(this);
    }
}
