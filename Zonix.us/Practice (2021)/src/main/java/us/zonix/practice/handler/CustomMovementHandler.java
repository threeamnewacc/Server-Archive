package us.zonix.practice.handler;

import java.util.HashMap;
import java.util.UUID;
import net.edater.spigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.events.lights.LightsEvent;
import us.zonix.practice.events.lights.LightsPlayer;
import us.zonix.practice.events.oitc.OITCEvent;
import us.zonix.practice.events.oitc.OITCPlayer;
import us.zonix.practice.events.parkour.ParkourEvent;
import us.zonix.practice.events.parkour.ParkourPlayer;
import us.zonix.practice.events.redrover.RedroverEvent;
import us.zonix.practice.events.redrover.RedroverPlayer;
import us.zonix.practice.events.sumo.SumoEvent;
import us.zonix.practice.events.sumo.SumoPlayer;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchState;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.util.BlockUtil;

public class CustomMovementHandler implements MovementHandler {
    private final Practice plugin = Practice.getInstance();
    private static HashMap<Match, HashMap<UUID, CustomLocation>> parkourCheckpoints = new HashMap<>();
    private static HashMap<Match, HashMap<UUID, Integer>> bridgesScore = new HashMap<>();

    public void handleUpdateLocation(Player player, Location to, Location from, PacketPlayInFlying packetPlayInFlying) {
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
        } else {
            if (playerData.getPlayerState() == PlayerState.FIGHTING) {
                Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
                if (match == null) {
                    return;
                }

                if (match.getKit().isSpleef() || match.getKit().isSumo()) {
                    if (match.getMatchState() == MatchState.FIGHTING && (BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1))) {
                        this.plugin.getMatchManager().removeFighter(player, playerData, true);
                    }

                    if ((to.getX() != from.getX() || to.getZ() != from.getZ())
                        && (match.getMatchState() == MatchState.STARTING || match.getMatchState() == MatchState.RESTARTING)) {
                        player.teleport(from);
                    }
                }

                if (match.getKit().isParkour()) {
                    if (!BlockUtil.isStandingOn(player, Material.GOLD_PLATE)) {
                        if (!BlockUtil.isStandingOn(player, Material.WATER) && !BlockUtil.isStandingOn(player, Material.STATIONARY_WATER)) {
                            if (BlockUtil.isStandingOn(player, Material.STONE_PLATE)
                                || BlockUtil.isStandingOn(player, Material.IRON_PLATE)
                                || BlockUtil.isStandingOn(player, Material.WOOD_PLATE)) {
                                boolean checkpoint = false;
                                if (!parkourCheckpoints.containsKey(match)) {
                                    checkpoint = true;
                                    parkourCheckpoints.put(match, new HashMap<>());
                                }

                                if (!parkourCheckpoints.get(match).containsKey(player.getUniqueId())) {
                                    checkpoint = true;
                                    parkourCheckpoints.get(match).put(player.getUniqueId(), CustomLocation.fromBukkitLocation(player.getLocation()));
                                } else if (parkourCheckpoints.get(match).containsKey(player.getUniqueId())
                                    && !BlockUtil.isSameLocation(
                                        player.getLocation(), parkourCheckpoints.get(match).get(player.getUniqueId()).toBukkitLocation()
                                    )) {
                                    checkpoint = true;
                                    parkourCheckpoints.get(match).put(player.getUniqueId(), CustomLocation.fromBukkitLocation(player.getLocation()));
                                }

                                if (checkpoint) {
                                }
                            }
                        } else {
                            this.teleportToSpawnOrCheckpoint(match, player);
                        }
                    } else {
                        for (UUID uuid : this.plugin.getMatchManager().getOpponents(match, player)) {
                            Player opponent = Bukkit.getPlayer(uuid);
                            if (opponent != null) {
                                this.plugin
                                    .getMatchManager()
                                    .removeFighter(opponent, this.plugin.getPlayerManager().getPlayerData(opponent.getUniqueId()), true);
                            }
                        }

                        parkourCheckpoints.remove(match);
                    }

                    if ((to.getX() != from.getX() || to.getZ() != from.getZ())
                        && (match.getMatchState() == MatchState.STARTING || match.getMatchState() == MatchState.RESTARTING)) {
                        player.teleport(from);
                    }
                }
            }

            PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);
            if (event != null) {
                if (event instanceof SumoEvent) {
                    SumoEvent sumoEvent = (SumoEvent)event;
                    if (sumoEvent.getPlayer(player).getFighting() != null && sumoEvent.getPlayer(player).getState() == SumoPlayer.SumoState.PREPARING) {
                        player.teleport(from);
                        return;
                    }

                    if (sumoEvent.getPlayers().size() <= 1) {
                        return;
                    }

                    if (sumoEvent.getPlayer(player) != null && sumoEvent.getPlayer(player).getState() != SumoPlayer.SumoState.FIGHTING) {
                        return;
                    }

                    Block legs = player.getLocation().getBlock();
                    Block head = legs.getRelative(BlockFace.UP);
                    if (legs.getType() == Material.WATER
                        || legs.getType() == Material.STATIONARY_WATER
                        || head.getType() == Material.WATER
                        || head.getType() == Material.STATIONARY_WATER) {
                        sumoEvent.onDeath().accept(player);
                    }
                } else if (event instanceof OITCEvent) {
                    OITCEvent oitcEvent = (OITCEvent)event;
                    if (oitcEvent.getPlayer(player).getState() == OITCPlayer.OITCState.FIGHTING && player.getLocation().getBlockY() >= 90) {
                        oitcEvent.teleportNextLocation(player);
                        player.sendMessage(ChatColor.RED + "You have been teleported back to the arena.");
                    }
                } else if (event instanceof RedroverEvent) {
                    RedroverEvent redroverEvent = (RedroverEvent)event;
                    if (redroverEvent.getPlayer(player).getFightTask() != null
                        && redroverEvent.getPlayer(player).getState() == RedroverPlayer.RedroverState.PREPARING) {
                        player.teleport(from);
                    }
                } else if (event instanceof LightsEvent) {
                    LightsEvent lightsEvent = (LightsEvent)event;
                    if (lightsEvent.getPlayer(player) != null
                        && lightsEvent.getPlayer(player).getState() == LightsPlayer.LightsState.INGAME
                        && lightsEvent.getCurrent() == LightsEvent.LightsGameState.RED) {
                        if (lightsEvent.getMovingPlayers().contains(player.getUniqueId()) && (to.getX() != from.getX() || to.getZ() != from.getZ())) {
                            player.teleport(from);
                        }

                        if (from.distance(to) >= 0.04 && !lightsEvent.getMovingPlayers().contains(player.getUniqueId())) {
                            lightsEvent.getMovingPlayers().add(player.getUniqueId());
                            lightsEvent.teleportToSpawn(player);
                        }
                    }

                    if (lightsEvent.getPlayer(player) != null
                        && lightsEvent.getPlayer(player).getState() == LightsPlayer.LightsState.INGAME
                        && BlockUtil.isStandingOn(player, Material.GOLD_PLATE)) {
                        String announce = ChatColor.DARK_RED
                            + player.getName()
                            + ChatColor.WHITE
                            + " has won our "
                            + ChatColor.DARK_RED
                            + "RedLightGreenLight"
                            + ChatColor.WHITE
                            + " event!";
                        Bukkit.broadcastMessage(announce);
                        lightsEvent.end();
                    }
                } else if (event instanceof ParkourEvent) {
                    ParkourEvent parkourEvent = (ParkourEvent)event;
                    if (parkourEvent.getPlayers().size() <= 1) {
                        return;
                    }

                    if (parkourEvent.getPlayer(player) != null && parkourEvent.getPlayer(player).getState() != ParkourPlayer.ParkourState.INGAME) {
                        return;
                    }

                    if (BlockUtil.isStandingOn(player, Material.WATER) || BlockUtil.isStandingOn(player, Material.STATIONARY_WATER)) {
                        parkourEvent.teleportToSpawnOrCheckpoint(player);
                    } else if (BlockUtil.isStandingOn(player, Material.STONE_PLATE)
                        || BlockUtil.isStandingOn(player, Material.IRON_PLATE)
                        || BlockUtil.isStandingOn(player, Material.WOOD_PLATE)) {
                        ParkourPlayer parkourPlayer = parkourEvent.getPlayer(player.getUniqueId());
                        if (parkourPlayer != null) {
                            boolean checkpointx = false;
                            if (parkourPlayer.getLastCheckpoint() == null) {
                                checkpointx = true;
                                parkourPlayer.setLastCheckpoint(CustomLocation.fromBukkitLocation(player.getLocation()));
                            } else if (parkourPlayer.getLastCheckpoint() != null
                                && !BlockUtil.isSameLocation(player.getLocation(), parkourPlayer.getLastCheckpoint().toBukkitLocation())) {
                                checkpointx = true;
                                parkourPlayer.setLastCheckpoint(CustomLocation.fromBukkitLocation(player.getLocation()));
                            }

                            if (checkpointx) {
                                parkourPlayer.setCheckpointId(parkourPlayer.getCheckpointId() + 1);
                            }
                        }
                    } else if (BlockUtil.isStandingOn(player, Material.GOLD_PLATE)) {
                        String announce = ChatColor.DARK_RED
                            + player.getName()
                            + ChatColor.WHITE
                            + " has won our "
                            + ChatColor.DARK_RED
                            + "Parkour"
                            + ChatColor.WHITE
                            + " event!";
                        Bukkit.broadcastMessage(announce);
                        parkourEvent.end();
                    }
                }
            }
        }
    }

    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {
    }

    private void teleportToSpawnOrCheckpoint(Match match, Player player) {
        if (!parkourCheckpoints.containsKey(match)) {
            player.sendMessage(ChatColor.GRAY + "Teleporting back to the beginning.");
            player.teleport(match.getArena().getA().toBukkitLocation());
        } else if (!parkourCheckpoints.get(match).containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.GRAY + "Teleporting back to the beginning.");
            player.teleport(match.getArena().getA().toBukkitLocation());
        } else {
            player.teleport(parkourCheckpoints.get(match).get(player.getUniqueId()).toBukkitLocation());
            player.sendMessage(ChatColor.GRAY + "Teleporting back to last checkpoint.");
        }
    }

    public static HashMap<Match, HashMap<UUID, CustomLocation>> getParkourCheckpoints() {
        return parkourCheckpoints;
    }
}
