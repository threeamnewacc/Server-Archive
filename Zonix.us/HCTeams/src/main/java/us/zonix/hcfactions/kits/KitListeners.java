package us.zonix.hcfactions.kits;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.crate.Crate;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.factions.claims.Claim;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.factions.type.SystemFaction;
import us.zonix.hcfactions.kits.command.KitCommand;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.kit.ProfileKit;
import us.zonix.hcfactions.profile.kit.ProfileKitEnergy;
import us.zonix.hcfactions.util.ItemBuilder;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class KitListeners implements Listener {

    private HashMap<UUID, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onSignChange(final SignChangeEvent event) {
        final Player player = event.getPlayer();

        if (!player.isOp()) {
            return;
        }

        if (event.getLines().length < 2) {
            return;
        }
        Sign sign = (Sign) event.getBlock().getState();

        if (ChatColor.stripColor(this.getLine(sign, 0)).equalsIgnoreCase("[Class]")) {

            final Kit kit = Kit.getByName(ChatColor.stripColor(this.getLine(sign, 1)));
            if (kit == null) {
                player.sendMessage(ChatColor.RED + "That kit does not exist.");
                return;
            }
            event.setLine(0, "§c[Class]");
            event.setLine(1, "§f" + kit.getName());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (!(event.getClickedBlock().getState() instanceof Sign)) {
                return;
            }
            final Sign sign = (Sign)event.getClickedBlock().getState();


            if (!ChatColor.stripColor(this.getLine(sign, 0)).equalsIgnoreCase("[Class]")) {
                return;
            }

            event.setCancelled(true);

            Claim claim = Claim.getProminentClaimAt(sign.getBlock().getLocation());

            if (claim != null) {
                Faction faction = claim.getFaction();

                if(!(faction instanceof SystemFaction)) {
                    return;
                }

                if(((SystemFaction) faction).isDeathban()) {
                    return;
                }

                final Kit kit = Kit.getByName(ChatColor.stripColor(this.getLine(sign, 1)));

                if (kit == null) {
                    return;
                }


                if(this.cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage(ChatColor.RED + "You must wait before you load another kit.");
                    return;
                }


                kit.loadFullKit(player);

                this.cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 10 * 1000L);
            }
        }
    }

    @EventHandler
    public void onInventoryMenuClickEvent(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getInventory().getTitle().contains("Preview")) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null) {
                return;
            }

            if (!event.getCurrentItem().hasItemMeta()) {
                return;
            }

            if (!event.getCurrentItem().getItemMeta().hasDisplayName()) {
                return;
            }

            if (event.getCurrentItem().getType() == Material.ARROW) {
                KitCommand.openKitsInventory(player);
            }
        }
    }

    @EventHandler
    public void onInventorySelectClickEvent(InventoryClickEvent event) {

        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getInventory().getTitle().equalsIgnoreCase("HCF Kits")) {

            event.setCancelled(true);

            if(event.getCurrentItem() == null) {
                return;
            }

            if(!event.getCurrentItem().hasItemMeta()) {
                return;
            }

            if(!event.getCurrentItem().getItemMeta().hasDisplayName()) {
                return;
            }

            if(event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE && event.getCurrentItem().getDurability() != 7) {

                String kitName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).split(": ")[1];
                Kit kit = Kit.getByName(kitName);

                if(kit != null) {

                    if(event.getClick() == ClickType.RIGHT) {
                        KitCommand.openPreviewInventory(player, kit);
                    } else if(event.getClick() == ClickType.LEFT) {

                        player.closeInventory();

                        if(!us.zonix.core.profile.Profile.getByUuid(player.getUniqueId()).getRank().isAboveOrEqual(Rank.SILVER)) {
                            player.sendMessage(ChatColor.YELLOW + "Purchase HCF Kits @ store.zonix.us");
                            return;
                        }

                        Profile profile = Profile.getByUuid(player.getUniqueId());

                        if (profile == null) {
                            return;
                        }

                        if (KitCommand.isCooldownActive(profile, kit)) {
                            player.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KIT_COMMAND.DELAY").replace("%TIME%", KitCommand.getTimeLeft(profile, kit)));
                            return;
                        }


                        kit.loadKit(player);
                        profile.getKitDelay().put(kit, System.currentTimeMillis() + 172800 * 1000L);

                        player.sendMessage(FactionsPlugin.getInstance().getLanguageConfig().getString("KIT_COMMAND.SUCCESS").replace("%KIT%", kit.getName()));
                    }
                }
            }
        }

    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        this.cooldown.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(final PlayerKickEvent event) {
        this.cooldown.remove(event.getPlayer().getUniqueId());
    }

    private String getLine(Sign sign, int line) {
        StringBuilder builder = new StringBuilder();
        for (char c : sign.getLine(line).toCharArray())
        {
            if (c < 0xF700 || c > 0xF747)
            {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
