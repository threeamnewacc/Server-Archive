package me.enzol.spigot.command;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.*;

public class KillEntitiesCommand extends Command
{
    public KillEntitiesCommand(final String name) {
        super(name);
        this.description = "Ping player";
        this.usageMessage = "§b/killentities";
        this.setPermission("tspigot.killentities");
    }

    @Override
    public boolean execute(final CommandSender sender, final String currentAlias, final String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        int i = 0;
        for (final World world : Bukkit.getWorlds()) {
            for (final Entity entity : world.getEntities()) {
                final EntityType entityType = entity.getType();
                if (entityType.equals(EntityType.DROPPED_ITEM) || entityType.equals(EntityType.PRIMED_TNT) || entityType.equals(EntityType.SKELETON) || entityType.equals(EntityType.ZOMBIE) || entityType.equals(EntityType.COW) || entityType.equals(EntityType.SHEEP) || entityType.equals(EntityType.ENDERMAN) || entityType.equals(EntityType.PIG_ZOMBIE) || entityType.equals(EntityType.PIG) || entityType.equals(EntityType.CREEPER) || entityType.equals(EntityType.BAT) || entityType.equals(EntityType.CHICKEN) || entityType.equals(EntityType.CAVE_SPIDER) || entityType.equals(EntityType.SPIDER)) {
                    entity.remove();
                    ++i;
                }
            }
        }
        sender.sendMessage(ChatColor.AQUA + "You have removed a total of " + ChatColor.GRAY + i + ChatColor.AQUA + " entities");
        return false;
    }
}
