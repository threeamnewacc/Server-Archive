package net.badlion.cosmetics.gadgets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.particles.Particle;
import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class LionBlasterGadget extends Gadget {

    private static ParticleLibrary particleLibrary = new ParticleLibrary(ParticleLibrary.ParticleType.FLAME, 0, 1, 0);
    public Map<UUID, Long> lastLionBlasterTimes = new HashMap<>();

    public LionBlasterGadget() {
        super("lion_blaster", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.GOLD_BARDING, ChatColor.GREEN + "Lion Blaster", ChatColor.GRAY + "Show your love for Badlion,", ChatColor.GRAY + "by shooting your own Lions!"));
    }

    public static void shootLions(final Player player) {
        final List<Entity> lions = new ArrayList<>();
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CAT_MEOW", "ENTITY_CAT_PURREOW"), 1.0f, 1.0f);
        LionBlasterGadget.particleLibrary.sendToLocation(player, player.getEyeLocation().add(player.getLocation().getDirection().multiply(0.2D)), true, 1.0, 10);
        for (int i = 0; i < 10; i++) {
            double x = -0.5F + (float) (Math.random() * 1.0D);
            double y = -0.5F + (float) (Math.random() * 1.0D);
            double z = -0.5F + (float) (Math.random() * 1.0D);
            Ocelot ocelot = (Ocelot) player.getWorld().spawnEntity(player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.0D)), EntityType.OCELOT);
            ocelot.setBaby();
            ocelot.setVelocity(player.getLocation().getDirection().multiply(2).add(new Vector(x, y, z)));
            lions.add(ocelot);
        }

        Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.SPELL, 0, 1, 0);
                for (Entity e : lions) {
                    particle.sendToLocation(player, Particle.getParticleLocation(e.getLocation(), 1.0D));
                    e.remove();
                }
            }
        }, 10L);
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.GOLD_BARDING) {
            Player player = event.getPlayer();

            if (this.lastLionBlasterTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastLionBlasterTimes.get(player.getUniqueId()) <= 1000 * 3) {
                player.sendMessage(ChatColor.RED + "Please wait " + (3 - (Math.round(System.currentTimeMillis() - lastLionBlasterTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
                event.setCancelled(true);
                return;
            }
            LionBlasterGadget.shootLions(player);
            this.lastLionBlasterTimes.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.GOLD_BARDING, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Lion Blaster"));
    }
}
