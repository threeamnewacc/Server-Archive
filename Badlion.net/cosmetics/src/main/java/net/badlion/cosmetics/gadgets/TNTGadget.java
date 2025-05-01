package net.badlion.cosmetics.gadgets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.FlightGCheatManager;
import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TNTGadget extends Gadget {

    public Map<UUID, Long> lastTNTTimes = new HashMap<>();

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.EXPLOSION_HUGE, 0, 1, 0);

    public TNTGadget() {
        super("tnt", ItemRarity.COMMON, ItemStackUtil.createItem(Material.TNT, ChatColor.GREEN + "TNT", ChatColor.GRAY + "Bang, bang, into the room!"));
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.TNT) {
            event.setCancelled(true);
            final Player player = event.getPlayer();
            if (lastTNTTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastTNTTimes.get(player.getUniqueId()) <= 1000 * 20) {
                player.sendMessage(ChatColor.RED + "Please wait " + (20 - (Math.round(System.currentTimeMillis() - lastTNTTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
                return;
            }
            final TNTPrimed tnt = player.getWorld().spawn(player.getLocation().add(0.0D, 1.0D, 0.0D), TNTPrimed.class);
            tnt.setFuseTicks(30);
            tnt.setVelocity(player.getLocation().getDirection().multiply(1));

            Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    TNTGadget.this.particle.sendToLocation(player, tnt.getLocation(), true, 0.3D, 5);
                    tnt.getWorld().playSound(tnt.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "EXPLODE", "ENTITY_GENERIC_EXPLODE"), 1.0f, 1.0f);

                    for (Entity e : tnt.getWorld().getEntities()) {
                        if (e.getLocation().distance(tnt.getLocation()) <= 5.0D) {
                            FlightGCheatManager.addToMapping(player, 20 * 5);
                            e.setVelocity(new Vector(0.0D, 2.0D, 0.0D));
                        }
                    }
                }
            }, 30L);
            this.lastTNTTimes.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.TNT, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "TNT"));
    }
}
