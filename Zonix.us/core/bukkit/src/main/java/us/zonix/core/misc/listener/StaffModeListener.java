package us.zonix.core.misc.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.zonix.core.CorePlugin;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.ItemUtil;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class StaffModeListener implements Listener {

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEntityEvent event) {

        Entity entity = event.getRightClicked();
        if (entity instanceof Player) {

            Player player = event.getPlayer();
            Player target = (Player) entity;

            if (CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {

                ItemStack hand = player.getItemInHand();

                if (hand.getType() == Material.PAPER) {
                    Bukkit.dispatchCommand(player, "punish " + target.getName());
                } else if (hand.getType() == Material.ICE) {
                    player.performCommand("freeze " + target.getName());

                } else if (hand.getType() == Material.BLAZE_ROD) {

                    Inventory contents = target.getInventory();
                    Inventory inventory = Bukkit.createInventory(null, 54, "Inspection Inventory");
                    inventory.setContents(contents.getContents());

                    for(int i = 36; i <= 44; i++) {
                        inventory.setItem(i, ItemUtil.createItem(Material.STAINED_GLASS_PANE, "&bArmor Split", 1, (short) 11));
                    }

                    inventory.setItem(45, target.getInventory().getHelmet());
                    inventory.setItem(46, target.getInventory().getChestplate());
                    inventory.setItem(47, target.getInventory().getLeggings());
                    inventory.setItem(48, target.getInventory().getBoots());

                    player.openInventory(inventory);

                }

                event.setCancelled(true);
                player.updateInventory();
            }

        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {

        if(event.getInventory().getTitle().equalsIgnoreCase("Inspection Inventory")) {
            event.setCancelled(true);
        }

        if(event.getWhoClicked() instanceof Player) {

            Player player = (Player) event.getWhoClicked();

            if(CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {
                event.setCancelled(true);
                player.updateInventory();
            }

        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

            final Player player = event.getPlayer();

            if(CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {

                ItemStack hand = player.getItemInHand();


                if(hand.getType() == Material.STICK) {

                    ArrayList<Player> players = new ArrayList<>();

                    for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                        if(!Profile.getByUuid(online.getUniqueId()).getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
                            players.add(online);
                        }
                    }

                    if(players.size() > 0) {
                        Player randomPlayer = players.get(new Random().nextInt(players.size()));
                        player.teleport(randomPlayer.getLocation());
                    } else {
                        player.sendMessage(ChatColor.RED + "There are no players to teleport.");
                    }

                }

                else if(hand.getType() == Material.GLASS) {
                    player.getInventory().setItem(7, ItemUtil.createItem(Material.STAINED_GLASS,"&c&lVanish"));
                    CorePlugin.getInstance().getStaffModeManager().unvanishPlayer(player);
                }

                else if(hand.getType() == Material.STAINED_GLASS) {
                    player.getInventory().setItem(7, ItemUtil.createItem(Material.GLASS, "&c&lUnvanish"));
                    CorePlugin.getInstance().getStaffModeManager().vanishPlayer(player);
                }

                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {

        Entity entity = event.getDamager();

        if(entity instanceof Player) {

            Player player = (Player) entity;

            if(CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {

                event.setCancelled(true);

            }

        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityTarget(EntityTargetLivingEntityEvent event){
        if (event.getTarget() instanceof Player){

            Player player = (Player) event.getTarget();

            if(CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {

                event.setCancelled(true);

            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageEvent(EntityDamageEvent event) {

        Entity entity = event.getEntity();

        if(entity instanceof Player) {

            Player player = (Player) entity;

            if(CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {

                event.setCancelled(true);

            }
        }


    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {

        Player player = event.getEntity();

        if(CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {

            event.getDrops().clear();
            event.setKeepInventory(true);

        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if(CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {
            CorePlugin.getInstance().getStaffModeManager().toggleStaffMode(player);
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {

        final Player player = event.getPlayer();

        if(CorePlugin.getInstance().getStaffModeManager().getVanishedPlayers().size() > 0) {

            for(UUID uuid : CorePlugin.getInstance().getStaffModeManager().getVanishedPlayers()) {

                Player staff = Bukkit.getPlayer(uuid);

                if(staff != null) {

                    player.hidePlayer(staff);

                }

            }
        }
    }


    @EventHandler
    public void onPlayerPickupEvent(PlayerPickupItemEvent event) {

        Player player = event.getPlayer();

        if(CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {
            event.setCancelled(true);
        }

    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if(CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {

        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if(CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();

        if(CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropEvent(PlayerDropItemEvent event) {

        Player player = event.getPlayer();

        if(CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)) {
            event.setCancelled(true);
        }

    }
}
