package net.badlion.potpvp.commands;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.Pair;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.helpers.DuelHelper;
import net.badlion.potpvp.inventories.duel.DuelChooseKitInventory;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.managers.MessageManager;
import net.badlion.potpvp.tasks.WarpCheckerTask;
import net.badlion.smellychat.managers.ChatSettingsManager;
import net.badlion.statemachine.State;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DuelCommand extends GCommandExecutor {

	public static final int MAX_PARTY_DUEL_PLAYERS = 8;

	public DuelCommand() {
		super(1); // 1 arg minimum
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		if (this.player.isOp()) {
			if (args.length == 3 && args[0].equalsIgnoreCase("test")) {
				Player smelly = Bukkit.getPlayerExact("SmellyPenguin");
				Player captain = Bukkit.getPlayerExact("CaptainKickass63");
				Player gorilla = Bukkit.getPlayerExact("Gorille");
				Player germany = Bukkit.getPlayerExact("Germany");

				smelly.performCommand("party create");
				smelly.performCommand("party invite CaptainKickass63");
				smelly.performCommand("party invite Gorille");
				smelly.performCommand("party invite Germany");

				captain.performCommand("party accept");
				gorilla.performCommand("party accept");
				germany.performCommand("party accept");
				return;
			} else if (args.length == 3 && args[0].equalsIgnoreCase("dupe")) {
				int X = Integer.valueOf(args[1]);
				int Z = Integer.valueOf(args[2]);

				List<Arena> arenas = ArenaManager.getAllArenasOfType(ArenaManager.ArenaType.BUILD_UHC);
				for (Arena arena : arenas) {
					String arenaName = arena.getArenaName().replaceAll("[0-9]", "");
					int arenaNumber = Integer.valueOf(arena.getArenaName().replaceAll("[^0-9]", "")) + 10;

					System.out.println("OLD NAME: " + arena.getArenaName());
					System.out.println("NEW NAME: " + arenaName + arenaNumber);
					// Add arena
					ArenaManager.addArena(null, arenaName + arenaNumber, "2", arenaName + arenaNumber + "-1", arenaName + arenaNumber + "-2");

					// Add warps
					System.out.println("OLD: " + arena.getWarp1().toString());
					System.out.println("NEW: " + arena.getWarp1().clone().add(X, 0, Z).toString());
					ArenaManager.addWarp(arenaName + arenaNumber + "-1", null, arena.getWarp1().clone().add(X, 0, Z));
					ArenaManager.addWarp(arenaName + arenaNumber + "-2", null, arena.getWarp2().clone().add(X, 0, Z));

					// Duplicate the JSON files
					List<String> keyValues = new ArrayList<>();
					File jsonFile = new File(PotPvP.getInstance().getDataFolder(), arena.getArenaName() + ".json");
					if (jsonFile.exists()) {
						try {
							JSONObject jsonObject = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(Paths.get(jsonFile.getAbsolutePath()))));

							for (String keyValue : (List<String>) jsonObject.get("key_values")) {
								String[] parts = keyValue.split(":");
								try {

									Pair key = Pair.fromString(parts[0]);
									Integer value = Integer.parseInt(parts[1]);

									//System.out.println("OLD: " + key.toString());
									// Add to coordinates
									key.setA(((Integer) key.getA()) + X);
									key.setB(((Integer) key.getB()) + Z);
									//System.out.println("NEW: " + key.toString());
									keyValues.add(key.toString() + ":" + value);

									JSONObject dupedJSONObject = new JSONObject();
									dupedJSONObject.put("key_values", keyValues);
									String jsonString = dupedJSONObject.toJSONString();

									// Write the file to disk
									File dupedJSONFile = new File(PotPvP.getInstance().getDataFolder(), arenaName + arenaNumber + ".json");
									try {
										FileUtils.write(dupedJSONFile, Gberry.formatJSON(jsonString));
									} catch (IOException e) {
										PotPvP.getInstance().getLogger().info("Failed to write arena string " + arenaName + arenaNumber);
									}
								} catch (NumberFormatException e) {
									PotPvP.getInstance().getLogger().info("Error reading data for " + arenaName);
									return;
								}
							}
						} catch (IOException e) {
							PotPvP.getInstance().getLogger().info("Error reading " + arenaName);
							return;
						}
					} else {
						PotPvP.getInstance().getLogger().info("File missing for BuildUHCArena " + arenaName);
						return;
					}
				}
				return;
			} else if (args.length == 3 && args[0].equalsIgnoreCase("mcsq")) {
				long time = System.currentTimeMillis();

				int x = 100;
				int y;
				int z;

				World world = this.player.getLocation().getWorld();
				List<Vector> tier2 = new ArrayList<>();
				while (x < 310) {
					y = 20;
					while (y < 140) {
						z = -5300;
						while (z < -5100) {
							Material type = world.getBlockAt(x, y, z).getType();
							if (type == Material.CHEST) {
								System.out.println("this.chestsTier1.add(world.getBlockAt(" + x + "," + y + "," + z + "));");
							} else if (type == Material.ENDER_CHEST) {
								tier2.add(new Vector(x, y, z));
							}
							z++;
						}
						y++;
					}
					x++;
				}

				for (Vector vector : tier2) {
					System.out.println("this.chestsTier2.add(world.getBlockAt(" + (int) vector.getX() + "," + (int) vector.getY() + "," + (int) vector.getZ() + "));");
				}

				System.out.println(System.currentTimeMillis() - time);
				return;
			} else if (args.length == 6 && args[0].equalsIgnoreCase("biomes")) {
				long time = System.currentTimeMillis();

				int x = -500;
				int z;

				World world = this.player.getLocation().getWorld();
				while (x < 110) {
					z = -410;
					while (z < 340) {
						// Load chunk
						world.loadChunk(x >> 4, z >> 4);

						world.setBiome(x, z, Biome.JUNGLE);
						z++;
					}
					x++;
				}

				System.out.println(System.currentTimeMillis() - time);
				return;
			} else if (args.length == 6 && args[0].equalsIgnoreCase("warps")) {
				new WarpCheckerTask(Integer.valueOf(args[1]), this.player).runTaskTimer(PotPvP.getInstance(), 240L, 240L);
				return;
			} else if (args.length == 5 && args[0].equalsIgnoreCase("biomec")) {
				Location location = this.player.getLocation();
				this.player.sendMessage(ChatColor.YELLOW + "Old biome: " + location.getWorld().getBiome(location.getBlockX(), location.getBlockZ()));
				this.player.getWorld().setBiome(location.getBlockX(), location.getBlockZ(), Biome.JUNGLE);
				this.player.sendMessage(ChatColor.YELLOW + "New biome: " + location.getWorld().getBiome(location.getBlockX(), location.getBlockZ()));
				return;
			}
		}

        Player pl = PotPvP.getInstance().getServer().getPlayerExact(args[0]);
        if (pl == null) {
            this.player.sendMessage(ChatColor.RED + "Player does not exist or is not online.");
            return;
        }

		Group otherGroup = PotPvP.getInstance().getPlayerGroup(pl);
		if (otherGroup == null) {
			this.player.sendMessage(ChatColor.RED + "Player not found.");
			return;
		} else if (otherGroup == this.group) {
			this.player.sendMessage(ChatColor.RED + "You cannot duel yourself.");
			return;
		}

		// Check to see if they can duel the other group
		if (this.group.isParty()) {
			// Party leader check
			if (this.player != this.group.getLeader()) {
				this.player.sendMessage(ChatColor.RED + "Only the party leader can send duel requests.");
				return;
			}

			if (!otherGroup.isParty()) {
				this.player.sendMessage(ChatColor.RED + "Other player is not in a party.");
				return;
			} /*else if (this.group.players().size() != otherGroup.players().size()) {
				this.player.sendMessage(ChatColor.RED + "Other party does not have the same number of players as yours.");
				return;
			}*/

			// Is the player trying to invite someone to their party?
			if (GroupStateMachine.partyRequestState.containsInvitingPlayer(this.player)) {
				this.player.sendMessage(ChatColor.RED + "Wait until the person you're inviting to your party accepts/declines.");
				return;
			}

			// Is the other player trying to invite their party?
			if (GroupStateMachine.partyRequestState.containsInvitingPlayer(pl)) {
				this.player.sendMessage(ChatColor.RED + "Other player is currently inviting someone to their party.");
				return;
			}

			// Do they have too many players in their party?
			if (this.group.players().size() > DuelCommand.MAX_PARTY_DUEL_PLAYERS) {
				// Is the party leader a pleb? (not famous)
				if (!group.getLeader().hasPermission("badlion.famous") && !group.getLeader().hasPermission("badlion.twitch")
						&& !group.getLeader().hasPermission("badlion.youtube") && !group.getLeader().hasPermission("badlion.staff")) {
					this.player.sendMessage(ChatColor.RED + "You can only have a maximum of " + DuelCommand.MAX_PARTY_DUEL_PLAYERS + " players in your party.");
					return;
				}
			}
		} else {
			if (otherGroup.isParty()) {
				this.player.sendMessage(ChatColor.RED + "Other player is in a party.");
				return;
			}
		}

		// Are they accepting duel requests?
		MessageManager.MessageOptions messageOptions = MessageManager.getMessageOptions(otherGroup.getLeader());
		if (!messageOptions.getMessageTagBoolean(MessageManager.MessageType.DUEL)) {
			if (!this.player.hasPermission("badlion.staff") && !this.player.hasPermission("badlion.famous")) {
				this.player.sendMessage(ChatColor.RED + "Player is not accepting duel requests.");
				return;
			}
		}

		// Is the sender ignored by the receiver?
		if (ChatSettingsManager.getChatSettings(otherGroup.getLeader()).getIgnoredList().contains(this.player.getUniqueId())) {
			this.player.sendMessage(ChatColor.RED + "Cannot duel player because they have you on their ignore list.");
			return;
		}

		if (this.group.hasDeadPlayers()) {
			this.player.sendMessage(ChatColor.RED + "All players must be alive in your party to duel.");
			this.player.sendMessage(ChatColor.YELLOW + "Dead players: " + this.group.getDeadPlayerString());

			BukkitUtil.closeInventory(this.player);
			return;
		}

        for (Player p1 : otherGroup.players()) {
            if (p1.isDead()) {
                this.player.sendMessage(ChatColor.RED + "Not everyone in the other party is alive at the moment.");
                return;
            }
        }

        // Check to see if they can both move
        State<Group> otherState = GroupStateMachine.getInstance().getCurrentState(otherGroup);

        if (this.currentState.isStateTransitionValid(GroupStateMachine.duelRequestState)) {
            if (!otherState.isStateTransitionValid(GroupStateMachine.duelRequestState)) {
	            this.player.sendMessage(ChatColor.RED + args[0] + " cannot accept a duel request at the moment because " + otherState.description());
	            return;
            }
        } else {
            this.player.sendMessage(ChatColor.RED + "You cannot send a duel request at the moment because " + this.currentState.description());
            return;
        }

        // If other player was creating a duel, we don't do anything...
        DuelHelper.DuelCreator duelCreator = GroupStateMachine.duelRequestState.getDuelCreator(otherGroup);
        if (duelCreator != null && duelCreator.getKitRuleSet() == null) {
            Gberry.log("DUEL", "Duel receiver was in the middle of creating a duel, removing their duel creator " + group + " " + otherGroup);
            this.player.sendMessage(ChatColor.RED + "Player already creating a duel request, cannot duel at the moment.");
            return;
        }

        // Create new duel creator
		new DuelHelper.DuelCreator(this.group, otherGroup);

		DuelChooseKitInventory.openDuelChooseKitInventory(this.player);
	}

	@Override
	public void usage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Command usage: /duel <player> to send a duel request");
	}

}
