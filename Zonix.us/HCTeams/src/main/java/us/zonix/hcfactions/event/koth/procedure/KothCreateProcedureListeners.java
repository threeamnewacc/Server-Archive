package us.zonix.hcfactions.event.koth.procedure;

import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.event.koth.KothEvent;
import us.zonix.hcfactions.profile.Profile;
import org.apache.commons.lang.StringUtils;
import us.zonix.hcfactions.factions.claims.ClaimPillar;
import us.zonix.hcfactions.event.EventZone;
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
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.factions.claims.ClaimPillar;
import us.zonix.hcfactions.profile.Profile;

public class KothCreateProcedureListeners implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = Profile.getByPlayer(player);

        KothCreateProcedure procedure = profile.getKothCreateProcedure();
        if (procedure != null) {
            if (procedure.stage() == KothCreateProcedureStage.CONFIRMATION && event.getInventory().getTitle().contains("Confirm KoTH?")) {
                event.setCancelled(true);

                ItemStack itemStack = event.getCurrentItem();
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    String displayName = itemStack.getItemMeta().getDisplayName();
                    if (displayName != null) {

                        if (displayName.contains("Cancel")) {
                            profile.setKothCreateProcedure(null);
                            player.sendMessage(" ");
                            player.sendMessage(ChatColor.RED + "KoTH create procedure cancelled.");
                            player.sendMessage(" ");
                            player.getInventory().removeItem(KothCreateProcedure.getWand());
                            player.closeInventory();
                            return;
                        }

                        if (displayName.contains("Confirm")) {
                            profile.setKothCreateProcedure(null);
                            player.sendMessage(" ");
                            player.sendMessage(ChatColor.YELLOW + "KoTH successfully created.");
                            player.sendMessage(" ");
                            player.getInventory().removeItem(KothCreateProcedure.getWand());
                            player.closeInventory();

                            EventZone zone = new EventZone(procedure.pillars()[0].getOriginalLocation(), procedure.pillars()[1].getOriginalLocation()); zone.setHeight(procedure.height());
                            new KothEvent(procedure.name(), procedure.height(), zone);
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

        KothCreateProcedure procedure = profile.getKothCreateProcedure();
        if (procedure != null) {
            if (procedure.stage() == KothCreateProcedureStage.CONFIRMATION && event.getInventory().getTitle().contains("Confirm KoTH?")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.openInventory(KothCreateProcedure.getConfirmationInventory(procedure));
                    }
                }.runTaskLaterAsynchronously(FactionsPlugin.getInstance(), 2L);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        KothCreateProcedure procedure = profile.getKothCreateProcedure();
        if (procedure != null) {
            event.setCancelled(true);

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                profile.setKothCreateProcedure(null);
                player.sendMessage(" ");
                player.sendMessage(ChatColor.RED + "KoTH create procedure cancelled.");
                player.sendMessage(" ");
                player.getInventory().removeItem(KothCreateProcedure.getWand());
                return;
            }

            if (procedure.stage() == KothCreateProcedureStage.NAME_SELECTION) {
                String name = event.getMessage().replace(" ", "");
                if (EventManager.getInstance().getByName(name) != null) {
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.RED + "An event with that name already exists.");
                    player.sendMessage(" ");
                    return;
                }

                player.sendMessage(" ");
                player.sendMessage(ChatColor.YELLOW + "KoTH name set to '" + procedure.name(name).name() + "'.");
                player.sendMessage(ChatColor.YELLOW + "Please type the height of the KoTH.");
                player.sendMessage(" ");
                procedure.stage(KothCreateProcedureStage.HEIGHT_SELECTION);
            }

            else if (procedure.stage() == KothCreateProcedureStage.HEIGHT_SELECTION) {
                String name = event.getMessage().replace(" ", "");
                if (!StringUtils.isNumeric(name)) {
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.RED + "That's an incorrect height number");
                    player.sendMessage(" ");
                    return;
                }

                player.sendMessage(" ");
                player.sendMessage(ChatColor.YELLOW + "KoTH height set to '" + procedure.height(Integer.parseInt(name)).height() + "'.");
                player.sendMessage(ChatColor.YELLOW + "You have received the zone wand.");
                player.sendMessage(" ");
                player.getInventory().removeItem(KothCreateProcedure.getWand());
                player.getInventory().addItem(KothCreateProcedure.getWand());
                procedure.stage(KothCreateProcedureStage.ZONE_SELECTION);
            }

        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        KothCreateProcedure procedure = profile.getKothCreateProcedure();
        if (event.getItemDrop().getItemStack().isSimilar(KothCreateProcedure.getWand())) {
            event.getItemDrop().remove();
            if (procedure != null) {
                profile.setKothCreateProcedure(null);
                player.sendMessage(" ");
                player.sendMessage(ChatColor.RED + "KoTH create procedure cancelled.");
                player.sendMessage(" ");
                player.getInventory().removeItem(KothCreateProcedure.getWand());
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        KothCreateProcedure procedure = profile.getKothCreateProcedure();
        Action action = event.getAction();

        if (procedure == null && event.getItem() != null && event.getItem().isSimilar(KothCreateProcedure.getWand())) {
            player.getInventory().removeItem(event.getItem());
            return;
        }

        if (procedure != null && event.getItem() != null && event.getItem().isSimilar(KothCreateProcedure.getWand())) {
            event.setCancelled(true);
            if (procedure.stage() == KothCreateProcedureStage.ZONE_SELECTION) {
                if (action == Action.LEFT_CLICK_BLOCK) {
                    ClaimPillar claimPillar = procedure.pillars()[0];
                    if (claimPillar != null) {
                        claimPillar.remove();
                    }

                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.YELLOW + "First position set.");
                    player.sendMessage(" ");

                    procedure.pillars()[0] = new ClaimPillar(player, event.getClickedBlock().getLocation()).show(Material.LAPIS_BLOCK, 0);
                    return;
                }

                if (action == Action.RIGHT_CLICK_BLOCK) {
                    ClaimPillar claimPillar = procedure.pillars()[1];
                    if (claimPillar != null) {
                        claimPillar.remove();
                    }

                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.YELLOW + "Second position set.");
                    player.sendMessage(" ");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            procedure.pillars()[1] = new ClaimPillar(player, event.getClickedBlock().getLocation()).show(Material.LAPIS_BLOCK, 0);
                        }
                    }.runTaskLaterAsynchronously(FactionsPlugin.getInstance(), 2L);
                    return;
                }

                if (action == Action.RIGHT_CLICK_AIR) {
                    int clicks = procedure.clicks();

                    if (procedure.pillars()[0] == null && procedure.pillars()[1] == null) {
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.RED + "KoTH pillars not defined.");
                        player.sendMessage(" ");
                    }

                    if (clicks == 0) {
                        procedure.clicks(1);
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.YELLOW + "Click again to reset KoTH pillars.");
                        player.sendMessage(" ");
                        return;
                    }

                    for (ClaimPillar claimPillar : procedure.pillars()) {
                        if (claimPillar != null) {
                            claimPillar.remove();
                        }
                    }

                    procedure.clicks(0);

                    procedure.pillars()[0] = null;
                    procedure.pillars()[1] = null;

                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.YELLOW + "KoTH zone pillars have been reset.");
                    player.sendMessage(" ");
                    return;
                }

                if (action == Action.LEFT_CLICK_AIR && player.isSneaking()) {

                    for (ClaimPillar claimPillar : procedure.pillars()) {
                        if (claimPillar == null) {
                            player.sendMessage(" ");
                            player.sendMessage(ChatColor.RED + "KoTH zone not defined.");
                            player.sendMessage(" ");
                            return;
                        }
                    }

                    for (ClaimPillar claimPillar : procedure.pillars()) {
                        claimPillar.remove();
                    }

                    player.getInventory().removeItem(KothCreateProcedure.getWand());
                    player.openInventory(KothCreateProcedure.getConfirmationInventory(procedure));
                    procedure.stage(KothCreateProcedureStage.CONFIRMATION);
                }

            }
        }
    }

}
