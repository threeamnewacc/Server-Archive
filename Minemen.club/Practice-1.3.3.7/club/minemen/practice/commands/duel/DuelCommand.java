// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.duel;

import club.minemen.practice.party.Party;
import club.minemen.practice.player.PlayerData;
import club.minemen.core.util.finalutil.StringUtil;
import club.minemen.practice.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import org.bukkit.command.Command;

public class DuelCommand extends Command
{
    private final Practice plugin;
    
    public DuelCommand() {
        super("duel");
        this.plugin = Practice.getInstance();
        this.setDescription("Duel a player.");
        this.setUsage(CC.RED + "Usage: /duel <player>");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (args.length < 1) {
            player.sendMessage(this.usageMessage);
            return true;
        }
        if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
            player.sendMessage(CC.RED + "You are in a tournament.");
            return true;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "You can't do this in your current state.");
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }
        if (this.plugin.getTournamentManager().getTournament(target.getUniqueId()) != null) {
            player.sendMessage(CC.RED + "This player is in a tournament.");
            return true;
        }
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if ((party != null && this.plugin.getPartyManager().isInParty(target.getUniqueId(), party)) || player.getName().equals(target.getName())) {
            player.sendMessage(CC.RED + "You can't duel yourself.");
            return true;
        }
        if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You aren't the leader!");
            return true;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        if (targetData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "That player isn't in spawn.");
            return true;
        }
        if (!targetData.isAcceptingDuels()) {
            player.sendMessage(CC.RED + "That player isn't accepting duel requests.");
            return true;
        }
        final Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
        if (party == null && targetParty != null) {
            player.sendMessage(CC.RED + "They are in a party!");
            return true;
        }
        if (party != null && targetParty == null) {
            player.sendMessage(CC.RED + "You are in a party!");
            return true;
        }
        playerData.setDuelSelecting(target.getUniqueId());
        player.openInventory(this.plugin.getInventoryManager().getDuelInventory().getCurrentPage());
        return true;
    }
}
