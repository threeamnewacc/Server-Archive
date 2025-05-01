package net.kohi.vaultbattle.listener;


import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.type.PlayerData;
import net.kohi.vaultbattle.type.SimpleLocation;
import net.kohi.vaultbattle.type.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AntiGriefListener implements Listener {


    private final VaultBattlePlugin plugin;

    private final List<BlockFace> blockFaces = Arrays.asList(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST);

    public AntiGriefListener(VaultBattlePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        SimpleLocation location = new SimpleLocation(block.getLocation());
        if (plugin.getAntiGriefManager().getPrivateBlocks().containsKey(location)) {
            if (plugin.getAntiGriefManager().getPrivateBlocks(event.getPlayer()).contains(location)) {
                //Allow interact since its theirs
                return;
            } else {
                Team team = plugin.getPlayerDataManager().get(player).getTeam();
                UUID uuid = plugin.getAntiGriefManager().getPrivateBlocks().get(location);
                Player owner = Bukkit.getPlayer(uuid);
                PlayerData ownerData = plugin.getPlayerDataManager().get(owner);
                if (owner == null || !owner.isOnline()) {
                    return;
                    //allow interact
                } else {
                    Team ownerTeam = ownerData.getTeam();
                    if (!team.equals(ownerTeam)) {
                        return;
                        //allow break
                    } else if (block.getType() != Material.ENCHANTMENT_TABLE) { // TODO: nicer way of adding usable protected blocks
                        if (ownerData.getAllowedPlayers().contains(player.getUniqueId())) {
                            player.sendFormattedMessage("{0}This is one of {1}''s private blocks.", ChatColor.GOLD, owner.getName());
                            return;
                            //allow since the player has added them
                        } else {
                            player.sendFormattedMessage("{0}You can not use that {1}, it is owned by {2}", ChatColor.RED, block.getType().toString(), owner.getName());
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataManager().get(player);
        if (playerData.getTeam() != null && !playerData.getTeam().isEliminated()) {
            Block block = event.getBlock();
            if (block.getType().equals(Material.HOPPER)) {
                SimpleLocation oneAbove = new SimpleLocation(block.getLocation().clone().add(0, 1, 0));
                if (plugin.getAntiGriefManager().getPrivateBlocks().containsKey(oneAbove)) {
                    event.setCancelled(true);
                    player.sendFormattedMessage("{0}You can not place hoppers under a private block!", ChatColor.RED);
                }
                return;
            }
            if (block.getType() == Material.BREWING_STAND || block.getType() == Material.FURNACE) {
                List<SimpleLocation> locations = plugin.getAntiGriefManager().getPrivateBlocks(player);
                if (locations.size() > 5) {
                    event.setCancelled(true);
                    player.sendFormattedMessage("{0}You can not have more than 6 brewing stands and furnaces.", ChatColor.RED);
                    return;
                }
                for (BlockFace face : blockFaces) {
                    Block rel = block.getRelative(face);
                    if (rel.getType().equals(Material.ENDER_CHEST)) {
                        player.sendFormattedMessage("{0}You can not place a private block right next to an enderchest.", ChatColor.RED);
                        event.setCancelled(true);
                        return;
                    }
                    SimpleLocation relLoc = new SimpleLocation(rel.getLocation());
                    if (plugin.getAntiGriefManager().getPrivateBlocks().containsKey(relLoc)) {
                        player.sendFormattedMessage("{0}You can not place a private block right next to another one.", ChatColor.RED);
                        event.setCancelled(true);
                        return;
                    }
                }
                if (plugin.getGameManager().getMap().getTeamSpawn(playerData.getTeam().getColor()).distance(block.getLocation()) < 6) {
                    event.setCancelled(true);
                    player.sendFormattedMessage("{0}You can not place a private block that close to your teams spawn point.", ChatColor.RED);
                    return;
                }
                if (plugin.getAntiGriefManager().getPrivateBlocks().get(new SimpleLocation(block.getLocation())) != null) {
                    plugin.getAntiGriefManager().getPrivateBlocks().remove(new SimpleLocation(block.getLocation()));
                }
                plugin.getAntiGriefManager().getPrivateBlocks().put(new SimpleLocation(block.getLocation()), player.getUniqueId());
                player.sendFormattedMessage("{0}This {1} is now protected, only you can use it and break it. {2}To add people to it, use /trust <name>", ChatColor.GREEN, block.getType().toString(), ChatColor.YELLOW);
            } else if (block.getType() == Material.ENCHANTMENT_TABLE) {
                // TODO: not duplicat this code, im in a hurry ok its midnight
                for (BlockFace face : blockFaces) {
                    Block rel = block.getRelative(face);
                    if (rel.getType().equals(Material.ENDER_CHEST)) {
                        player.sendFormattedMessage("{0}You can not place an enchantment table right next to an enderchest.", ChatColor.RED);
                        event.setCancelled(true);
                        return;
                    }
                    SimpleLocation relLoc = new SimpleLocation(rel.getLocation());
                    if (plugin.getAntiGriefManager().getPrivateBlocks().containsKey(relLoc)) {
                        player.sendFormattedMessage("{0}You can not place an enchantment table right next to a protected block.", ChatColor.RED);
                        event.setCancelled(true);
                        return;
                    }
                }
                if (plugin.getGameManager().getMap().getTeamSpawn(playerData.getTeam().getColor()).distance(block.getLocation()) < 6) {
                    event.setCancelled(true);
                    player.sendFormattedMessage("{0}You can not place an enchantment table that close to your teams spawn point.", ChatColor.RED);
                    return;
                }
                if (plugin.getAntiGriefManager().getPrivateBlocks().get(new SimpleLocation(block.getLocation())) != null) {
                    plugin.getAntiGriefManager().getPrivateBlocks().remove(new SimpleLocation(block.getLocation()));
                }
                plugin.getAntiGriefManager().getPrivateBlocks().put(new SimpleLocation(block.getLocation()), player.getUniqueId());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
            if (!event.getBlock().getLocation().add(0, -1, 0).getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
                event.setCancelled(true);
                event.getPlayer().sendFormattedMessage("{0}You cant break the last sugar cane block.", ChatColor.RED);
                return;
            }
        }
        if (!event.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
            if (event.getBlock().getLocation().add(0, 1, 0).getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
                event.setCancelled(true);
                event.getPlayer().sendFormattedMessage("{0}You cant break the block under a sugar cane.", ChatColor.RED);
                return;
            }
        }
        if (event.getBlock().getType().equals(Material.MOB_SPAWNER)) {
            event.setCancelled(true);
            event.getPlayer().sendFormattedMessage("{0}You can not break mob spawners.", ChatColor.RED);
            return;
        }
        Block block = event.getBlock();
        Player player = event.getPlayer();
        SimpleLocation location = new SimpleLocation(block.getLocation());
        if (plugin.getAntiGriefManager().getPrivateBlocks().containsKey(location)) {
            if (plugin.getAntiGriefManager().getPrivateBlocks(event.getPlayer()).contains(location)) {
                //Allow break since its their private block
                player.sendFormattedMessage("{0}You just broke one of your private blocks.", ChatColor.GOLD);
                plugin.getAntiGriefManager().getPrivateBlocks().remove(location);
            } else {
                Team team = plugin.getPlayerDataManager().get(player).getTeam();
                UUID uuid = plugin.getAntiGriefManager().getPrivateBlocks().get(location);
                Player owner = Bukkit.getPlayer(uuid);
                if (owner == null) {
                    plugin.getAntiGriefManager().getPrivateBlocks().remove(location);
                    //allow break
                } else {
                    PlayerData ownerData = plugin.getPlayerDataManager().get(owner);
                    Team ownerTeam = ownerData.getTeam();
                    if (!team.equals(ownerTeam)) {
                        plugin.getAntiGriefManager().getPrivateBlocks().remove(location);
                        //allow break
                    } else if (ownerData.getAllowedPlayers().contains(player.getUniqueId())) {
                        owner.sendFormattedMessage("{0} just broke one of your private blocks.", ChatColor.GOLD + player.getName());
                        player.sendFormattedMessage("{0}You just broke one of {1}''s private blocks.", ChatColor.GOLD, owner.getName());
                        plugin.getAntiGriefManager().getPrivateBlocks().remove(location);
                        //allow since the player has added them
                    } else {
                        player.sendFormattedMessage("{0}You can not break that {1}, it is owned by {2}.", ChatColor.RED, block.getType().toString(), owner.getName());
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
