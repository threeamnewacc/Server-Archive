package us.zonix.practice.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.util.Clickable;
import us.zonix.practice.util.ItemBuilder;
import us.zonix.practice.util.StringUtil;

public class PartyCommand extends Command {
    private static final String NOT_LEADER = ChatColor.RED + "You are not the leader of the party!";
    private static final String[] HELP_MESSAGE = new String[]{
        ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
        ChatColor.RED + "Party Commands:",
        ChatColor.GRAY + "(*) /party help " + ChatColor.WHITE + "- Displays the help menu",
        ChatColor.GRAY + "(*) /party create " + ChatColor.WHITE + "- Creates a party instance",
        ChatColor.GRAY + "(*) /party leave " + ChatColor.WHITE + "- Leave your current party",
        ChatColor.GRAY + "(*) /party info " + ChatColor.WHITE + "- Displays your party information",
        ChatColor.GRAY + "(*) /party join (player) " + ChatColor.WHITE + "- Join a party (invited or unlocked)",
        "",
        ChatColor.RED + "Leader Commands:",
        ChatColor.GRAY + "(*) /party open " + ChatColor.WHITE + "- Open your party for others to join",
        ChatColor.GRAY + "(*) /party lock " + ChatColor.WHITE + "- Lock your party for others to join",
        ChatColor.GRAY + "(*) /party setlimit (amount) " + ChatColor.WHITE + "- Set a limit to your party",
        ChatColor.GRAY + "(*) /party invite (player) " + ChatColor.WHITE + "- Invites a player to your party",
        ChatColor.GRAY + "(*) /party kick (player) " + ChatColor.WHITE + "- Kicks a player from your party",
        ChatColor.GRAY + "(*) /party bard (player) " + ChatColor.WHITE + "- Gives a player the bard role for HCTeams",
        ChatColor.GRAY + "(*) /party archer (player) " + ChatColor.WHITE + "- Gives a player the archer role for HCTeams",
        ChatColor.GRAY + "(*) /party hcteams " + ChatColor.WHITE + "- Opens a role selection menu for HCTeams",
        ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
    };
    private final Practice plugin = Practice.getInstance();
    private static final String ARROW = "→";

    public PartyCommand() {
        super("party");
        this.setDescription("Party Command.");
        this.setUsage(ChatColor.RED + "Usage: /party <subcommand> [player]");
        this.setAliases(Collections.singletonList("p"));
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
            String subCommand = args.length < 1 ? "help" : args[0];
            String var8 = subCommand.toLowerCase();
            switch (var8) {
                case "hcteams":
                    if (party != null) {
                        if (!party.getLeader().equals(player.getUniqueId())) {
                            sender.sendMessage(NOT_LEADER);
                            return true;
                        }

                        this.openSelectionMenu(player);
                        return true;
                    }

                    player.sendMessage(ChatColor.RED + "You're not in a party.");
                    break;
                case "create":
                    if (party != null) {
                        player.sendMessage(ChatColor.RED + "You are already in a party.");
                    } else if (playerData.getPlayerState() != PlayerState.SPAWN) {
                        player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    } else {
                        this.plugin.getPartyManager().createParty(player);
                    }
                    break;
                case "leave":
                    if (party == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a party.");
                    } else if (playerData.getPlayerState() != PlayerState.SPAWN) {
                        player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    } else {
                        this.plugin.getPartyManager().leaveParty(player);
                    }
                    break;
                case "inv":
                case "invite":
                    if (party == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a party.");
                    } else if (!this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You are not the leader of the party.");
                    } else if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
                        player.sendMessage(ChatColor.RED + "You are currently in a tournament.");
                    } else if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "Usage: /party invite (player)");
                    } else if (party.isOpen()) {
                        player.sendMessage(ChatColor.RED + "This party is open, so anyone can join.");
                    } else if (party.getMembers().size() >= party.getLimit()) {
                        player.sendMessage(ChatColor.RED + "Party size has reached it's limit");
                    } else {
                        if (party.getLeader() != player.getUniqueId()) {
                            player.sendMessage(NOT_LEADER);
                            return true;
                        }

                        Player targetx = this.plugin.getServer().getPlayer(args[1]);
                        if (targetx == null) {
                            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                            return true;
                        }

                        PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                        if (targetx.getUniqueId() == player.getUniqueId()) {
                            player.sendMessage(ChatColor.RED + "You can't invite yourself.");
                        } else if (this.plugin.getPartyManager().getParty(targetx.getUniqueId()) != null) {
                            player.sendMessage(ChatColor.RED + "That player is already in a party.");
                        } else if (targetData.getPlayerState() != PlayerState.SPAWN) {
                            player.sendMessage(ChatColor.RED + "That player is currently busy.");
                        } else if (this.plugin.getPartyManager().hasPartyInvite(targetx.getUniqueId(), player.getUniqueId())) {
                            player.sendMessage(ChatColor.RED + "You have already sent a party invitation to this player, please wait.");
                        } else {
                            this.plugin.getPartyManager().createPartyInvite(player.getUniqueId(), targetx.getUniqueId());
                            Clickable partyInvite = new Clickable(
                                ChatColor.GREEN
                                    + sender.getName()
                                    + ChatColor.YELLOW
                                    + " has invited you to their party! "
                                    + ChatColor.GRAY
                                    + "[Click to Accept]",
                                ChatColor.GRAY + "Click to accept",
                                "/party accept " + sender.getName()
                            );
                            partyInvite.sendToPlayer(targetx);
                            party.broadcast(
                                ChatColor.GREEN.toString() + ChatColor.BOLD + "[*] " + ChatColor.YELLOW + targetx.getName() + " has been invited to the party."
                            );
                        }
                    }
                    break;
                case "accept":
                    if (party != null) {
                        player.sendMessage(ChatColor.RED + "You are already in a party.");
                    } else if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "Usage: /party accept <player>.");
                    } else if (playerData.getPlayerState() != PlayerState.SPAWN) {
                        player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    } else {
                        Player target = this.plugin.getServer().getPlayer(args[1]);
                        if (target == null) {
                            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                            return true;
                        }

                        Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
                        if (targetParty == null) {
                            player.sendMessage(ChatColor.RED + "That player is not in a party.");
                        } else if (targetParty.getMembers().size() >= targetParty.getLimit()) {
                            player.sendMessage(ChatColor.RED + "Party size has reached it's limit");
                        } else if (!this.plugin.getPartyManager().hasPartyInvite(player.getUniqueId(), targetParty.getLeader())) {
                            player.sendMessage(ChatColor.RED + "You do not have any pending requests.");
                        } else {
                            this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
                        }
                    }
                    break;
                case "join":
                    if (party != null) {
                        player.sendMessage(ChatColor.RED + "You are already in a party.");
                    } else if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "Usage: /party join <player>.");
                    } else if (playerData.getPlayerState() != PlayerState.SPAWN) {
                        player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    } else {
                        Player targetxxxxx = this.plugin.getServer().getPlayer(args[1]);
                        if (targetxxxxx == null) {
                            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                            return true;
                        }

                        Party targetParty = this.plugin.getPartyManager().getParty(targetxxxxx.getUniqueId());
                        if (targetParty != null && targetParty.isOpen() && targetParty.getMembers().size() < targetParty.getLimit()) {
                            this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
                        } else {
                            player.sendMessage(ChatColor.RED + "You can't join this party.");
                        }
                    }
                    break;
                case "kick":
                    if (party == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a party.");
                    } else if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "Usage: /party kick <player>.");
                    } else {
                        if (party.getLeader() != player.getUniqueId()) {
                            player.sendMessage(NOT_LEADER);
                            return true;
                        }

                        Player targetxxxx = this.plugin.getServer().getPlayer(args[1]);
                        if (targetxxxx == null) {
                            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                            return true;
                        }

                        Party targetParty = this.plugin.getPartyManager().getParty(targetxxxx.getUniqueId());
                        if (targetParty != null && targetParty.getLeader() == party.getLeader()) {
                            this.plugin.getPartyManager().leaveParty(targetxxxx);
                        } else {
                            player.sendMessage(ChatColor.RED + "That player is not in your party.");
                        }
                    }
                    break;
                case "bard":
                    if (party == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a party.");
                    } else if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "Usage: /party bard <player>.");
                    } else {
                        if (party.getLeader() != player.getUniqueId()) {
                            player.sendMessage(NOT_LEADER);
                            return true;
                        }

                        Player targetxxx = this.plugin.getServer().getPlayer(args[1]);
                        if (targetxxx == null) {
                            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                            return true;
                        }

                        Party targetParty = this.plugin.getPartyManager().getParty(targetxxx.getUniqueId());
                        if (targetParty != null && targetParty.getLeader() == party.getLeader()) {
                            if (party.getArchers().size() >= party.getMaxBards()) {
                                player.sendMessage(ChatColor.RED + String.format("Your party has already reached the limit of %s bard.", party.getMaxBards()));
                                return true;
                            }

                            if (party.getBards().contains(targetxxx.getUniqueId())) {
                                player.sendMessage(ChatColor.RED + String.format("%s's role is already bard.", targetxxx.getName()));
                                return true;
                            }

                            party.addBard(targetxxx);
                            player.sendMessage(ChatColor.GREEN + targetxxx.getName() + "'s role is now bard.");
                        } else {
                            player.sendMessage(ChatColor.RED + "That player is not in your party.");
                        }
                    }
                    break;
                case "archer":
                    if (party == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a party.");
                    } else if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "Usage: /party archer <player>.");
                    } else {
                        if (party.getLeader() != player.getUniqueId()) {
                            player.sendMessage(NOT_LEADER);
                            return true;
                        }

                        Player targetxx = this.plugin.getServer().getPlayer(args[1]);
                        if (targetxx == null) {
                            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                            return true;
                        }

                        Party targetParty = this.plugin.getPartyManager().getParty(targetxx.getUniqueId());
                        if (targetParty != null && targetParty.getLeader() == party.getLeader()) {
                            if (party.getArchers().size() >= party.getMaxArchers()) {
                                player.sendMessage(
                                    ChatColor.RED + String.format("Your party has already reached the limit of %s archers.", party.getMaxArchers())
                                );
                                return true;
                            }

                            if (party.getArchers().contains(targetxx.getUniqueId())) {
                                player.sendMessage(ChatColor.RED + String.format("%s's role is already archer.", targetxx.getName()));
                                return true;
                            }

                            party.addArcher(targetxx);
                            player.sendMessage(ChatColor.GREEN + targetxx.getName() + "'s role is now archer.");
                        } else {
                            player.sendMessage(ChatColor.RED + "That player is not in your party.");
                        }
                    }
                    break;
                case "setlimit":
                    if (party == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a party.");
                    } else if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "Usage: /party setlimit <amount>.");
                    } else {
                        if (party.getLeader() != player.getUniqueId()) {
                            player.sendMessage(NOT_LEADER);
                            return true;
                        }

                        try {
                            int limit = Integer.parseInt(args[1]);
                            if (limit >= 2 && limit <= 50) {
                                party.setLimit(limit);
                                player.sendMessage(ChatColor.GREEN + "You have set the party player limit to " + ChatColor.YELLOW + limit + " players.");
                            } else {
                                player.sendMessage(ChatColor.RED + "That is not a valid limit.");
                            }
                        } catch (NumberFormatException var13) {
                            player.sendMessage(ChatColor.RED + "That is not a number.");
                        }
                    }
                    break;
                case "open":
                case "lock":
                    if (party == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a party.");
                    } else {
                        if (party.getLeader() != player.getUniqueId()) {
                            player.sendMessage(NOT_LEADER);
                            return true;
                        }

                        party.setOpen(!party.isOpen());
                        party.broadcast(
                            ChatColor.GREEN.toString()
                                + ChatColor.BOLD
                                + "[*] "
                                + ChatColor.YELLOW
                                + "Your party is now "
                                + ChatColor.BOLD
                                + (party.isOpen() ? "OPEN" : "LOCKED")
                        );
                    }
                    break;
                case "info":
                    if (party == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a party.");
                    } else {
                        List<UUID> members = new ArrayList<>(party.getMembers());
                        members.remove(party.getLeader());
                        StringBuilder builder = new StringBuilder(ChatColor.GOLD + "Members (" + party.getMembers().size() + "): ");
                        members.stream()
                            .<Player>map(this.plugin.getServer()::getPlayer)
                            .filter(Objects::nonNull)
                            .forEach(member -> builder.append(ChatColor.GRAY).append(member.getName()).append(","));
                        String[] information = new String[]{
                            ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
                            ChatColor.RED + "Party Information:",
                            ChatColor.GOLD + "Leader: " + ChatColor.GRAY + this.plugin.getServer().getPlayer(party.getLeader()).getName(),
                            ChatColor.GOLD + builder.toString(),
                            ChatColor.GOLD + "Party State: " + ChatColor.GRAY + (party.isOpen() ? "Open" : "Locked"),
                            ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
                        };
                        player.sendMessage(information);
                    }
                    break;
                default:
                    player.sendMessage(HELP_MESSAGE);
            }

            return true;
        }
    }

    private void openSelectionMenu(Player sender) {
        Party.CLASS_SELECTION_MENU.open(sender);
    }

    private ItemBuilder getItemBuilder(PartyCommand.PlayerRole role) {
        return new ItemBuilder(role.getMaterial())
            .lore(ChatColor.GREEN + "→" + role.getFormattedName())
            .lore(ChatColor.GRAY + "next " + role.next().getFormattedName());
    }

    public static class HCTeamsCommand extends Command {
        public HCTeamsCommand() {
            super("hcteams");
        }

        public boolean execute(CommandSender commandSender, String s, String[] strings) {
            ((Player)commandSender).chat("/party hcteams");
            return true;
        }
    }

    private static enum PlayerRole {
        DIAMOND(Material.DIAMOND_HELMET),
        BARD(Material.GOLD_HELMET),
        ARCHER(Material.LEATHER_HELMET);

        private final Material material;

        public String getFormattedName() {
            return StringUtils.capitalize(this.toString().toLowerCase());
        }

        public PartyCommand.PlayerRole next() {
            return values()[(this.ordinal() + 1) % values().length];
        }

        private PlayerRole(Material material) {
            this.material = material;
        }

        public Material getMaterial() {
            return this.material;
        }
    }
}
