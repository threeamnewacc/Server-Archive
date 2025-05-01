package net.badlion.uhc.commands.handlers;

import net.badlion.common.Configurator;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.gberry.Gberry;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.PlayerData;
import net.badlion.smellychat.SmellyChat;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.commands.WhitelistCommand;
import net.badlion.uhc.events.BorderShrinkSetEvent;
import net.badlion.uhc.events.GameStartEvent;
import net.badlion.uhc.events.PVPProtectionTurnedOnEvent;
import net.badlion.uhc.events.SpecialTeamsEvent;
import net.badlion.uhc.events.UHCTeleportPlayerLocationEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.managers.UHCTeamManager;
import net.badlion.uhc.tasks.AutoButcherTask;
import net.badlion.uhc.tasks.CheckGameplayTimerTask;
import net.badlion.uhc.tasks.DonatorSpectatorTask;
import net.badlion.uhc.tasks.GameTimeTask;
import net.badlion.uhc.tasks.HealTask;
import net.badlion.uhc.tasks.PvPTimerTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StartCommandHandler {

    public static int countdown = 0;
    public static int playersPer5Ticks = 5;

    private static ArrayList<UHCTeam> uhcTeams = new ArrayList<>();
    public static UHCTeam teamNeedingPlayers = null;

    public static void handleStartCommand(CommandSender sender, String[] args) {
        if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.PRE_START) {
            sender.sendMessage(ChatColor.RED + "Order to start a UHC is Generate world, Config, Generate Spawns and then start.");
            return;
        }

        if (BadlionUHC.getInstance().getBorderShrink() != null && BadlionUHC.getInstance().getBorderShrink()) {
            if (BadlionUHC.getInstance().getBorderShrinkTask() == null) {
                sender.sendMessage(ChatColor.RED + "Please set the border shrink settings before starting with \"/bs\"!");
                return;
            }
        } else if (BadlionUHC.getInstance().getBorderShrink() == null) {
            sender.sendMessage(ChatColor.RED + "Set border shrinking before trying to start a game.");
            return;
        }

        if (!BadlionUHC.getInstance().getConfigurator().checkIfAllOptionsSet()) {
            sender.sendMessage(ChatColor.YELLOW + "The following options are not set currently and are required.");
            for (Configurator.Option option : BadlionUHC.getInstance().getConfigurator().unconfiguredOptions()) {
                sender.sendMessage(option.toString());
            }
            return;
        }

	    // Disable cosmetics
        Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.GADGET, false);
        Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.MORPH, false);
        Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.PET, false);
        Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.PARTICLE, false);

        // Turn on whitelist
        WhitelistCommand.whitelistAllPlayers(sender);
        BadlionUHC.getInstance().setWhitelistBoolean(true);
        sender.sendMessage(ChatColor.YELLOW + "Whitelist is enabled.");

        SpecialTeamsEvent event = new SpecialTeamsEvent();
        BadlionUHC.getInstance().getServer().getPluginManager().callEvent(event);

        if (!event.isOverriden()) {
		    for (UHCTeam uhcTeam : UHCTeamManager.getAllUHCTeams()) {
			    UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uhcTeam.getLeader());
			    if (uhcPlayer == null || (uhcPlayer.getState() != UHCPlayer.State.PLAYER && uhcPlayer.getState() != UHCPlayer.State.SPEC_IN_GAME)) {
				    continue;
			    }

			    // Not enough players?
			    if (BadlionUHC.getInstance().getGameType() == UHCTeam.GameType.TEAM
					    && (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.TEAMSIZE.name()).getValue() != uhcTeam.getSize()
					    && uhcTeam.getSize() == 1) {

				    if (uhcPlayer.isSolo()) {
					    StartCommandHandler.uhcTeams.add(uhcTeam);
					    continue;
				    }


				    StartCommandHandler.handleMergingTeams(uhcPlayer, uhcTeam);
				    continue;
			    }

			    StartCommandHandler.uhcTeams.add(uhcTeam);
		    }
	    }

        // Might have a leftover team
        if (StartCommandHandler.teamNeedingPlayers != null) {
            StartCommandHandler.uhcTeams.add(StartCommandHandler.teamNeedingPlayers);
        }

        StartCommandHandler.playersPer5Ticks = (int) Math.ceil((double) StartCommandHandler.uhcTeams.size() / 54);
        StartCommandHandler.countdown = BadlionUHC.getInstance().getConfig().getInt("countdown");

        World w = Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME);
        int y = w.getHighestBlockYAt(0, 0) + 10;

        // Host & mods go to the middle section
        if (!BadlionUHC.getInstance().isMiniUHC()) {
            Player host = BadlionUHC.getInstance().getServer().getPlayer(BadlionUHC.getInstance().getHost().getUUID());

            if (host != null) {
                host.teleport(new Location(w, 0, y, 0));
                host.setGameMode(GameMode.CREATIVE);
                host.setFlying(true);
	            host.getInventory().clear();
	            host.getInventory().addItem(new ItemStack(Material.COMPASS), BadlionUHC.getInstance().getSpectatorItem());
	            host.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
            }
        }

        ConcurrentLinkedQueue<UHCPlayer> moderators = UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.MOD);
        for (UHCPlayer mod : moderators) {
            Player pl = BadlionUHC.getInstance().getServer().getPlayer(mod.getUUID());
            if (pl != null) {
                pl.teleport(new Location(w, 0, y, 0));
                pl.setGameMode(GameMode.CREATIVE);
                pl.setFlying(true);
	            pl.getInventory().clear();
                pl.getInventory().addItem(new ItemStack(Material.COMPASS), BadlionUHC.getInstance().getSpectatorItem());
	            pl.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
            }
        }

        // Do it before-hand just cuz
        Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME).setTime(0);

        BadlionUHC.getInstance().setState(BadlionUHC.BadlionUHCState.COUNTDOWN);

        // Keep it sync...
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!StartCommandHandler.countdown()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(BadlionUHC.getInstance(), 0L, 100L);

	    Gberry.broadcastMessage(ChatColor.AQUA + "You will be teleported into the map over the next 270 seconds." +
			    "\nDuring that time, the rules will be listed. It is your responsibility to know the rules. Breaking them will result in a punishment." +
			    "\nType /rules to repeat them at any time during the game. Please wait until rules are finished before asking questions.");

        // Start the Rules
        RulesCommandHandler.initialize();
        RulesCommandHandler.handleRulesCommand();

        if (!SmellyChat.GLOBAL_MUTE) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "globalmute");
        }
    }

    public static void handleMergingTeams(UHCPlayer uhcPlayer, UHCTeam uhcTeam) {
        if (StartCommandHandler.teamNeedingPlayers == null) {
            StartCommandHandler.teamNeedingPlayers = uhcTeam;
        } else {
            // Add to the team needing players
            Player leader = BadlionUHC.getInstance().getServer().getPlayer(StartCommandHandler.teamNeedingPlayers.getLeader());
            if (leader == null) {
                return;
            }

            Player p1 = uhcPlayer.getPlayer();
            if (p1 == null) {
	            return;
            }

            leader.sendMessage(ChatColor.GREEN + "Your team has been changed.");
            p1.sendMessage(ChatColor.GREEN + "Your team has been changed.");

            // Merge teams
            UHCTeamManager.removeUHCTeam(uhcPlayer.getTeam());
            uhcPlayer.setTeam(StartCommandHandler.teamNeedingPlayers);
            teamNeedingPlayers.addPlayer(uhcPlayer.getUUID());

            if (StartCommandHandler.teamNeedingPlayers.getSize() == (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.TEAMSIZE.name()).getValue()) {
                StartCommandHandler.uhcTeams.add(StartCommandHandler.teamNeedingPlayers);
                StartCommandHandler.teamNeedingPlayers = null;
            }
        }
    }

    public static boolean countdown() {
        if (StartCommandHandler.countdown <= 0) {
            endCountdown();
            return false;
        } else {
            if (StartCommandHandler.countdown % 15 == 0 && StartCommandHandler.countdown != 0) {
                Bukkit.broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "UHC starting in " + StartCommandHandler.countdown + " seconds!");
            }

            // Down 5 no matter what
            StartCommandHandler.countdown -= 5;

            // Teams...ugh more work
            for (int i = 0; i < StartCommandHandler.playersPer5Ticks; i++) {
                if (StartCommandHandler.uhcTeams.size() > 0) {
                    UHCTeam uhcTeam = StartCommandHandler.uhcTeams.remove(0);
                    Location location = null;
                    if (GenerateSpawnsCommandHandler.scatterPoints.size() > 0) {
                        location = GenerateSpawnsCommandHandler.scatterPoints.remove(0);
                    }

                    // Might have ran out of locations
                    if (location == null) {
                        location = GenerateSpawnsCommandHandler.getNewLocation();
                    }

                    for (UUID uuid : uhcTeam.getUuids()) {
                        // Verify they are still online
                        Player pl = BadlionUHC.getInstance().getServer().getPlayer(uuid);

                        // Might not be online anymore, oh well
                        if (pl != null) {
                            StartCommandHandler.prepPlayerForStart(pl);

                            // Let custom game modes do something first
                            UHCTeleportPlayerLocationEvent event = new UHCTeleportPlayerLocationEvent(pl);
                            BadlionUHC.getInstance().getServer().getPluginManager().callEvent(event);

                            // Did the event change the location?
                            if (event.getLocation() != null) {
                                location = event.getLocation();
                            }

	                        Gberry.safeTeleport(pl, location);
                        }
                    }
                } else {
                    break;
                }
            }
        }

        return true;
    }

    public static void endCountdown() {
        // Go through and force all the remaining players
	    for (Player player : BadlionUHC.getInstance().getServer().getWorld("uhclobby").getPlayers()) {
		    BadlionUHC.getInstance().handlePlayerTeleportAndStart(player);
	    }

	    // Teleport all the players in practice
	    for (Player player : BadlionUHC.getInstance().getServer().getWorld("uhcpractice").getPlayers()) {
		    BadlionUHC.getInstance().handlePlayerTeleportAndStart(player);
	    }

        // Start gameplay timer
	    BadlionUHC.getInstance().setGameplayTimerTask(Bukkit.getScheduler().runTaskTimer(BadlionUHC.getInstance(), new CheckGameplayTimerTask(), 20 * 60 * 5L, 20 * 60 * 5).getTaskId());

        // Start AFK timer
        //BadlionUHC.getInstance().setAfkCheckerTask(Bukkit.getScheduler().runTaskTimer(BadlionUHC.getInstance(), new AFKCheckerTask(), 0L, 20 * 60 * 300).getTaskId()); // Check every 5 minutes

        // Start Game timer
        new GameTimeTask().runTaskTimer(BadlionUHC.getInstance(), 20, 20);

        new DonatorSpectatorTask().runTaskTimer(BadlionUHC.getInstance(), 5, 5);

        // Start the pvp timer
        if (((int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.PVPTIMER.name()).getValue()) != 0) {
            Bukkit.getScheduler().runTaskLater(BadlionUHC.getInstance(), new PvPTimerTask(),
                    (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.PVPTIMER.name()).getValue() * 60L * 20L);
        } else {
            BadlionUHC.getInstance().setPVP(true);
            Bukkit.getPluginManager().callEvent(new PVPProtectionTurnedOnEvent());
        }
        Gberry.broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "UHC has started!");

        for (Player player : Bukkit.getOnlinePlayers()) {
	        // Don't remove potion effects (night vision) for mods/host
	        if (player.getGameMode() == GameMode.CREATIVE) continue;

            BadlionUHC.getInstance().removeBeginningPotionEffects(player);
        }

        BadlionUHC.getInstance().setState(BadlionUHC.BadlionUHCState.STARTED);
        BadlionUHC.getInstance().setStartTime(System.currentTimeMillis());
        Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME).setTime(0);

        // Shrink border
        if (BadlionUHC.getInstance().getBorderShrink() != null && BadlionUHC.getInstance().getBorderShrink()) {
            BorderShrinkSetEvent event = new BorderShrinkSetEvent();
            BadlionUHC.getInstance().getServer().getPluginManager().callEvent(event);

            // Some game modes might not permit border shrinking such as EnderDragonRush
            if (!event.isCancelled()) {
                int startTime = BadlionUHC.getInstance().getBorderShrinkTask().startTime;
                BadlionUHC.getInstance().getBorderShrinkTask().runTaskTimer(BadlionUHC.getInstance(), startTime * 1200L, 20L);
            }
        }

        new AutoButcherTask().runTaskTimer(BadlionUHC.getInstance(), 20 * 30, 20 * 30);

        if (!BadlionUHC.getInstance().isMiniUHC()) {
            Player player = Bukkit.getPlayer(UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.HOST).iterator().next().getUUID());
            if (player != null) {
                BadlionUHC.getInstance().addMuteBanPerms(player);
            }
        }

        // Start setting up for the final heal (if there is one)
        Configurator.Option option = BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.HEALTIME.name());
        if (option.getValue() != null && (int) option.getValue() > 0) {
            new HealTask().runTaskLater(BadlionUHC.getInstance(), ((int) option.getValue()) * 20 * 60);
        }

        // If we are not doing end dragon rush unload the end
        // TODO: Fix
        if (!GameModeHandler.GAME_MODES.contains("ENDER_DRAGON_RUSH")) {
            BadlionUHC.getInstance().getServer().unloadWorld(BadlionUHC.UHCWORLD_END_NAME, false);
        }

        // Game has started
        BadlionUHC.getInstance().getServer().getPluginManager().callEvent(new GameStartEvent());
        BadlionUHC.getInstance().createRecipes();

        // Data tracking
        if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).getValue()) {
            // Load them into the map
            for (UHCPlayer uhcPlayer : UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER)) {
                PlayerData playerData = new PlayerData(uhcPlayer.getUUID(), BadlionUHC.getInstance().getUsername(uhcPlayer.getUUID()), BadlionUHC.UHCWORLD_NAME);

                // Only do it for ppl online (we will handle this elsewhere)
                Player pl = BadlionUHC.getInstance().getServer().getPlayer(playerData.getUniqueId());
                if (pl != null) {
                    MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().put(uhcPlayer.getUUID(), playerData);
                }
            }

            // Stat Tracking
            MiniStats.getInstance().startListening();
        }

        // Give food/heal/feed
        new BukkitRunnable() {
            @Override
            public void run() {
                int food = (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.FOOD.name()).getValue();
                if (food != 0) {
                    BadlionUHC.getInstance().getServer().dispatchCommand(BadlionUHC.getInstance().getServer().getConsoleSender(), "food " + food);
                }
            }
        }.runTaskLater(BadlionUHC.getInstance(), 5 * 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                BadlionUHC.getInstance().getServer().dispatchCommand(BadlionUHC.getInstance().getServer().getConsoleSender(), "feed");
                BadlionUHC.getInstance().getServer().dispatchCommand(BadlionUHC.getInstance().getServer().getConsoleSender(), "heal");
            }
        }.runTaskLater(BadlionUHC.getInstance(), 15 * 20);
    }

    public static void prepPlayerForStart(final Player player) {
	    // Respawn them first if they're dead in Practice
	    if (player.isDead()) {
		    player.spigot().respawn();
	    }

        player.setGameMode(GameMode.SURVIVAL);

        player.setFoodLevel(20);
	    player.setHealth(player.getMaxHealth());
        player.setExhaustion(0);
        player.setSaturation(20);

        player.setFireTicks(0);

	    player.setItemOnCursor(null);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

	    if (player.getOpenInventory() != null && player.getOpenInventory().getType() == InventoryType.CRAFTING) {
		    player.getOpenInventory().getTopInventory().clear();
	    }

        // Can't move
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 127, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -5, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 127, false));

        // Data tracking stuff
        player.setStatistic(Statistic.DAMAGE_TAKEN, 0);
        player.setStatistic(Statistic.MOB_KILLS, 0);
        player.setStatistic(Statistic.PLAYER_KILLS, 0);

        for (Material material : BadlionUHC.getInstance().getMaterials()) {
            player.setStatistic(Statistic.MINE_BLOCK, material, 0);
        }

        for (EntityType entityType : BadlionUHC.getInstance().getEntityTypes()) {
            player.setStatistic(Statistic.KILL_ENTITY, entityType, 0);
        }
    }

}