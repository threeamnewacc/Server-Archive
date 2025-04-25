package us.zonix.practice.settings;

import java.util.Arrays;
import me.maiko.dexter.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.zonix.practice.Practice;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.settings.item.ProfileOptionsItem;
import us.zonix.practice.settings.item.ProfileOptionsItemState;

public class ProfileOptionsListeners implements Listener {
    @EventHandler
    public void onInventoryInteractEvent(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        PlayerData profile = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        Inventory inventory = event.getInventory();
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            Inventory options = profile.getOptions().getInventory();
            if (inventory.getTitle().equals(options.getTitle()) && Arrays.equals((Object[])inventory.getContents(), (Object[])options.getContents())) {
                event.setCancelled(true);
                ProfileOptionsItem item = ProfileOptionsItem.fromItem(itemStack);
                if (item != null) {
                    if (item == ProfileOptionsItem.DUEL_REQUESTS) {
                        profile.getOptions().setDuelRequests(!profile.getOptions().isDuelRequests());
                        inventory.setItem(
                            event.getRawSlot(),
                            item.getItem(profile.getOptions().isDuelRequests() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED)
                        );
                    } else if (item == ProfileOptionsItem.PARTY_INVITES) {
                        profile.getOptions().setPartyInvites(!profile.getOptions().isPartyInvites());
                        inventory.setItem(
                            event.getRawSlot(),
                            item.getItem(profile.getOptions().isPartyInvites() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED)
                        );
                    } else if (item == ProfileOptionsItem.TOGGLE_SCOREBOARD) {
                        if (profile.getOptions().getScoreboard() == ProfileOptionsItemState.ENABLED) {
                            profile.getOptions().setScoreboard(ProfileOptionsItemState.SHOW_PING);
                        } else if (profile.getOptions().getScoreboard() == ProfileOptionsItemState.SHOW_PING) {
                            profile.getOptions().setScoreboard(ProfileOptionsItemState.DISABLED);
                        } else if (profile.getOptions().getScoreboard() == ProfileOptionsItemState.DISABLED) {
                            profile.getOptions().setScoreboard(ProfileOptionsItemState.ENABLED);
                        }

                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().getScoreboard()));
                    } else if (item == ProfileOptionsItem.ALLOW_SPECTATORS) {
                        profile.getOptions().setSpectators(!profile.getOptions().isSpectators());
                        inventory.setItem(
                            event.getRawSlot(),
                            item.getItem(profile.getOptions().isSpectators() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED)
                        );
                    } else if (item == ProfileOptionsItem.TOGGLE_VISIBILITY) {
                        if (!player.hasPermission("practice.visibility")) {
                            player.sendMessage(CC.RED + "No permission.");
                            return;
                        }

                        PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
                        if (playerData.getPlayerState() != PlayerState.SPAWN) {
                            player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                            return;
                        }

                        playerData.getOptions().setVisibility(!playerData.getOptions().isVisibility());
                        inventory.setItem(
                            event.getRawSlot(),
                            item.getItem(profile.getOptions().isVisibility() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED)
                        );
                        Bukkit.getServer()
                            .getOnlinePlayers()
                            .forEach(
                                p -> {
                                    boolean playerSeen = playerData.getOptions().isVisibility()
                                        && player.hasPermission("practice.visibility")
                                        && Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId()).getPlayerState() == PlayerState.SPAWN;
                                    boolean pSeen = playerData.getOptions().isVisibility()
                                        && player.hasPermission("practice.visibility")
                                        && Practice.getInstance().getPlayerManager().getPlayerData(p.getUniqueId()).getPlayerState() == PlayerState.SPAWN;
                                    if (playerSeen) {
                                        p.showPlayer(player);
                                    } else {
                                        p.hidePlayer(player);
                                    }

                                    if (pSeen) {
                                        player.showPlayer(p);
                                    } else {
                                        player.hidePlayer(p);
                                    }
                                }
                            );
                        player.sendMessage(ChatColor.YELLOW + "You have toggled the visibility.");
                    } else if (item == ProfileOptionsItem.TOGGLE_TIME) {
                        if (profile.getOptions().getTime() == ProfileOptionsItemState.DAY) {
                            profile.getOptions().setTime(ProfileOptionsItemState.SUNSET);
                            player.performCommand("sunset");
                        } else if (profile.getOptions().getTime() == ProfileOptionsItemState.SUNSET) {
                            profile.getOptions().setTime(ProfileOptionsItemState.NIGHT);
                            player.performCommand("night");
                        } else if (profile.getOptions().getTime() == ProfileOptionsItemState.NIGHT) {
                            profile.getOptions().setTime(ProfileOptionsItemState.DAY);
                            player.performCommand("day");
                        }

                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().getTime()));
                    } else if (item == ProfileOptionsItem.TOGGLE_PING) {
                        if (!player.hasPermission("practice.pingmatching")) {
                            player.sendMessage(CC.RED + "No permission.");
                            player.closeInventory();
                            return;
                        }

                        if (profile.getOptions().getPingBased() == ProfileOptionsItemState.NO_RANGE) {
                            profile.getOptions().setPingBased(ProfileOptionsItemState.RANGE_25);
                            profile.setPingRange(25);
                        } else if (profile.getOptions().getPingBased() == ProfileOptionsItemState.RANGE_25) {
                            profile.getOptions().setPingBased(ProfileOptionsItemState.RANGE_50);
                            profile.setPingRange(50);
                        } else if (profile.getOptions().getPingBased() == ProfileOptionsItemState.RANGE_50) {
                            profile.getOptions().setPingBased(ProfileOptionsItemState.RANGE_75);
                            profile.setPingRange(75);
                        } else if (profile.getOptions().getPingBased() == ProfileOptionsItemState.RANGE_75) {
                            profile.getOptions().setPingBased(ProfileOptionsItemState.RANGE_100);
                            profile.setPingRange(100);
                        } else if (profile.getOptions().getPingBased() == ProfileOptionsItemState.RANGE_100) {
                            profile.getOptions().setPingBased(ProfileOptionsItemState.NO_RANGE);
                            profile.setPingRange(-1);
                        }

                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().getPingBased()));
                    }
                }
            }
        }
    }
}
