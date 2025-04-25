package us.zonix.hcfactions.deathlookup;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.fight.ProfileFight;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.fight.ProfileFight;

import java.text.SimpleDateFormat;

public class DeathLookupListeners implements Listener {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = Profile.getByPlayer(player);
        ItemStack itemStack = event.getCurrentItem();

        if (itemStack != null && itemStack.getType() != Material.AIR) {
            String title = event.getInventory().getTitle();
            String displayName = itemStack.getItemMeta().getDisplayName();
            DeathLookup lookup = profile.getDeathLookup();

            if (((title.contains("Deaths") || title.contains("Death #") || title.contains("Inventory #")) && us.zonix.core.profile.Profile.getByUuid(player.getUniqueId()).getRank().isAboveOrEqual(Rank.ADMINISTRATOR) && lookup != null)) {
                event.setCancelled(true);

                if (displayName == null) {
                    return;
                }

                int page = 0;
                int total = 0;
                if (title.contains("Deaths")) {
                    page = Integer.parseInt(title.substring(title.lastIndexOf("/") - 1, title.lastIndexOf("/")));
                    total = Integer.parseInt(title.substring(title.lastIndexOf("/") + 1, title.lastIndexOf("/") + 2));
                }

                if (displayName.contains("Next Page")) {
                    if (page + 1 > total) {
                        player.sendMessage(ChatColor.RED + "There are no more pages.");
                        return;
                    }
                    player.openInventory(lookup.getDeathInventory(page + 1));
                    return;
                }

                if (displayName.contains("Previous Page")) {
                    if (page == 1) {
                        player.sendMessage(ChatColor.RED + "You're on the first page.");
                        return;
                    }
                    player.openInventory(lookup.getDeathInventory(page - 1));
                    return;
                }

                if (displayName.contains("Previous Death")) {
                    ProfileFight fight = lookup.getData().getFight();
                    if (fight != null) {

                        if (lookup.getData().getIndex() == 0 && lookup.getData().getPage() == 1) {
                            player.sendMessage(ChatColor.RED + "You're viewing the latest death.");
                            return;
                        }

                        ProfileFight previousFight;
                        int previousPage;
                        int previousIndex;
                        if (lookup.getData().getIndex() == 0) {
                            previousFight = lookup.getDeaths(lookup.getData().getPage() - 1).get(8);
                            previousPage = lookup.getData().getPage() - 1;
                            previousIndex = 8;

                        } else {
                            previousFight = lookup.getDeaths(lookup.getData().getPage()).get(lookup.getData().getIndex() - 1);
                            previousPage = lookup.getData().getPage();
                            previousIndex = lookup.getData().getIndex() - 1;
                        }

                        lookup.getData().setFight(previousFight);
                        lookup.getData().setPage(previousPage);
                        lookup.getData().setIndex(previousIndex);

                        player.openInventory(lookup.getFightInventory(previousFight));
                    }

                    return;
                }

                if (displayName.contains("Next Death")) {
                    ProfileFight fight = lookup.getData().getFight();
                    if (fight != null) {
                        if (lookup.getData().getIndex() == (lookup.getDeaths(lookup.getTotalPages()).size() - 1) && lookup.getData().getPage() == lookup.getTotalPages()) {
                            player.sendMessage(ChatColor.RED + "You're viewing the last death.");
                            return;
                        }

                        ProfileFight nextFight;
                        int nextPage;
                        int nextIndex;
                        if (lookup.getData().getIndex() == 8) {
                            nextFight = lookup.getDeaths(lookup.getData().getPage() + 1).get(0);
                            nextPage = lookup.getData().getPage() + 1;
                            nextIndex = 0;

                        } else {
                            nextFight = lookup.getDeaths(lookup.getData().getPage()).get(lookup.getData().getIndex() + 1);
                            nextPage = lookup.getData().getPage();
                            nextIndex = lookup.getData().getIndex() + 1;
                        }

                        lookup.getData().setFight(nextFight);
                        lookup.getData().setPage(nextPage);
                        lookup.getData().setIndex(nextIndex);

                        player.openInventory(lookup.getFightInventory(nextFight));
                    }

                    return;
                }

                if (displayName.contains("Death Location")) {
                    player.teleport(lookup.getData().getFight().getLocation());
                    return;
                }

                if (displayName.contains("Inventory Contents")) {
                    ProfileFight fight = lookup.getData().getFight();
                    if (fight != null) {
                        player.openInventory(lookup.getFightItemInventory(fight));
                    }
                }

                if (displayName.contains("Return")) {
                    ProfileFight fight = lookup.getData().getFight();
                    if (fight != null) {
                        player.openInventory(lookup.getFightInventory(fight));
                    }
                }

                if (displayName.contains("Copy Inventory")) {
                    ProfileFight fight = lookup.getData().getFight();
                    if (fight != null) {
                        Bukkit.dispatchCommand(player, "lastinv " + lookup.getProfile().getName() + " " + fight.getUuid());
                        player.closeInventory();
                    }
                }

                if (displayName.contains("Revive")) {
                    ProfileFight fight = lookup.getData().getFight();
                    if (fight != null) {
                        Bukkit.dispatchCommand(player, "pvp revive " + lookup.getProfile().getName());
                        player.closeInventory();
                    }
                }

                if (itemStack.getType() == Material.SKULL_ITEM && itemStack.getDurability() == 0) {
                    ProfileFight fight = lookup.getDeaths(page).get(event.getRawSlot() - 9);
                    lookup.getData().setFight(fight);
                    lookup.getData().setPage(page);
                    lookup.getData().setIndex(event.getRawSlot() - 9);
                    player.openInventory(lookup.getFightInventory(fight));
                }

            }
        }

    }

}
