package us.zonix.core.misc.command.game;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.ItemBuilder;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class SpawnerCommand extends BaseCommand {

    @Command(name = "spawner", aliases = {"changespawner"}, rank = Rank.MANAGER, requiresPlayer = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /spawner <entity>");
            return;
        }

        EntityType type;

        try {
            type = EntityType.valueOf(args[0].toUpperCase());
        }
        catch (Exception ex) {
            type = null;
        }

        if (type == null) {
            player.sendMessage(ChatColor.RED + "Not an entity named '" + args[0] + "'.");
            return;
        }

        player.getInventory().addItem(new ItemBuilder(Material.MOB_SPAWNER).name(ChatColor.GREEN + type.getName() + " Spawner").build());
    }

}
