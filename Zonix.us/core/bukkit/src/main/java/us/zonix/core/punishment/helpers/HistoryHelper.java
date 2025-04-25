package us.zonix.core.punishment.helpers;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.profile.Profile;
import us.zonix.core.punishment.Punishment;
import us.zonix.core.punishment.PunishmentType;
import us.zonix.core.shared.api.callback.Callback;
import us.zonix.core.util.ItemUtil;
import us.zonix.core.util.UUIDType;
import us.zonix.core.util.inventory.InventoryUI;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

public class HistoryHelper {

    private Player sender;

    private UUID uuid;
    private String name;

    public HistoryHelper(Player sender, String name) {
        this.sender = sender;
        this.name = name;

        this.getPlayerInformation((useless) -> {
            if (this.uuid == null || this.name == null) {
                sender.sendMessage(ChatColor.RED + "Failed to find that player.");
            }
            else {
                this.attempt();
            }
        });
    }

    private void getPlayerInformation(Callback callback) {
        Player player = Bukkit.getPlayer(this.name);

        if (player != null) {
            this.uuid = player.getUniqueId();
            this.name = player.getName();

            callback.callback(null);
        }
        else {
            this.sender.sendMessage(ChatColor.GRAY + "(Resolving player information...)");

            Profile.getPlayerInformation(this.name, this.sender, (retrieved) -> {
                if (retrieved != null) {
                    uuid = UUIDType.fromString(retrieved.getAsJsonObject().get("uuid").getAsString());
                }

                callback.callback(null);
            });
        }
    }

    private void attempt() {
        new BukkitRunnable() {
            public void run() {
                Profile profile = Profile.getByUuid(uuid);

                if (profile.getAlts().size() == 0) {
                    profile.loadProfileAlts();
                }

                buildDisplayInventory(sender, profile);

            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }

    private void buildDisplayInventory(Player sender, Profile profile) {

        new BukkitRunnable() {
            public void run() {
                InventoryUI inventoryUI = new InventoryUI("Punishments (Menu)", true, 1);

                inventoryUI.setItem(0, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.WOOL, ChatColor.RED.toString() + ChatColor.BOLD + "Blacklist", 1, (short) 14)) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player) event.getWhoClicked();
                        buildInventory(player, profile, PunishmentType.BLACKLIST);
                    }
                });
                inventoryUI.setItem(2, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.WOOL, ChatColor.RED.toString() + ChatColor.BOLD + "Bans", 1, (short) 1)) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player) event.getWhoClicked();
                        buildInventory(player, profile, PunishmentType.BAN);
                    }
                });
                inventoryUI.setItem(4, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.WOOL, ChatColor.RED.toString() + ChatColor.BOLD + "Temporary Bans", 1, (short) 4)) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player) event.getWhoClicked();
                        buildInventory(player, profile, PunishmentType.TEMPBAN);
                    }
                });
                inventoryUI.setItem(6, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.WOOL, ChatColor.RED.toString() + ChatColor.BOLD + "Mutes", 1, (short) 8)) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player) event.getWhoClicked();
                        buildInventory(player, profile, PunishmentType.MUTE);
                    }
                });
                inventoryUI.setItem(8, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.WOOL, ChatColor.RED.toString() + ChatColor.BOLD + "Alts Punishments", 1, (short) 5)) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player) event.getWhoClicked();

                        if (profile.getAlts().size() == 0) {
                            player.sendMessage(ChatColor.RED + "That user doesn't have any alts.");
                            return;
                        }

                        InventoryUI inventory = new InventoryUI("Punishments (Alts)", true, 5);

                        for (UUID altUUID : profile.getAlts()) {

                            if (Bukkit.getOfflinePlayer(altUUID) == null) {
                                continue;
                            }

                            if (!altUUID.equals(uuid)) {
                                String altName = Bukkit.getOfflinePlayer(altUUID).getName();
                                inventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.SKULL_ITEM, ChatColor.GREEN.toString() + ChatColor.BOLD + altName)) {
                                    @Override
                                    public void onClick(InventoryClickEvent event) {
                                        Player player = (Player) event.getWhoClicked();

                                        CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
                                            Profile altProfile = Profile.getByUuid(altUUID);

                                            if (altProfile == null) {
                                                player.closeInventory();
                                                return;
                                            }

                                            buildDisplayInventory(player, altProfile);
                                        });
                                    }
                                });
                            }
                        }

                        sender.closeInventory();
                        player.openInventory(inventory.getCurrentPage());
                    }
                });

                sender.closeInventory();
                sender.openInventory(inventoryUI.getCurrentPage());
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }

    private void buildInventory(Player sender, Profile profile, PunishmentType punishmentType) {

        new BukkitRunnable() {
            public void run() {
                List<Punishment> punishments = profile.getPunishmentsByType(punishmentType);

                if (punishments.size() == 0) {
                    sender.sendMessage(ChatColor.RED + "There's nothing to show here.");
                    return;
                }

                InventoryUI inventoryUI = new InventoryUI("Punishments " + "(" + StringUtils.capitalize(punishmentType.name().toLowerCase()) + ")", true, 5);

                inventoryUI.setItem(36, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.ARROW, ChatColor.RED + "Go Back")) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player) event.getWhoClicked();
                        player.closeInventory();
                        buildDisplayInventory(player, profile);
                    }

                });

                int count = 1;

                for (Punishment punishment : punishments) {

                    if(count == 37) {
                        count++;
                        continue;
                    }

                    if (punishment.getUuid() == null) {
                        continue;
                    }

                    if (Bukkit.getOfflinePlayer(punishment.getUuid()) == null) {
                        continue;
                    }

                    String punished = Bukkit.getOfflinePlayer(punishment.getUuid()).getName();

                    ItemStack item = ItemUtil.createItem(Material.PAPER, ChatColor.GREEN.toString() + ChatColor.BOLD + "PUNISHMENT #" + count);

                    String addedBy = punishment.getAddedBy() == null ? "CONSOLE" : Bukkit.getOfflinePlayer(punishment.getAddedBy()).getName();

                    if (punishment.isRemoved()) {
                        String removedBy = punishment.getRemovedBy() == null ? "CONSOLE" : Bukkit.getOfflinePlayer(punishment.getRemovedBy()).getName();
                        ItemUtil.reloreItem(item, ChatColor.GRAY + "User: " + ChatColor.GREEN + punished, ChatColor.GRAY + "Added by: " + ChatColor.GREEN + addedBy, ChatColor.GRAY + "Reason: " + ChatColor.GREEN + punishment.getReason(), ChatColor.GRAY + "Time Left: " + ChatColor.GREEN + punishment.getTimeLeft(), ChatColor.GRAY + "Added at: " + ChatColor.GREEN + punishment.getAddedAtFormatted(), ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------", ChatColor.GRAY + "Removed by: " + ChatColor.GREEN + removedBy, ChatColor.GRAY + "Removed Reason: " + ChatColor.GREEN + punishment.getRemovedReason(), ChatColor.GRAY + "Removed at: " + ChatColor.GREEN + punishment.getRemovedAtFormatted());
                    } else {
                        ItemUtil.reloreItem(item, ChatColor.GRAY + "User: " + ChatColor.GREEN + punished, ChatColor.GRAY + "Added by: " + ChatColor.GREEN + addedBy, ChatColor.GRAY + "Reason: " + ChatColor.GREEN + punishment.getReason(), ChatColor.GRAY + "Time Left: " + ChatColor.GREEN + punishment.getTimeLeft(), ChatColor.GRAY + "Added at: " + ChatColor.GREEN + punishment.getAddedAtFormatted());
                    }

                    inventoryUI.addItem(new InventoryUI.AbstractClickableItem(item) {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            Player player = (Player) event.getWhoClicked();

                            String addedBy = punishment.getAddedBy() == null ? "CONSOLE" : Bukkit.getOfflinePlayer(punishment.getAddedBy()).getName();
                            player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");

                            player.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "PUNISHMENT INFO");
                            player.sendMessage(ChatColor.GREEN + "User: " + punished);
                            player.sendMessage(ChatColor.GREEN + "Added by: " + addedBy);
                            player.sendMessage(ChatColor.GREEN + "Reason: " + punishment.getReason());
                            player.sendMessage(ChatColor.GREEN + "Date added: " + punishment.getAddedAtFormatted());
                            player.sendMessage(ChatColor.GREEN + "Time Left: " + punishment.getTimeLeft());

                            if (punishment.isRemoved()) {
                                String removedBy = punishment.getRemovedBy() == null ? "CONSOLE" : Bukkit.getOfflinePlayer(punishment.getRemovedBy()).getName();
                                player.sendMessage(ChatColor.GREEN + "Removed by: " + removedBy);
                                player.sendMessage(ChatColor.GREEN + "Removed reason: " + punishment.getRemovedReason());
                                player.sendMessage(ChatColor.GREEN + "Date removed: " + punishment.getRemovedAtFormatted());

                            }

                            player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");

                            player.closeInventory();
                        }

                    });

                    count++;
                }

                sender.closeInventory();
                sender.openInventory(inventoryUI.getCurrentPage());
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }
}
