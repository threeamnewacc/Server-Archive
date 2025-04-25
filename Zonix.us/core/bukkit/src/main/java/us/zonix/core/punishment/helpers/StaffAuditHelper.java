package us.zonix.core.punishment.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.api.request.PunishmentRequest;
import us.zonix.core.profile.Profile;
import us.zonix.core.punishment.Punishment;
import us.zonix.core.shared.api.callback.Callback;
import us.zonix.core.util.ItemUtil;
import us.zonix.core.util.UUIDType;
import us.zonix.core.util.inventory.InventoryUI;

import java.util.*;

public class StaffAuditHelper {

    private Player sender;

    private UUID uuid;
    private String name;

    public StaffAuditHelper(Player sender, String name) {
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

                List<Punishment> punishments = new ArrayList<>();
                JsonElement response = CorePlugin.getInstance().getRequestProcessor().sendRequest(new PunishmentRequest.FetchByStaffUuidRequest(uuid));

                if (response.isJsonNull() || response.isJsonPrimitive()) {
                    System.out.println("Error while getting JSON response.");
                    System.out.println("Issue: " + response.toString());
                    return;
                }

                JsonArray data = response.getAsJsonArray();

                data.iterator().forEachRemaining((punishmentElement) -> {
                    JsonObject punishmentObject = punishmentElement.getAsJsonObject();
                    punishments.add(Punishment.fromJson(punishmentObject));
                });

                if (punishments.size() == 0) {
                    sender.sendMessage(ChatColor.RED + "That player doesn't have any punishments logs.");
                    return;
                }

                int count = 1;
                InventoryUI inventoryUI = new InventoryUI("Punishment Logs", true, 5);

                Collections.reverse(punishments);

                for(Punishment punishment : punishments) {

                    if(punishment.getUuid() == null) {
                        continue;
                    }

                    if(Bukkit.getOfflinePlayer(punishment.getUuid()) == null) {
                        continue;
                    }

                    String punished = Bukkit.getOfflinePlayer(punishment.getUuid()).getName();

                    ItemStack item = ItemUtil.createItem(Material.PAPER, ChatColor.GREEN.toString() + ChatColor.BOLD + "PUNISHMENT #" + count);

                    String addedBy = punishment.getAddedBy() == null ? "CONSOLE" : Bukkit.getOfflinePlayer(punishment.getAddedBy()).getName();

                    if(punishment.isRemoved()) {
                        String removedBy = punishment.getRemovedBy() == null ? "CONSOLE" : Bukkit.getOfflinePlayer(punishment.getRemovedBy()).getName();
                        ItemUtil.reloreItem(item, ChatColor.GRAY + "User: " + ChatColor.GREEN + punished, ChatColor.GRAY + "Added by: " + ChatColor.GREEN + addedBy, ChatColor.GRAY + "Reason: " + ChatColor.GREEN + punishment.getReason(), ChatColor.GRAY + "Time Left: " + ChatColor.GREEN + punishment.getTimeLeft(), ChatColor.GRAY + "Added at: " + ChatColor.GREEN + punishment.getAddedAtFormatted(), ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------", ChatColor.GRAY +"Removed by: " + ChatColor.GREEN + removedBy, ChatColor.GRAY +"Removed Reason: " + ChatColor.GREEN + punishment.getRemovedReason(), ChatColor.GRAY +"Removed at: " + ChatColor.GREEN + punishment.getRemovedAtFormatted());
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

                            if(punishment.isRemoved()) {
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

                sender.openInventory(inventoryUI.getCurrentPage());


            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }

}
