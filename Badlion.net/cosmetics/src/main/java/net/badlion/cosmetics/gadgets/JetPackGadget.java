package net.badlion.cosmetics.gadgets;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.managers.FlightGCheatManager;
import net.badlion.cosmetics.particles.Particle;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JetPackGadget extends Gadget {

    public static ArrayList<UUID> playerJetPackRecharging = new ArrayList<>();
    public static Map<UUID, Double> playerJetPackFuel = new HashMap<>();

    public JetPackGadget() {
        super("jetpack", ItemRarity.SUPER_RARE, ItemStackUtil.createItem(Material.BLAZE_ROD, ChatColor.GREEN + "JetPack", ChatColor.GRAY + "JetPacks are sweet, you should", ChatColor.GRAY + "try out this one!"));
    }

    public static double getJetPackFuel(Player player) {
        Double fuel = playerJetPackFuel.get(player.getUniqueId());
        if (fuel == null) {
            fuel = 0.0D;
            playerJetPackFuel.put(player.getUniqueId(), fuel);
        }

        return fuel;
    }

    public static String getJetPackFuelString(Player player) {
        String newString = ChatColor.GREEN + "";
        double jetPackFuel = getJetPackFuel(player);
        for (int i = 0; i < 20 - jetPackFuel; i++) {
            newString += "|";
        }
        newString += ChatColor.RED;
        for (int i = 0; i < jetPackFuel; i++) {
            newString += "|";
        }
        return newString;
    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.BLAZE_ROD) {
            Player player = event.getPlayer();
            if (playerJetPackRecharging.contains(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Your JetPack is recharging!");
                return;
            }
            if (getJetPackFuel(player) >= 20) {
                playerJetPackRecharging.add(player.getUniqueId());
                return;
            }
            for (int i = 0; i < 5; i++) {
                player.getWorld().spigot().playEffect(Particle.getParticleLocation(player.getLocation(), 1.0D), Effect.ENDER_SIGNAL, 0, 0,
                        0.1337F, 0.1337F, 0.1337F, 0.05F, 9, 64);
            }
            FlightGCheatManager.addToMapping(player, 20);
            player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "WITHER_SHOOT", "ENTITY_WITHER_SHOOT"), 1.8f, 1.0f);
            player.setVelocity(player.getLocation().getDirection().multiply(1.0D));
            player.setVelocity(player.getVelocity().add(new Vector(0.0D, 1.0D, 0.0D)));
            playerJetPackFuel.put(player.getUniqueId(), getJetPackFuel(player) + 2.0D);
        }
    }

    @Override
    public void giveGadget(Player player) {
        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.BLAZE_ROD, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "JetPack " + getJetPackFuelString(player)));
    }

}
