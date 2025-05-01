package net.badlion.uhc.commands;

import net.badlion.gberry.Gberry;
import net.badlion.ministats.managers.DatabaseManager;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.commands.handlers.AddGameModeHandler;
import net.badlion.uhc.commands.handlers.BanCommandHandler;
import net.badlion.uhc.commands.handlers.ConfigCommandHandler;
import net.badlion.uhc.commands.handlers.GameModeHandler;
import net.badlion.uhc.commands.handlers.GenerateSpawnsCommandHandler;
import net.badlion.uhc.commands.handlers.GenerationCommandHandler;
import net.badlion.uhc.commands.handlers.ModCommandHandler;
import net.badlion.uhc.commands.handlers.RemoveGameModeHandler;
import net.badlion.uhc.commands.handlers.SetHostCommandHandler;
import net.badlion.uhc.commands.handlers.StartCommandHandler;
import net.badlion.uhc.commands.handlers.TestStartCommandHandler;
import net.badlion.uhc.listeners.gamemodes.TrollGame1;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.managers.UHCTeamManager;
import net.badlion.uhc.tasks.DonatorSpectatorTask;
import net.badlion.uhc.util.GoldenHeadUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UHCCommand implements CommandExecutor {

	private ItemStack helpBook;

	public UHCCommand() {
		// Create informational books
		this.helpBook = new ItemStack(Material.WRITTEN_BOOK);

		BookMeta bookMeta = (BookMeta) this.helpBook.getItemMeta();
		bookMeta.addPage("How to host a UHC:" +
				"\n1. Use \"/uhc generateworld <radius>\" (500-5000) to pre-generate the world (WILL LAG SERVER)" +
				"\n2. Decide whether you want to do a solo UHC, have random teams, or have custom teams" +
				"\n   - Solo: Skip to next step");
		bookMeta.addPage("   - Custom Teams: Use \"/uhc customteams\" to enable/disable custom teams (\"/team\")" +
				"\n   - Random Teams: Use \"/uhc randomteams <team-size>\" to create random teams once all players have joined" +
				"\n   * Reset teams by using \"/uhc resetteams\"");
		bookMeta.addPage("   * List all teams by using \"/uhc listteams\"" +
				"\n   * Warn players to join teams by using \"/uhc warnteams\" (Kicks any players not in a team after 5 minutes)");
		bookMeta.addPage("3. Use \"/bs\" to configure border shrinking settings" +
				"\n4. Use \"/uhc config\" to set a flag for every configuration option" +
				"\n5. Use \"/uhc generatespawns\" to generate enough spawn points for the max player count");
		bookMeta.addPage("6. Use \"/uhc teststart\" to see if you have everything configured for starting" +
				"\n7. When ready to start, do \"/wl on\" to not let anymore players join the server. Then do \"/wl all\" to whitelist all online players.");
		bookMeta.addPage("8. When ready to start the UHC, use \"/uhc start\" and let the players be slowly teleported to the UHC world" +
				"\n\n You have started the UHC successfully!");
		bookMeta.addPage("Misc Commands:" +
				"\n* Use \"/uhc mod <player>\" or \"/uhc demod <player>\" to mod and demod players" +
				"\n* Use \"/uhc deathban <player>\" or \"/uhc undeathban <player>\" to manually deathban and undeathban players" +
				"\n* Use \"/uhc sethost <player>\" to change hosts");
		bookMeta.setDisplayName("UHC Hosting Information");
		bookMeta.setTitle("UHC Hosting Information");
		bookMeta.setAuthor("The Badlion Network");

		this.helpBook.setItemMeta(bookMeta);
	}

    public boolean onCommand(final CommandSender sender, Command command, String s, String[] args) {
        Player player = null;

        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (args.length >= 1) {
            if (sender.hasPermission("badlion.uhctrial") && sender instanceof Player ) {
                if (args[0].equalsIgnoreCase("sethost")) {
                    SetHostCommandHandler.handleSetHostCommand(sender, Arrays.copyOfRange(args, 1, args.length));
                    return true;
                } else if (args[0].equalsIgnoreCase("mod")) {
                    ModCommandHandler.handleModCommand(player, Arrays.copyOfRange(args, 1, args.length));
                    return true;
                } else if (args[0].equalsIgnoreCase("demod")) {
                    ModCommandHandler.handleDemodCommand(player, Arrays.copyOfRange(args, 1, args.length));
                    return true;
                }
            }

	        // Sick of this shit
	        if (player != null && player.isOp() && args.length == 1) {
		        if (args[0].equals("asd")) {
			        player.performCommand("uhc ffa");
			        player.performCommand("uhc gamemode classic 2");
			        player.performCommand("uhc config stats false");
			        player.performCommand("uhc config pvptimer 0");
			        return true;
		        } else if (args[0].equals("qwe")){
			        player.performCommand("uhc teams");
			        player.performCommand("uhc gamemode classic 2");
			        player.performCommand("uhc config stats false");
			        player.performCommand("uhc config pvptimer 0");
			        return true;
		        }
	        }

            // Special command
            if ((sender.isOp() || sender.hasPermission("badlion.antixray")) && (args[0].equalsIgnoreCase("xray") || args[0].equalsIgnoreCase("antixray"))) {
                // Only ops can disable anti-xray for someone else
	            if (sender.isOp() && args.length == 2) {
		            Player target = BadlionUHC.getInstance().getServer().getPlayerExact(args[1]);

		            if (target != null) {
			            target.setIgnoreXray(!target.ignoreXray());
			            target.sendMessage("Anti-Xray ignoring is now set to " + target.ignoreXray() + " for you");
		            } else {
			            sender.sendMessage(ChatColor.RED + "Player " + args[1] + " not found.");
		            }
	            } else {
		            if (player != null) {
			            player.setIgnoreXray(!player.ignoreXray());
			            player.sendMessage("Anti-Xray ignoring is now set to " + player.ignoreXray() + " for you");
		            }
	            }

                return true;
            }

            if (sender.isOp() || (BadlionUHC.getInstance().getHost() != null && player != null && player.getUniqueId().equals(BadlionUHC.getInstance().getHost().getUUID()))) {
                if (args[0].equalsIgnoreCase("test")) {
                    Gberry.executeCommand(sender, "uhc ffa");
                    Gberry.executeCommand(sender, "uhc gamemode classic 10");
                    Gberry.executeCommand(sender, "uhc config pvptimer 0");
                    return true;
                } else if (BadlionUHC.getInstance().getConfigurator() == null && !args[0].equalsIgnoreCase("teams") && !args[0].equalsIgnoreCase("ffa")) {
                    sender.sendMessage(ChatColor.RED + "You must first configure the UHC server to be FFA or Teams.");
                    sender.sendMessage(ChatColor.YELLOW + "Use \"/uhc teams\" or \"/uhc ffa\" to start setting up a Badlion UHC.");
                    return true;
                }

                // Step 1 Set Configurator
                if (BadlionUHC.getInstance().getConfigurator() == null && args[0].equalsIgnoreCase("teams")) {
                    BadlionUHC.getInstance().createTeamConfigurator();
                    BadlionUHC.getInstance().setGameType(UHCTeam.GameType.TEAM);
                    sender.sendMessage(ChatColor.GREEN + "Team Configurations generated.");
                    return true;
                } else if (BadlionUHC.getInstance().getConfigurator() == null && args[0].equalsIgnoreCase("ffa")) {
                    BadlionUHC.getInstance().createSoloConfigurator();
                    sender.sendMessage(ChatColor.GREEN + "FFA Configurations generated.");
                    return true;
                }

                if (args[0].equalsIgnoreCase("addgamemode")) {
                    AddGameModeHandler.handleGameMode(sender, Arrays.copyOfRange(args, 1, args.length));
                } else if (args[0].equalsIgnoreCase("removegamemode")) {
                    RemoveGameModeHandler.handleGameMode(sender, Arrays.copyOfRange(args, 1, args.length));
                } else if (args[0].equalsIgnoreCase("gamemode")) {
                    GameModeHandler.handleGameMode(sender, Arrays.copyOfRange(args, 1, args.length));
                } else if (args[0].equalsIgnoreCase("start")) {
                    StartCommandHandler.handleStartCommand(sender, Arrays.copyOfRange(args, 1, args.length));
                } else if (args[0].equalsIgnoreCase("teststart")) {
                    TestStartCommandHandler.handleTestStartCommand(sender, Arrays.copyOfRange(args, 1, args.length));
                } else if (args[0].equalsIgnoreCase("generateworld")) {
                    GenerationCommandHandler.handleGenerateWorldCommand(sender, Arrays.copyOfRange(args, 1, args.length));
                } else if (args[0].equalsIgnoreCase("config")) {
                    ConfigCommandHandler.handleConfigCommand(sender, Arrays.copyOfRange(args, 1, args.length));
                } else if (args[0].equalsIgnoreCase("generatespawns")) {
                    if (BadlionUHC.getInstance().getState().ordinal() < BadlionUHC.BadlionUHCState.COUNTDOWN.ordinal()) {
                        GenerateSpawnsCommandHandler.handleGenerateSpawnsCommand(sender, Arrays.copyOfRange(args, 1, args.length));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Game has already started. Cannot change player count.");
                    }
                } else if (args[0].equalsIgnoreCase(BadlionUHC.UHCWORLD_NAME) && player != null) {
                    // Backdoor for debug
                    sender.sendMessage("Number of chunks loaded: " + Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME).getLoadedChunks().length);
                    World w = Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME);
                    player.teleport(new Location(w, 0, w.getHighestBlockYAt(0, 0) + 10, 0));
                } else if (args[0].equalsIgnoreCase("nether") && player != null) {
                    World w = Bukkit.getWorld(BadlionUHC.UHCWORLD_NETHER_NAME);
                    player.teleport(w.getSpawnLocation());
                } else if (sender.isOp() && args[0].equalsIgnoreCase("lobby") && player != null) {
                    // Backdoor for debug
                    player.teleport(BadlionUHC.getInstance().getSpawnLocation());
                } else if (sender.isOp() && args[0].equalsIgnoreCase("changestate")) {
                    if (args.length == 3) {
                        Player pl = BadlionUHC.getInstance().getServer().getPlayer(args[1]);
                        if (pl != null) {
                            UHCPlayer.State state = UHCPlayer.State.valueOf(args[2]);
                            if (state != null) {
                                UHCPlayerManager.updateUHCPlayerState(pl.getUniqueId(), state);
                            } else {
                                sender.sendMessage(ChatColor.RED + "Invalid state.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Player is offline.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "/uhc changestate [name] [state]");
                    }
                } else if (args[0].equalsIgnoreCase("deathban")) {
                    BanCommandHandler.handleBanCommand(sender, Arrays.copyOfRange(args, 1, args.length));
                } else if (args[0].equalsIgnoreCase("undeathban")) {
                    BanCommandHandler.handleUnbanCommand(sender, Arrays.copyOfRange(args, 1, args.length));
                } else if (args[0].equalsIgnoreCase("fakegen")) {
                    if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.RADIUS.name()).getValue() != null) {
                        BadlionUHC.getInstance().setState(BadlionUHC.BadlionUHCState.CONFIG);
                        sender.sendMessage(ChatColor.YELLOW + "Fake world has now been generated.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "You must have the shape and radius configured before fake generating.");
                    }
                } else if (args[0].equalsIgnoreCase("deleteworld") || args[0].equalsIgnoreCase("deleteworlds")) {
                    if (!sender.isOp() && !BadlionUHC.getInstance().isCanRestart()) {
                        sender.sendMessage(ChatColor.RED + "Cannot reboot yet. Stats are being stored. If this takes more than 5-10 minutes contact an admin.");
                        return true;
                    }

                    World uhcworld = BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_NAME);
                    World uhcworldNether = BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_NETHER_NAME);
                    World uhcworldTheEnd = BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_END_NAME);

                    // Unload worlds
                    if (uhcworld != null) {
                        BadlionUHC.getInstance().getServer().unloadWorld(uhcworld, false);
                    }
                    if (uhcworldNether != null) {
                        BadlionUHC.getInstance().getServer().unloadWorld(uhcworldNether, false);
                    }
                    if (uhcworldTheEnd != null) {
                        BadlionUHC.getInstance().getServer().unloadWorld(uhcworldTheEnd, false);
                    }

                    // Delete worlds
                    if (BadlionUHC.getInstance().deleteDirectory(new File(BadlionUHC.UHCWORLD_NAME))) {
                        sender.sendMessage(ChatColor.YELLOW + "Deleted old \"uhcworld\" world!");
                    }
                    if (BadlionUHC.getInstance().deleteDirectory(new File(BadlionUHC.UHCWORLD_NETHER_NAME))) {
                        sender.sendMessage(ChatColor.YELLOW + "Deleted old \"uhcworld_nether\" world!");
                    }
                    if (BadlionUHC.getInstance().deleteDirectory(new File(BadlionUHC.UHCWORLD_END_NAME))) {
                        sender.sendMessage(ChatColor.YELLOW + "Deleted old \"uhcworld_the_end\" world!");
                    }

                    String generator = "uhcgen";
                    String size = BadlionUHC.getInstance().isMiniUHC() ? "500" : "3k";
                    if (BadlionUHC.getInstance().isMiniUHC()) {
                        Pattern p = Pattern.compile("mini(\\d+)");
                        Matcher matcher = p.matcher(Gberry.serverName);
                        if (matcher.find()) {
                            Integer serverId;
                            try {
                                serverId = Integer.parseInt(matcher.group(1));
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.RED + "Error deleting world. Bad config in Gberry.");
                                return true;
                            }

                            generator += (serverId / 6) + 1;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Error deleting world. Bad config in Gberry.");
                            return true;
                        }
                    } /*else if (!Gberry.serverName.contains("au") && !Gberry.serverName.contains("eu")) {
                        if (Gberry.serverName.contains("3") || Gberry.serverName.contains("4")) {
                            generator += "2";
                        }
                    }*/

                    // Find a world from the other folder
                    for (int i = 1; i <= 99; i++) {
                        File world = new File("../" + generator + "/worlds/uhcworld_" + size + "_" + i);
                        if (!world.exists()) {
                            continue;
                        }

                        // Check if it's generating
                        File lock = new File("../" + generator + "/worlds/uhcworld_" + size + "_" + i + "/gen.lock");
                        if (lock.exists()) {
                            continue;
                        }

                        try {
                            FileUtils.moveDirectory(world, new File(BadlionUHC.UHCWORLD_NAME));
                            FileUtils.moveDirectory(new File("../" + generator + "/worlds/uhcworld_" + size + "_" + i + "_nether"), new File(BadlionUHC.UHCWORLD_NETHER_NAME));

                            try {
                                FileUtils.moveDirectory(new File("../" + generator + "/worlds/uhcworld_" + size + "_" + i + "_the_end"), new File(BadlionUHC.UHCWORLD_END_NAME));
                            } catch (Exception e) {
                                // Do nothing
                            }
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // MAYDAY MAYDAY
                    BadlionUHC.getInstance().getServer().dispatchCommand(BadlionUHC.getInstance().getServer().getConsoleSender(), "stop");
                } else if (args[0].equalsIgnoreCase("help") && player != null) {
                    player.getInventory().addItem(this.helpBook);
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (BadlionUHC.getInstance().getState().ordinal() >= BadlionUHC.BadlionUHCState.STARTED.ordinal()) {
                        sender.sendMessage(ChatColor.RED + "Once a game has started you can only use /uhc deleteworld");
                        return true;
                    }

                    BadlionUHC.getInstance().getServer().dispatchCommand(BadlionUHC.getInstance().getServer().getConsoleSender(), "stop");
                } else if (args[0].equalsIgnoreCase("lockdown")) {
                    BadlionUHC.lockdown = !BadlionUHC.lockdown;
                    sender.sendMessage(ChatColor.GREEN + "Lockdown now set to " + BadlionUHC.lockdown);
                } else if (args[0].equalsIgnoreCase("animalAI") && player != null) {
                    player.getWorld().setNerfAnimals(!player.getWorld().getNerfAnimals());
                    player.sendMessage(ChatColor.GREEN + "Animal AI is set to " + player.getWorld().getNerfAnimals());
                } else if (sender.isOp() && args[0].equalsIgnoreCase("flushdb")) {
                    BadlionUHC.getInstance().getServer().getScheduler().runTaskAsynchronously(BadlionUHC.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            DatabaseManager.saveMatchData(BadlionUHC.getInstance().getGame());
                            sender.sendMessage("done");
                        }
                    });
                } else if (sender.isOp() && args[0].equalsIgnoreCase("head") && player != null) {
                    GoldenHeadUtils.dropBlockNaturally(args[1], player.getLocation().getBlock());
                } else if (sender.isOp() && args[0].equalsIgnoreCase("giveitems")) {
                    ItemStack item = new ItemStack(Material.valueOf(args[1]), Integer.parseInt(args[2]), Short.parseShort(args[3]));
                    for (UHCPlayer uhcPlayer : UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER)) {
                        Player p = uhcPlayer.getPlayer();
                        if (p != null) {
                            p.getInventory().addItem(item);
                        }
                    }
                } else if (sender.isOp() && args[0].equalsIgnoreCase("troll_ticks")) {
                    TrollGame1.TICKS_IN_BETWEEN = Integer.parseInt(args[1]);
                } else if (sender.isOp() && args[0].equalsIgnoreCase("blockdata")) {
                    Block block = Bukkit.getServer().getWorld(BadlionUHC.UHCWORLD_NAME).getBlockAt(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                    sender.sendMessage(block.getType().name());
                    sender.sendMessage(block.getBlockPower() + "");
                    sender.sendMessage(block.getData() + "");
                    sender.sendMessage(block.getState().toString());
                } else if (args[0].equalsIgnoreCase("debug")) {
                    Bukkit.getLogger().info("Alive players: " + UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER));
                    sender.sendMessage("Alive players: " + UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER));
                    Bukkit.getLogger().info("Alive teams: " + UHCTeamManager.getAllAlivePlayingTeams());
                    sender.sendMessage("Alive teams: " + UHCTeamManager.getAllAlivePlayingTeams());
                } else if (sender.isOp() && args[0].equalsIgnoreCase("spectator_bypass")) {
                    DonatorSpectatorTask.bypassSpectatorLimits = !DonatorSpectatorTask.bypassSpectatorLimits;
                    sender.sendMessage("Spectator bypass is now set to " + DonatorSpectatorTask.bypassSpectatorLimits);
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid command, use \"/uhc help\" for help.");
                }
                /*else if (args[0].equalsIgnoreCase("rules")) {
                    RulesCommandHandler.handleRulesCommand(player, args);
                }*/
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have access to this command.");
            }
        } else if (BadlionUHC.getInstance().getHost() != null && player != null && player.getUniqueId().equals(BadlionUHC.getInstance().getHost().getUUID())) {
            sender.sendMessage(ChatColor.RED + "Invalid command, use \"/uhc help\" for help.");
        } else if (sender.isOp()) {
            sender.sendMessage("Use \"/uhc sethost <name>\" to set the UHC host");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have access to this command.");
        }

        return false;
    }

}
