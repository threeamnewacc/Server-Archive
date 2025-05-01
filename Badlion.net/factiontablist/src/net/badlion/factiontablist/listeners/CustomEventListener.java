package net.badlion.factiontablist.listeners;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.*;
import net.badlion.archmoney.events.MoneyChangeEvent;
import net.badlion.gfactions.bukkitevents.EventStateChangeEvent;
import net.badlion.gfactions.bukkitevents.HomeChangeEvent;
import net.badlion.factiontablist.TabList;
import net.badlion.factiontablist.TabMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomEventListener implements Listener {

    public TabMain plugin;

    public CustomEventListener(TabMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void homeChangeEvent(final HomeChangeEvent e) {
        final TabList tl = this.plugin.tabs.get(e.getPlayer().getName());

        // No need to run ALL queries, just the home ones
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {

                // Run all queries asynchronously
                plugin.updateNumberOfHomes(e.getPlayer(), tl);
                plugin.updateHomes(e.getPlayer(), tl);

                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {

                        // Update player tab list w/o running any queries
                        plugin.updatePlayerNoQueries(e.getPlayer());
                    }
                });
            }
        });
    }

    @EventHandler
    public void moneyChangeEvent(MoneyChangeEvent e) {
        if (e.getPlayer() != null) { // Player affected
            this.plugin.updatePlayerMoney(e.getPlayer());
        } else { // Faction affected
            int moolah = this.plugin.archMoney.checkBalance("~faction_" + e.getFactionID());
            Faction faction = Factions.i.getBestIdMatch(e.getFactionID());
            if (faction != null) {
                for (Player p : faction.getOnlinePlayers()) {
                    plugin.updatePlayerFactionMoney(p, moolah);
                }
            }
        }
    }

    @EventHandler
    public void eventChangeEvent(EventStateChangeEvent e) {
        if (e.isActive()) {
            this.plugin.events.add(e.getEventName());
        } else {
            this.plugin.events.remove(e.getEventName());
        }

        // Update all player tab lists
        this.plugin.updateAllTabListsNoQueries();
    }

    @EventHandler
    public void landClaim(final LandClaimEvent e) {
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
            @Override
            public void run() {
                if (e.getPlayer() != null) {
                    Faction faction = FPlayers.i.get(e.getPlayer()).getFaction();
                    if (!faction.getId().equals("0")) {
                        for (Player p2 : faction.getOnlinePlayers()) {
                            plugin.updatePlayerNoQueries(p2);
                        }
                    }
                }
            }
        }, 1L);
    }

    @EventHandler
    public void landUnclaim(final LandUnclaimEvent e) {
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
            @Override
            public void run() {
                if (e.getPlayer() != null) {
                    Faction faction = FPlayers.i.get(e.getPlayer()).getFaction();
                    if (!faction.getId().equals("0")) {
                        for (Player p2 : faction.getOnlinePlayers()) {
                            plugin.updatePlayerNoQueries(p2);
                        }
                    }
                }
            }
        }, 1L);
    }

    @EventHandler
    public void landUnclaimAll(final LandUnclaimAllEvent e) {
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
            @Override
            public void run() {
                if (e.getPlayer() != null) {
                    Faction faction = FPlayers.i.get(e.getPlayer()).getFaction();
                    if (!faction.getId().equals("0")) {
                        for (Player p2 : faction.getOnlinePlayers()) {
                            plugin.updatePlayerNoQueries(p2);
                        }
                    }
                }
            }
        }, 1L);
    }

    @EventHandler
    public void powerGain(final PowerGainEvent e) {
        //this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
        //    @Override
        //    public void run() {
        if (e.getPlayer() != null) {
            Faction faction = FPlayers.i.get(e.getPlayer()).getFaction();
            if (!faction.getId().equals("0")) {
                for (Player p2 : faction.getOnlinePlayers()) {
                    this.plugin.updatePlayerNoQueries(p2);
                }
            }
        }
        //    }
        //}, 1L);
    }

    @EventHandler
    public void powerLoss(final PowerLossEvent e) {
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
            @Override
            public void run() {
                if (e.getPlayer() != null) {
                    Faction faction = FPlayers.i.get(e.getPlayer()).getFaction();
                    if (!faction.getId().equals("0")) {
                        for (Player p2 : faction.getOnlinePlayers()) {
                            plugin.updatePlayerNoQueries(p2);
                        }
                    }
                }
            }
        }, 1L);
    }

    @EventHandler
    public void playerJoinFactionEvent(final FPlayerJoinEvent e) {
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
            @Override
            public void run() {
                Faction faction = e.getFaction();
                if (!faction.getId().equals("0")) {
                    int moolah = CustomEventListener.this.plugin.archMoney.checkBalance("~faction_" + e.getFaction().getId());
                    for (Player p2 : faction.getOnlinePlayers()) {
                        plugin.updatePlayerFactionMoney(p2, moolah);
                    }
                }
            }
        }, 1L);
    }

    @EventHandler
    public void playerLeaveFactionEvent(final FPlayerLeaveEvent e) {
        // Store old faction to update their tab lists
        final Faction faction = e.getFaction();
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
            @Override
            public void run() {
                // Remove player from their member list for old faction
                for (Player p2 : faction.getOnlinePlayers()) {
                    CustomEventListener.this.plugin.updatePlayerNoQueries(p2);
                }

                if (e.getFPlayer().getPlayer() != null) {
                    // Update player's own list
                    CustomEventListener.this.plugin.updatePlayerNoQueries(e.getFPlayer().getPlayer());
                }
            }
        }, 1L);
    }

}
