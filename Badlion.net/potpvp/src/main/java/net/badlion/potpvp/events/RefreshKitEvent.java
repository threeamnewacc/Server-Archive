package net.badlion.potpvp.events;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.potpvp.exceptions.OutOfArenasException;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.managers.EventManager;
import net.badlion.potpvp.rulesets.EventRuleSet;
import net.badlion.potpvp.rulesets.HorseRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class RefreshKitEvent extends Event {

    protected Set<UUID> winners = new HashSet<>();

    public RefreshKitEvent(Player player, ItemStack eventItem, KitRuleSet kitRuleSet, EventType eventType, ArenaManager.ArenaType arenaType) throws OutOfArenasException {
        super(player, eventItem, kitRuleSet, eventType, arenaType);
    }

    public void refreshKit(final Player player, boolean verbose) {
        if (!this.contains(player) || player.isDead() || player.getHealth() <= 0.0) {
            return;
        }
        if (this.kitRuleSet instanceof EventRuleSet) {
            player.getInventory().setContents(this.inventoryContents);
            player.getInventory().setArmorContents(this.armorContents);

        } else {
            KitHelper.loadKit(player, this.kitRuleSet);
        }

        if (this.kitRuleSet instanceof HorseRuleSet) {
            if (player.getVehicle() != null && player.getVehicle() instanceof Horse) {
                // Heal the horse
                Horse horse = ((Horse) player.getVehicle());
                horse.setHealth(40D);
            } else {
                // Spawn the horse
                HorseRuleSet.createHorseAndAttach(player, player.getLocation(), this.arena);
            }
        }

        PlayerHelper.healPlayer(player);

        if (verbose) {
            player.sendMessage(ChatColor.BLUE + "Health and hunger set to max for killing a player!");
        } else {
	        // First time loading this kit (not a respawn)
	        this.kitRuleSet.sendMessages(player);
        }

        Gberry.log("LMS", "Refreshing kit for " + player.getName());
    }

    @Override
    public void startGame() {
        super.startGame();
    }

    @Override
    public void endGame(boolean premature) {
        super.endGame(premature);

        List<EventManager.EventStats> statsList = new ArrayList<>();
        if (this.participants != null) {
            for (Player player : this.participants) {
                EventManager.EventStats stats = new EventManager.EventStats(this.getEventType(), player.getUniqueId(), 0, 0, 0, 0, 0);

                // Add kills & deaths
	            int kills = Integer.valueOf(ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).getSuffix());
	            int deaths = Integer.valueOf(ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", "", ChatColor.GOLD + "Deaths: " + ChatColor.WHITE).getSuffix());

	            stats.addKills(kills);
                stats.addDeaths(deaths);

                if (this.winners.contains(stats.getUuid())) {
                    stats.addWin();
                }

                stats.addGame();
                statsList.add(stats);
            }
        }

        EventManager.updateStats(statsList);
    }

}
