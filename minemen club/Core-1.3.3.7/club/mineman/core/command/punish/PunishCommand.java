// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command.punish;

import java.beans.ConstructorProperties;
import org.bukkit.entity.Player;
import club.mineman.core.util.finalutil.CC;
import java.sql.Timestamp;
import club.mineman.core.util.finalutil.StringUtil;
import club.mineman.core.util.finalutil.TimeUtil;
import club.mineman.core.util.finalutil.PlayerUtil;
import org.bukkit.command.CommandSender;
import club.mineman.core.rank.Rank;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public abstract class PunishCommand extends Command
{
    protected final CorePlugin plugin;
    private final PunishType type;
    private final Rank rank;
    
    PunishCommand(final Rank rank, final String name, final String desc, final String usage, final PunishType type) {
        super(name);
        this.plugin = CorePlugin.getInstance();
        this.rank = rank;
        this.description = desc;
        this.usageMessage = usage + " [-s]";
        this.type = type;
    }
    
    public final boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!PlayerUtil.testPermission(sender, this.rank)) {
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(this.usageMessage);
            return true;
        }
        final long timeUntilThing = (args.length > 1) ? TimeUtil.parseTime(args[1]) : 1L;
        boolean temporary = args.length > 1 && timeUntilThing != -1L;
        if (this.type == PunishType.BLACKLIST || this.type == PunishType.IPBAN || this.type.getName().toLowerCase().startsWith("un")) {
            temporary = false;
        }
        long time = 0L;
        if (args.length > 1) {
            time = timeUntilThing;
        }
        String reason = StringUtil.buildMessage(args, temporary ? 2 : 1);
        Timestamp expiry = null;
        if (temporary) {
            expiry = new Timestamp(System.currentTimeMillis() + time);
        }
        final boolean silent = reason.toLowerCase().endsWith("-s");
        if (args.length == 1 || (silent && args.length == 2)) {
            switch (this.type) {
                case BLACKLIST:
                case BAN:
                case IPBAN: {
                    reason = "Unfair Advantage";
                    break;
                }
                default: {
                    reason = "Misconduct";
                    break;
                }
            }
        }
        if (silent && args.length > 2) {
            reason = reason.substring(0, reason.length() - 3);
        }
        String ip = null;
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (this.type == PunishType.KICK && target == null) {
            sender.sendMessage(CC.RED + "That player isn't online!");
            return true;
        }
        String name = args[0];
        if (target != null) {
            ip = target.getAddress().getAddress().getHostAddress();
            name = target.getName();
        }
        this.plugin.getPunishmentManager().punish(sender, this.type, name, reason, ip, expiry, !silent, temporary);
        return true;
    }
    
    public enum PunishType
    {
        IPBAN("ipban"), 
        BAN("ban"), 
        BLACKLIST("blacklist"), 
        UNBLACKLIST("unblacklist"), 
        UNBAN("unban"), 
        MUTE("mute"), 
        UNMUTE("unmute"), 
        KICK("kick");
        
        private final String name;
        
        public String getName() {
            return this.name;
        }
        
        @ConstructorProperties({ "name" })
        private PunishType(final String name) {
            this.name = name;
        }
    }
}
