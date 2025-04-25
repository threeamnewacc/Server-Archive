package us.zonix.practice.commands.event;

import me.maiko.dexter.profile.Profile;
import me.maiko.dexter.rank.Rank;
import me.maiko.dexter.util.CC;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.events.EventState;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.util.Clickable;

public class HostCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public HostCommand() {
        super("host");
        this.setDescription("Host an event.");
        this.setUsage(ChatColor.RED + "Usage: /host <event>");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (args.length < 1) {
                player.openInventory(this.plugin.getInventoryManager().getEventsInventory().getCurrentPage());
                return true;
            } else {
                String eventName = args[0];
                if (eventName == null) {
                    return true;
                } else {
                    Profile profile = Profile.getByUuid(player.getUniqueId());
                    Rank rank = profile.getRank();
                    if (eventName.equalsIgnoreCase("Parkour") && !sender.hasPermission("practice.events.parkour")) {
                        player.sendMessage(CC.RED + "You cannot host the Parkour Event with " + rank.getGameColor() + rank.getId() + CC.RED + " rank.");
                        return false;
                    } else if (eventName.equalsIgnoreCase("Sumo") && !sender.hasPermission("practice.events.sumo")) {
                        player.sendMessage(CC.RED + "You cannot host the Sumo Event with " + rank.getGameColor() + rank.getId() + CC.RED + " rank.");
                        return false;
                    } else if (eventName.equalsIgnoreCase("RedLightGreenLight") && !sender.hasPermission("practice.events.redlightgreenlight")) {
                        player.sendMessage(
                            CC.RED + "You cannot host the Red Light Green Light Event with " + rank.getGameColor() + rank.getId() + CC.RED + " rank."
                        );
                        return false;
                    } else if (eventName.equalsIgnoreCase("BlockParty") && !sender.hasPermission("practice.events.blockparty")) {
                        player.sendMessage(CC.RED + "You cannot host the Block Party Event with " + rank.getGameColor() + rank.getId() + CC.RED + " rank.");
                        return false;
                    } else if (eventName.equalsIgnoreCase("TNTTag") && !sender.hasPermission("practice.events.tnttag")) {
                        player.sendMessage(CC.RED + "You cannot host the TnT Tag Event with " + rank.getGameColor() + rank.getId() + CC.RED + " rank.");
                        return false;
                    } else if (this.plugin.getEventManager().getByName(eventName) == null) {
                        player.sendMessage(ChatColor.RED + "That event doesn't exist.");
                        return true;
                    } else if (System.currentTimeMillis() < this.plugin.getEventManager().getCooldown()) {
                        player.sendMessage(ChatColor.RED + "There is a cooldown. Event can't start at this moment.");
                        return true;
                    } else {
                        PracticeEvent event = this.plugin.getEventManager().getByName(eventName);
                        if (!event.isEnabled()) {
                            player.sendMessage(ChatColor.RED + "That event is currently disabled.");
                        }

                        if (event.getState() != EventState.UNANNOUNCED) {
                            player.sendMessage(ChatColor.RED + "There is currently an active event.");
                            return true;
                        } else {
                            boolean eventBeingHosted = this.plugin
                                .getEventManager()
                                .getEvents()
                                .values()
                                .stream()
                                .anyMatch(e -> e.getState() != EventState.UNANNOUNCED);
                            if (eventBeingHosted) {
                                player.sendMessage(ChatColor.RED + "There is currently an active event.");
                                return true;
                            } else {
                                String toSend = ChatColor.RED.toString()
                                    + ChatColor.BOLD
                                    + "[Event] "
                                    + ChatColor.WHITE
                                    + ""
                                    + event.getName()
                                    + " is starting soon. "
                                    + ChatColor.GRAY
                                    + "[Join]";
                                String toSendDonor = ChatColor.GRAY
                                    + "["
                                    + ChatColor.BOLD
                                    + "*"
                                    + ChatColor.GRAY
                                    + "] "
                                    + ChatColor.RED.toString()
                                    + ChatColor.BOLD
                                    + player.getName()
                                    + ChatColor.WHITE
                                    + " is hosting a "
                                    + ChatColor.WHITE.toString()
                                    + ChatColor.BOLD
                                    + event.getName()
                                    + " Event. "
                                    + ChatColor.GRAY
                                    + "[Join]";
                                Clickable message = new Clickable(
                                    player.hasPermission("practice.donator") ? toSendDonor : toSend,
                                    ChatColor.GRAY + "Click to join this event.",
                                    "/join " + event.getName()
                                );
                                this.plugin.getServer().getOnlinePlayers().forEach(message::sendToPlayer);
                                if (player.hasPermission("host.limit.50")) {
                                    event.setLimit(50);
                                } else if (player.hasPermission("host.limit.45")) {
                                    event.setLimit(45);
                                } else if (player.hasPermission("host.limit.40")) {
                                    event.setLimit(40);
                                } else if (player.hasPermission("host.limit.35")) {
                                    event.setLimit(35);
                                } else {
                                    event.setLimit(30);
                                }

                                if (args.length == 2 && player.isOp()) {
                                    if (!NumberUtils.isNumber(args[1])) {
                                        player.sendMessage(ChatColor.RED + "That's not a correct amount.");
                                        return true;
                                    }

                                    event.setLimit(Integer.parseInt(args[1]));
                                }

                                Practice.getInstance().getEventManager().hostEvent(event, player);
                                return true;
                            }
                        }
                    }
                }
            }
        }
    }
}
