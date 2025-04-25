package cc.patrone.practice.command;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.match.Match;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class SpectateCommand {

    private PracticePlugin plugin;

    @Command(name = "spectate", aliases = {"spec", "sp"}, inGameOnly = true)
    public void onCommand(CommandArgs args) {
        if (args.getArgs().length != 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player target = this.plugin.getServer().getPlayer(args.getArgs(0));
        if (target == null || target == args.getPlayer() || args.getArgs(0).equalsIgnoreCase(CorePlugin.verzideName)) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(args.getPlayer());
        if (practiceProfile.getPlayerState() != PlayerState.LOBBY) {
            args.getSender().sendMessage(ChatColor.RED + "You must be in the lobby to do this.");
            return;
        }
        PracticeProfile targetProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(target);
        if (targetProfile.getPlayerState() != PlayerState.MATCH) {
            args.getSender().sendMessage(ChatColor.RED + "That player is not in match.");
            return;
        }
        Match match = targetProfile.getMatch();
        practiceProfile.setSpectating(match);
        practiceProfile.setPlayerState(PlayerState.SPECTATING);
        if (!args.getSender().hasPermission("practice.staff")) {
            match.broadcast(ChatColor.GRAY + " * " + ChatColor.DARK_PURPLE + args.getSender().getName() + ChatColor.LIGHT_PURPLE + " is now spectating.");
        }
        match.getSpectators().add(args.getPlayer().getUniqueId());
        this.plugin.getManagerHandler().getPlayerManager().hideAll(args.getPlayer());
        args.getPlayer().showPlayer(match.getP1());
        args.getPlayer().showPlayer(match.getP2());
        this.plugin.getManagerHandler().getPlayerManager().giveSpectateItems(args.getPlayer());
        args.getPlayer().setAllowFlight(true);
        args.getPlayer().setFlying(true);
        args.getPlayer().teleport(target);
        return;
    }

}
