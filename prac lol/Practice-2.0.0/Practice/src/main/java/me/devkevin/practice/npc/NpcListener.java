package me.devkevin.practice.npc;

import club.inverted.chatcolor.CC;
import me.devkevin.npc.events.NPCInteractEvent;
import me.devkevin.npc.npcs.NPC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.matches.OngoingMatchesMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Copyright 28/11/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class NpcListener implements Listener {
    private final Practice plugin = Practice.getInstance();

    @EventHandler
    public void onInteractNPC(NPCInteractEvent event) {
        Player player = event.getPlayer();
        NPC npc = event.getNpc();

        switch (npc.getName()) {
            case "Unranked":
                player.openInventory(plugin.getQueueMenu().getUnrankedMenu().getCurrentPage());
                break;
            case "Ranked":
                player.openInventory(plugin.getQueueMenu().getRankedMenu().getCurrentPage());
                break;
            case "Matches":
                new OngoingMatchesMenu().openMenu(player);
                break;
            case "KitEditor":
                break;
            case "Store":
                player.sendMessage(CC.GRAY + "Store: " + CC.RED + "https://store.zelta.cc");
                break;
            case "Website":
                player.sendMessage(CC.GRAY + "Website: " + CC.RED + "https://www.zelta.cc");
                break;
            default:
                break;
        }
    }
}
