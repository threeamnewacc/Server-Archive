package net.badlion.uhc.commands.handlers;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.listeners.gamemodes.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GameModeHandler {

    public static HashSet<String> GAME_MODES = new HashSet<>();
    public static Map<String, GameMode> gamemodes = new HashMap<>();

    public static void handleGameMode(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("classic") || args[0].equalsIgnoreCase("rush") || args[0].equalsIgnoreCase("ender_dragon_rush") || args[0].equalsIgnoreCase("miniuhc")) {
                if (args.length > 2) {
                    Gberry.executeCommand(sender, "uhc config radius " + args[2]);
                } else {
                    Gberry.executeCommand(sender, "uhc config radius 3000");
                }

                Gberry.executeCommand(sender, "uhc fakegen");
                Gberry.executeCommand(sender, "uhc config maxplayers " + args[1]);
	            if (BadlionUHC.getInstance().isMiniUHC()) {
		            Gberry.executeCommand(sender, "uhc config nether false");
	            } else {
		            Gberry.executeCommand(sender, "uhc config nether true");
	            }
                Gberry.executeCommand(sender, "uhc config pvptimer " + (BadlionUHC.getInstance().isMiniUHC() ? "10" : "20"));
                Gberry.executeCommand(sender, "uhc config ipvp false");
                Gberry.executeCommand(sender, "uhc config goldenheads true");
                Gberry.executeCommand(sender, "uhc config goldenheadsstack true");
                Gberry.executeCommand(sender, "uhc config str false");
                Gberry.executeCommand(sender, "uhc config str2 false");
                Gberry.executeCommand(sender, "uhc config invisibility false");
                Gberry.executeCommand(sender, "uhc config godapples false");
                Gberry.executeCommand(sender, "uhc config absorption true");
                Gberry.executeCommand(sender, "uhc config pearls true");
                Gberry.executeCommand(sender, "uhc config stats true");
                Gberry.executeCommand(sender, "uhc config scoreboardhealthscale 1");
                Gberry.executeCommand(sender, "uhc config healtime " + (BadlionUHC.getInstance().isMiniUHC() ? "5" : "10"));
                Gberry.executeCommand(sender, "uhc config food 10");
                Gberry.executeCommand(sender, "uhc config horse_regen true");

                // Rush
                if (args[0].equalsIgnoreCase("rush")) {
                    Gberry.executeCommand(sender, "uhc config food 20");
                    Gberry.executeCommand(sender, "uhc config nether false");
                    Gberry.executeCommand(sender, "uhc config str false");
                    Gberry.executeCommand(sender, "uhc config pvptimer 10");
                    Gberry.executeCommand(sender, "uhc config healtime 5");
                }

                // By default only ender_dragon_rush doesnt use border shrinking
                if (!args[0].equalsIgnoreCase("ender_dragon_rush")) {
                    if (args[0].equalsIgnoreCase("rush")) {
                        Gberry.executeCommand(sender, "bs 30 5 500 100");
                        Gberry.executeCommand(sender, "bs on");
                    } else if (args[0].equals("miniuhc")) {
                        Gberry.executeCommand(sender, "bs 25 5 200 100");
                        Gberry.executeCommand(sender, "bs on");
                    } else {
                        Gberry.executeCommand(sender, "bs 60 5 500 100");
                        Gberry.executeCommand(sender, "bs on");
                    }
                } else {
                    Gberry.executeCommand(sender, "bs off");
                }

                GameModeHandler.registerListener(sender, args[0]);

                if (BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.TEAMSIZE.name()) != null) {
                    Gberry.executeCommand(sender, "uhc config teamsize 2");
                    Gberry.executeCommand(sender, "uhc config teamhealthtransfer false");
                    Gberry.executeCommand(sender, "uhc config teamhealthshare false");
                }

                Gberry.executeCommand(sender, "uhc generatespawns");
            }
        } else {
            GameModeHandler.usage(sender);
        }
    }

    public static void usage(CommandSender p ) {
        p.sendMessage(ChatColor.RED + "/uhc gamemode classic|cutclean [# of players]");
    }

    public static boolean registerListener(CommandSender sender, String gamemode) {
        if (GameModeHandler.GAME_MODES.contains(gamemode.toUpperCase())) {
            return false;
        }

        GameMode gameMode;

        if (gamemode.equalsIgnoreCase("cutclean")) {
            gameMode = new CutCleanGameMode();
        } else if (gamemode.equalsIgnoreCase("diamondless")) {
            gameMode = new DiamondlessGameMode();
        } else if (gamemode.equalsIgnoreCase("vaecon")) {
            gameMode = new VaeconGameMode();
        } else if (gamemode.equalsIgnoreCase("ender_dragon_rush")) {
            gameMode = new EndDragonRushGameMode();
        } else if (gamemode.equalsIgnoreCase("ore_frenzy")) {
	        gameMode = new OreFrenzyGameMode();
        } else if (gamemode.equalsIgnoreCase("ops_vs_world")) {
	        gameMode = new OpsVsWorldGameMode();
        } else if (gamemode.equalsIgnoreCase("troll_game_1")) {
            gameMode = new TrollGame1();
        } else if (gamemode.equalsIgnoreCase("cube")) {
            gameMode = new CubeGameMode();
        } else if (gamemode.equalsIgnoreCase("minesweeper")) {
            gameMode = new MineSweeperGameMode();
        } else if (gamemode.equalsIgnoreCase("horseless")) {
            gameMode = new HorselessGameMode();
        } else if (gamemode.equalsIgnoreCase("nineslot")) {
            gameMode = new NineSlotGameMode();
        } else if (gamemode.equalsIgnoreCase("twitch")) {
            gameMode = new TwitchGameMode();
        } else if (gamemode.equalsIgnoreCase("goldless")) {
            gameMode = new GoldlessGameMode();
        } else if (gamemode.equalsIgnoreCase("blood_diamond")) {
	        gameMode = new BloodDiamondMode();
        } else if (gamemode.equalsIgnoreCase("quadrants")) {
	        gameMode = new QuadrantsGameMode(sender);
        } else if (gamemode.equalsIgnoreCase("bowless")) {
	        gameMode = new BowlessGameMode();
        //} else if (gamemode.equalsIgnoreCase("ironless")) {
	    //    gameMode = new IronlessGameMode();
        } else if (gamemode.equalsIgnoreCase("monster_hunter")) {
	        gameMode = new MonsterHunterGameMode();
        } else if (gamemode.equalsIgnoreCase("lone_enchanter")) {
	        gameMode = new LimitedEnchantsGameMode();
        } else if (gamemode.equalsIgnoreCase("rodless")) {
	        gameMode = new RodlessGameMode();
        //} else if (gamemode.equalsIgnoreCase("agents_vs_world")) {
	        // gameMode = new AgentsGameMode(); Disabled until 6.0
        } else if (gamemode.equalsIgnoreCase("risky_retrieval")) {
	        gameMode = new RiskyRetrievalGameMode();
        } else if (gamemode.equalsIgnoreCase("backpacks")) {
	        gameMode = new BackpacksGameMode();
        } else if (gamemode.equalsIgnoreCase("fireless")) {
	        gameMode = new FirelessGameMode();
        //} else if (gamemode.equalsIgnoreCase("coalless")) {
	    //    gameMode = new CoallessGameMode();
        } else if (gamemode.equalsIgnoreCase("limitations")) {
	        gameMode = new LimitationGameMode();
        } else if (gamemode.equalsIgnoreCase("blood_enchant")) {
	        gameMode = new BloodEnchantGameMode();
        } else if (gamemode.equalsIgnoreCase("erratic_pvp")) {
	        gameMode = new ErraticPvPGameMode();
        } else if (gamemode.equalsIgnoreCase("op_uhc")) {
	        gameMode = new OPGameMode();
        //} else if (gamemode.equalsIgnoreCase("longshots")) {
	    //    gameMode = new LongshotsGameMode();
        } else if (gamemode.equalsIgnoreCase("broadcaster")) {
	        gameMode = new BroadcasterGameMode();
        //} else if (gamemode.equalsIgnoreCase("agents")) {
	       // gameMode = new AgentsGameMode(); Disabled until 6.0
        //} else if (gamemode.equalsIgnoreCase("trump")) {
	       // gameMode = new TrumpGameMode();
        } else if (gamemode.equalsIgnoreCase("barebones")) {
            gameMode = new BarebonesGameMode();
        } else if (gamemode.equalsIgnoreCase("statless")) {
            gameMode = new StatlessGameMode();
        } else if (gamemode.equalsIgnoreCase("time_bomb")) {
            gameMode = new TimeBombGameMode();
        } else if (gamemode.equalsIgnoreCase("water_world")) {
            sender.sendMessage(ChatColor.GREEN + "You will probably disconnect. Reconnect in 1 minute");
            gameMode = new WaterWorldGameMode();
        } else if (gamemode.equalsIgnoreCase("vanilla+")) {
            gameMode = new VanillaPlusGameMode();
        } else if (gamemode.equalsIgnoreCase("soup")) {
            gameMode = new SoupGameMode();
        } else if (gamemode.equalsIgnoreCase("twitch_on_air")) {
            gameMode = new TwitchOnAirGameMode();
        } else if (gamemode.equalsIgnoreCase("shared_health") && BadlionUHC.getInstance().getGameType() == UHCTeam.GameType.TEAM) {
            gameMode = new SharedHealthGameMode();
        } else {
            // Invalid game mode
            return false;
        }

        GameModeHandler.gamemodes.put(gamemode.toUpperCase(), gameMode);
        BadlionUHC.getInstance().getServer().getPluginManager().registerEvents(gameMode, BadlionUHC.getInstance());
        GameModeHandler.GAME_MODES.add(gamemode.toUpperCase());

        return true;
    }

    public static boolean unregisterListener(String gamemode) {
        if (!GameModeHandler.GAME_MODES.contains(gamemode.toUpperCase())) {
            return false;
        }

        GameModeHandler.GAME_MODES.remove(gamemode.toUpperCase());
        GameMode gameMode = GameModeHandler.gamemodes.remove(gamemode.toUpperCase());
        gameMode.unregister();

        return true;
    }

}
