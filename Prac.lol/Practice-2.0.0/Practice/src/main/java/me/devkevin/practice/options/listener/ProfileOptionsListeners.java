package me.devkevin.practice.options.listener;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.practice.Practice;
import me.devkevin.practice.options.item.ProfileOptionsItem;
import me.devkevin.practice.options.item.ProfileOptionsItemState;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Copyright 11/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ProfileOptionsListeners implements Listener {

    @EventHandler
    public void onInventoryInteractEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());
        CoreProfile playerData = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

        Inventory inventory = event.getInventory();
        ItemStack itemStack = event.getCurrentItem();

        if (itemStack != null && itemStack.getType() != Material.AIR) {
            Inventory options = profile.getOptions().getInventory();
            if (inventory.getTitle().equals(options.getTitle()) && Arrays.equals(inventory.getContents(), options.getContents())) {
                event.setCancelled(true);
                ProfileOptionsItem item = ProfileOptionsItem.fromItem(itemStack);

                if (itemStack.getType() == Material.BOOKSHELF) {
                    event.setCancelled(true);
                    player.performCommand("settings");
                    System.out.println("TEST");
                    return;
                }

                if (itemStack.getType() == Material.BED) {
                    event.setCancelled(true);
                    player.openInventory(Practice.getInstance().getGeneralSettingMenu().getGeneralMenu().getCurrentPage());
                    return;
                }

                if (item != null) {
                    if (item == ProfileOptionsItem.DUEL_REQUESTS) {
                        profile.getOptions().setDuelRequests(!profile.getOptions().isDuelRequests());
                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().isDuelRequests() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
                    } else if (item == ProfileOptionsItem.PARTY_INVITES) {
                        profile.getOptions().setPartyInvites(!profile.getOptions().isPartyInvites());
                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().isPartyInvites() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
                    } else if (item == ProfileOptionsItem.TOGGLE_SCOREBOARD) {
                        profile.getOptions().setScoreboard(!profile.getOptions().isScoreboard());
                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().isScoreboard() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));
                    } else if (item == ProfileOptionsItem.ALLOW_SPECTATORS) {

                        if (!player.hasPermission("practice.donors.*")) {
                            player.sendMessage("");
                            player.sendMessage(CC.RED + "You cannot toggle spectators with " + playerData.getGrant().getRank().getColor() + playerData.getGrant().getRank().getName() + CC.RED + " rank.");
                            player.sendMessage(CC.GRAY + "Purchase rank at https://udrop.buycraft.net/.");
                            player.sendMessage("");
                            return;
                        }

                        profile.getOptions().setSpectators(!profile.getOptions().isSpectators());
                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().isSpectators() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));

                    } else if (item == ProfileOptionsItem.TOGGLE_TIME) {

                        if (profile.getOptions().getTime() == ProfileOptionsItemState.DAY) {
                            profile.getOptions().setTime(ProfileOptionsItemState.SUNSET);
                            player.performCommand("sunset");
                        }
                        else if(profile.getOptions().getTime() == ProfileOptionsItemState.SUNSET) {
                            profile.getOptions().setTime(ProfileOptionsItemState.NIGHT);
                            player.performCommand("night");
                        }

                        else if (profile.getOptions().getTime() == ProfileOptionsItemState.NIGHT) {
                            profile.getOptions().setTime(ProfileOptionsItemState.DAY);
                            player.performCommand("day");
                        }

                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().getTime()));
                    } else if (itemStack.getType() != Material.STAINED_GLASS_PANE) {
                        event.setCancelled(true);
                    } else if (item == ProfileOptionsItem.TOGGLE_VISIBILITY) {

                        if (!player.hasPermission("practice.donors.*")) {
                            player.sendMessage(CC.RED + "You do not have permission to use that command.");
                            return;
                        }

                        if (profile.getState() != ProfileState.SPAWN) {
                            player.sendMessage(CC.RED + "Cannot execute this command in your current state.");
                            return;
                        }

                        profile.getOptions().setVisibility(!profile.getOptions().isVisibility());
                        inventory.setItem(event.getRawSlot(), item.getItem(profile.getOptions().isVisibility() ? ProfileOptionsItemState.ENABLED : ProfileOptionsItemState.DISABLED));

                        Bukkit.getServer().getOnlinePlayers().forEach(p -> {

                            boolean playerSeen = profile.getOptions().isVisibility() && player.hasPermission("practice.donors.*") && Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId()).getState() == ProfileState.SPAWN;
                            boolean pSeen = profile.getOptions().isVisibility() && player.hasPermission("practice.donors.*") && Practice.getInstance().getProfileManager().getProfileData(p.getUniqueId()).getState() == ProfileState.SPAWN;

                            if(playerSeen) {
                                p.showPlayer(player);
                            } else {
                                p.hidePlayer(player);
                            }

                            if(pSeen) {
                                player.showPlayer(p);
                            } else {
                                player.hidePlayer(p);
                            }
                        });

                        player.sendMessage(CC.YELLOW + "You have toggled the visibility.");
                    } else if (item == ProfileOptionsItem.TOGGLE_PING) {

                        if (!player.hasPermission("practice.donors.emerald")) {
                            player.sendMessage(CC.RED + "This option requires Void Emerald or up.");
                            player.closeInventory();
                            return;
                        }

                        if (profile.getOptions().getPingBased() == ProfileOptionsItemState.NO_RANGE) {
                            profile.getOptions().setPingBased(ProfileOptionsItemState.RANGE_25);
                            profile.setPingRange(25);
                        }
                        else if (profile.getOptions().getPingBased() == ProfileOptionsItemState.RANGE_25) {
                            profile.getOptions().setPingBased(ProfileOptionsItemState.RANGE_50);
                            profile.setPingRange(50);
                        }

                        else if (profile.getOptions().getPingBased() == ProfileOptionsItemState.RANGE_50) {
                            profile.getOptions().setPingBased(ProfileOptionsItemState.RANGE_75);
                            profile.setPingRange(75);
                        }

                        else if (profile.getOptions().getPingBased() == ProfileOptionsItemState.RANGE_75) {
                            profile.getOptions().setPingBased(ProfileOptionsItemState.RANGE_100);
                            profile.setPingRange(100);
                        }

                        else if (profile.getOptions().getPingBased() == ProfileOptionsItemState.RANGE_100) {
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

