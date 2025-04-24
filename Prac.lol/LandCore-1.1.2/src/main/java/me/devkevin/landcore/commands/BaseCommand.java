package me.devkevin.landcore.commands;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public abstract class BaseCommand extends Command {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private final Rank requiredRank;

    protected BaseCommand(String name, Rank requiredRank) {
        super(name);
        this.requiredRank = requiredRank;
    }

    protected BaseCommand(String name) {
        this(name, Rank.MEMBER);
    }

    @Override
    public final boolean execute(CommandSender sender, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

            if (!profile.hasRank(requiredRank)) {
                player.sendMessage(CC.RED + "You don't have the required rank to perform this command.");
                return true;
            }
        }

        execute(sender, args);
        return true;
    }

    protected final void setAliases(String... aliases) {
        if (aliases.length > 0) {
            setAliases(aliases.length == 1 ? Collections.singletonList(aliases[0]) : Arrays.asList(aliases));
        }
    }

    protected final void setUsage(String... uses) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < uses.length; i++) {
            String use = uses[i];

            builder.append(use);

            if (i + 1 != uses.length) {
                builder.append(LINE_SEPARATOR);
            }
        }

        setUsage(builder.toString());
    }

    protected abstract void execute(CommandSender sender, String[] args);
}
