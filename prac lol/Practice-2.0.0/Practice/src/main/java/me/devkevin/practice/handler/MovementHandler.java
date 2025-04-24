package me.devkevin.practice.handler;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.events.oitc.OITCEvent;
import me.devkevin.practice.events.oitc.OITCPlayer;
import me.devkevin.practice.events.sumo.SumoEvent;
import me.devkevin.practice.events.sumo.SumoPlayer;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchState;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import me.devkevin.practice.util.BlockUtil;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Copyright 03/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class MovementHandler implements xyz.refinedev.spigot.api.handlers.impl.MovementHandler {
    private final Practice plugin = Practice.getInstance();

    @Override
    public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packetPlayInFlying) {
        Profile profile = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());

        if (profile == null) {
            Practice.getInstance().getLogger().warning(player.getName() + "'s player data is null");
            player.kickPlayer(CC.RED + "Your data is null please reconnect or contact with the Developer");
            return;
        }

        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = Practice.getInstance().getMatchManager().getMatch(player.getUniqueId());

            if (match == null) {
                return;
            }

            if (match.getKit().isSumo()) {
                if (BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to,1)) {
                    Practice.getInstance().getMatchManager().removeFighter(player, profile, true);
                    //((CraftPlayer) player).getHandle().playerConnection. = false;
                }

                if (to.getX() != from.getX() || to.getZ() != from.getZ()) {
                    if (match.getMatchState() == MatchState.STARTING) {
                        player.teleport(from);
                        //((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
                    }
                }
            }

            if (player.hasPotionEffect(PotionEffectType.WEAKNESS)) {
                return;
            }
        }

        PracticeEvent event = Practice.getInstance().getEventManager().getEventPlaying(player);

        if (event != null) {

            if(event instanceof SumoEvent) {
                SumoEvent sumoEvent = (SumoEvent) event;

                if (sumoEvent.getPlayer(player).getFighting() != null && sumoEvent.getPlayer(player).getState() == SumoPlayer.SumoState.PREPARING) {
                    player.teleport(from);
                }
            } else if(event instanceof OITCEvent) {
                OITCEvent oitcEvent = (OITCEvent) event;

                if (oitcEvent.getPlayer(player).getState() == OITCPlayer.OITCState.RESPAWNING) {
                    player.teleport(from);
                } else if(oitcEvent.getPlayer(player).getState() == OITCPlayer.OITCState.FIGHTING) {
                    if(player.getLocation().getBlockY() >= 90) {
                        oitcEvent.teleportNextLocation(player);
                    }
                }
            }

        }
    }

    @Override
    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {

    }
}
