package net.kohi.vaultbattle.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.type.Phase;
import net.kohi.vaultbattle.type.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AntiGriefCommands {

    private final VaultBattlePlugin plugin;

    public AntiGriefCommands(VaultBattlePlugin plugin) {
        this.plugin = plugin;
    }

    @Command(
            aliases = {"trust"},
            usage = "<player>",
            desc = "Add someone to your trusted list.",
            min = 1,
            max = 1
    )
    public void trust(final CommandContext args, final CommandSender sender) throws CommandException {
        Player player = CommandValidation.requirePlayer(sender);
        Player target = CommandValidation.targetPlayer(args.getString(0));
        if (target.equals(player)) {
            throw new CommandException("You can not trust yourself.");
        }
        PlayerData playerData = plugin.getPlayerDataManager().get(player);
        PlayerData targetData = plugin.getPlayerDataManager().get(target);
        if (playerData.getAllowedPlayers().contains(target.getUniqueId())) {
            player.sendFormattedMessage("{0} is already trusted by you. {1}If you want to untrust this player use /untrust <name>", ChatColor.GOLD + target.getName(), ChatColor.YELLOW);
            return;
        }
        if (plugin.getGameManager().getPhase().equals(Phase.STARTED)) {
            if (playerData.getTeam() != null && !playerData.getTeam().isEliminated()) {
                if (targetData.getTeam() != null && !targetData.getTeam().isEliminated()) {
                    if (targetData.getTeam().equals(playerData.getTeam())) {
                        playerData.getAllowedPlayers().add(target.getUniqueId());
                        player.sendFormattedMessage("{0}Added {1} to your trusted players list. {2}Use /untrust <name> to remove them.", ChatColor.GREEN, ChatColor.GOLD + target.getName(), ChatColor.YELLOW);
                        target.sendFormattedMessage("{0} added you to their trusted player list.", ChatColor.GREEN + player.getName());
                    } else {
                        throw new CommandException("That player is not on your team.");
                    }
                } else {
                    throw new CommandException("That player is not on a team or their team has already lost.");
                }
            } else {
                throw new CommandException("You are not on a team or your team has already lost.");
            }
        } else {
            throw new CommandException("The game is not active.");
        }
    }

    @Command(
            aliases = {"untrust"},
            usage = "<player>",
            desc = "Remove someone from your trusted list.",
            min = 1,
            max = 1
    )
    public void unTrust(final CommandContext args, final CommandSender sender) throws CommandException {
        Player player = CommandValidation.requirePlayer(sender);
        Player target = CommandValidation.targetPlayer(args.getString(0));
        if (target.equals(player)) {
            throw new CommandException("You can not untrust yourself.");
        }
        PlayerData playerData = plugin.getPlayerDataManager().get(player);
        PlayerData targetData = plugin.getPlayerDataManager().get(target);
        if (!playerData.getAllowedPlayers().contains(target.getUniqueId())) {
            player.sendFormattedMessage("{0} is not trusted by you. {1}If you want to trust this player use /trust <name>", ChatColor.GOLD + target.getName(), ChatColor.YELLOW);
            return;
        }
        if (plugin.getGameManager().getPhase().equals(Phase.STARTED)) {
            if (playerData.getTeam() != null && !playerData.getTeam().isEliminated()) {
                if (targetData.getTeam() != null && !targetData.getTeam().isEliminated()) {
                    if (targetData.getTeam().equals(playerData.getTeam())) {
                        playerData.getAllowedPlayers().remove(target.getUniqueId());
                        player.sendFormattedMessage("{0}Removed {1} from your trusted players list. {2}Use /trust <name> to readd them.", ChatColor.RED, ChatColor.GOLD + target.getName(), ChatColor.YELLOW);
                        target.sendFormattedMessage("{0} removed you from their trusted player list.", ChatColor.GREEN + player.getName());
                    } else {
                        throw new CommandException("That player is not on your team.");
                    }
                } else {
                    throw new CommandException("That player is not on a team or their team has already lost.");
                }
            } else {
                throw new CommandException("You are not on a team or your team has already lost.");
            }
        } else {
            throw new CommandException("The game is not active.");
        }
    }
}
