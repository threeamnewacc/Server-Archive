package us.zonix.hcfactions.potionlimiter;

import us.zonix.hcfactions.FactionsPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import us.zonix.hcfactions.FactionsPlugin;

public class PotionLimiterListeners implements Listener {

    private static FactionsPlugin main = FactionsPlugin.getInstance();

    @EventHandler
    public void onPotionSplashEvent(PotionSplashEvent event) {
        ItemStack itemStack = event.getPotion().getItem();
        if (PotionLimiter.getInstance().isBlocked(itemStack.getDurability())) {

            if (event.getPotion().getShooter() instanceof Player) {
                Player player = (Player) event.getPotion().getShooter();
                player.sendMessage(main.getLanguageConfig().getString("POTION_LIMITER.BLOCKED"));
                player.updateInventory();
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getType() == Material.POTION) {
            if (PotionLimiter.getInstance().isBlocked(event.getItem().getDurability())) {
                player.sendMessage(main.getLanguageConfig().getString("POTION_LIMITER.BLOCKED"));
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }

}
