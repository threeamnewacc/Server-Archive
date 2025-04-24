package club.minemen.practice.commands;

import club.minemen.core.clickable.Clickable;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.StringUtil;
import club.minemen.practice.Practice;
import club.minemen.practice.party.Party;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCommand extends Command {
	private final static String NOT_LEADER = CC.RED + "You are not the leader of the party!";
	private final static String HELP_MESSAGE = StringUtil.center(CC.PRIMARY + "[==" + CC.SECONDARY + "Party Commands" + CC.PRIMARY + "==]\n")
			+ StringUtil.center(CC.SECONDARY + "/party create" + CC.PRIMARY + " - Creates a party\n")
			+ StringUtil.center(CC.SECONDARY + "/party leave" + CC.PRIMARY + " - Leaves or disbands\n")
			+ StringUtil.center(CC.SECONDARY + "/party invite <player>" + CC.PRIMARY + " - Invites a player\n")
			+ StringUtil.center(CC.SECONDARY + "/party accept <player>" + CC.PRIMARY + " - Accepts an invite\n")
			+ StringUtil.center(CC.SECONDARY + "/party join <player>" + CC.PRIMARY + " - Joins an open party\n")
			+ StringUtil.center(CC.SECONDARY + "/party kick <player>" + CC.PRIMARY + " - Kicks a player\n")
			+ StringUtil.center(CC.SECONDARY + "/party limit <limit>" + CC.PRIMARY + " - Sets a player limit\n")
			+ StringUtil.center(CC.SECONDARY + "/party open" + CC.PRIMARY + " - Opens a party\n")
			+ StringUtil.center(CC.SECONDARY + "/party close" + CC.PRIMARY + " - Closes a party\n")
			+ StringUtil.center(CC.SECONDARY + "/party list" + CC.PRIMARY + " - Lists the party members");

	private final Practice plugin = Practice.getInstance();

	public PartyCommand() {
		super("party");
		this.setDescription("Manager player parties.");
		this.setUsage(CC.RED + "Usage: /party <subcommand> [player]");
		this.setAliases(Collections.singletonList("p"));
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

		String subCommand = args.length < 1 ? "help" : args[0];

		switch (subCommand.toLowerCase()) {
			case "create":
				if (party != null) {
					player.sendMessage(CC.RED + "You are already in a party.");
				} else if (playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(CC.RED + "You can't do this in your current state.");
				} else {
					this.plugin.getPartyManager().createParty(player);
				}
				break;
			case "leave":
				if (party == null) {
					player.sendMessage(CC.RED + "You are not in a party.");
				} else if (playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(CC.RED + "You can't do this in your current state.");
				} else {
					this.plugin.getPartyManager().leaveParty(player);
				}
				break;
			case "inv":
			case "invite":
				if (party == null) {
					player.sendMessage(CC.RED + "You are not in a party.");
				} else if (!this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
					player.sendMessage(CC.RED + "You are not the party leader.");
				} else if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
					player.sendMessage(CC.RED + "You are in a tournament.");
				} else if (args.length < 2) {
					player.sendMessage(CC.RED + "Usage: /party invite <player>.");
				} else if (party.isOpen()) {
					player.sendMessage(CC.PRIMARY + "The party is open, so anyone can join.");
				} else if (party.getMembers().size() >= party.getLimit()) {
					player.sendMessage(CC.RED + "The party has reached its member limit.");
				} else {
					if (party.getLeader() != player.getUniqueId()) {
						player.sendMessage(PartyCommand.NOT_LEADER);
						return true;
					}
					Player target = this.plugin.getServer().getPlayer(args[1]);

					if (target == null) {
						player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
						return true;
					}
					PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

					if (target.getUniqueId() == player.getUniqueId()) {
						player.sendMessage(CC.RED + "You can't invite yourself.");
					} else if (this.plugin.getPartyManager().getParty(target.getUniqueId()) != null) {
						player.sendMessage(CC.RED + "That player is already in a party.");
					} else if (targetData.getPlayerState() != PlayerState.SPAWN) {
						player.sendMessage(CC.RED + "That player isn't in spawn.");
					} else if (this.plugin.getPartyManager().hasPartyInvite(target.getUniqueId(), player.getUniqueId())) {
						player.sendMessage(CC.RED + "You already sent a party request to that player. Please wait until it expires.");
					} else {
						this.plugin.getPartyManager().createPartyInvite(player.getUniqueId(), target.getUniqueId());

						Clickable partyInvite = new Clickable(CC.SECONDARY + sender.getName() + CC.PRIMARY + " has sent you a party invite. " + CC.GREEN + "[Accept]",
								CC.GREEN + "Click to accept",
								"/party accept " + sender.getName());

						partyInvite.sendToPlayer(target);

						party.broadcast(CC.SECONDARY + target.getName() + CC.PRIMARY + " was invited to the party.");
					}
				}
				break;
			case "accept":
				if (party != null) {
					player.sendMessage(CC.RED + "You are already in a party.");
				} else if (args.length < 2) {
					player.sendMessage(CC.RED + "Usage: /party accept <player>.");
				} else if (playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(CC.RED + "You can't do this in your current state.");
				} else {
					Player target = this.plugin.getServer().getPlayer(args[1]);
					if (target == null) {
						player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
						return true;
					}
					Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());

					if (targetParty == null) {
						player.sendMessage(CC.RED + "That player does not have a party.");
					} else if (targetParty.getMembers().size() >= targetParty.getLimit()) {
						player.sendMessage(CC.RED + "That party is full.");
					} else if (!this.plugin.getPartyManager().hasPartyInvite(player.getUniqueId(), targetParty.getLeader())) {
						player.sendMessage(CC.RED + "You don't have an invite from that player.");
					} else {
						this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
					}
				}
				break;
			case "join":
				if (party != null) {
					player.sendMessage(CC.RED + "You are already in a party.");
				} else if (args.length < 2) {
					player.sendMessage(CC.RED + "Usage: /party join <player>.");
				} else if (playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(CC.RED + "You can't do this in your current state.");
				} else {
					Player target = this.plugin.getServer().getPlayer(args[1]);
					if (target == null) {
						player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
						return true;
					}
					Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());

					if (targetParty == null || !targetParty.isOpen() || targetParty.getMembers().size() >= targetParty.getLimit()) {
						player.sendMessage(CC.RED + "You can't join this party.");
					} else {
						this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
					}
				}
				break;
			case "kick":
				if (party == null) {
					player.sendMessage(CC.RED + "You are not in a party.");
				} else if (args.length < 2) {
					player.sendMessage(CC.RED + "Usage: /party kick <player>.");
				} else {
					if (party.getLeader() != player.getUniqueId()) {
						player.sendMessage(PartyCommand.NOT_LEADER);
						return true;
					}
					Player target = this.plugin.getServer().getPlayer(args[1]);

					if (target == null) {
						player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
						return true;
					}
					Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());

					if (targetParty == null || targetParty.getLeader() != party.getLeader()) {
						player.sendMessage(CC.RED + "That player is not in your party.");
					} else {
						this.plugin.getPartyManager().leaveParty(target);
					}
				}
				break;
			case "limit":
				if (party == null) {
					player.sendMessage(CC.RED + "You are not in a party.");
				} else if (args.length < 2) {
					player.sendMessage(CC.RED + "Usage: /party kick <player>.");
				} else {
					if (party.getLeader() != player.getUniqueId()) {
						player.sendMessage(PartyCommand.NOT_LEADER);
						return true;
					}
					try {
						int limit = Integer.parseInt(args[1]);

						if (limit < 2 || limit > 50) {
							player.sendMessage(CC.RED + "That is not a valid limit.");
						} else {
							party.setLimit(limit);
							player.sendMessage(CC.PRIMARY + "Your party's limit is now " + CC.SECONDARY
									+ limit + CC.PRIMARY + " members.");
						}
					} catch (NumberFormatException e) {
						player.sendMessage(CC.RED + "That is not a valid limit.");
					}
				}
				break;
			case "open":
			case "close":
				if (party == null) {
					player.sendMessage(CC.RED + "You are not in a party.");
				} else {
					if (party.getLeader() != player.getUniqueId()) {
						player.sendMessage(PartyCommand.NOT_LEADER);
						return true;
					}
					party.setOpen(!party.isOpen());

					party.broadcast(CC.PRIMARY + "Your party is now " + CC.SECONDARY +
							(party.isOpen() ? "open" : "closed") + CC.PRIMARY + ".");
				}
				break;
			case "list":
				if (party == null) {
					player.sendMessage(CC.RED + "You are not in a party.");
				} else {
					StringBuilder builder = new StringBuilder(CC.PRIMARY + "Your party (" + party.getMembers().size() + "):\n");

					List<UUID> members = new ArrayList<>(party.getMembers());

					members.remove(party.getLeader());

					builder.append(CC.GREEN).append("Leader: ").append(this.plugin.getServer().getPlayer(party.getLeader()).getName()).append("\n");

					members.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull).forEach(member -> builder.append(CC.AQUA).append(member.getName()).append("\n"));

					player.sendMessage(builder.toString());
				}
				break;
			default:
				player.sendMessage(PartyCommand.HELP_MESSAGE);
				break;
		}
		return true;
	}
}
