package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.commands.handlers.GenerateSpawnsCommandHandler;
import net.badlion.uhc.events.GiveLobbyItemsEvent;
import net.badlion.uhc.events.ObjectivesCommandEvent;
import net.badlion.uhc.events.SpecialTeamsEvent;
import net.badlion.uhc.events.TeamListCommandEvent;
import net.badlion.uhc.events.UHCTeleportPlayerLocationEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.managers.UHCTeamManager;
import net.badlion.uhc.util.ScatterUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class QuadrantsGameMode implements GameMode {

	public static Map<UUID, String> playerTeams = new HashMap<>();

	public static Map<String, Integer> ironMined = new HashMap<>();
	public static Map<String, Integer> goldMined = new HashMap<>();
	public static Map<String, Integer> diamondsMined = new HashMap<>();

	private Set<String> hasPrize = new HashSet<>();

	public QuadrantsGameMode(CommandSender sender) {
		// Auto teams of 10
		BadlionUHC.getInstance().getConfigurator().getBooleanOption(BadlionUHC.CONFIG_OPTIONS.TEAMSIZE.name()).setValue(10);
		BadlionUHC.getInstance().getConfigurator().getBooleanOption(BadlionUHC.CONFIG_OPTIONS.NETHER.name()).setValue("false");
		BadlionUHC.getInstance().getConfigurator().getBooleanOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).setValue("false");
		//BadlionUHC.getInstance().getServer().dispatchCommand(sender, "uhc config teamsize 10");
		//BadlionUHC.getInstance().getServer().dispatchCommand(sender, "uhc config stats false");
		//BadlionUHC.getInstance().getServer().dispatchCommand(sender, "uhc config nether false");

		QuadrantsGameMode.ironMined.put("Red", 0);
		QuadrantsGameMode.ironMined.put("Blue", 0);
		QuadrantsGameMode.ironMined.put("Yellow", 0);
		QuadrantsGameMode.ironMined.put("Green", 0);

		QuadrantsGameMode.goldMined.put("Red", 0);
		QuadrantsGameMode.goldMined.put("Blue", 0);
		QuadrantsGameMode.goldMined.put("Yellow", 0);
		QuadrantsGameMode.goldMined.put("Green", 0);

		QuadrantsGameMode.diamondsMined.put("Red", 0);
		QuadrantsGameMode.diamondsMined.put("Blue", 0);
		QuadrantsGameMode.diamondsMined.put("Yellow", 0);
		QuadrantsGameMode.diamondsMined.put("Green", 0);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		String team = playerTeams.get(event.getPlayer().getUniqueId());
		// They already have completed the Quadrants challenge
		if (hasPrize.contains(team)) {
			return;
		}

		// Update the team's mined blocks
		switch (event.getBlock().getType()) {
			case IRON_ORE:
				QuadrantsGameMode.ironMined.put(team, QuadrantsGameMode.ironMined.get(team) + 1);
				break;
			case GOLD_ORE:
				QuadrantsGameMode.goldMined.put(team, QuadrantsGameMode.goldMined.get(team) + 1);
				break;
			case DIAMOND_ORE:
				QuadrantsGameMode.diamondsMined.put(team, QuadrantsGameMode.diamondsMined.get(team) + 1);
				break;
		}

		// Check if they have te required shit
		if (QuadrantsGameMode.diamondsMined.get(team) >= QuadrantsGameMode.getNeededDiamonds(team) && QuadrantsGameMode.ironMined.get(team) >= QuadrantsGameMode.getNeededIron(team) && QuadrantsGameMode.goldMined.get(team) >= QuadrantsGameMode.getNeededGold(team)) {
			this.hasPrize.add(team);

			Gberry.broadcastMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + team + " team has completed the Quadrants objective!");

			for (Map.Entry<UUID, String> entry : playerTeams.entrySet()) {
				if (!entry.getValue().equals(team)) continue;
				Player player = BadlionUHC.getInstance().getServer().getPlayer(entry.getKey());

				QuadrantsGameMode.giveItem(player, ItemStackUtil.STRENGTH_POTION_EXT);

				QuadrantsGameMode.giveItem(player, new ItemStack(Material.BOOK, 8));
				QuadrantsGameMode.giveItem(player, new ItemStack(Material.ENDER_PEARL, 2));
				player.setLevel(player.getLevel() + 30);

				player.sendMessage(ChatColor.GREEN + "Congratulations for completing the Quadrants objective!");
				player.sendMessage(ChatColor.GREEN + "You have received a strength potion, 8 books, 2 ender pearls, and 30 levels for your efforts!");
			}
		}
	}

	@EventHandler
	public void onGiveLobbyItems(GiveLobbyItemsEvent event) {
		event.getPlayer().sendMessage(ChatColor.YELLOW + "Use the Wool or /uhc teams to choose a quadrant!");
		event.getPlayer().getInventory().setItem(4, ItemStackUtil.createItem(Material.WOOL, ChatColor.GOLD + "Choose Quadrant"));
	}

	@EventHandler
	public void onTeleportPlayer(UHCTeleportPlayerLocationEvent event) {
		Location location = null;

		boolean randomPoint = false;

		while (location == null || !isGoodLocation(location, event.getPlayer())) {
			try {
				randomPoint = false;
				location = GenerateSpawnsCommandHandler.scatterPoints.get(0);
			} catch (IndexOutOfBoundsException e) {
				randomPoint = true;
				ArrayList<Location> scatterPoints;
				scatterPoints = ScatterUtils.randomSquareScatter(1);
				location = scatterPoints.get(0);
			}
		}

		if (!randomPoint) {
			GenerateSpawnsCommandHandler.scatterPoints.remove(location);
		}

		event.setLocation(location);
	}

	public static void giveItem(Player player, ItemStack itemStack) {
		if (player.getInventory().getSize() <= player.getInventory().getContents().length) {
			player.getWorld().dropItem(player.getLocation(), itemStack);
		} else {
			player.getInventory().addItem(itemStack);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player1 = (Player) event.getEntity();
			Player player = null;
			if (event.getDamager() instanceof Player) {
				player = (Player) event.getDamager();
			} else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
				player = (Player) ((Arrow) event.getDamager()).getShooter();
			} else if (event.getDamager() instanceof FishHook && ((FishHook) event.getDamager()).getShooter() instanceof Player) {
				player = (Player) ((FishHook) event.getDamager()).getShooter();
			}
			if (player != null) {
				// If they are in the same QTeam, they can't damage them
				if (QuadrantsGameMode.playerTeams.get(player.getUniqueId()).equals(playerTeams.get(player1.getUniqueId()))) {
					event.setCancelled(true);
					player.sendMessage(ChatColor.RED + "You can't damage this player.");
				}
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getInventory().getName().contains("Choose Team")) {
			if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasDisplayName()) {
				return;
			}

			String team = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).replace(" Team", "");

			if (getPlayersByTeam(team).size() >= (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.MAXPLAYERS.name()).getValue() / 4) {
				((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "This team is full! Please try another one.");
				return;
			}

			playerTeams.put(event.getWhoClicked().getUniqueId(), team);

			((Player) event.getWhoClicked()).sendMessage(ChatColor.GREEN + "You have joined " + team + " team");

			event.getWhoClicked().closeInventory();
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getAction() != Action.PHYSICAL) {
			if (event.getItem().getType() == Material.WOOL && BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.PRE_START) {
				event.getPlayer().openInventory(getTeamsInventory());
				event.setCancelled(true);
			}
		}
	}

	public static Inventory getTeamsInventory() {
		Inventory teamsInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(), 9, ChatColor.RED + "Choose Team").getMainInventory();
		teamsInventory.setItem(2, ItemStackUtil.createItem(Material.WOOL, (byte) 14, ChatColor.RED + "Red Team", ChatColor.AQUA + "Players:", ChatColor.GOLD.toString() + getPlayersByTeam("Red").size()));
		teamsInventory.setItem(3, ItemStackUtil.createItem(Material.WOOL, (byte) 11, ChatColor.BLUE + "Blue Team", ChatColor.AQUA + "Players:", ChatColor.GOLD.toString() + getPlayersByTeam("Blue").size()));
		teamsInventory.setItem(4, ItemStackUtil.createItem(Material.WOOL, (byte) 4, ChatColor.GOLD + "Yellow Team", ChatColor.AQUA + "Players:", ChatColor.GOLD.toString() + getPlayersByTeam("Yellow").size()));
		teamsInventory.setItem(5, ItemStackUtil.createItem(Material.WOOL, (byte) 5, ChatColor.GREEN + "Green Team", ChatColor.AQUA + "Players:", ChatColor.GOLD.toString() + getPlayersByTeam("Green").size()));
		return teamsInventory;
	}

	public static int getNeededGold(String team) {
		return getPlayersByTeam(team).size() * 16;
	}

	public static int getNeededIron(String team) {
		return getPlayersByTeam(team).size() * 32;
	}

	public static int getNeededDiamonds(String team) {
		return getPlayersByTeam(team).size() * 2;
	}

	public static String getPrefix(UHCPlayer uhcPlayer) {
		String team = QuadrantsGameMode.playerTeams.get(uhcPlayer.getUUID()) == null ? "THIS CAN BE ANYTHING LOL" : QuadrantsGameMode.playerTeams.get(uhcPlayer.getUUID());
		switch (team) {
			case "Red":
				return ChatColor.RED + "[Red]";
			case "Yellow":
				return ChatColor.GOLD + "[Yellow]";
			case "Blue":
				return ChatColor.BLUE + "[Blue]";
			case "Green":
				return ChatColor.GREEN + "[Green]";
			default:
				return "";
		}
	}

	public static List<UUID> getPlayersByTeam(String team) {
		List<UUID> players = new ArrayList<>();
		for (UUID uuid : playerTeams.keySet()) {
			if (playerTeams.get(uuid).equals(team)) {
				players.add(uuid);
			}
		}
		return players;
	}

	@EventHandler
	public void onTeamsCreation(SpecialTeamsEvent event) {
		event.setOverriden(true);

		// We want to remove all teams, and override it ourselves..
		for (UHCTeam uhcTeam : UHCTeamManager.getAllUHCTeams()) {
			UHCTeamManager.removeUHCTeam(uhcTeam);
		}

		// First check for players who didn't choose a team
		for (UHCPlayer uhcPlayer : UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER)) {
			if (!QuadrantsGameMode.playerTeams.containsKey(uhcPlayer.getUUID())) {
				// They haven't chosen a team
				int random = Gberry.generateRandomInt(0, 3);
				int maxPlayersPerTeam = (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.MAXPLAYERS.name()).getValue() / 4;

				if (random == 0 && getPlayersByTeam("Red").size() < maxPlayersPerTeam) {
					QuadrantsGameMode.playerTeams.put(uhcPlayer.getUUID(), "Red");
				} else if (random == 1 && getPlayersByTeam("Yellow").size() < maxPlayersPerTeam) {
					QuadrantsGameMode.playerTeams.put(uhcPlayer.getUUID(), "Yellow");
				} else if (random == 2 && getPlayersByTeam("Green").size() < maxPlayersPerTeam) {
					QuadrantsGameMode.playerTeams.put(uhcPlayer.getUUID(), "Green");
				} else if (random == 3 && getPlayersByTeam("Blue").size() < maxPlayersPerTeam) {
					QuadrantsGameMode.playerTeams.put(uhcPlayer.getUUID(), "Blue");
				}
			}

			if (!QuadrantsGameMode.playerTeams.containsKey(uhcPlayer.getUUID())) {
				// It could not find them a team, so kick them because they can't play without a team4
				uhcPlayer.getPlayer().kickPlayer("An error has occurred. Please send this error code to a developer ASAP - #3EA");
			}
		}

		// Assign the new teams
		for (UUID uuid : QuadrantsGameMode.playerTeams.keySet()) {
			String team = QuadrantsGameMode.playerTeams.get(uuid);
			UHCTeam uhcTeam = getUHCTeamOfTeam(team);
			// uhcTeam will only be null if there are no more teams for them to join
			// So many a new one.
			if (uhcTeam == null) {
				UHCPlayerManager.getUHCPlayer(uuid).setTeam(new UHCTeam(uuid));
				continue;
			}
			UHCPlayerManager.getUHCPlayer(uuid).setTeam(uhcTeam);
		}
	}

	@EventHandler
	public void onObjectivesCommand(ObjectivesCommandEvent event) {
		event.setSentMessages(true);

		Player player = event.getPlayer();
		player.sendMessage(ChatColor.GOLD + "Complete this UHC objective by doing the following:");
		String team = QuadrantsGameMode.playerTeams.get(player.getUniqueId());
		int iron, gold, diamonds;
		if (team != null) {
			iron = QuadrantsGameMode.ironMined.get(team);
			gold = QuadrantsGameMode.goldMined.get(team);
			diamonds = QuadrantsGameMode.diamondsMined.get(team);
		} else {
			iron = 0;
			gold = 0;
			diamonds = 0;
		}
		int neededIron = QuadrantsGameMode.getNeededIron(team);
		int neededGold = QuadrantsGameMode.getNeededGold(team);
		int neededDiamonds = QuadrantsGameMode.getNeededDiamonds(team);
		player.sendMessage(ChatColor.YELLOW + "Mine " + neededIron + " iron (" + (iron >= neededIron ? ChatColor.GREEN : ChatColor.RED) + iron + ChatColor.YELLOW + ")");
		player.sendMessage(ChatColor.YELLOW + "Mine " + neededGold + " gold (" + (gold >= neededGold ? ChatColor.GREEN : ChatColor.RED) + gold + ChatColor.YELLOW + ")");
		player.sendMessage(ChatColor.YELLOW + "Mine " + neededDiamonds + " diamonds (" + (diamonds >= neededDiamonds ? ChatColor.GREEN : ChatColor.RED) + diamonds + ChatColor.YELLOW + ")");
	}

	@EventHandler
	public void onTeamList(TeamListCommandEvent event) {
		event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this with Quadrants.");
		event.setCancelled(true);
	}

	private static UHCTeam getUHCTeamOfTeam(String team) {
		for (UHCTeam uhcTeam : UHCTeamManager.getAllUHCTeams()) {
			// If the leader of this UHCTeam is in the same QTeam
			// And they aren't full, return it
			if (QuadrantsGameMode.playerTeams.get(uhcTeam.getLeader()).equals(team) && uhcTeam.getSize() < (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.TEAMSIZE.name()).getValue()) {
				return uhcTeam;
			}
		}
		return null;
	}

	public static boolean isGoodLocation(Location location, Player player) {
		String team = QuadrantsGameMode.playerTeams.get(player.getUniqueId());
		return !((team.equals("Red") && (location.getX() > 0 || location.getZ() > 0))
				|| (team.equals("Green") && (location.getX() < 0 || location.getZ() < 0))
				|| (team.equals("Yellow") && (location.getX() < 0 || location.getZ() > 0))
				|| (team.equals("Blue") && (location.getX() > 0 || location.getZ() < 0)));
	}

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.WOOL, 1, (byte) 1);
        ItemMeta itemMeta = item.getItemMeta();
	    itemMeta.setDisplayName(ChatColor.GREEN + "Quadrants");

        List<String> lore = new ArrayList<>();

	    lore.add(ChatColor.AQUA + "- 4 large teams");
	    lore.add(ChatColor.AQUA + "- Mini teams of 10");
	    lore.add(ChatColor.AQUA + "- Stats are off");
	    lore.add(ChatColor.AQUA + "- Nether is off");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "https://www.badlion.net/profile/user/MaccaTacca";
    }

    @Override
    public void unregister() {
	    AsyncPlayerChatEvent.getHandlerList().unregister(this);
	    InventoryClickEvent.getHandlerList().unregister(this);
	    BlockBreakEvent.getHandlerList().unregister(this);
	    EntityDamageByEntityEvent.getHandlerList().unregister(this);
		GiveLobbyItemsEvent.getHandlerList().unregister(this);
		UHCTeleportPlayerLocationEvent.getHandlerList().unregister(this);
		SpecialTeamsEvent.getHandlerList().unregister(this);
		ObjectivesCommandEvent.getHandlerList().unregister(this);
		TeamListCommandEvent.getHandlerList().unregister(this);
    }

}
