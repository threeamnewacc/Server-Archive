package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.events.ObjectivesCommandEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MonsterHunterGameMode implements GameMode {

	private static Map<Integer, Integer> teamZombies = new HashMap<>();
	private static Map<Integer, Integer> teamSpiders = new HashMap<>();
	private static Map<Integer, Integer> teamCreepers = new HashMap<>();
	private static Map<Integer, Integer> teamSkeletons = new HashMap<>();

	private Set<Integer> hasPrize = new HashSet<>();

	@EventHandler
	public void onKillMob(EntityDeathEvent event) {
		if (event.getEntity().getKiller() != null) {
			UHCTeam uhcTeam = UHCPlayerManager.getUHCPlayer(event.getEntity().getKiller().getUniqueId()).getTeam();
			if (uhcTeam == null) {
				return;
			}

			int uhcTeamID = uhcTeam.getTeamNumber();
			if (hasPrize.contains(uhcTeamID)) {
				return;
			}

			if (event.getEntity() instanceof Zombie && !event.getEntity().hasMetadata("CombatLoggerNPC")) {
				teamZombies.put(uhcTeamID, getTeamZombieKills(uhcTeamID) + 1);
			} else if (event.getEntity() instanceof Spider) {
				teamSpiders.put(uhcTeamID, getTeamSpiderKills(uhcTeamID) + 1);
			} else if (event.getEntity() instanceof Skeleton) {
				teamSkeletons.put(uhcTeamID, getTeamSkeletonKills(uhcTeamID) + 1);
			} else if (event.getEntity() instanceof Creeper) {
				teamCreepers.put(uhcTeamID, getTeamCreeperKills(uhcTeamID) + 1);
			} else {
				return;
			}

			int toGet = uhcTeam.getSize() * 5;

			if (getTeamZombieKills(uhcTeamID) >= toGet && getTeamSkeletonKills(uhcTeamID) >= toGet && getTeamSpiderKills(uhcTeamID) >= toGet && getTeamCreeperKills(uhcTeamID) >= toGet) {
				hasPrize.add(uhcTeamID);

				Gberry.broadcastMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Team #" + uhcTeamID + " have completed the Potential Hearts objective!");

				for (UUID uuid : uhcTeam.getUuids()) {
					Player player = BadlionUHC.getInstance().getServer().getPlayer(uuid);
					player.setMaxHealth(player.getMaxHealth() + 20.0D);

					QuadrantsGameMode.giveItem(player, new ItemStack(Material.POTION, 1, (byte) 8193));

					player.sendMessage(ChatColor.GREEN + "Congratulations for completing the Potential Hearts objective!");
					player.sendMessage(ChatColor.GREEN + "You have received an extra 20 team health, and a regeneration potion for your efforts!");
				}
			}
		}
	}

	@EventHandler
	public void onObjectivesCommand(ObjectivesCommandEvent event) {
		event.setSentMessages(true);

		Player player = event.getPlayer();
		player.sendMessage(ChatColor.GOLD + "Complete this UHC objective by doing the following:");
		UHCTeam uhcTeam = UHCPlayerManager.getUHCPlayer(player.getUniqueId()).getTeam();

		int toGet = uhcTeam.getSize() * 5;
		int killedZombies = MonsterHunterGameMode.getTeamZombieKills(uhcTeam.getTeamNumber());
		int killedCreepers = MonsterHunterGameMode.getTeamCreeperKills(uhcTeam.getTeamNumber());
		int killedSpiders = MonsterHunterGameMode.getTeamSpiderKills(uhcTeam.getTeamNumber());
		int killedSkeletons = MonsterHunterGameMode.getTeamSkeletonKills(uhcTeam.getTeamNumber());

		player.sendMessage(ChatColor.YELLOW + "Kill " + toGet + " zombies " + "(" + (killedZombies >= toGet ? ChatColor.GREEN : ChatColor.RED) + killedZombies + ChatColor.YELLOW + ")");
		player.sendMessage(ChatColor.YELLOW + "Kill " + toGet + " spiders " + "(" + (killedSpiders >= toGet ? ChatColor.GREEN : ChatColor.RED) + killedSpiders + ChatColor.YELLOW + ")");
		player.sendMessage(ChatColor.YELLOW + "Kill " + toGet + " skeletons " + "(" + (killedSkeletons >= toGet ? ChatColor.GREEN : ChatColor.RED) + killedSkeletons + ChatColor.YELLOW + ")");
		player.sendMessage(ChatColor.YELLOW + "Kill " + toGet + " creepers " + "(" + (killedCreepers >= toGet ? ChatColor.GREEN : ChatColor.RED) + killedCreepers + ChatColor.YELLOW + ")");
	}

	public static int getTeamZombieKills(int teamID) {
		if (!teamZombies.containsKey(teamID)) {
			teamZombies.put(teamID, 0);
		}
		return teamZombies.get(teamID);
	}

	public static int getTeamSpiderKills(int teamID) {
		if (!teamSpiders.containsKey(teamID)) {
			teamSpiders.put(teamID, 0);
		}
		return teamSpiders.get(teamID);
	}

	public static int getTeamSkeletonKills(int teamID) {
		if (!teamSkeletons.containsKey(teamID)) {
			teamSkeletons.put(teamID, 0);
		}
		return teamSkeletons.get(teamID);
	}

	public static int getTeamCreeperKills(int teamID) {
		if (!teamCreepers.containsKey(teamID)) {
			teamCreepers.put(teamID, 0);
		}
		return teamCreepers.get(teamID);
	}

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.APPLE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Monster Hunter");

        List<String> lore = new ArrayList<>();

		lore.add(ChatColor.AQUA + "- Kill a total of (team size) * 5 zombies, spiders, skeletons and creepers");
        lore.add(ChatColor.AQUA + "- Complete the objective to gain 10 extra maximum hearts");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

    @Override
    public void unregister() {
	    EntityDeathEvent.getHandlerList().unregister(this);
		ObjectivesCommandEvent.getHandlerList().unregister(this);
    }

}
