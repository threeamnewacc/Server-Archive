package cc.patrone.practice.command.management;

import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.arena.Arena;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class ArenaCommand {

    private PracticePlugin plugin;

    @Command(name = "arena", permission = "practice.command.arena", inGameOnly = true)
    public void arena(CommandArgs args) {
        args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <create:delete:first:second:sumo> <name>");
        return;
    }

    @Command(name = "arena.create", permission = "practice.command.arena", inGameOnly = true)
    public void arenaCreate(CommandArgs args) {
        if (args.getArgs().length != 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <name>");
            return;
        }
        String arenaName = args.getArgs(0);
        Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
        if (arena != null) {
            args.getSender().sendMessage(ChatColor.RED + "That arena already exists!");
            return;
        }
        this.plugin.getManagerHandler().getArenaManager().addArena(new Arena(arenaName, null, null, false));
        args.getSender().sendMessage(ChatColor.GREEN + "You have created the arena " + arenaName + ".");
        return;
    }

    @Command(name = "arena.delete", permission = "practice.command.arena", inGameOnly = true)
    public void arenaDelete(CommandArgs args) {
        if (args.getArgs().length != 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <name>");
            return;
        }
        String arenaName = args.getArgs(0);
        Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
        if (arena == null) {
            args.getSender().sendMessage(ChatColor.RED + "That arena doesn't exists!");
            return;
        }
        this.plugin.getManagerHandler().getArenaManager().removeArena(arena);
        args.getSender().sendMessage(ChatColor.GREEN + "You have deleted the arena " + arena.getName() + ".");
        return;
    }

    @Command(name = "arena.first", permission = "practice.command.arena", inGameOnly = true)
    public void arenaFirst(CommandArgs args) {
        if (args.getArgs().length != 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <name>");
            return;
        }
        String arenaName = args.getArgs(0);
        Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
        if (arena == null) {
            args.getSender().sendMessage(ChatColor.RED + "That arena doesn't exists!");
            return;
        }
        arena.setSpawn1(args.getPlayer().getLocation());
        args.getSender().sendMessage(ChatColor.GREEN + "You have set the first spawn for arena " + arena.getName() + ".");
        return;
    }

    @Command(name = "arena.second", permission = "practice.command.arena", inGameOnly = true)
    public void arenaSecond(CommandArgs args) {
        if (args.getArgs().length != 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <name>");
            return;
        }
        String arenaName = args.getArgs(0);
        Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
        if (arena == null) {
            args.getSender().sendMessage(ChatColor.RED + "That arena doesn't exists!");
            return;
        }
        arena.setSpawn2(args.getPlayer().getLocation());
        args.getSender().sendMessage(ChatColor.GREEN + "You have set the second spawn for arena " + arena.getName() + ".");
        return;
    }

    @Command(name = "arena.sumo", permission = "practice.command.arena", inGameOnly = true)
    public void arenaSumo(CommandArgs args) {
        if (args.getArgs().length != 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <name>");
            return;
        }
        String arenaName = args.getArgs(0);
        Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
        if (arena == null) {
            args.getSender().sendMessage(ChatColor.RED + "That arena doesn't exists!");
            return;
        }
        arena.setSumo(!arena.isSumo());
        args.getSender().sendMessage(ChatColor.GREEN + "You have set the arena " + arena.getName() + " sumo to " + arena.isSumo() + ".");
        return;
    }
}
