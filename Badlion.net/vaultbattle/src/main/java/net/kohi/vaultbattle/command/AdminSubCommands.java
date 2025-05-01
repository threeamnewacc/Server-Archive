package net.kohi.vaultbattle.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.kohi.vaultbattle.Permission;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.menu.admin.AdminMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminSubCommands {


    private final VaultBattlePlugin plugin;

    public AdminSubCommands(VaultBattlePlugin plugin) {
        this.plugin = plugin;
    }

    @Command(
            aliases = {"menu"},
            desc = "Open the vaultbattle admins menu"
    )
    @CommandPermissions(Permission.ADMIN)
    public void adminMenu(final CommandContext args, final CommandSender sender) throws CommandException {
        Player player = CommandValidation.requirePlayer(sender);
        new AdminMenu(plugin).open(player);
    }

    @Command(
            aliases = {"start"},
            desc = "Start the vaultbattle game"
    )
    @CommandPermissions(Permission.ADMIN)
    public void start(final CommandContext args, final CommandSender sender) throws CommandException {
        plugin.getGameManager().preStart();
    }
}
