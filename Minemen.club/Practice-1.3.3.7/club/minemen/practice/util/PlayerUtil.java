// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.util;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.potion.PotionEffect;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class PlayerUtil
{
    private PlayerUtil() {
    }
    
    public static void setFirstSlotOfType(final Player player, final Material type, final ItemStack itemStack) {
        for (int i = 0; i < player.getInventory().getContents().length; ++i) {
            final ItemStack itemStack2 = player.getInventory().getContents()[i];
            if (itemStack2 == null || itemStack2.getType() == type || itemStack2.getType() == Material.AIR) {
                player.getInventory().setItem(i, itemStack);
                break;
            }
        }
    }
    
    public static void clearPlayer(final Player player) {
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(12.8f);
        player.setMaximumNoDamageTicks(20);
        player.setFireTicks(0);
        player.setFallDistance(0.0f);
        player.setLevel(0);
        player.setExp(0.0f);
        player.setWalkSpeed(0.2f);
        player.getInventory().setHeldItemSlot(0);
        player.setAllowFlight(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents((ItemStack[])null);
        player.closeInventory();
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
        ((CraftPlayer)player).getHandle().getDataWatcher().watch(9, (Object)0);
        player.updateInventory();
    }
    
    public static void sendMessage(final String message, final Player... players) {
        for (final Player player : players) {
            player.sendMessage(message);
        }
    }
}
