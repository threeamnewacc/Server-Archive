package us.zonix.practice.commands.event;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.events.EventState;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.events.oitc.OITCEvent;
import us.zonix.practice.events.parkour.ParkourEvent;
import us.zonix.practice.events.redrover.RedroverEvent;
import us.zonix.practice.events.sumo.SumoEvent;
import us.zonix.practice.events.tnttag.TNTTagEvent;
import us.zonix.practice.events.woolmixup.WoolMixUpEvent;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;

public class SpectateEventCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public SpectateEventCommand() {
        super("eventspectate");
        this.setDescription("Spectate an event.");
        this.setUsage(ChatColor.RED + "Usage: /eventspectate <event>");
        this.setAliases(Arrays.asList("eventspec", "specevent"));
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (args.length < 1) {
                player.sendMessage(this.usageMessage);
                return true;
            } else {
                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                Party party = this.plugin.getPartyManager().getParty(playerData.getUniqueId());
                if (party == null && (playerData.getPlayerState() == PlayerState.SPAWN || playerData.getPlayerState() == PlayerState.SPECTATING)) {
                    PracticeEvent event = this.plugin.getEventManager().getByName(args[0]);
                    if (event == null) {
                        player.sendMessage(ChatColor.RED + "That player is currently not in an event.");
                        return true;
                    } else if (event.getState() != EventState.STARTED) {
                        player.sendMessage(ChatColor.RED + "That event hasn't started, please wait.");
                        return true;
                    } else {
                        if (playerData.getPlayerState() == PlayerState.SPECTATING) {
                            if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                                player.sendMessage(ChatColor.RED + "You are already spectating this event.");
                                return true;
                            }

                            this.plugin.getEventManager().removeSpectator(player);
                        }

                        player.sendMessage(ChatColor.GREEN + "You are now spectating " + ChatColor.GRAY + event.getName() + " Event" + ChatColor.GREEN + ".");
                        if (event instanceof SumoEvent) {
                            this.plugin.getEventManager().addSpectatorSumo(player, playerData, (SumoEvent)event);
                        } else if (event instanceof OITCEvent) {
                            this.plugin.getEventManager().addSpectatorOITC(player, playerData, (OITCEvent)event);
                        } else if (event instanceof ParkourEvent) {
                            this.plugin.getEventManager().addSpectatorParkour(player, playerData, (ParkourEvent)event);
                        } else if (event instanceof RedroverEvent) {
                            this.plugin.getEventManager().addSpectatorRedrover(player, playerData, (RedroverEvent)event);
                        } else if (event instanceof WoolMixUpEvent) {
                            this.plugin.getEventManager().addSpectatorWoolMixUp(player, playerData, (WoolMixUpEvent)event);
                        } else if (event instanceof TNTTagEvent) {
                            this.plugin.getEventManager().addSpectatorTntTag(player, playerData, (TNTTagEvent)event);
                        }

                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    return true;
                }
            }
        }
    }
}
