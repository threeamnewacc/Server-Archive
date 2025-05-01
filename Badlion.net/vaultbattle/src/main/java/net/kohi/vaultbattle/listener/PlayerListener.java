package net.kohi.vaultbattle.listener;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.utils.ItemStackUtil;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.manager.GameManager;
import net.kohi.vaultbattle.type.Phase;
import net.kohi.vaultbattle.type.PlayerData;
import net.kohi.vaultbattle.type.Region;
import net.kohi.vaultbattle.type.Team;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PlayerListener implements Listener {

    private final VaultBattlePlugin plugin;

    private final List<Material> nonExplode = Arrays.asList(Material.MOB_SPAWNER, Material.ENDER_CHEST, Material.EMERALD_BLOCK, Material.BREWING_STAND, Material.FURNACE, Material.BURNING_FURNACE);

    public PlayerListener(VaultBattlePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (block.getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
            }
        }
        Player player = event.getPlayer();
        if (plugin.getGameMapManager().isEditing(player)) {
            return;
        }
        if (event.hasItem()) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (event.getItem().getType() == Material.FLINT_AND_STEEL || event.getItem().getType() == Material.LAVA_BUCKET) {
                    event.setCancelled(true);
                    event.getPlayer().sendFormattedMessage("{0}You can''t use that!", ChatColor.RED);
                    return;
                }
            }
        }
        if (!event.hasItem()) {
            return;
        }
        ItemStack item = event.getItem();
        PlayerData playerData = plugin.getPlayerDataManager().get(player);
        if (playerData.isPickingTeam() && !plugin.getGameManager().isWallsDropped()) {
            event.setCancelled(true);
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (item.equals(plugin.getTeamManager().getRandomTeamItem())) {
                    plugin.getTeamManager().joinSmallest(player);
                } else {
                    plugin.getTeamManager().getTeams().stream().filter(team -> team.getJoinItem().equals(item)).forEach(team -> {
                        plugin.getTeamManager().joinTeam(team, player);
                    });
                }
            } else {
                return;
            }
        }

        if (plugin.getGameManager().getPhase().equals(Phase.PRE_START)) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (item.equals(plugin.getTeamManager().getLeaveItem())) {
                    if (plugin.getTeamManager().removeFromAllTeams(player)) {
                        player.sendFormattedMessage("{0}You are no longer on a team.", ChatColor.GOLD);
                    }
                    player.getInventory().clear();
                    player.getInventory().setItem(2, plugin.getTeamManager().getRandomTeamItem());
                    int i = 3;
                    for (Team team : plugin.getTeamManager().getTeams()) {
                        player.getInventory().setItem(i, team.getJoinItem());
                        i++;
                    }
                    player.updateInventory();
                    return;
                } else if (item.equals(plugin.getTeamManager().getRandomTeamItem())) {
                    plugin.getTeamManager().joinSmallest(player);
                } else {
                    plugin.getTeamManager().getTeams().stream().filter(team -> team.getJoinItem().equals(item)).forEach(team -> {
                        plugin.getTeamManager().joinTeam(team, player);
                    });
                }
            } else {
                return;
            }
        } else if (plugin.getGameManager().getPhase().equals(Phase.STARTED)) {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            if (!block.getType().equals(Material.ENDER_CHEST)) {
                return;
            }
            event.setCancelled(true);
            Team team = plugin.getPlayerDataManager().get(player).getTeam();
            if (team == null) {
                return;
            }
            if (plugin.getTeamManager().getConvertValues().keySet().contains(item.getType())) {
                if (block.getLocation().distance(plugin.getGameManager().getMap().getTeamSpawn(team.getColor())) > 20) {
                    player.sendFormattedMessage("{0}That enderchest is not your teams.", ChatColor.RED);
                    return;
                }
                double convertAmount = plugin.getTeamManager().getConvertValues().get(item.getType());
                int itemAmount = item.getAmount();
                int bankPrevious = team.getBankRounded();
                player.setItemInHand(null);
                player.updateInventory();
                team.setBank(team.getBank() + convertAmount * itemAmount);
                for (int i = bankPrevious; i <= team.getBankRounded(); i++) {
                    Region region = plugin.getGameManager().getMap().getBanks().get(team.getColor());
                    Location location = region.getBlock(i);
                    location.setWorld(GameManager.gameMapWorld);
                    location.getBlock().setType(Material.EMERALD_BLOCK);
                    location.getWorld().playEffect(location, Effect.STEP_SOUND, 133);
                }
                team.broadcastMessage(ChatColor.GOLD + "[Bank] " + player.getDisplayName() + ChatColor.GREEN +
                        " converted " + ChatColor.GOLD + itemAmount + "x " + ItemStackUtil.getName(item) + ChatColor.GREEN +
                        " to " + String.format("%.2f", convertAmount * itemAmount) + " emerald point" + (convertAmount * itemAmount == 1.0 ? "." : "s."));
                player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "SHEEP_SHEAR", "ENTITY_SHEEP_SHEAR"), 1.0F, 1.0F);
            } else {
                player.sendFormattedMessage("{0}You can not convert that item.", ChatColor.RED);
            }
        }
    }

    @EventHandler
    public void onBlockPlaceHeight(BlockPlaceEvent event) {
        if (plugin.getGameManager().getMap() != null) {
            if (event.getBlock().getLocation().getY() >= plugin.getGameManager().getMap().getMaxBuildHeight()) {
                event.getPlayer().sendFormattedMessage("{0}You can not build above y={1}", ChatColor.RED, plugin.getGameManager().getMap().getMaxBuildHeight());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreakHeight(BlockBreakEvent event) {
        if (plugin.getGameManager().getMap() != null) {
            if (event.getBlock().getLocation().getY() >= plugin.getGameManager().getMap().getMaxBuildHeight()) {
                event.getPlayer().sendFormattedMessage("{0}You can not build above y={1}", ChatColor.RED, plugin.getGameManager().getMap().getMaxBuildHeight());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLiquidPlace(PlayerBucketEmptyEvent event) {
        if (event.getItemStack().getType().equals(Material.LAVA_BUCKET)) {
            event.setCancelled(true);
            return;
        }
        if (plugin.getGameManager().getMap() != null) {
            if (event.getBlockClicked().getLocation().getY() >= plugin.getGameManager().getMap().getMaxBuildHeight()) {
                event.getPlayer().sendFormattedMessage("{0}You can not build above y={1}", ChatColor.RED, plugin.getGameManager().getMap().getMaxBuildHeight());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.FINAL) // EventPriority.FINAL to let worldedit do compass stuff
    public void onSpectateInteract(PlayerInteractEvent event) {
        if (plugin.getPlayerDataManager().get(event.getPlayer()).isSpectating()) {
            event.setCancelled(true);
            if (event.hasItem() && event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                switch (event.getItem().getType()) {
                    case SKULL_ITEM:
                        plugin.getGameManager().getSpectatePlayersMenu().open(event.getPlayer());
                        break;
                    case WATCH:
                        plugin.getGameManager().getSpectateWarpsMenu().open(event.getPlayer());
                        break;
                }
            }
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onPickupItem(PlayerPickupItemEvent event) {
        if (plugin.getPlayerDataManager().get(event.getPlayer()).isSpectating()) {
            event.setCancelled(true);
        }
        if (plugin.getGameManager().getPhase().equals(Phase.PRE_START)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (event.getEntity() instanceof Player) {
                Team team = plugin.getPlayerDataManager().get((Player) event.getDamager()).getTeam();
                if (team != null) {
                    if (team.getPlayers().contains(event.getEntity().getUniqueId())) {
                        event.setCancelled(true);
                    }
                }
            }
            if (plugin.getPlayerDataManager().get((Player) event.getDamager()).isSpectating()) {
                event.setCancelled(true);
                return;
            }
            if (plugin.getGameManager().getPhase().equals(Phase.PRE_START)) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                Player shooter = (Player) projectile.getShooter();
                if (event.getEntity() instanceof Player) {
                    Team team = plugin.getPlayerDataManager().get(shooter).getTeam();
                    if (team != null) {
                        if (team.getPlayers().contains(event.getEntity().getUniqueId())) {
                            event.setCancelled(true);
                        }
                    }
                }
                if (plugin.getPlayerDataManager().get(shooter).isSpectating()) {
                    event.setCancelled(true);
                }
                if (plugin.getGameManager().getPhase().equals(Phase.PRE_START)) {
                    event.setCancelled(true);
                    return;
                }
            }

        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (plugin.getPlayerDataManager().get((Player) event.getEntity()).isSpectating()) {
                event.setCancelled(true);
            }
            if (plugin.getGameManager().getPhase().equals(Phase.PRE_START)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (plugin.getPlayerDataManager().get(event.getPlayer()).isPickingTeam()) {
            event.setCancelled(true);
        }
        if (plugin.getPlayerDataManager().get(event.getPlayer()).isSpectating()) {
            event.setCancelled(true);
        }
        if (plugin.getGameManager().getPhase().equals(Phase.PRE_START)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST,
            ignoreCancelled = true
    )
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType().equals(Material.LAVA) || event.getBlock().getType().equals(Material.STATIONARY_LAVA) || event.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
            event.setCancelled(true);
            return;
        }
        if (plugin.getGameMapManager().isEditing(event.getPlayer())) {
            return;
        }
        if (plugin.getPlayerDataManager().get(event.getPlayer()).isPickingTeam()) {
            event.setCancelled(true);
        }
        if (plugin.getPlayerDataManager().get(event.getPlayer()).isSpectating()) {
            event.setCancelled(true);
        }
        if (plugin.getGameManager().getPhase().equals(Phase.PRE_START)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST,
            ignoreCancelled = true
    )
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getGameMapManager().isEditing(event.getPlayer())) {
            return;
        }
        if (plugin.getPlayerDataManager().get(event.getPlayer()).isPickingTeam()) {
            event.setCancelled(true);
        }
        if (plugin.getPlayerDataManager().get(event.getPlayer()).isSpectating()) {
            event.setCancelled(true);
        }
        if (plugin.getGameManager().getPhase().equals(Phase.PRE_START)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        Iterator<Block> blocks = event.blockList().iterator();
        while (blocks.hasNext()) {
            Block block = blocks.next();
            if (nonExplode.contains(block.getType())) {
                blocks.remove();
            }
        }
    }


    @EventHandler(
            priority = EventPriority.FINAL,
            ignoreCancelled = true
    )
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (plugin.getPlayerDataManager().get(event.getPlayer()).isSpectating()) {
            event.setCancelled(true);
        }
        if (plugin.getPlayerDataManager().get(event.getPlayer()).isPickingTeam()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST,
            ignoreCancelled = true
    )
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            if (plugin.getPlayerDataManager().get((Player) event.getTarget()).isSpectating()) {
                event.setCancelled(true);
            }
            if (plugin.getGameManager().getPhase().equals(Phase.PRE_START)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST,
            ignoreCancelled = true
    )
    public void onInventoryClick(InventoryClickEvent event) {
        if (plugin.getGameMapManager().isEditing((Player) event.getWhoClicked())) {
            return;
        }
        if (plugin.getPlayerDataManager().get((Player) event.getWhoClicked()).isPickingTeam()) {
            event.setCancelled(true);
        }
        if (plugin.getPlayerDataManager().get((Player) event.getWhoClicked()).isSpectating()) {
            event.setCancelled(true);
        }
        if (plugin.getGameManager().getPhase().equals(Phase.PRE_START)) {
            event.setCancelled(true);
            return;
        }
    }


    @EventHandler
    public void craftItem(PrepareItemCraftEvent event) {
        Material itemType = event.getRecipe().getResult().getType();
        Byte itemData = event.getRecipe().getResult().getData().getData();
        if (itemType == Material.ENDER_CHEST || itemType == Material.PISTON_BASE || (itemType == Material.GOLDEN_APPLE && itemData == 1)) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            event.getViewers().stream().filter(he -> he instanceof Player).forEach(he -> {
                ((Player) he).sendFormattedMessage("{0}You cannot craft this!", ChatColor.RED);
            });
        }
    }

    @EventHandler
    public void onPickupExp(PlayerExpChangeEvent event) {
        event.setAmount(event.getAmount() * 4); // TODO: could be configurable in the future
    }
}
