package net.badlion.capturetheflag.inventories;


import net.badlion.capturetheflag.CTFPlayer;
import net.badlion.capturetheflag.CTFTeam;
import net.badlion.capturetheflag.CTF;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.managers.MPGTeamManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamSelectorInventory {

    public static ItemStack selectTeamItem;

	public static SmellyInventory teamSelector;

	private static Map<CTFTeam, ItemStack> teamItems = new HashMap<>();

    public static void initialize() {
	    TeamSelectorInventory.selectTeamItem = ItemStackUtil.createItem(Material.WOOD_SWORD, ChatColor.GOLD + "Team Selector", ChatColor.AQUA + "Click to select your team!");

	    TeamSelectorInventory.teamSelector = new SmellyInventory(new TeamSelectorInventoryHandler(),
			    18, ChatColor.BOLD + ChatColor.AQUA.toString() + "CTF Team Selector");

	    for (MPGTeam mpgTeam : MPGTeamManager.getAllMPGTeams()) {
		    ItemStack teamItem = new ItemStack(Material.WOOL, 0, TeamSelectorInventory.getWoolColorFromChatColor(mpgTeam.getColor()));
		    ItemMeta itemMeta = teamItem.getItemMeta();
		    itemMeta.setDisplayName(mpgTeam.getColor() + mpgTeam.getTeamName() + " Team");
		    List<String> lore = new ArrayList<>();
		    lore.add(ChatColor.GOLD + "Click to join!");
		    itemMeta.setLore(lore);
		    teamItem.setItemMeta(itemMeta);
		    TeamSelectorInventory.teamItems.put((CTFTeam) mpgTeam, teamItem);
	    }

	    int i = 0;
	    for (ItemStack teamItem : TeamSelectorInventory.teamItems.values()) {
		    TeamSelectorInventory.teamSelector.getMainInventory().setItem(i, teamItem);
		    i++;
	    }

        (new PlayerListenerTask()).runTaskTimer(CTF.getInstance(), 0L, 20L);
    }

	public static void giveSelectTeamItem(Player player) {
        player.getInventory().setItem(0, TeamSelectorInventory.selectTeamItem);
        player.updateInventory();
    }

    public static short getWoolColorFromChatColor(ChatColor chatColor) {
        switch(chatColor){
            case AQUA: return 3;
            case BLACK: return 15;
            case BLUE: return 9;
            case DARK_BLUE: return 11;
            case GRAY: return 8;
            case DARK_GRAY: return 7;
            case DARK_GREEN: return 13;
            case DARK_PURPLE: return 10;
            case RED: return 14;
            case GOLD: return 1;
            case GREEN: return 5;
            case LIGHT_PURPLE: return 2;
            case WHITE: return 0;
            case YELLOW: return 4;
            default: return 0;
        }
    }

    private static class TeamSelectorInventoryHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
            CTFPlayer ctfPlayer = (CTFPlayer) MPGPlayerManager.getMPGPlayer(player);
            for (CTFTeam ctfTeam: TeamSelectorInventory.teamItems.keySet()) {
                if (item.getItemMeta().getDisplayName().equals(TeamSelectorInventory.teamItems.get(ctfTeam).getItemMeta().getDisplayName())) {
                    if (ctfPlayer != null) {
                        if (ctfPlayer.getTeam() == ctfTeam) {
                            ctfPlayer.getPlayer().sendMessage(ChatColor.GOLD + "Already on this team!");
                            return;
                        }
                    }

                    if (ctfPlayer.getTeam() != null) {
                        ctfPlayer.getTeam().remove(ctfPlayer);
                    }

                    ctfTeam.add(ctfPlayer);

	                ctfPlayer.getPlayer().sendMessage(ChatColor.GOLD + "You have joined the " + item.getItemMeta().getDisplayName());

                    return;
                }
            }
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }

    }

    private static class PlayerListenerTask extends BukkitRunnable {
        public void run() {
            for (MPGTeam mpgTeam : MPGTeamManager.getAllMPGTeams()) {
                ItemStack teamItem = new ItemStack(Material.WOOL, mpgTeam.getTeamSize(), TeamSelectorInventory.getWoolColorFromChatColor(mpgTeam.getColor()));
                ItemMeta itemMeta = teamItem.getItemMeta();
                itemMeta.setDisplayName(mpgTeam.getColor() + mpgTeam.getTeamName() + " Team");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GOLD + "Click to join!");
                itemMeta.setLore(lore);
                teamItem.setItemMeta(itemMeta);
                TeamSelectorInventory.teamItems.put((CTFTeam) mpgTeam, teamItem);
            }

            int i = 0;
            for (ItemStack teamItem : TeamSelectorInventory.teamItems.values()) {
                TeamSelectorInventory.teamSelector.getMainInventory().setItem(i, teamItem);
                i++;
            }
        }
    }

}
