package me.devkevin.practice.profile.listener;

import lombok.RequiredArgsConstructor;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchState;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffectType;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 05/02/2023 @ 23:59
 * MovementListener / me.devkevin.practice.profile.listener / Practice
 */
@RequiredArgsConstructor
public class MovementListener implements Listener {
    private final Practice plugin;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer() == null) {
            return;
        }

        Location to = event.getTo();
        Location from = event.getFrom();
        Player player = event.getPlayer();
        Profile practicePlayerData = plugin.getProfileManager().getProfileData(player.getUniqueId());
        if (practicePlayerData == null) {
            plugin.getLogger().warning(player.getName() + "'s player data is null" + "(" + this.getClass().getName() + ")");
            return;
        }

        if (practicePlayerData.getState() == ProfileState.FIGHTING) {
            Match match = plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match == null) {
                return;
            }

            if (match.getKit().isSumo() || match.getKit().isSpleef() || match.getKit().isBedWars()) {
                if (match.getKit().isSumo() || match.getKit().isSpleef()) {
                    if (to.getBlock().isLiquid()) {
                        plugin.getMatchManager().removeFighter(player, practicePlayerData, true);
                    }
                }

                if (to.getX() != from.getX() || to.getZ() != from.getZ()) {
                    if (match.getMatchState() == MatchState.STARTING) {
                        player.teleport(from);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFlight(PlayerToggleFlightEvent event) {
        if (event.getPlayer().hasPotionEffect(PotionEffectType.WEAKNESS)) {
            event.setCancelled(true);
        }
    }
}
