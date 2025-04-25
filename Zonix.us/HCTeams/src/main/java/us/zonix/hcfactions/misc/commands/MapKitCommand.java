package us.zonix.hcfactions.misc.commands;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.enchantmentlimiter.EnchantmentLimiter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MapKitCommand implements Listener {

    public MapKitCommand() {
        Bukkit.getPluginManager().registerEvents(this, FactionsPlugin.getInstance());
    }

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/mapkit")) {

            if (event.getMessage().startsWith("/mapkit")) {
                if (!(event.getMessage().equalsIgnoreCase("/mapkit")) && event.getMessage().toCharArray()[7] != ' ') {
                    return;
                }
            }

            event.getPlayer().sendMessage(ChatColor.BLUE + "Enchant Limits: " + ChatColor.GRAY + "Protection " + EnchantmentLimiter.getInstance().getEnchantmentLimit(Enchantment.PROTECTION_ENVIRONMENTAL) + ", Sharpness " + EnchantmentLimiter.getInstance().getEnchantmentLimit(Enchantment.DAMAGE_ALL) + ", Power " + EnchantmentLimiter.getInstance().getEnchantmentLimit(Enchantment.ARROW_DAMAGE));
            event.setCancelled(true);
        }
    }

}
