package net.badlion.potpvp.events;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.PartyHelper;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.managers.EventManager;
import net.badlion.potpvp.matchmaking.WarMatch;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class War extends Event {

    private WarMatch match;

    public War(Player creator, ItemStack eventItem, KitRuleSet kitRuleSet, ItemStack[] armorContents, ItemStack[] inventoryContents) {
        super(creator, eventItem, kitRuleSet, EventType.WAR, ArenaManager.ArenaType.WAR);

	    this.armorContents = armorContents;
	    this.inventoryContents = inventoryContents;
        this.minPlayers = 2;
	    this.maxPlayers = 50;
    }

    @Override
    public void startGame() {
        super.startGame();

        Iterator<Player> iterator = this.players.iterator();
        Player host1 = iterator.next();
        Player host2 = iterator.next();

        Group group1 = PartyHelper.handleCreate(host1, GroupStateMachine.matchMakingState, false);
        Group group2 = PartyHelper.handleCreate(host2, GroupStateMachine.matchMakingState, false);

        // Add players to teams
        boolean team1 = true;
        while (iterator.hasNext()) {
            Player player = iterator.next();

            if (player == null) {
                Gberry.log("EVENT2", "Handling player in war with kit " + this.kitRuleSet.getName() + " with player null");
                continue;
            }

            Gberry.log("EVENT2", "Handling player in war with kit " + this.kitRuleSet.getName() + " with player " + player.getName());

            Group groupToAddTo = team1 ? group1 : group2;
            team1 = !team1;

            PartyHelper.addToPartyGroup(player, groupToAddTo.getParty(), false);
        }

        // Make match for use below
        this.match = new WarMatch(this.arena, this.kitRuleSet, this, this.inventoryContents, this.armorContents);

        // Push them into the proper sub-states
        try {
            GroupStateMachine.matchMakingState.push(GroupStateMachine.regularMatchState, group1, this.match);
            GroupStateMachine.matchMakingState.push(GroupStateMachine.regularMatchState, group2, this.match);

            GroupStateMachine.regularMatchState.push(GroupStateMachine.warState, group1);
            GroupStateMachine.regularMatchState.push(GroupStateMachine.warState, group2);
        } catch (IllegalStateTransitionException e) {
            PotPvP.getInstance().somethingBroke(group1.getLeader(), group1);
            PotPvP.getInstance().somethingBroke(group2.getLeader(), group2);
        }

        // Kick match off
        this.match.prepGame(group1, group2);
        this.match.startGame();
    }

    @Override
    public void endGame(boolean premature) {
        super.endGame(premature);

        List<EventManager.EventStats> statsList = new ArrayList<>();
        if (!premature) {
            for (Player player : this.match.getCopyOfGroup1().players()) {
                EventManager.EventStats stats = new EventManager.EventStats(EventType.WAR, player.getUniqueId(), 0, 0, 0, 0, 0);

                if (this.match.getWinner() != null && this.match.getWinner() == this.match.getCopyOfGroup1()) {
                    stats.addWin();
                }

                for (int i = 0; i < this.match.getKillCounts().get(player.getUniqueId()); i++) {
                    stats.addKill();
                }

                if (!this.match.getParty1AlivePlayers().contains(player)) {
                    stats.addDeath();
                }

                stats.addGame();
                statsList.add(stats);
            }

            for (Player player : this.match.getCopyOfGroup2().players()) {
                EventManager.EventStats stats = new EventManager.EventStats(EventType.WAR, player.getUniqueId(), 0, 0, 0, 0, 0);

                if (this.match.getWinner() != null && this.match.getWinner() == this.match.getCopyOfGroup2()) {
                    stats.addWin();
                }

                for (int i = 0; i < this.match.getKillCounts().get(player.getUniqueId()); i++) {
                    stats.addKill();
                }

                if (!this.match.getParty2AlivePlayers().contains(player)) {
                    stats.addDeath();
                }

                stats.addGame();
                statsList.add(stats);
            }
        }

        EventManager.updateStats(statsList);
    }

    @Override
    public void handleDeath(Player player) {
    }

    @Override
    public Location handleRespawn(Player player) {
        return PotPvP.getInstance().getDefaultRespawnLocation();
    }

    @Override
    public boolean handleQuit(Player player, String reason) {
        return false;
    }

}
