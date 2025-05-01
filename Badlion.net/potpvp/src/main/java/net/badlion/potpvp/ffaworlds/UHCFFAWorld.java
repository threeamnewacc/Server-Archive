package net.badlion.potpvp.ffaworlds;

import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.rulesets.KitRuleSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class UHCFFAWorld extends FFAWorld implements Listener {

    public UHCFFAWorld(ItemStack ffaItem, KitRuleSet kitRuleSet) {
        super(ffaItem, kitRuleSet);

        // Run task that repairs armor/weapons every 10 seconds
        RepairGearTask task = new RepairGearTask();
        task.runTaskTimer(PotPvP.getInstance(), 200L, 200L);
    }

    @Override
    public void startGame() {
        this.spawn = new Location(PotPvP.getInstance().getServer().getWorld("world"), 177.5, 134, -4490.5);
    }

    @Override
    public void handleDeath(Player player) {
        Player killer = GroupStateMachine.ffaState.handleScoreboardDeath(player, this);

        // Golden apple for killing someone
        if (killer != null && this.players.contains(killer)) {
            killer.getInventory().addItem(ItemStackUtil.createGoldenHead());
        }
    }

    private class RepairGearTask extends BukkitRunnable {

        @Override
        public void run() {
            for (Player pl : UHCFFAWorld.this.players) {
                for (ItemStack itemStack : pl.getInventory().getArmorContents()) {
                    if (itemStack != null && itemStack.getType() != Material.AIR) {
                        itemStack.setDurability((short) 0);
                    }
                }

                int slot = pl.getInventory().first(Material.IRON_SWORD);
	            if (slot != -1) {
		            pl.getInventory().getItem(slot).setDurability((short) 0);
	            }

                slot = pl.getInventory().first(Material.BOW);
                if (slot != -1) {
	                pl.getInventory().getItem(slot).setDurability((short) 0);
                }
            }
        }

    }

}
