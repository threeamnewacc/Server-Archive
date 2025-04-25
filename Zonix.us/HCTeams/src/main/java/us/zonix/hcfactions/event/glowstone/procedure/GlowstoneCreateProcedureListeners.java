package us.zonix.hcfactions.event.glowstone.procedure;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.event.utils.Cuboid;
import us.zonix.hcfactions.profile.Profile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.hcfactions.event.glowstone.GlowstoneEvent;
import us.zonix.hcfactions.event.utils.Cuboid;
import us.zonix.hcfactions.profile.Profile;

public class GlowstoneCreateProcedureListeners implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = Profile.getByPlayer(player);

        GlowstoneCreateProcedure procedure = profile.getGlowstoneCreateProcedure();

        if (procedure != null) {
            if (procedure.stage() == GlowstoneCreateProcedureStage.CONFIRMATION && event.getInventory().getTitle().contains("Confirm Glowstone?")) {
                event.setCancelled(true);

                ItemStack itemStack = event.getCurrentItem();
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    String displayName = itemStack.getItemMeta().getDisplayName();
                    if (displayName != null) {

                        if (displayName.contains("Cancel")) {
                            profile.setGlowstoneCreateProcedure(null);
                            player.sendMessage(" ");
                            player.sendMessage(ChatColor.RED + "Glowstone Mountain create procedure cancelled.");
                            player.sendMessage(" ");
                            player.getInventory().removeItem(GlowstoneCreateProcedure.getWand());
                            player.closeInventory();
                            return;
                        }

                        if (displayName.contains("Confirm")) {
                            profile.setGlowstoneCreateProcedure(null);
                            player.sendMessage(" ");
                            player.sendMessage(ChatColor.YELLOW + "Glowstone Mountain successfully created.");
                            player.sendMessage(" ");
                            player.getInventory().removeItem(GlowstoneCreateProcedure.getWand());
                            player.closeInventory();

                            Cuboid zone = new Cuboid(procedure.locationOne(), procedure.locationTwo());
                            new GlowstoneEvent(procedure.name(), zone);
                        }

                    }

                }

            }
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        GlowstoneCreateProcedure procedure = profile.getGlowstoneCreateProcedure();
        if (procedure != null) {
            if (procedure.stage() == GlowstoneCreateProcedureStage.CONFIRMATION && event.getInventory().getTitle().contains("Confirm Glowstone?")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.openInventory(GlowstoneCreateProcedure.getConfirmationInventory(procedure));
                    }
                }.runTaskLaterAsynchronously(FactionsPlugin.getInstance(), 2L);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        GlowstoneCreateProcedure procedure = profile.getGlowstoneCreateProcedure();
        if (procedure != null) {
            event.setCancelled(true);

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                profile.setGlowstoneCreateProcedure(null);
                player.sendMessage(" ");
                player.sendMessage(ChatColor.RED + "Glowstone Mountain create procedure cancelled.");
                player.sendMessage(" ");
                player.getInventory().removeItem(GlowstoneCreateProcedure.getWand());
                return;
            }

            if (procedure.stage() == GlowstoneCreateProcedureStage.NAME_SELECTION) {
                String name = event.getMessage().replace(" ", "");
                if (EventManager.getInstance().getByName(name) != null) {
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.RED + "An event with that name already exists.");
                    player.sendMessage(" ");
                    return;
                }

                player.sendMessage(" ");
                player.sendMessage(ChatColor.YELLOW + "Glowstone Mountain name set to '" + procedure.name(name).name() + "'.");
                player.sendMessage(ChatColor.YELLOW + "You have received the zone wand.");
                player.sendMessage(" ");
                player.getInventory().removeItem(GlowstoneCreateProcedure.getWand());
                player.getInventory().addItem(GlowstoneCreateProcedure.getWand());
                procedure.stage(GlowstoneCreateProcedureStage.ZONE_SELECTION);
            }

        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        GlowstoneCreateProcedure procedure = profile.getGlowstoneCreateProcedure();
        if (event.getItemDrop().getItemStack().isSimilar(GlowstoneCreateProcedure.getWand())) {
            event.getItemDrop().remove();
            if (procedure != null) {
                profile.setGlowstoneCreateProcedure(null);
                player.sendMessage(" ");
                player.sendMessage(ChatColor.RED + "Glowstone Mountain create procedure cancelled.");
                player.sendMessage(" ");
                player.getInventory().removeItem(GlowstoneCreateProcedure.getWand());
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        GlowstoneCreateProcedure procedure = profile.getGlowstoneCreateProcedure();
        Action action = event.getAction();

        if (procedure == null && event.getItem() != null && event.getItem().isSimilar(GlowstoneCreateProcedure.getWand())) {
            player.getInventory().removeItem(event.getItem());
            return;
        }

        if (procedure != null && event.getItem() != null && event.getItem().isSimilar(GlowstoneCreateProcedure.getWand())) {
            event.setCancelled(true);
            if (procedure.stage() == GlowstoneCreateProcedureStage.ZONE_SELECTION) {
                if (action == Action.LEFT_CLICK_BLOCK) {

                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.YELLOW + "First position set.");
                    player.sendMessage(" ");

                    procedure.locationOne(event.getClickedBlock().getLocation());
                    return;
                }

                if (action == Action.RIGHT_CLICK_BLOCK) {

                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.YELLOW + "Second position set.");
                    player.sendMessage(" ");

                    procedure.locationTwo(event.getClickedBlock().getLocation());
                    return;
                }

                if (action == Action.RIGHT_CLICK_AIR) {
                    int clicks = procedure.clicks();

                    if (procedure.locationOne() == null && procedure.locationTwo() == null) {
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.RED + "Glowstone Mountain locations not defined.");
                        player.sendMessage(" ");
                    }

                    if (clicks == 0) {
                        procedure.clicks(1);
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.YELLOW + "Click again to reset Glowstone Mountain locations.");
                        player.sendMessage(" ");
                        return;
                    }


                    procedure.clicks(0);

                    procedure.locationOne(null);
                    procedure.locationTwo(null);

                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.YELLOW + "Glowstone Mountain zone locations have been reset.");
                    player.sendMessage(" ");
                    return;
                }

                if (action == Action.LEFT_CLICK_AIR && player.isSneaking()) {

                    if (procedure.locationOne() == null && procedure.locationTwo() == null) {
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.RED + "Glowstone Mountain locations not defined.");
                        player.sendMessage(" ");
                        return;
                    }

                    player.getInventory().removeItem(GlowstoneCreateProcedure.getWand());
                    player.openInventory(GlowstoneCreateProcedure.getConfirmationInventory(procedure));
                    procedure.stage(GlowstoneCreateProcedureStage.CONFIRMATION);
                }

            }
        }
    }

}
