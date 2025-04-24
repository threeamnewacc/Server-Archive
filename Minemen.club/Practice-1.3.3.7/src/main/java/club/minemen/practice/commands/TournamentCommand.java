package club.minemen.practice.commands;

import club.minemen.core.clickable.Clickable;
import club.minemen.core.rank.Rank;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.PlayerUtil;
import club.minemen.core.util.finalutil.StringUtil;
import club.minemen.practice.Practice;
import club.minemen.practice.kit.Kit;
import club.minemen.practice.match.Match;
import club.minemen.practice.match.MatchTeam;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import club.minemen.practice.tournament.Tournament;
import club.minemen.practice.tournament.TournamentState;
import club.minemen.practice.util.TeamUtil;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TournamentCommand extends Command {

	private final static String HELP_MESSAGE = StringUtil.center(CC.PRIMARY + "[==" + CC.SECONDARY + "Tournament Commands" + CC.PRIMARY + "==]\n")
			+ StringUtil.center(CC.SECONDARY + "/tournament join <id>" + CC.PRIMARY + " - Joins a tournament\n")
			+ StringUtil.center(CC.SECONDARY + "/tournament status <id>" + CC.PRIMARY + " - Gives you a status");
	private final Practice plugin = Practice.getInstance();

	public TournamentCommand() {
		super("tournament");
		this.setUsage(CC.RED + "Usage: /tournament [args]");
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] args) {
		if (args.length == 0) {
			commandSender.sendMessage(TournamentCommand.HELP_MESSAGE);
			return true;
		}

		switch (args[0].toLowerCase()) {
			case "create":
				if (!PlayerUtil.testPermission(commandSender, Rank.DEVELOPER)) {
					return true;
				}
				if (args.length == 5) {
					try {
						int id = Integer.parseInt(args[1]);
						int teamSize = Integer.parseInt(args[3]);
						int size = Integer.parseInt(args[4]);
						String kitName = args[2];

						if (size % teamSize != 0) {
							commandSender.sendMessage(CC.RED + "This tournament size and team size would not work together.");
							return true;
						}

						if (this.plugin.getTournamentManager().getTournament(id) != null) {
							commandSender.sendMessage(CC.RED + "This tournament already exists.");
							return true;
						}

						Kit kit = this.plugin.getKitManager().getKit(kitName);

						if (kit == null) {
							commandSender.sendMessage(CC.RED + "This kit does not exist!");
							return true;
						}

						this.plugin.getTournamentManager().createTournament(commandSender, id, teamSize, size, kitName);
					} catch (NumberFormatException e) {
						commandSender.sendMessage(CC.RED + "Usage: /tournament create <id> <kit> <team size> <tournament size>");
					}
				} else {
					commandSender.sendMessage(CC.RED + "Usage: /tournament create <id> <kit> <team size> <tournament size>");
				}
				break;
			case "remove":
				if (!PlayerUtil.testPermission(commandSender, Rank.DEVELOPER)) {
					return true;
				}
				if (args.length == 2) {
					int id = Integer.parseInt(args[1]);
					Tournament tournament = this.plugin.getTournamentManager().getTournament(id);

					if (tournament != null) {
						this.plugin.getTournamentManager().removeTournament(id);
						commandSender.sendMessage(CC.PRIMARY + "Successfully removed tournament " + CC.SECONDARY + id + CC.PRIMARY + ".");
					} else {
						commandSender.sendMessage(CC.RED + "This tournament does not exist.");
					}
				} else {
					commandSender.sendMessage(CC.RED + "Usage: /tournament remove <id>");
				}
				break;
			case "announce":
				if (!PlayerUtil.testPermission(commandSender, Rank.DEVELOPER)) {
					return true;
				}
				if (args.length == 2) {
					int id = Integer.parseInt(args[1]);
					Tournament tournament = this.plugin.getTournamentManager().getTournament(id);

					if (tournament != null) {
						Clickable clickable = new Clickable(CC.SECONDARY + commandSender.getName() + CC.PRIMARY + " is hosting a "
								+ CC.SECONDARY + tournament.getTeamSize() + "v" + tournament.getTeamSize() + " " + tournament.getKitName() + CC.PRIMARY
								+ " tournament! " + CC.GREEN + "[Join]", CC.GREEN + "Click to join!",
								"/tournament join " + id);

						this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () ->
								this.plugin.getServer().getOnlinePlayers().forEach(clickable::sendToPlayer));
					}
				} else {
					commandSender.sendMessage(CC.RED + "Usage: /tournament announce <id>");
				}
				break;
			case "join":
				if (!(commandSender instanceof Player)) {
					return true;
				}
				Player player = (Player) commandSender;
				PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
				if (playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(CC.RED + "You can't do that in this state.");
					return true;
				}
				if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
					player.sendMessage(CC.RED + "You are already in a tournament!");
					return true;
				}
				if (args.length == 2) {
					try {
						int id = Integer.parseInt(args[1]);
						Tournament tournament = this.plugin.getTournamentManager().getTournament(id);

						if (tournament != null) {
							if (tournament.getSize() > tournament.getPlayers().size()) {
								if ((tournament.getTournamentState() == TournamentState.WAITING
										|| tournament.getTournamentState() == TournamentState.STARTING)
										&& tournament.getCurrentRound() == 1) {
									this.plugin.getTournamentManager().joinTournament(id, player);
								} else {
									player.sendMessage(CC.RED + "This tournament has already started!");
								}
							} else {
								player.sendMessage(CC.RED + "This tournament is already full!");
							}
						} else {
							player.sendMessage(CC.RED + "This tournament doesn't exist!");
						}
					} catch (NumberFormatException e) {
						player.sendMessage(CC.RED + "This is not a number!");
					}
				} else {
					player.sendMessage(CC.RED + "Usage: /tournament join <id>");
				}
				break;
			case "status":
				if (args.length == 2) {
					try {
						int id = Integer.parseInt(args[1]);
						Tournament tournament = this.plugin.getTournamentManager().getTournament(id);

						if (tournament != null) {
							StringBuilder builder = new StringBuilder();
							builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
							builder.append(CC.SECONDARY).append("Tournament ").append(tournament.getId()).append(CC.PRIMARY).append("'s matches:");
							builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
							for (UUID matchUUID : tournament.getMatches()) {
								Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);

								MatchTeam teamA = match.getTeams().get(0);
								MatchTeam teamB = match.getTeams().get(1);

								String teamANames = TeamUtil.getNames(teamA);
								String teamBNames = TeamUtil.getNames(teamB);

								builder.append(teamANames).append(" vs. ").append(teamBNames).append("\n");
							}
							builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
							builder.append(CC.PRIMARY).append("Round: ").append(CC.SECONDARY).append(tournament.getCurrentRound()).append("\n");
							builder.append(CC.PRIMARY).append("Players: ").append(CC.SECONDARY).append(tournament.getPlayers().size()).append("\n");
							builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
							commandSender.sendMessage(builder.toString());
						} else {
							commandSender.sendMessage(CC.RED + "This tournament does not exist!");
						}
					} catch (NumberFormatException e) {
						commandSender.sendMessage(CC.RED + "This is not a number!");
					}
				}
				break;
			default:
				commandSender.sendMessage(TournamentCommand.HELP_MESSAGE);
				break;
		}
		return false;
	}
}
