package us.zonix.practice.commands.duel;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.util.StringUtil;

public class DuelCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public DuelCommand() {
        super("duel");
        this.setDescription("Duel a player.");
        this.setUsage(ChatColor.RED + "Usage: /duel <player>");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (args.length < 1) {
                player.sendMessage(this.usageMessage);
                return true;
            } else if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
                player.sendMessage(ChatColor.RED + "You are currently in a tournament.");
                return true;
            } else {
                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                if (playerData.getPlayerState() != PlayerState.SPAWN) {
                    player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    return true;
                } else {
                    Player target = this.plugin.getServer().getPlayer(args[0]);
                    if (target == null) {
                        player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
                        return true;
                    } else if (this.plugin.getTournamentManager().getTournament(target.getUniqueId()) != null) {
                        player.sendMessage(ChatColor.RED + "That player is currently in a tournament.");
                        return true;
                    } else {
                        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
                        Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
                        if (player.getName().equals(target.getName())) {
                            player.sendMessage(ChatColor.RED + "You can't duel yourself.");
                            return true;
                        } else if (party != null && targetParty != null && party == targetParty) {
                            player.sendMessage(ChatColor.RED + "You can't duel yourself.");
                            return true;
                        } else if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                            player.sendMessage(ChatColor.RED + "You are not the leader fo the party.");
                            return true;
                        } else {
                            PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
                            if (targetData.getPlayerState() != PlayerState.SPAWN) {
                                player.sendMessage(ChatColor.RED + "That player is currently busy.");
                                return true;
                            } else if (!targetData.getOptions().isDuelRequests()) {
                                player.sendMessage(ChatColor.RED + "That player has ignored duel requests.");
                                return true;
                            } else if (party == null && targetParty != null) {
                                player.sendMessage(ChatColor.RED + "That player is currently in a party.");
                                return true;
                            } else if (party != null && targetParty == null) {
                                player.sendMessage(ChatColor.RED + "You are currently in a party.");
                                return true;
                            } else if (this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId()) != null) {
                                player.performCommand("accept " + target.getName());
                                return true;
                            } else {
                                playerData.setDuelSelecting(target.getUniqueId());
                                player.openInventory(this.plugin.getInventoryManager().getDuelInventory().getCurrentPage());
                                return true;
                            }
                        }
                    }
                }
            }
        }
    }
}
