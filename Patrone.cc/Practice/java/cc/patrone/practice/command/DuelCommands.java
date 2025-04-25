package cc.patrone.practice.command;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.duel.Duel;
import cc.patrone.practice.match.Match;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class DuelCommands {

    private PracticePlugin plugin;

    @Command(name = "duel", inGameOnly = true)
    public void duel(CommandArgs args) {
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
        if (targetProfile.getPlayerState() != PlayerState.LOBBY) {
            args.getSender().sendMessage(ChatColor.RED + "That player is not in the lobby.");
            return;
        }
        Duel duel = targetProfile.getDuelList().stream().filter(d -> d.getRequester() == args.getPlayer() && (System.currentTimeMillis() - d.getTimestamp()) <= 60000).findFirst().orElse(null);
        if (duel != null) {
            args.getSender().sendMessage(ChatColor.RED + "You already have a duel going out to that player.");
            return;
        }
        practiceProfile.setDueling(target);
        args.getPlayer().openInventory(this.plugin.getManagerHandler().getInventoryManager().getDuelInventory());
        return;
    }

    @Command(name = "accept", inGameOnly = true)
    public void accept(CommandArgs args) {
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
        if (targetProfile.getPlayerState() != PlayerState.LOBBY) {
            args.getSender().sendMessage(ChatColor.RED + "That player is not in the lobby.");
            return;
        }
        Duel duel = practiceProfile.getDuelList().stream().filter(d -> d.getRequester() == target && (System.currentTimeMillis() - d.getTimestamp()) <= 60000).findFirst().orElse(null);
        if (duel == null) {
            args.getSender().sendMessage(ChatColor.RED + "You do not have a duel request from that player.");
            return;
        }
        practiceProfile.getDuelList().remove(duel);
        Match match = new Match(this.plugin, args.getPlayer(), target, duel.getKit(), false);
        match.start();
        return;
    }
}
