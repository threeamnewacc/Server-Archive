package net.badlion.potpvp.rulesets;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FourMinutePotionRuleSet extends KitRuleSet {

    public FourMinutePotionRuleSet(int id, String name, ItemStack itemStack, ArenaManager.ArenaType arenaType, boolean usesCustomChests, boolean allowsExtraArmorSets) {
        super(id, name, itemStack, arenaType, usesCustomChests, allowsExtraArmorSets);
    }

    @EventHandler(priority= EventPriority.LOW)
    public void onPotionUse(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);

        // Make sure we are using a rule set that uses the 4 minute potions
        if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
            if (event.getItem() != null && event.getItem().getType() == Material.POTION) {
                Potion potion = Potion.fromItemStack(event.getItem());
                for (PotionEffect effect : potion.getEffects()) {
                    if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                        player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE); // Force reset
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 4, 1));
                        player.setItemInHand(null);
                        event.setItem(null);
                        event.setCancelled(true);
                    } else if (effect.getType().equals(PotionEffectType.SPEED)) {
                        player.removePotionEffect(PotionEffectType.SPEED); // Force reset
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 * 4, 1));
                        player.setItemInHand(null);
                        event.setItem(null);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPotionThrown(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof ThrownPotion) {
            Group group = PotPvP.getInstance().getPlayerGroup((Player) event.getEntity().getShooter());

            // Make sure we are using a rule set that uses the 4 minute potions
            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                ThrownPotion potion = (ThrownPotion) event.getEntity();
                for (PotionEffect effect : potion.getEffects()) {
                    if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                        potion.setItem(new ItemStack(Material.POTION, 1, (short) 16489));
                    } else if (effect.getType().equals(PotionEffectType.SPEED)) {
                        potion.setItem(new ItemStack(Material.POTION, 1, (short) 16482));
                    }
                }
            }
        }
    }
}
