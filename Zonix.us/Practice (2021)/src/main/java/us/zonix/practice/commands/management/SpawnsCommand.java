package us.zonix.practice.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;

public class SpawnsCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public SpawnsCommand() {
        super("setspawn");
        this.setDescription("Spawn command.");
        this.setUsage(ChatColor.RED + "Usage: /setspawn <subcommand>");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (!player.hasPermission("practice.admin.spawnmanager")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
                return true;
            } else if (args.length < 1) {
                sender.sendMessage(this.usageMessage);
                return true;
            } else {
                String var5 = args[0].toLowerCase();
                switch (var5) {
                    case "spawnlocation":
                        this.plugin.getSpawnManager().setSpawnLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("SPAWN.LOCATION", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the spawn location.");
                        break;
                    case "spawnmin":
                        this.plugin.getSpawnManager().setSpawnMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("SPAWN.MIN", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the spawn min.");
                        break;
                    case "spawnmax":
                        this.plugin.getSpawnManager().setSpawnMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("SPAWN.MAX", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the spawn max.");
                        break;
                    case "editorlocation":
                        this.plugin.getSpawnManager().setEditorLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("EDITOR.LOCATION", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the editor location.");
                        break;
                    case "editormin":
                        this.plugin.getSpawnManager().setEditorMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("EDITOR.MIN", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the editor min.");
                        break;
                    case "editormax":
                        this.plugin.getSpawnManager().setEditorMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("EDITOR.MAX", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the editor max.");
                        break;
                    case "ffalocation":
                        this.plugin.getSpawnManager().setFfaLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("FFA.LOCATION", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the ffa location.");
                        break;
                    case "ffamin":
                        this.plugin.getSpawnManager().setFfaMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("FFA.MIN", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the ffa min.");
                        break;
                    case "ffamax":
                        this.plugin.getSpawnManager().setFfaMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("FFA.MAX", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the ffa max.");
                        break;
                    case "sumolocation":
                        this.plugin.getSpawnManager().setSumoLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("SUMO.LOCATION", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the sumo location.");
                        break;
                    case "sumofirst":
                        this.plugin.getSpawnManager().setSumoFirst(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("SUMO.FIRST", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the sumo location A.");
                        break;
                    case "sumosecond":
                        this.plugin.getSpawnManager().setSumoSecond(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("SUMO.SECOND", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the sumo location B.");
                        break;
                    case "sumomin":
                        this.plugin.getSpawnManager().setSumoMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("SUMO.MIN", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the sumo min.");
                        break;
                    case "sumomax":
                        this.plugin.getSpawnManager().setSumoMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("SUMO.MAX", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the sumo max.");
                        break;
                    case "oitclocation":
                        this.plugin.getSpawnManager().setOitcLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("OITC.LOCATION", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the OITC location.");
                        break;
                    case "oitcmin":
                        this.plugin.getSpawnManager().setOitcMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("OITC.MIN", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the OITC min.");
                        break;
                    case "oitcmax":
                        this.plugin.getSpawnManager().setOitcMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("OITC.MAX", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the OITC max.");
                        break;
                    case "oitcspawnpoints":
                        this.plugin.getSpawnManager().getOitcSpawnpoints().add(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("OITC.SPAWN_POINTS", this.plugin.getSpawnManager().fromLocations(this.plugin.getSpawnManager().getOitcSpawnpoints()));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(
                            ChatColor.GREEN + "Successfully set the OITC spawn-point #" + this.plugin.getSpawnManager().getOitcSpawnpoints().size() + "."
                        );
                        break;
                    case "parkourlocation":
                        this.plugin.getSpawnManager().setParkourLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("PARKOUR.LOCATION", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the parkour location.");
                        break;
                    case "parkourgamelocation":
                        this.plugin.getSpawnManager().setParkourGameLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("PARKOUR.GAME_LOCATION", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the parkour Game location.");
                        break;
                    case "parkourmax":
                        this.plugin.getSpawnManager().setParkourMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("PARKOUR.MAX", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the parkour max location.");
                        break;
                    case "parkourmin":
                        this.plugin.getSpawnManager().setParkourMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("PARKOUR.MIN", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the parkour min location.");
                        break;
                    case "waterdroplocation":
                        this.plugin.getSpawnManager().setWaterDropLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("WATERDROP.LOCATION", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the waterdrop location.");
                        break;
                    case "waterdropjump":
                        this.plugin.getSpawnManager().setWaterDropJump(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("WATERDROP.JUMP_LOCATION", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the waterdrop jump location.");
                        break;
                    case "waterdropmin":
                        this.plugin.getSpawnManager().setWaterDropMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("WATERDROP.MIN", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the waterdrop min location.");
                        break;
                    case "waterdropmax":
                        this.plugin.getSpawnManager().setWaterDropMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("WATERDROP.MAX", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the waterdrop max location.");
                        break;
                    case "waterdropfirst":
                        this.plugin
                            .getSpawnManager()
                            .setWaterDropFirst(CustomLocation.fromBukkitLocation(player.getLocation().clone().subtract(0.0, 1.0, 0.0)));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("WATERDROP.FIRST", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the waterdrop first location.");
                        break;
                    case "waterdropsecond":
                        this.plugin
                            .getSpawnManager()
                            .setWaterDropSecond(CustomLocation.fromBukkitLocation(player.getLocation().clone().subtract(0.0, 1.0, 0.0)));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("WATERDROP.SECOND", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the waterdrop second location.");
                        break;
                    case "redroverlocation":
                        this.plugin.getSpawnManager().setRedroverLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("REDROVER.LOCATION", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the redrover location.");
                        break;
                    case "redroverfirst":
                        this.plugin.getSpawnManager().setRedroverFirst(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("REDROVER.FIRST", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the redrover location A.");
                        break;
                    case "redroversecond":
                        this.plugin.getSpawnManager().setRedroverSecond(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("REDROVER.SECOND", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the redrover location B.");
                        break;
                    case "redrovermin":
                        this.plugin.getSpawnManager().setRedroverMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("REDROVER.MIN", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the redrover min.");
                        break;
                    case "redrovermax":
                        this.plugin.getSpawnManager().setRedroverMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("REDROVER.MAX", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the redrover max.");
                        break;
                    case "woollocation":
                        this.plugin.getSpawnManager().setWoolLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("WOOL.LOCATION", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the wool location.");
                        break;
                    case "woolmax":
                        this.plugin.getSpawnManager().setWoolMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("WOOL.MAX", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the wool max.");
                        break;
                    case "woolmin":
                        this.plugin.getSpawnManager().setWoolMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("WOOL.MIN", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the wool min.");
                        break;
                    case "woolcenter":
                        this.plugin.getSpawnManager().setWoolCenter(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("WOOL.CENTER", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the wool center.");
                        break;
                    case "lightslocation":
                        this.plugin.getSpawnManager().setLightsLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("LIGHTS.LOCATION", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the lights spawn.");
                        break;
                    case "lightsstart":
                        this.plugin.getSpawnManager().setLightsStart(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("LIGHTS.START", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the lights start.");
                        break;
                    case "lightsmax":
                        this.plugin.getSpawnManager().setLightsMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("LIGHTS.MAX", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the lights max.");
                        break;
                    case "lightsmin":
                        this.plugin.getSpawnManager().setLightsMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("LIGHTS.MIN", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the lights min.");
                        break;
                    case "tntspawn":
                        this.plugin.getSpawnManager().setTntTagSpawn(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("TNT_TAG.SPAWN", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the tnt spawn.");
                        break;
                    case "tntlocation":
                        this.plugin.getSpawnManager().setTntTagLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                        this.plugin
                            .getSpawnManager()
                            .getConfig()
                            .getConfiguration()
                            .set("TNT_TAG.LOCATION", CustomLocation.locationToString(CustomLocation.fromBukkitLocation(player.getLocation())));
                        this.plugin.getSpawnManager().saveLocationsFile();
                        player.sendMessage(ChatColor.GREEN + "Successfully set the tnt location.");
                }

                return false;
            }
        }
    }
}
