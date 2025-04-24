// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.event;

import club.minemen.core.rank.Rank;
import club.minemen.core.util.cmd.annotation.commandTypes.Command;
import club.minemen.practice.events.PracticeEvent;
import java.util.function.Consumer;
import club.minemen.core.clickable.Clickable;
import club.minemen.practice.events.EventState;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.cmd.annotation.Param;
import org.bukkit.entity.Player;
import club.minemen.practice.Practice;
import club.minemen.core.util.cmd.CommandHandler;

public class HostCommand implements CommandHandler
{
    private final Practice plugin;
    
    public HostCommand() {
        this.plugin = Practice.getInstance();
    }
    
    @Command(name = { "host" }, rank = Rank.PARTYMAN, description = "Host an event.")
    public void hostEvent(final Player player, @Param(name = "event") final String eventName) {
        if (eventName == null) {
            return;
        }
        if (this.plugin.getEventManager().getByName(eventName) == null) {
            player.sendMessage(CC.RED + eventName + " doesn't exist.");
            return;
        }
        final PracticeEvent event = this.plugin.getEventManager().getByName(eventName);
        if (event.getState() != EventState.UNANNOUNCED) {
            player.sendMessage(CC.RED + "This event is already in progress!");
            return;
        }
        final boolean eventBeingHosted = this.plugin.getEventManager().getEvents().values().stream().anyMatch(e -> e.getState() != EventState.UNANNOUNCED);
        if (eventBeingHosted) {
            player.sendMessage(CC.RED + "An event is already being hosted!");
            return;
        }
        final Clickable message = new Clickable(CC.B_GOLD + player.getName() + " is hosting " + event.getName() + "! Click to join!", CC.GREEN + "Click to join!", "/joinevent " + event.getName());
        this.plugin.getServer().getOnlinePlayers().forEach(message::sendToPlayer);
        Practice.getInstance().getEventManager().hostEvent(event, player);
    }
}
