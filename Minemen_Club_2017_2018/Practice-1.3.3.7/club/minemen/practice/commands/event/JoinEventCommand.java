// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.event;

import club.minemen.core.rank.Rank;
import club.minemen.core.util.cmd.annotation.commandTypes.Command;
import club.minemen.practice.events.PracticeEvent;
import club.minemen.practice.events.EventState;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.cmd.annotation.Param;
import org.bukkit.entity.Player;
import club.minemen.practice.Practice;
import club.minemen.core.util.cmd.CommandHandler;

public class JoinEventCommand implements CommandHandler
{
    private final Practice plugin;
    
    public JoinEventCommand() {
        this.plugin = Practice.getInstance();
    }
    
    @Command(name = { "joinevent" }, rank = Rank.NORMAL, description = "Join an event.")
    public void joinEvent(final Player player, @Param(name = "join") final String eventName) {
        if (eventName == null) {
            return;
        }
        if (this.plugin.getEventManager().getByName(eventName) == null) {
            player.sendMessage(CC.RED + eventName + " doesn't exist.");
            return;
        }
        final PracticeEvent event = this.plugin.getEventManager().getByName(eventName);
        if (event.getState() != EventState.WAITING) {
            player.sendMessage(CC.RED + "You cannot join this event!");
            return;
        }
        if (event.getPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are already playing " + event.getName() + "!");
            return;
        }
        event.join(player);
    }
}
