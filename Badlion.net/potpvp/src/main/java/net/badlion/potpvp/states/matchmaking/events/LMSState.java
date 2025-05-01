package net.badlion.potpvp.states.matchmaking.events;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class LMSState extends GameState implements Listener {

    public LMSState() {
        super("lms", "they are in an LMS.", GroupStateMachine.getInstance());
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity human = event.getView().getPlayer();
        if (human instanceof Player) {
            Player player = (Player) human;
            Group group = PotPvP.getInstance().getPlayerGroup(player);
            if (this.contains(group)) {
                if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot change armor while in this game mode.");
                }
            }
        }
    }

    @EventHandler
    public void onArmorBreakEvent(PlayerItemBreakEvent event) {
        for (ItemStack item : ItemStackUtil.ALL_ARMOR()) {
            if (item.getType() == event.getBrokenItem().getType()) {
                if (LMSState.isNaked(event.getPlayer())) {
                    event.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
                }
            }
        }
    }

    public static boolean isNaked(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (ItemStack item : armor) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }

}
