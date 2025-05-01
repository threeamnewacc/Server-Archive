package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.events.GameStartEvent;
import net.badlion.uhc.events.TeamListCommandEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AgentsGameMode implements GameMode {

	// This is for 6.0...

	private static Map<Integer, UUID> teamAgents = new HashMap<>();

    public AgentsGameMode() {
        BadlionUHC.getInstance().getConfigurator().getBooleanOption(BadlionUHC.CONFIG_OPTIONS.TEAMSIZE.name()).setValue(15);
    }

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		UHCTeam uhcTeam = UHCPlayerManager.getUHCPlayer(event.getEntity().getUniqueId()).getTeam();
		if (getAgent(uhcTeam.getTeamNumber()) == event.getEntity().getUniqueId()) {
			// They agent died!
			Gberry.broadcastMessage(ChatColor.GREEN.toString() + uhcTeam.getTeamNumber() + " have slain their Agent!");
		}
	}

	@EventHandler
	public void onGameStart(GameStartEvent event) {

	}

    @EventHandler
    public void onTeamList(TeamListCommandEvent event) {
        event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this with Agents.");
        event.setCancelled(true);
    }

	public static UUID getAgent(int teamID) {
		return teamAgents.get(teamID);
	}

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Agents vs. World");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Teams of 15");
        lore.add(ChatColor.AQUA + "- 1 agent per team");
        lore.add(ChatColor.AQUA + "- Agent must eliminate all the non-agents");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

    @Override
    public void unregister() {
        PlayerDeathEvent.getHandlerList().unregister(this);
        GameStartEvent.getHandlerList().unregister(this);
        TeamListCommandEvent.getHandlerList().unregister(this);
    }

}
