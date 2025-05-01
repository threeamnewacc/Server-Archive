package net.kohi.vaultbattle.command;

import com.sk89q.minecraft.util.commands.*;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.kohi.vaultbattle.Permission;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.menu.admin.TeamPicker;
import net.kohi.vaultbattle.type.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand {

    private final VaultBattlePlugin plugin;

    public AdminCommand(VaultBattlePlugin plugin) {
        this.plugin = plugin;
    }

    @Command(
            aliases = {"adminmenu", "am"},
            desc = "Open the vaultbattle admins menu"
    )
    @CommandPermissions(Permission.ADMIN)
    @NestedCommand(AdminSubCommands.class)
    public void adminMenu(final CommandContext args, final CommandSender sender) throws CommandException {
    }


    @Command(
            aliases = {"setteamspawn"},
            desc = "Setup teams spawns and other stuff."
    )
    @CommandPermissions(Permission.ADMIN)
    public void teamSetupSpawns(final CommandContext args, final CommandSender sender) throws CommandException {
        Player player = CommandValidation.requirePlayer(sender);
        if (plugin.getGameMapManager().isEditing(player)) {
            new TeamPicker(plugin, EditType.SETSPAWN, player.getLocation(), null).open(player);
        } else {
            player.sendMessage(ChatColor.RED + "You are not editing any maps.");
        }
    }

    @Command(
            aliases = {"setteamwalls"},
            desc = "Setup teams vaultbattle."
    )
    @CommandPermissions(Permission.ADMIN)
    public void teamSetupWalls(final CommandContext args, final CommandSender sender) throws CommandException {
        Player player = CommandValidation.requirePlayer(sender);
        if (plugin.getGameMapManager().isEditing(player)) {
            Selection s = plugin.getWorldEdit().getSelection(player);
            CommandValidation.notNull(s, "You must make a worldedit selection.");
            if (!(s instanceof CuboidSelection)) {
                throw new CommandException("You must make a cuboid selection.");
            }
            CuboidSelection selection = (CuboidSelection) s;
            Region region = new Region(new SimpleLocation(selection.getMinimumPoint()), new SimpleLocation(selection.getMaximumPoint()));
            plugin.getGameMapManager().getMapEditing(player).getWalls().add(region);
            player.sendMessage(ChatColor.RED + "Added a wall to this map.");
        } else {
            player.sendMessage(ChatColor.RED + "You are not editing any maps.");
        }
    }

    @Command(
            aliases = {"setteambank"},
            desc = "Setup teams bank."
    )
    @CommandPermissions(Permission.ADMIN)
    public void teamSetupBanks(final CommandContext args, final CommandSender sender) throws CommandException {
        Player player = CommandValidation.requirePlayer(sender);
        if (plugin.getGameMapManager().isEditing(player)) {
            Selection s = plugin.getWorldEdit().getSelection(player);
            CommandValidation.notNull(s, "You must make a worldedit selection.");
            if (!(s instanceof CuboidSelection)) {
                throw new CommandException("You must make a cuboid selection.");
            }
            CuboidSelection selection = (CuboidSelection) s;
            Region region = new Region(new SimpleLocation(selection.getMinimumPoint()), new SimpleLocation(selection.getMaximumPoint()));
            new TeamPicker(plugin, EditType.SETBANK, null, region).open(player);
        } else {
            player.sendMessage(ChatColor.RED + "You are not editing any maps.");
        }
    }


    @Command(
            aliases = {"finishedsetup"},
            desc = "Finish setting up world, and save world changes."
    )
    @CommandPermissions(Permission.ADMIN)
    public void finishedSetup(final CommandContext args, final CommandSender sender) throws CommandException {
        Player player = CommandValidation.requirePlayer(sender);
        if (plugin.getGameMapManager().isEditing(player)) {
            GameMap map = plugin.getGameMapManager().getMapEditing(player);
            plugin.getGameMapManager().getAdminsEditing().remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Removed from editing " + map.getMapName() + " world/map, if you edited the map, make sure to move it into the plugins folder!");
        } else {
            player.sendMessage(ChatColor.RED + "You are not editing any maps.");
        }
    }

    @Command(
            aliases = {"setteam"},
            usage = "<player> <team>",
            desc = "Force player to a team or spectator",
            min = 2,
            max = 2
    )
    @CommandPermissions(Permission.ADMIN)
    public void setTeam(final CommandContext args, final CommandSender sender) throws CommandException {
        Player player = CommandValidation.targetPlayer(args.getString(0));
        if (args.getString(1).toLowerCase().startsWith("spec")) {
            plugin.getPlayerDataManager().makeSpectator(player);
            sender.sendMessage(player.getName() + " is now spectating");
            return;
        }
        Team team = plugin.getTeamManager().getTeams().stream()
                .filter(t -> t.getColor().name().toLowerCase().equals(args.getString(1))).findFirst()
                .orElseThrow(() -> new CommandException("No such team"));
        plugin.getTeamManager().joinTeam(team, player);
        sender.sendMessage(player.getName() + " is now on " + team.getName());
    }
}
